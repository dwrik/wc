package org.dwrik.service;

import java.io.BufferedReader;
import java.io.IOException;

import org.dwrik.model.WordCount;

public class WordCountService {

    private final BufferedReader reader;

    public WordCountService(BufferedReader reader) {
        this.reader = reader;
    }

    public WordCount getCount() throws IOException {
        int lineCount = 0;
        int wordCount = 0;
        int byetOrCharCount = 0;

        int input = 0;
        boolean isPrevCharSpace = true;

        while ((input = reader.read()) != -1) {
            char character = (char) input;

            if (Character.isWhitespace(character)) {
                wordCount += !isPrevCharSpace ? 1 : 0;
                lineCount += character == '\n' ? 1 : 0;
                isPrevCharSpace = true;
            } else if (character != '\n') {
                isPrevCharSpace = false;
            }

            byetOrCharCount++;
        }

        return new WordCount(lineCount, wordCount, byetOrCharCount);
    }
}
