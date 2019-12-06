/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.query.controllers;

import com.connexta.search.IndexResult;
import com.connexta.search.query.QueryService;
import com.connexta.search.rest.models.Result;
import com.connexta.search.rest.spring.QueryApi;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class QueryController implements QueryApi {

  public static final String URL_TEMPLATE = "/search";
  public static final String QUERY_URL_TEMPLATE = "q={q}";

  @NotNull private final QueryService queryService;

  @Override
  public ResponseEntity<List<Result>> query(final String q) {
    List<IndexResult> indexResults = List.copyOf(queryService.find(q));

    List<Result> results = new ArrayList<>();
    for (IndexResult indexResult : indexResults) {
      results.add(
          new Result()
              .irmLocation(indexResult.getIrmLocation())
              .metacardLocation(indexResult.getMetacardLocation()));
    }

    return new ResponseEntity<>(results, HttpStatus.OK);
  }
}
