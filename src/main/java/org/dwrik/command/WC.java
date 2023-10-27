package org.dwrik.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import org.dwrik.model.WordCount;
import org.dwrik.service.WordCountService;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "wc", mixinStandardHelpOptions = true, version = "wc 1.1", description = "Displays number of lines, words, bytes contained in input file or stdin")
public class WC implements Callable<Integer> {

    @Parameters(arity = "0..1", description = "The input file if any")
    private File file;

    @Option(names = { "-c" }, description = "The number of bytes in input file is written to stdout")
    private boolean showBytes;

    @Option(names = { "-m" }, description = "The number of characters in input file is written to stdout")
    private boolean showChars;

    @Option(names = { "-w" }, description = "The number of words in input file is written to stdout")
    private boolean showWords;

    @Option(names = { "-l" }, description = "The number of lines in input file is written to stdout")
    private boolean showLines;

    @Override
    public Integer call() throws Exception {
        Charset charset = showChars
                ? StandardCharsets.UTF_8
                : StandardCharsets.ISO_8859_1;

        try (BufferedReader reader = file == null
                ? new BufferedReader(new InputStreamReader(System.in))
                : Files.newBufferedReader(file.toPath(), charset)) {
            var wordCountService = new WordCountService(reader);
            var wordCount = wordCountService.getCount();
            System.out.println(getOutputString(wordCount));
        } catch (IOException e) {
            System.err.println("failed to read input: " + e.getMessage());
            return ExitCode.SOFTWARE;
        } catch (Exception e) {
            System.err.println("something went wrong: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }

    private String getOutputString(WordCount wordCount) {
        var sb = new StringBuilder();
        final boolean showAll = !showLines && !showWords && !showBytes && !showChars;

        if (showAll || showLines)
            sb.append(file != null ? space(4) : space(7)).append(wordCount.lineCount());
        if (showAll || showWords)
            sb.append(file != null ? space(3) : space(7)).append(wordCount.wordCount());
        if (showAll || showBytes || showChars)
            sb.append(file != null ? space(2) : space(6)).append(wordCount.byteOrCharCount());
        if (file != null)
            sb.append(space(1)).append(file.getName());

        return sb.toString();
    }

    private static String space(int n) {
        var sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(" ");
        return sb.toString();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WC()).execute(args);
        System.exit(exitCode);
    }
}
