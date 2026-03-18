package dev.wildilde.car_stereo_wiki;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.Tag;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class CarStereoWikiApplication {

    private CarStereoRepository carStereoRepository;
    private TagRepository tagRepository;

    public CarStereoWikiApplication(TagRepository tagRepository, CarStereoRepository carStereoRepository) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarStereoWikiApplication.class, args);
    }
}
