package com.iot.tb.datasender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DataSenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataSenderApplication.class, args);
    }

}
