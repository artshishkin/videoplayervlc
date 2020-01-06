package com.artarkatesoft.clipmakerapp.bestframechooser;

import com.artarkatesoft.clipmakerapp.clipmaker.ClipMaker;
import com.artarkatesoft.clipmakerapp.clipmaker.ClipMakerUtility;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;


@Component
public class FrameChooser {

    private Logger logger = LoggerFactory.getLogger(FrameChooser.class);

    private  JFileChooser fileChooser = new JFileChooser();

    private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    private EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

    private List<File> filesInCurrentDirectory;
    private File currentFile;

    @Autowired
    private ClipMaker clipMaker;

//This is the path for libvlc.dll

//    public static void main(String[] args) {
//        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
//        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
//        SwingUtilities.invokeLater(() -> {
//            FrameChooser frameChooser = new FrameChooser();
//        });
//
//    }

    public FrameChooser() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

//MAXIMIZE TO SCREEN
//createUserInterface();

//        SwingUtilities.invokeLater(() -> {
//            createUserInterface();
//        });


    }

    @PostConstruct
    public void init(){
        createUserInterface();
//        SwingUtilities.invokeLater(() -> {
//            createUserInterface();
//        });

    }

    private void createUserInterface() {
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame = new JFrame();


        Canvas canvas = new Canvas();
        canvas.setBackground(Color.black);
        canvas.setBounds(100, 500, 1050, 500);

        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(canvas, BorderLayout.CENTER);
        jPanel1.setBounds(100, 50, 1050, 600);
        frame.add(jPanel1, BorderLayout.NORTH);

        JPanel jPanel2 = new JPanel();

        jPanel2.setBounds(100, 900, 105, 200);
        frame.add(jPanel2, BorderLayout.SOUTH);

        jPanel2.add(createOpenButton());
        jPanel2.add(createPlayButton());
        jPanel2.add(createPauseButton());
        jPanel2.add(createNextButton());
        jPanel2.add(createTimeTickButton());
        jPanel2.add(createMakeClipButton());

        JSlider volumeSlider = new JSlider();
        volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
        volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
        volumeSlider.setPreferredSize(new Dimension(100, 40));
        volumeSlider.setToolTipText("Change volume");
        jPanel2.add(volumeSlider);


        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(canvas));
        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JButton createTimeTickButton() {
        JButton timeTickButton;
        timeTickButton = new JButton();
        timeTickButton.setBounds(110, 50, 150, 100);
        timeTickButton.setText("Tick");


        timeTickButton.addActionListener(actionEvent -> {
            long currentTime = mediaPlayer.getTime();


            String messageText = currentTime + " ms";
//            currentTimeLabel.setText(messageText);
            logger.info("Current tick is "+messageText);

            try {
                addTimeTick(currentTime);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        });
        return timeTickButton;
    }

    private JButton createOpenButton() {
        JButton openButton;
        openButton = new JButton();

//        openButton.setIcon(new ImageIcon("C:/Users/biznis/Desktop/Newspaper/sangbadpratidin/d/play.png"));
//        openButton.setIcon(new ImageIcon("images/play.png"));
        openButton.setText("Open");

        openButton.setBounds(20, 50, 150, 100);
        openButton.addActionListener(actionEvent -> {

            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            File currentDirectory = fileChooser.getCurrentDirectory();

            String defaultDir = "d:\\Users\\admin\\IdeaProjects\\VideoRedactorMaterials";


//            if (currentDirectory == null && Files.exists(Paths.get(defaultDir)))
            if (Files.exists(Paths.get(defaultDir)))
                currentDirectory = new File(defaultDir);
            fileChooser.setCurrentDirectory(currentDirectory);
            int openDialog = fileChooser.showOpenDialog(null);
            if (openDialog == JFileChooser.APPROVE_OPTION) {

                currentFile = fileChooser.getSelectedFile();
                String mediaAbsoluteFileName = currentFile.getAbsolutePath();

                Path mediaPath = currentFile.toPath();

                filesInCurrentDirectory = ClipMakerUtility.findAppropriateVideoFilesInDirectory(mediaPath.getParent());
                mediaPlayer.playMedia(mediaAbsoluteFileName);
                System.out.println(filesInCurrentDirectory);
            }
        });
        return openButton;
    }

    private JButton createPlayButton() {
        JButton playButton;
        playButton = new JButton();

//        playButton.setIcon(new ImageIcon("C:/Users/biznis/Desktop/Newspaper/sangbadpratidin/d/play.png"));
//        playButton.setIcon(new ImageIcon("images/play.png"));
        playButton.setText("Play");
        //  this.setSize(800,450);
        playButton.setBounds(50, 50, 150, 100);
        // playButton.addActionListener((ActionListener) this);

        playButton.addActionListener(actionEvent -> {
            mediaPlayer.play();
        });
        return playButton;
    }

    private JButton createNextButton() {
        JButton nextButton;
        nextButton = new JButton();


        nextButton.setText("Next");
        nextButton.setBounds(50, 50, 150, 100);

        nextButton.addActionListener(actionEvent -> {
            if (filesInCurrentDirectory == null || filesInCurrentDirectory.isEmpty()) return;
            int index = filesInCurrentDirectory.indexOf(currentFile);
            index++;
            if (index >= filesInCurrentDirectory.size()) index = 0;
            currentFile = filesInCurrentDirectory.get(index);
            mediaPlayer.playMedia(currentFile.getPath());
        });
        return nextButton;
    }

    private JButton createMakeClipButton() {
        JButton makeClipButton = new JButton();


        makeClipButton.setText("Make clip");
        makeClipButton.setBounds(50, 50, 150, 100);

        makeClipButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.resetChoosableFileFilters();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Audio files", "wav", "mp3"));
            fileChooser.setDialogTitle("Choose audio file of Clip");
            File currentDir = new File("d:\\Users\\admin\\IdeaProjects\\VideoRedactorMaterials\\");

            if (Files.exists(currentDir.toPath()))
                fileChooser.setCurrentDirectory(currentDir);

            int openDialog = fileChooser.showOpenDialog(null);
            if (openDialog == JFileChooser.APPROVE_OPTION) {

                File audioFile = fileChooser.getSelectedFile();

                long start = System.currentTimeMillis();
                clipMaker.make(audioFile.getAbsolutePath());
                logger.info("Total time of clipMaker.make() is {} ms", System.currentTimeMillis() - start);


            }


        });
        return makeClipButton;
    }

    private JButton createPauseButton() {
        JButton pausebutton;
        pausebutton = new JButton();

//        pausebutton.setIcon(new ImageIcon("C:/Users/biznis/Desktop/Newspaper/sangbadpratidin/d/pause.png"));
//        pausebutton.setIcon(new ImageIcon("D:\\Users\\admin\\IdeaProjects\\videoplayervlc\\src\\main\\resources\\images\\pause.png"));
        pausebutton.setBounds(80, 50, 150, 100);
        pausebutton.setText("Pause");

        pausebutton.addActionListener(actionEvent -> {
            mediaPlayer.pause();
            // or mediaPlayer.pause() depending on what works.
            final long time = mediaPlayer.getTime();
            long length = mediaPlayer.getLength();
            System.out.println(time + " of " + length);
        });
        return pausebutton;
    }


    private void addTimeTick(long timeTick) throws IOException {
        if (timeTick<0) return;
        Path timeTickFilePath = getTimeTickFilePath();
        Files.write(timeTickFilePath, Collections.singletonList(timeTick + ""), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private Path getTimeTickFilePath() throws IOException {
        Path destinationPath = ClipMakerUtility.getTimeTickFileOfMediaFile(currentFile);
        if (!Files.exists(destinationPath)) Files.createFile(destinationPath);
        return destinationPath;
    }


}