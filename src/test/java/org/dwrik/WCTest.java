package org.dwrik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.dwrik.command.WC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

public class WCTest {

    private WC app;
    private CommandLine cmd;
    private StringWriter writer;

    private record ProcessOutput(int exitCode, String output) {
    }

    @BeforeEach
    private void init() {
        app = new WC();
        cmd = new CommandLine(app);
        writer = new StringWriter();
        cmd.setOut(new PrintWriter(writer));
    }

    @Test
    public void testWCNoOptions() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "test.txt" });
    }

    @Test
    public void testWCLines() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "-l", "test.txt" });
    }

    @Test
    public void testWCWords() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "-w", "test.txt" });
    }

    @Test
    public void testWCBytes() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "-c", "test.txt" });
    }

    @Test
    public void testWCCharacters() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "-m", "test.txt" });
    }

    @Test
    public void testWCRandomOptions() throws IOException, InterruptedException {
        executeAndAssert(new String[] { "-mw", "test.txt" });
    }

    private void executeAndAssert(final String[] args) throws IOException, InterruptedException {
        // execute native & own "wc"
        var expected = executeNativeWC(args);
        int exitCode = cmd.execute(args);
        // assert exit code & stdout
        Assertions.assertEquals(expected.exitCode(), exitCode, "exit code");
        Assertions.assertEquals(expected.output().strip(), writer.toString().strip(), "output");
    }

    private ProcessOutput executeNativeWC(String[] args) throws IOException, InterruptedException {
        // prepare process args
        String[] processArgs = new String[args.length + 1];
        System.arraycopy(args, 0, processArgs, 1, args.length);
        processArgs[0] = "wc";
        // execute native "wc"
        var runtime = Runtime.getRuntime();
        var process = runtime.exec(processArgs);
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // grab its output
        String line = null;
        var output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        // return exit code and stdout
        return new ProcessOutput(process.waitFor(), output.toString());
    }
}
