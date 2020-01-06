package com.artarkatesoft.clipmakerapp.processwrapper;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

@Component
public class ProcessWrapper {

    /**
     * Create and Execute Process for each command
     * @return
     */
    public ProcessResult executeProcess(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;

        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        while ((line = reader.readLine()) != null) {
            joiner.add(line);
        }
        int exitVal = process.waitFor();

        return new ProcessResult(exitVal, joiner.toString());
    }
}
