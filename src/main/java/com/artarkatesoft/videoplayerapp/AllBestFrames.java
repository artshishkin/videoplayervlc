package com.artarkatesoft.videoplayerapp;

import java.util.List;

public class AllBestFrames {
    private List<BestFrame> bestFrameList;

    public AllBestFrames() {
    }

    public AllBestFrames(List<BestFrame> bestFrameList) {
        this.bestFrameList = bestFrameList;
    }

    public List<BestFrame> getBestFrameList() {
        return bestFrameList;
    }

    public void setBestFrameList(List<BestFrame> bestFrameList) {
        this.bestFrameList = bestFrameList;
    }
}
