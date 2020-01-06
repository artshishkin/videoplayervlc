package com.artarkatesoft.clipmakerapp;

import com.artarkatesoft.clipmakerapp.bestframechooser.FrameChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClipMakerApplication implements CommandLineRunner {

    @Autowired
    private FrameChooser frameChooser;

    public static void main(String[] args) {
//        SpringApplication.run(ClipMakerApplication.class, args);

        //To use Swing application we need to set Headless mode to false
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ClipMakerApplication.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}

