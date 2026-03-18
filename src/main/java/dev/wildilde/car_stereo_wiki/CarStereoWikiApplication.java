package dev.wildilde.car_stereo_wiki;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.Tag;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import dev.wildilde.car_stereo_wiki.service.CsvLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public CommandLineRunner loadData(CsvLoaderService csvLoaderService) {
        return args -> {
            boolean shouldLoad = false;
            for (String arg : args) {
                if ("--load-csv".equalsIgnoreCase(arg)) {
                    shouldLoad = true;
                    break;
                }
            }
            if (shouldLoad) {
                csvLoaderService.loadCsvOnStartup("classpath:data/data.csv");
            }
        };
    }
}
