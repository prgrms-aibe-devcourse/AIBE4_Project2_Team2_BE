package kr.java.aibe4_project2_team2_be.majormate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class MajorMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(MajorMateApplication.class, args);
    }

}
