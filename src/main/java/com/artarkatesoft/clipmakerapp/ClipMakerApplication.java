package com.artarkatesoft.clipmakerapp;

import com.artarkatesoft.clipmakerapp.bestframechooser.FrameChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClipMakerApplication implements CommandLineRunner {

    @Autowired
    private FrameChooser frameChooser;

    public static void main(String[] args) {
        SpringApplication.run(ClipMakerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
