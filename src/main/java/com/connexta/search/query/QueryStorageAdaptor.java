/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.query;

import com.connexta.search.IndexResult;
import com.connexta.search.common.exceptions.SearchException;
import java.util.Set;

public interface QueryStorageAdaptor {

  /**
   * Query with the given {@code commonQL}.
   *
   * @return the set of {@link IndexResult}s matching the query
   * @throws SearchException if there was an error querying or the {@code commonQL} was invalid
   */
  Set<IndexResult> query(String commonQL);
}
