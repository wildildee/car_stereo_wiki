package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.GalleryImage;
import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.Resource;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import dev.wildilde.car_stereo_wiki.service.PricingService;
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
    private final PricingService pricingService;

    public CarStereoController(CarStereoRepository carStereoRepository, TagRepository tagRepository, PricingService pricingService) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
        this.pricingService = pricingService;
    }

    @GetMapping("/carStereo/{name}")
    String viewCarStereo(@PathVariable String name, Model model, @RequestParam(defaultValue = "") String query, Authentication authentication) {
        // Get the car stereo by name
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            return "redirect:/";
        }

        // Ensure pricing info exists and is up to date
        carStereo = pricingService.updatePricingInfo(carStereo);

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

        // Ensure pricing info exists and is up to date
        carStereo = pricingService.updatePricingInfo(carStereo);

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
                         @RequestParam(value = "inputs", required = false) List<Long> inputIds,
                         @RequestParam(value = "galleryImageUrls", required = false) List<String> galleryImageUrls,
                         @RequestParam(value = "resourceIcon", required = false) List<String> resourceIcons,
                         @RequestParam(value = "resourceName", required = false) List<String> resourceNames,
                         @RequestParam(value = "resourceLink", required = false) List<String> resourceLinks) {
        CarStereo existing = carStereoRepository.findCarStereoByName(name);
        if(existing == null) {
            return "redirect:/";
        }
        
        existing.setName(carStereo.getName());
        existing.setYear(carStereo.getYear());
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

        if (galleryImageUrls != null) {
            existing.getGalleryImages().clear();
            for (String url : galleryImageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    existing.getGalleryImages().add(new GalleryImage(existing, url));
                }
            }
        } else {
            existing.getGalleryImages().clear();
        }

        if (resourceIcons != null && resourceNames != null && resourceLinks != null) {
            existing.getResources().clear();
            for (int i = 0; i < resourceIcons.size(); i++) {
                String icon = resourceIcons.get(i);
                String rName = resourceNames.get(i);
                String link = resourceLinks.get(i);
                if (rName != null && !rName.trim().isEmpty() && link != null && !link.trim().isEmpty()) {
                    existing.getResources().add(new Resource(existing, icon, rName, link));
                }
            }
        } else {
            existing.getResources().clear();
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
                           @RequestParam(value = "inputs", required = false) List<Long> inputIds,
                           @RequestParam(value = "galleryImageUrls", required = false) List<String> galleryImageUrls,
                           @RequestParam(value = "resourceIcon", required = false) List<String> resourceIcons,
                           @RequestParam(value = "resourceName", required = false) List<String> resourceNames,
                           @RequestParam(value = "resourceLink", required = false) List<String> resourceLinks) {
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

        List<GalleryImage> galleryImages = new ArrayList<>();
        if (galleryImageUrls != null) {
            for (String url : galleryImageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    galleryImages.add(new GalleryImage(carStereo, url));
                }
            }
        }
        carStereo.setGalleryImages(galleryImages);

        List<dev.wildilde.car_stereo_wiki.entity.Resource> resources = new ArrayList<>();
        if (resourceIcons != null && resourceNames != null && resourceLinks != null) {
            for (int i = 0; i < resourceIcons.size(); i++) {
                String icon = resourceIcons.get(i);
                String rName = resourceNames.get(i);
                String link = resourceLinks.get(i);
                if (rName != null && !rName.trim().isEmpty() && link != null && !link.trim().isEmpty()) {
                    resources.add(new dev.wildilde.car_stereo_wiki.entity.Resource(carStereo, icon, rName, link));
                }
            }
        }
        carStereo.setResources(resources);

        carStereoRepository.save(carStereo);

        // Ensure at least one pricing info exists
        pricingService.createInitialPricingInfo(carStereo);

        return "redirect:/carStereo/" + carStereo.getName();
    }
}
