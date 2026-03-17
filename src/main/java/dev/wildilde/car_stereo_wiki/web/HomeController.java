package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CarStereoRepository carStereoRepository;

    public HomeController(CarStereoRepository carStereoRepository) {
        this.carStereoRepository = carStereoRepository;
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("carStereos", carStereoRepository.findAll());
        return "home";
    }
}
