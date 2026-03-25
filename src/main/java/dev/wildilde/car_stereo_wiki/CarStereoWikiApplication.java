package dev.wildilde.car_stereo_wiki;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class CarStereoWikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarStereoWikiApplication.class, args);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
