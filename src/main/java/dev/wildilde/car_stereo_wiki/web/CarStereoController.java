package dev.wildilde.car_stereo_wiki.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CarStereoController {
    @GetMapping("/carStereo/{name}")
    String getCarStereo(@PathVariable String name) {
        return "carStereo";
    }
}
