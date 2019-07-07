package com.hand.zhang.stockmvc.controller;

import com.hand.zhang.stockmvc.job.MyJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FimvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FimvcApplication.class, args);

        MyJob.StartJob(15,30);
    }

}
