package com.isxcode.acorn.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class AcornApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcornApplication.class, args);
    }
}