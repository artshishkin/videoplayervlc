package com.artarkatesoft.clipmakerapp.bestframechooser;

import java.util.Objects;

public class BestFrame {

    private String fileName;
    private long duration;
    private long bestFrameTime;

    public BestFrame() {
    }

    public BestFrame(String fileName, long duration, long bestFrameTime) {
        this.fileName = fileName;
        this.duration = duration;
        this.bestFrameTime = bestFrameTime;
    }

    public String getFileName() {
        return fileName;
    }

    public long getDuration() {
        return duration;
    }

    public long getBestFrameTime() {
        return bestFrameTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setBestFrameTime(long bestFrameTime) {
        this.bestFrameTime = bestFrameTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestFrame bestFrame = (BestFrame) o;
        return duration == bestFrame.duration &&
                bestFrameTime == bestFrame.bestFrameTime &&
                fileName.equals(bestFrame.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, duration, bestFrameTime);
    }

    @Override
    public String toString() {
        return "BestFrame{" +
                "fileName='" + fileName + '\'' +
                ", duration=" + duration +
                ", bestFrameTime=" + bestFrameTime +
                '}';
    }
}
