/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.index;

import static com.connexta.search.common.configs.SolrConfiguration.CONTENTS_ATTRIBUTE_NAME;
import static com.connexta.search.common.configs.SolrConfiguration.ID_ATTRIBUTE_NAME;
import static com.connexta.search.common.configs.SolrConfiguration.MEDIA_TYPE_ATTRIBUTE_NAME;

import com.connexta.search.common.configs.SolrConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = SolrConfiguration.SOLR_COLLECTION)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Index {

  @Indexed(name = ID_ATTRIBUTE_NAME, type = "string")
  private String id;

  @Indexed(name = CONTENTS_ATTRIBUTE_NAME, type = "string")
  private String contents;

  @Indexed(name = MEDIA_TYPE_ATTRIBUTE_NAME, type = "string")
  private String mediaType;
}
