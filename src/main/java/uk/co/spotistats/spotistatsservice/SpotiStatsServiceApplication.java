package uk.co.spotistats.spotistatsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SpotiStatsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotiStatsServiceApplication.class, args);
    }

}
