package com.mentorlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MentorlinkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentorlinkBackendApplication.class, args);
    }
}
