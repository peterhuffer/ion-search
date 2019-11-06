/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.index;

import static com.connexta.search.common.configs.SolrConfiguration.QUERY_TERMS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.connexta.search.common.configs.SolrConfiguration;
import com.connexta.search.index.exceptions.IndexException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.geotools.data.solr.FilterToSolr;
import org.geotools.filter.Filters;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

@Slf4j
public class IndexServiceImpl implements IndexService {

  private static final String EXT_EXTRACTED_TEXT = "ext.extracted.text";
  private final IndexRepository indexRepository;
  private final SolrClient solrClient;

  public IndexServiceImpl(
      @NotNull final IndexRepository indexRepository, @NotNull final SolrClient solrClient) {
    this.indexRepository = indexRepository;
    this.solrClient = solrClient;
  }

  @Override
  public void index(
      @NotBlank final String datasetId,
      @NotBlank final String mediaType,
      @NotNull final InputStream inputStream)
      throws IndexException {
    // TODO check that the dataset exists in S3

    if (!StringUtils.equals(mediaType, ContentType.APPLICATION_JSON.getMimeType())) {
      throw new IndexException(
          INTERNAL_SERVER_ERROR,
          "Expected the content type to be "
              + ContentType.APPLICATION_JSON.getMimeType()
              + " , but was "
              + mediaType);
    }

    // TODO 11/4/2019 PeterHuffer: this check should be done by the database so separate
    // index instances don't have timing issues
    final boolean idAlreadyExists;
    try {
      idAlreadyExists = indexRepository.existsById(datasetId);
    } catch (final Exception e) {
      throw new IndexException(INTERNAL_SERVER_ERROR, "Unable to query index", e);
    }
    if (idAlreadyExists) {
      throw new IndexException(BAD_REQUEST, "Dataset already exists. Overwriting is not supported");
    }

    final String contents;
    try {
      contents = getElement(parseJson(inputStream), EXT_EXTRACTED_TEXT);
    } catch (IOException e) {
      throw new IndexException(INTERNAL_SERVER_ERROR, "Unable to convert InputStream to JSON", e);
    }

    log.info("Attempting to index dataset id {}", datasetId);
    try {
      indexRepository.save(new Index(datasetId, contents, mediaType));
    } catch (final Exception e) {
      throw new IndexException(INTERNAL_SERVER_ERROR, "Unable to save index", e);
    }
  }

  @Override
  public Set<String> query(String cql) throws IndexException {
    return this.query(cql, new FilterToSolr(null));
  }

  @VisibleForTesting
  Set<String> query(String cql, FilterToSolr filterToSolr) {
    Filter filter;
    try {
      filter = ECQL.toFilter(cql);
    } catch (CQLException e) {
      try {
        // The ECQL class does not support an equals on id, ie `id=foo`, but CQL does.
        filter = CQL.toFilter(cql);
      } catch (CQLException cqle) {
        log.debug("Invalid CQL received: {}", cql, cqle);
        throw new IndexException(BAD_REQUEST, "Invalid CQL received");
      }
    }

    final Set<String> unsupportedAttributes =
        Filters.propertyNames(filter).stream()
            .map(PropertyName::getPropertyName)
            .filter(attribute -> !QUERY_TERMS.contains(attribute))
            .collect(Collectors.toSet());
    if (!unsupportedAttributes.isEmpty()) {
      throw new IndexException(BAD_REQUEST, "Received invalid attributes to index on");
    }

    String solrQueryStr;
    try {
      solrQueryStr = filterToSolr.encodeToString(filter);
    } catch (Exception e) {
      log.debug("Unable to transform CQL {} to valid Solr query", cql, e);
      throw new IndexException(INTERNAL_SERVER_ERROR, "Error processing CQL");
    }

    SolrQuery solrQuery = new SolrQuery(solrQueryStr);
    QueryResponse response;
    try {
      response = solrClient.query(SolrConfiguration.SOLR_COLLECTION, solrQuery);
    } catch (SolrServerException | IOException e) {
      log.debug("Failed to query solr", e);
      throw new IndexException(INTERNAL_SERVER_ERROR, "Error querying index");
    }

    Set<String> ids = new HashSet<>();
    for (SolrDocument doc : response.getResults()) {
      Object obj = doc.get(SolrConfiguration.ID_ATTRIBUTE_NAME);
      if (obj instanceof String) {
        ids.add((String) doc.get(SolrConfiguration.ID_ATTRIBUTE_NAME));
      } else {
        // shouldn't hit this since the schema enforces the ID is a string
        log.debug("Skipping invalid solr result {}", doc);
      }
    }

    return ids;
  }

  private static JsonNode parseJson(InputStream stream) throws IOException {
    final ObjectMapper objectMapper;
    objectMapper = new ObjectMapper();
    return objectMapper.readTree(stream);
  }

  private static String getElement(JsonNode json, String fieldName) throws IndexException {
    if (json.get(fieldName) == null) {
      throw new IndexException(INTERNAL_SERVER_ERROR, "JSON is malformed");
    }
    return json.get(fieldName).asText();
  }
}
