package com.artarkatesoft.clipmakerapp.clipmaker;

import com.artarkatesoft.clipmakerapp.bestframechooser.AllBestFrames;
import com.artarkatesoft.clipmakerapp.bestframechooser.BestFrame;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuggle.xuggler.IContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ClipMaker {

    private String audioFileName;
    public static final String BEST_FRAMES_JSON_FILE = "best_frames.json";
    public static final String OUTPUT_PROJECT_FILE = "output_project.json";


    private OutputVideoFileProject outputVideoFileProject;


    private AllBestFrames allBestFrames;
    private Path workingDir;

    public ClipMaker(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public void make() {
        Path audioPath = Paths.get(audioFileName);
        workingDir = audioPath.getParent();

        allBestFrames = findAllBestFrames(workingDir);
        if (allBestFrames == null) return;

        outputVideoFileProject = getOutputVideoFileProject();

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(workingDir.resolve("project.txt").toFile()))) {
//            for (VideoClip videoClip : outputVideoFileProject.getVideoClips()) {
//                writer.write("file " + Paths.get(videoClip.getFullFileName()).getFileName() + "\n");
//                writer.write(String.format("inpoint %.3f\n", 0.001 * videoClip.getStartTimeMs()));
//                writer.write(String.format("outpoint %.3f\n", 0.001 * videoClip.getStopTimeMs()));
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private AllBestFrames findAllBestFrames(Path workingDir) {
        File bestFramesFile = workingDir.resolve(BEST_FRAMES_JSON_FILE).toFile();
        if (bestFramesFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(bestFramesFile, AllBestFrames.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<File> videoFiles = ClipMakerUtility.findAppropriateVideoFilesInDirectory(workingDir);
        List<BestFrame> bestFrames = new ArrayList<>();

        IContainer container = IContainer.make();

        for (File videoFile : videoFiles) {
            try {

                Path timeTickFile = ClipMakerUtility.getTimeTickFileOfMediaFile(videoFile);
                if (timeTickFile == null) continue;
                if (Files.exists(timeTickFile)) {

                    long startTime = System.currentTimeMillis();
                    int result = container.open(videoFile.getAbsolutePath(), IContainer.Type.READ, null);
                    long duration = container.getDuration() / 1000;
//                    long fileSize = container.getFileSize();
                    System.out.printf("Duration %d ms, takes %d ms of %s\n", duration, System.currentTimeMillis() - startTime, videoFile.getName());

                    List<String> lines = Files.readAllLines(timeTickFile);
                    for (String line : lines) {
                        long tickMs = Long.parseLong(line);

                        BestFrame bestFrame = new BestFrame(videoFile.toString(), duration, tickMs);
                        bestFrames.add(bestFrame);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AllBestFrames allBestFrames = new AllBestFrames();
        allBestFrames.setBestFrameList(bestFrames);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(workingDir.resolve(BEST_FRAMES_JSON_FILE).toFile(), allBestFrames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allBestFrames;
    }

    private OutputVideoFileProject getOutputVideoFileProject() {

        Path outputProjectPath = workingDir.resolve(OUTPUT_PROJECT_FILE);
        if (Files.exists(outputProjectPath)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(outputProjectPath.toFile(), OutputVideoFileProject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return createOutputVideoFileProject();
    }


    private OutputVideoFileProject createOutputVideoFileProject() {


        OutputVideoFileProject project = new OutputVideoFileProject();

        try {
            Path audioPath = Paths.get(audioFileName);
            Path timeTickFileOfAudio = ClipMakerUtility.getTimeTickFileOfMediaFile(audioPath.toFile());
            List<String> audioTimeTicks = Files.readAllLines(timeTickFileOfAudio);
            List<Long> timeTicks = audioTimeTicks.stream()
                    .map(Long::parseLong)
                    .sorted()
                    .collect(Collectors.toList());

            long lastTickEnd = 0;
            long desiredOverlay = 500;

            List<BestFrame> tempBestFrames = new ArrayList<>(allBestFrames.getBestFrameList());
            Random r = new Random();
            for (Long timeTick : timeTicks) {
                long minTickTime = timeTick - lastTickEnd;
                List<BestFrame> appropriateFrames = tempBestFrames.stream()
                        .filter(frame -> frame.getBestFrameTime() >= minTickTime)
                        .collect(Collectors.toList());
                BestFrame randomBestFrame = appropriateFrames.get(r.nextInt(appropriateFrames.size()));

                tempBestFrames.remove(randomBestFrame);
                if (tempBestFrames.isEmpty()) Collections.copy(tempBestFrames, allBestFrames.getBestFrameList());

                String fileName = randomBestFrame.getFileName();
                long bestFrameTime = randomBestFrame.getBestFrameTime();
                long startTimeMs = bestFrameTime - minTickTime;
                long duration = randomBestFrame.getDuration();
                long stopTimeMs = Math.min(duration, bestFrameTime + desiredOverlay);

                VideoClip videoClip = new VideoClip(fileName, startTimeMs, stopTimeMs);

                project.addVideoClip(videoClip);

                long realOverLay = stopTimeMs - bestFrameTime;
                lastTickEnd = timeTick + realOverLay;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(workingDir.resolve(OUTPUT_PROJECT_FILE).toFile(), project);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return project;
    }

}
