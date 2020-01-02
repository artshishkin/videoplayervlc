package com.artarkatesoft.videoplayerapp;

public class VideoClip {
    private String fullFileName;
    private long startTimeMs;
    private long stopTimeMs;

    public VideoClip() {
    }

    public VideoClip(String fullFileName, long startTimeMs, long stopTimeMs) {
        this.fullFileName = fullFileName;
        this.startTimeMs = startTimeMs;
        this.stopTimeMs = stopTimeMs;
    }


    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    public void setStartTimeMs(long startTimeMs) {
        this.startTimeMs = startTimeMs;
    }

    public long getStopTimeMs() {
        return stopTimeMs;
    }

    public void setStopTimeMs(long stopTimeMs) {
        this.stopTimeMs = stopTimeMs;
    }
}
