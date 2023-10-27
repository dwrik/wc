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
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

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

    @Spec
    private CommandSpec spec;

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
            printOut(getOutputString(wordCount));
        } catch (IOException e) {
            printErr("failed to read input: " + e.getMessage());
            return ExitCode.SOFTWARE;
        } catch (Exception e) {
            printErr("something went wrong: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }

    private String getOutputString(WordCount wordCount) {
        var sb = new StringBuilder();
        final boolean showAll = !showLines && !showWords && !showBytes && !showChars;

        if (showAll || showLines)
            sb.append(space(4)).append(wordCount.lineCount());
        if (showAll || showWords)
            sb.append(space(3)).append(wordCount.wordCount());
        if (showAll || showBytes || showChars)
            sb.append(space(2)).append(wordCount.byteOrCharCount());
        if (file != null)
            sb.append(space(1)).append(file.getName());

        return sb.toString();
    }

    private void printOut(String s) {
        spec.commandLine().getOut().println(s);
    }

    private void printErr(String s) {
        spec.commandLine().getErr().println(s);
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
