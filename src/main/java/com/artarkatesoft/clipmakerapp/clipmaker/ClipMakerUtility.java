package com.artarkatesoft.clipmakerapp.clipmaker;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ClipMakerUtility {
    public static List<File> findAppropriateVideoFilesInDirectory(Path directoryPath) {

        File directory = directoryPath.toFile();

        File[] files = directory.listFiles(pathname -> {
            List<String> availableExtensions = Arrays.asList("mp4", "avi", "mkv", "h264");
            String extension = FilenameUtils.getExtension(pathname.getName());
            return availableExtensions.contains(extension);
        });
        return Arrays.asList(files);
    }

    public static Path getTimeTickFileOfMediaFile(File mediaFile)  {
        Path mediaPath = mediaFile.toPath();
        String extension = FilenameUtils.getExtension(mediaPath.getFileName().toString());
        Path destinationPath = mediaPath.resolveSibling(mediaPath.getFileName().toString().replace("." + extension, ".txt"));
        return destinationPath;
    }
}
