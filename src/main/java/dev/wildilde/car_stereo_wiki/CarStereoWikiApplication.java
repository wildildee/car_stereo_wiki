package dev.wildilde.car_stereo_wiki;
import dev.wildilde.car_stereo_wiki.entity.GalleryImage;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.GalleryImageRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class CarStereoWikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarStereoWikiApplication.class, args);
    }
}
