package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CarStereoController {
    private final CarStereoRepository carStereoRepository;

    public CarStereoController(CarStereoRepository carStereoRepository) {
        this.carStereoRepository = carStereoRepository;
    }

    @GetMapping("/carStereo/{name}")
    String viewCarStereo(@PathVariable String name, Model model, @RequestParam(defaultValue = "") String query) {
        // Get the car stereo by name
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            return "redirect:/";
        }
        model.addAttribute("carStereo", carStereo);
        model.addAttribute("query", query);
        return "page/carStereo";
    }
}
