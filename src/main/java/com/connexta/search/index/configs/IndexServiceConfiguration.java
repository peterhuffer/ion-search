/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.index.configs;

import com.connexta.search.index.ContentExtractorImpl;
import com.connexta.search.index.IndexService;
import com.connexta.search.index.IndexServiceImpl;
import com.connexta.search.index.IndexStorageAdaptor;
import com.connexta.search.index.IonResourceLoader;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class IndexServiceConfiguration {

  @Bean
  public IndexService indexService(
      @NotNull final IndexStorageAdaptor indexStorageAdaptor,
      @NotNull final ResourceLoader resourceLoader) {
    return new IndexServiceImpl(
        indexStorageAdaptor, new IonResourceLoader(resourceLoader), new ContentExtractorImpl());
  }
}
