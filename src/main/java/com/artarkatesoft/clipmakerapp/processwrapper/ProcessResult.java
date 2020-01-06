package com.artarkatesoft.clipmakerapp.processwrapper;

public class ProcessResult {

    private int exitValue;
    private String logMessage;


    public ProcessResult(int exitValue, String logMessage) {
        this.exitValue = exitValue;
        this.logMessage = logMessage;
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getLogMessage() {
        return logMessage;
    }


}
