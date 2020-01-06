package com.artarkatesoft.clipmakerapp.processwrapper;

public class VideoProcessResult {

    private String resultFileName;
    private final ProcessResult processResult;

    public VideoProcessResult(String resultFileName, ProcessResult processResult) {
        this.resultFileName = resultFileName;
        this.processResult = processResult;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public int getExitValue() {
        return processResult.getExitValue();
    }

    public String getLogMessage() {
        return  processResult.getLogMessage();
    }




}
