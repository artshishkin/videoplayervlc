package com.artarkatesoft.clipmakerapp.clipmaker;

import java.util.ArrayList;
import java.util.List;

public class OutputVideoFileProject {
    private List<VideoClip> videoClips = new ArrayList<>();

    public OutputVideoFileProject() {
    }

    public List<VideoClip> getVideoClips() {
        return videoClips;
    }

    public void setVideoClips(List<VideoClip> videoClips) {
        this.videoClips = videoClips;
    }

    public void addVideoClip(VideoClip clip) {
        videoClips.add(clip);
    }

//    public void createFfmpegTxt(){
//
//    }
}
