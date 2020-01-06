package com.artarkatesoft.clipmakerapp.service;

import com.artarkatesoft.clipmakerapp.processwrapper.ProcessResult;
import com.artarkatesoft.clipmakerapp.processwrapper.ProcessWrapper;
import com.artarkatesoft.clipmakerapp.processwrapper.VideoProcessResult;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class VideoFilterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${c.a.v.filter.template.stabilize.part1}")
    private String FILTER_STABILIZATION_1_TEMPLATE;

    @Value("${c.a.v.filter.template.stabilize.part2}")
    private String FILTER_STABILIZATION_2_TEMPLATE;

    @Value("${c.a.v.filter.template.antiflicker}")
    private String FILTER_ANTIFLICKER_TEMPLATE;

    @Value("${c.a.v.filter.template.cropvertical}")
    private String FILTER_CROP_V_TEMPLATE;

    @Value("${c.a.v.filter.template.crophorizontal}")
    private String FILTER_CROP_H_TEMPLATE;

    @Value("${c.a.v.filter.template.rotate}")
    private String FILTER_ROTATE_TEMPLATE;

    @Value("${c.a.v.filter.template.cut}")
    private String FILTER_CUT_TEMPLATE;

    @Value("${c.a.v.filter.template.merge}")
    private String FILTER_MERGE_TEMPLATE;

    @Value("${c.a.v.filter.template.addaudio}")
    private String FILTER_ADD_AUDIO_TEMPLATE;

    @Value("${c.a.v.videofiles.directory}")
    private String videofilesDirectory;

    @Value("${ffmpeg.directory}")
    private String ffmpegDirectory;

    @Value("${c.a.v.filter.sequence}")
    private List<Operation> operationSequence;


    private String ffmpegPath;// = "c:\\Users\\Art\\Downloads\\ffmpeg-20170312-58f0bbc-win64-static\\ffmpeg-20170312-58f0bbc-win64-static\\bin\\ffmpeg.exe";

    private List<Path> videoFiles;


    @Autowired
    private ProcessWrapper processWrapper;

    @PostConstruct
    private void init() throws IOException {
        if (ffmpegDirectory == null) ffmpegDirectory = "";
        ffmpegDirectory = ffmpegDirectory.trim();
        if (!ffmpegDirectory.isEmpty() && !(ffmpegDirectory.endsWith("\\") || ffmpegDirectory.endsWith("/")))
            ffmpegDirectory += "\\";
        ffmpegPath = ffmpegDirectory + "ffmpeg.exe";


        List<String> videoFormats = Arrays.asList("mp4", "avi", "vob", "ogg");
        videoFiles = Files.walk(Paths.get(videofilesDirectory))
                .filter(Files::isRegularFile)
                .filter(filePath -> {
                    String name = filePath.getFileName().toString();
                    if (!name.contains(".")) return false;
                    String extension = name.substring(name.lastIndexOf(".") + 1);
                    return videoFormats.contains(extension);
                })
                .collect(Collectors.toList());

        logger.info("Found files to convert: {}", videoFiles);


    }

    public void convert() {

        for (Path videoFile : videoFiles) {
            String fileName = videoFile.toString();
            VideoProcessResult videoProcessResult = null;
            try {
                for (Operation operation : operationSequence) {

                    switch (operation) {
                        case STABILIZE1:
                            videoProcessResult = stabilize1(fileName);
                            break;
                        case STABILIZE2:
                            videoProcessResult = stabilize2(fileName);
                            break;
                        case ANTIFLICKER:
                            videoProcessResult = antiflicker(fileName);
                            break;
                        case CROP_VERTICAL:
                            videoProcessResult = cropCentralVertical(fileName);
                            break;
                        case CROP_HORIZONTAL:
                            videoProcessResult = cropCentralHorizontal(fileName);
                            break;
                        case ROTATE_CCW:
                            videoProcessResult = rotateCCW(fileName);
                            break;
                    }

                    fileName = videoProcessResult.getResultFileName();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    private VideoProcessResult stabilize1(String fileName) throws IOException, InterruptedException {

        String command = String.format(FILTER_STABILIZATION_1_TEMPLATE, ffmpegPath, fileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(fileName, processResult);
        return videoProcessResult;

    }

    private VideoProcessResult stabilize2(String fileName) throws IOException, InterruptedException {

        String resultFileName = addOperationSuffix(fileName, "stab");

        String command = String.format(FILTER_STABILIZATION_2_TEMPLATE, ffmpegPath, fileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;

    }

    private VideoProcessResult antiflicker(String fileName) throws IOException, InterruptedException {

        String resultFileName = addOperationSuffix(fileName, "antiflicker");

        String command = String.format(FILTER_ANTIFLICKER_TEMPLATE, ffmpegPath, fileName, fileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;

    }

    private VideoProcessResult cropCentralVertical(String fileName) throws IOException, InterruptedException {

        String resultFileName = addOperationSuffix(fileName, "cropv");

        String command = String.format(FILTER_CROP_V_TEMPLATE, ffmpegPath, fileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;

    }

    private VideoProcessResult cropCentralHorizontal(String fileName) throws IOException, InterruptedException {

        String resultFileName = addOperationSuffix(fileName, "croph");

        String command = String.format(FILTER_CROP_H_TEMPLATE, ffmpegPath, fileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;
    }

    private VideoProcessResult rotateCCW(String fileName) throws IOException, InterruptedException {

        String resultFileName = addOperationSuffix(fileName, "rotate");

        String command = String.format(FILTER_ROTATE_TEMPLATE, ffmpegPath, fileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;

    }

    public VideoProcessResult cutFromTo(String fileName, long startTimeMs, long stopTimeMs, String outputFileName) throws IOException, InterruptedException {

//        String resultFileName = addOperationSuffix(fileName, "rotate");
        String resultFileName = outputFileName;

        fileName = quotFileName(fileName);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTime = sdf.format(new Date(startTimeMs));
        String stopTime = sdf.format(new Date(stopTimeMs));

//        c.a.v.filter.template.cut=%s -y -i %s -ss %s -to %s -filter:v fps=fps=30 -async 1 -c:v libx264 %s
        String command = String.format(FILTER_CUT_TEMPLATE, ffmpegPath, fileName, startTime, stopTime, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;

    }

    public VideoProcessResult mergeVideoClips(String clipsListFileName, String outputFileName) throws IOException, InterruptedException {

        String resultFileName = outputFileName;

        clipsListFileName = quotFileName(clipsListFileName);

//c.a.v.filter.template.merge=%s -y -safe 0 -f concat -segment_time_metadata 1 -i %s -vf select=concatdec_select -af aselect=concatdec_select,aresample=async=1 %s

        String command = String.format(FILTER_MERGE_TEMPLATE, ffmpegPath, clipsListFileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;
    }

    public VideoProcessResult addAudioToVideo(String videoFileName, String audioFileName, String outputFileName) throws IOException, InterruptedException {

        String resultFileName = outputFileName;

        videoFileName = quotFileName(videoFileName);
        audioFileName = quotFileName(audioFileName);

//c.a.v.filter.template.addaudio=%s -i %s -i %s -c:v copy -filter_complex "[0:a]aformat=fltp:44100:stereo,apad[0a];[1]aformat=fltp:44100:stereo,volume=1.1[1a];[0a][1a]amerge[a]" -map 0:v -map "[a]" -ac 2 %s
        String command = String.format(FILTER_ADD_AUDIO_TEMPLATE, ffmpegPath, videoFileName, audioFileName, resultFileName);
        ProcessResult processResult = processWrapper.executeProcess(command);
        VideoProcessResult videoProcessResult = new VideoProcessResult(resultFileName, processResult);
        return videoProcessResult;
    }

    private String quotFileName(String fileName) {
        if (fileName.contains(" ") && !(fileName.startsWith("\"") || fileName.startsWith("'")))
            fileName = "\"" + fileName + "\"";
        return fileName;
    }

    private String addOperationSuffix(String filename, String suffix) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        String dotExtension = filename.substring(lastIndexOfDot);
        return filename.replace(dotExtension, "_" + suffix + dotExtension);
    }


}
