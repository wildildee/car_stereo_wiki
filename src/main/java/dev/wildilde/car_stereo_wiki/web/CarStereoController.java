package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.GalleryImage;
import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.GalleryImageRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CarStereoController {
    private final CarStereoRepository carStereoRepository;
    private final TagRepository tagRepository;
    private final GalleryImageRepository galleryImageRepository;

    public CarStereoController(CarStereoRepository carStereoRepository, TagRepository tagRepository, GalleryImageRepository galleryImageRepository) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
        this.galleryImageRepository = galleryImageRepository;
    }

    @GetMapping("/carStereo/{name}")
    String viewCarStereo(@PathVariable String name, Model model, @RequestParam(defaultValue = "") String query, Authentication authentication) {
        // Get the car stereo by name
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            return "redirect:/";
        }
        model.addAttribute("carStereo", carStereo);
        model.addAttribute("query", query);
        
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        return "page/carStereo";
    }

    @GetMapping("/carStereo/{name}/edit")
    String editCarStereo(@PathVariable String name, Model model) {
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            return "redirect:/";
        }
        model.addAttribute("carStereo", carStereo);
        model.addAttribute("allBrands", tagRepository.findAllByType("brand"));
        model.addAttribute("allSizes", tagRepository.findAllByType("size"));
        model.addAttribute("allDisplays", tagRepository.findAllByType("display"));
        model.addAttribute("allInputs", tagRepository.findAllByType("input"));
        return "page/carStereoEdit";
    }

    @PostMapping("/carStereo/{name}/edit")
    String saveCarStereo(@PathVariable String name, @ModelAttribute CarStereo carStereo,
                         @RequestParam(value = "brands", required = false) List<Long> brandIds,
                         @RequestParam(value = "sizes", required = false) List<Long> sizeIds,
                         @RequestParam(value = "displays", required = false) List<Long> displayIds,
                         @RequestParam(value = "inputs", required = false) List<Long> inputIds) {
        CarStereo existing = carStereoRepository.findCarStereoByName(name);
        if(existing == null) {
            return "redirect:/";
        }
        
        existing.setName(carStereo.getName());
        existing.setYear(carStereo.getYear());
        existing.setImage(carStereo.getImage());
        existing.setDescription(carStereo.getDescription());
        
        if (brandIds != null) {
            existing.setBrands(tagRepository.findAllById(brandIds));
        } else {
            existing.setBrands(new ArrayList<>());
        }

        if (sizeIds != null) {
            existing.setSizes(tagRepository.findAllById(sizeIds));
        } else {
            existing.setSizes(new ArrayList<>());
        }

        if (displayIds != null) {
            existing.setDisplays(tagRepository.findAllById(displayIds));
        } else {
            existing.setDisplays(new ArrayList<>());
        }

        if (inputIds != null) {
            existing.setInputs(tagRepository.findAllById(inputIds));
        } else {
            existing.setInputs(new ArrayList<>());
        }
        
        carStereoRepository.save(existing);
        return "redirect:/carStereo/" + existing.getName();
    }

    @GetMapping("/carStereo/add")
    String addCarStereo(Model model) {
        model.addAttribute("carStereo", new CarStereo());
        model.addAttribute("allBrands", tagRepository.findAllByType("brand"));
        model.addAttribute("allSizes", tagRepository.findAllByType("size"));
        model.addAttribute("allDisplays", tagRepository.findAllByType("display"));
        model.addAttribute("allInputs", tagRepository.findAllByType("input"));
        return "page/carStereoEdit";
    }

    @PostMapping("/carStereo/add")
    String createCarStereo(@ModelAttribute CarStereo carStereo,
                           @RequestParam(value = "brands", required = false) List<Long> brandIds,
                           @RequestParam(value = "sizes", required = false) List<Long> sizeIds,
                           @RequestParam(value = "displays", required = false) List<Long> displayIds,
                           @RequestParam(value = "inputs", required = false) List<Long> inputIds) {
        if (brandIds != null) {
            carStereo.setBrands(tagRepository.findAllById(brandIds));
        } else {
            carStereo.setBrands(new ArrayList<>());
        }
        if (sizeIds != null) {
            carStereo.setSizes(tagRepository.findAllById(sizeIds));
        } else {
            carStereo.setSizes(new ArrayList<>());
        }
        if (displayIds != null) {
            carStereo.setDisplays(tagRepository.findAllById(displayIds));
        } else {
            carStereo.setDisplays(new ArrayList<>());
        }
        if (inputIds != null) {
            carStereo.setInputs(tagRepository.findAllById(inputIds));
        } else {
            carStereo.setInputs(new ArrayList<>());
        }
        carStereoRepository.save(carStereo);

        if (carStereo.getImage() != null && !carStereo.getImage().isEmpty()) {
            galleryImageRepository.save(new GalleryImage(carStereo, carStereo.getImage()));
        }

        return "redirect:/carStereo/" + carStereo.getName();
    }
}
