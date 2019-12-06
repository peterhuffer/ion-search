/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.search.index;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentExtractorImplTest {

  private static final String SAMPLE_PDF = "extractor/sample.pdf";

  private static final String SAMPLE_DOC = "extractor/file-sample_100kB.doc";

  private static final String SAMPLE_DOCX = "extractor/file-sample_100kB.docx";

  private static final String SAMPLE_PPT = "extractor/file_example_PPT_250kB.ppt";

  private static final String SAMPLE_PPTX = "extractor/sample-pptx.pptx";

  private static final String SAMPLE_XLS = "extractor/file_example_XLS_10.xls";

  private static final String SAMPLE_XLSX = "extractor/file_example_XLSX_10.xlsx";

  private static final String SAMPLE_JPG = "extractor/file_example_JPG_100kB.jpg";

  private static final String SAMPLE_TXT = "extractor/sample1.txt";

  private ContentExtractor contentExtractor;

  @BeforeEach
  void beforeEach() {
    contentExtractor = new ContentExtractorImpl();
  }

  @Test
  @DisplayName("Text Extraction from PDF")
  void testExtractTextPdf() throws Exception {
    final String EXPECTED_WORD = "demonstration";
    try (InputStream inputStream = getFileInputStream(SAMPLE_PDF)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from DOC")
  void testExtractTextDoc() throws Exception {
    final String EXPECTED_WORD = "sollicitudin";
    try (InputStream inputStream = getFileInputStream(SAMPLE_DOC)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from DOCX")
  void testExtractTextDocx() throws Exception {
    final String EXPECTED_WORD = "Pellentesque";
    try (InputStream inputStream = getFileInputStream(SAMPLE_DOCX)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from PPT")
  void testExtractTextPpt() throws Exception {
    final String EXPECTED_WORD = "Maecenas";
    try (InputStream inputStream = getFileInputStream(SAMPLE_PPT)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from PPTX")
  void testExtractTextPptx() throws Exception {
    final String EXPECTED_WORD = "outline";
    try (InputStream inputStream = getFileInputStream(SAMPLE_PPTX)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from XLS")
  void testExtractTextXls() throws Exception {
    final String EXPECTED_WORD = "Hashimoto";
    try (InputStream inputStream = getFileInputStream(SAMPLE_XLS)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from XLSX")
  void testExtractTextXlsx() throws Exception {
    final String EXPECTED_WORD = "Melgar";
    try (InputStream inputStream = getFileInputStream(SAMPLE_XLSX)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, containsString(EXPECTED_WORD));
    }
  }

  @Test
  @DisplayName("Test Text Extraction from JPG")
  void testExtractTextJpg() throws Exception {
    try (InputStream inputStream = getFileInputStream(SAMPLE_JPG)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, emptyString());
    }
  }

  @Test
  @DisplayName("Test Text Extraction from null Input Stream")
  void testExtractTextNullInputStreamThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> {
          contentExtractor.extractText(null);
        });
  }

  @Test
  @DisplayName("Test Max Content Length")
  void testMaxContentLength() throws Exception {
    final String EXPECTED_WORD = "This";
    final int MAX_CONTENT_LENGTH = 4;
    contentExtractor = new ContentExtractorImpl(MAX_CONTENT_LENGTH);
    try (InputStream inputStream = getFileInputStream(SAMPLE_TXT)) {
      final String content = contentExtractor.extractText(inputStream);
      assertThat(content, is(EXPECTED_WORD));
    }
  }

  private InputStream getFileInputStream(String testResourcePath) {
    final InputStream inputStream =
        this.getClass().getClassLoader().getResourceAsStream(testResourcePath);
    if (inputStream != null) {
      return inputStream;
    }

    throw new AssertionError(
        String.format("No test resource found at relative path %s", testResourcePath));
  }
}
