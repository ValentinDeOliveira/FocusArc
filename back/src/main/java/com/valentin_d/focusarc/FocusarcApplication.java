package com.valentin_d.focusarc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class FocusarcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusarcApplication.class, args);
    }

}