package org.dwrik.command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.dwrik.contants.Constants;
import org.dwrik.model.WordCount;
import org.dwrik.service.WordCountService;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "wc", mixinStandardHelpOptions = true, version = "wc 1.0", description = "Displays number of lines, words, bytes contained in input file or stdin")
public class WC implements Callable<Integer> {

    @Parameters(arity = "0..1", description = "The input file")
    private File file;

    @Option(names = { "-c" }, description = "The number of bytes in input file is written to stdout")
    private boolean showBytes;

    @Option(names = { "-m" }, description = "The number of characters in input file is written to stdout."
            + "If current locale doesn't support multibyte characters this will match the -c option")
    private boolean showChars;

    @Option(names = { "-w" }, description = "The number of words in input file is written to stdout")
    private boolean showWords;

    @Option(names = { "-l" }, description = "The number of lines in input file is written to stdout")
    private boolean showLines;

    @Override
    public Integer call() throws Exception {
        WordCountService wordCountService = new WordCountService(file, showLines, showWords, showBytes, showChars);
        try {
            WordCount wordCount = wordCountService.getCount();
            System.out.println(getOutputString(wordCount));
        } catch (IOException e) {
            System.err.println("something went wrong: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }
        return ExitCode.OK;
    }

    private String getOutputString(WordCount wordCount) {
        StringBuilder sb = new StringBuilder();
        boolean showAll = !showLines && !showWords && !showBytes && !showChars;
        if (showAll || showLines) sb.append(Constants.FOUR_SPACES).append(wordCount.lineCount());
        if (showAll || showWords) sb.append(Constants.THREE_SPACES).append(wordCount.wordCount());
        if (showAll || showBytes || showChars) sb.append(Constants.TWO_SPACES).append(wordCount.byteOrCharCount());
        sb.append(Constants.SINGLE_SPACE).append(file.getName());
        return sb.toString();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WC()).execute(args);
        System.exit(exitCode);
    }
}
