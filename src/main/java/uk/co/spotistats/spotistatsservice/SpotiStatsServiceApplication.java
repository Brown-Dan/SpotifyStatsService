package uk.co.spotistats.spotistatsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableRetry
public class SpotiStatsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotiStatsServiceApplication.class, args);
    }

}
