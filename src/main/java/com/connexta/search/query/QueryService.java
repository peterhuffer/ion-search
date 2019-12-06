/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.query;

import com.connexta.search.IndexResult;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public interface QueryService {

  /**
   * @throws com.connexta.search.common.exceptions.SearchException if there was an error querying or
   *     the {@code commonQL} was invalid
   */
  @NotNull
  Set<IndexResult> find(
      @NotNull @Pattern(regexp = ".*\\S.*") @Size(min = 1, max = 5000) final String commonQL);
}
