package org.dwrik.service;

import org.dwrik.model.WordCount;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class WordCountService {

  private final File file;

  private final boolean showBytes;

  private final boolean showLines;

  private final boolean showWords;

  private final boolean showChars;

  public WordCountService(
      File file, boolean showLines, boolean showWords, boolean showBytes, boolean showChars) {
    this.file = file;
    this.showLines = showLines;
    this.showWords = showWords;
    this.showBytes = showBytes;
    this.showChars = showChars;
  }

  public WordCount getCount() throws IOException {
    int lineCount = 0;
    int wordCount = 0;
    int byteOrCharCount = 0;
    Charset charset = showChars ? StandardCharsets.UTF_8 : StandardCharsets.ISO_8859_1;
    try (BufferedReader br = Files.newBufferedReader(file.toPath(), charset)) {
      int c;
      boolean prevCharSpace = true;
      while ((c = br.read()) != -1) {
        char ch = (char) c;
        if (Character.isWhitespace(ch)) {
          if (!prevCharSpace) wordCount++;
          if (ch == '\n') lineCount++;
          prevCharSpace = true;
        } else if (ch != '\n') {
          prevCharSpace = false;
        }
        byteOrCharCount++;
      }
    }
    return new WordCount(lineCount, wordCount, byteOrCharCount);
  }
}
