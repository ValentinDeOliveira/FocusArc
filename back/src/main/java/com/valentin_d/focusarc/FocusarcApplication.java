package com.valentin_d.focusarc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@EnableScheduling
@Validated
public class FocusarcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusarcApplication.class, args);
    }

}