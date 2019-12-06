/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.index;

import static org.apache.commons.lang3.Validate.notNull;

import com.connexta.search.index.exceptions.ContentException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class ContentExtractorImpl implements ContentExtractor {

  private static final int DEFAULT_MAX_CONTENT_LENGTH = 10485760;

  private final Tika tika;

  public ContentExtractorImpl() {
    this(DEFAULT_MAX_CONTENT_LENGTH);
  }

  public ContentExtractorImpl(int maxContentLength) {
    this.tika = new Tika();
    this.tika.setMaxStringLength(maxContentLength);
  }

  @Override
  public String extractText(InputStream inputStream) throws ContentException {
    try {
      return tika.parseToString(notNull(inputStream));
    } catch (IOException | TikaException e) {
      throw new ContentException("Could not extract text", e);
    }
  }
}
