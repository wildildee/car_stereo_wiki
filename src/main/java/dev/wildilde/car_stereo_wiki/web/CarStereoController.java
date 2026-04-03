package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.GalleryImage;
import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.Resource;
import dev.wildilde.car_stereo_wiki.entity.CarStereoComment;
import dev.wildilde.car_stereo_wiki.repository.CarStereoCommentRepository;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import dev.wildilde.car_stereo_wiki.service.PricingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class CarStereoController {
    private final CarStereoRepository carStereoRepository;
    private final TagRepository tagRepository;
    private final PricingService pricingService;
    private final CarStereoCommentRepository commentRepository;

    public CarStereoController(CarStereoRepository carStereoRepository, TagRepository tagRepository, PricingService pricingService, CarStereoCommentRepository commentRepository) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
        this.pricingService = pricingService;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/carStereo/{name}")
    String viewCarStereo(@PathVariable String name, Model model, @RequestParam(defaultValue = "") String query, Authentication authentication) {
        // Get the car stereo by name
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            throw new ResourceNotFoundException("Car stereo not found: " + name);
        }

        // Ensure pricing info exists and is up to date
        carStereo = pricingService.updatePricingInfo(carStereo);

        model.addAttribute("carStereo", carStereo);
        model.addAttribute("query", query);
        
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        boolean isUserLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isUserLoggedIn", isUserLoggedIn);
        if(isUserLoggedIn) {
            model.addAttribute("currentUser", authentication.getName());
        }

        List<CarStereoComment> comments;
        if (isAdmin) {
            comments = commentRepository.findAllByCarStereoIdOrderByCreatedAtDesc(carStereo.getId());
        } else if (isUserLoggedIn && authentication.getPrincipal() != null) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String userId = Objects.requireNonNull(oauth2User.getAttribute("id")).toString();
            comments = commentRepository.findAllByCarStereoIdAndUserIdOrderByCreatedAtDesc(carStereo.getId(), userId);
        } else {
            comments = new ArrayList<>();
        }
        model.addAttribute("comments", comments);
        
        return "page/carStereo";
    }

    @GetMapping("/carStereo/{name}/edit")
    String editCarStereo(@PathVariable String name, Model model) {
        CarStereo carStereo = carStereoRepository.findCarStereoByName(name);
        if(carStereo == null) {
            throw new ResourceNotFoundException("Car stereo not found: " + name);
        }

        // Ensure pricing info exists and is up to date
        carStereo = pricingService.updatePricingInfo(carStereo);

        model.addAttribute("carStereo", carStereo);
        model.addAttribute("allBrands", tagRepository.findAllByType("brand"));
        model.addAttribute("allSizes", tagRepository.findAllByType("size"));
        model.addAttribute("allDisplays", tagRepository.findAllByType("display"));
        model.addAttribute("allInputs", tagRepository.findAllByType("input"));
        model.addAttribute("resourceTypes", Resource.TYPES.keySet());
        return "page/carStereoEdit";
    }

    @PostMapping("/carStereo/{name}/edit")
    String saveCarStereo(@PathVariable String name, @ModelAttribute CarStereo carStereo,
                         @RequestParam(value = "brands", required = false) List<Long> brandIds,
                         @RequestParam(value = "sizes", required = false) List<Long> sizeIds,
                         @RequestParam(value = "displays", required = false) List<Long> displayIds,
                         @RequestParam(value = "inputs", required = false) List<Long> inputIds,
                         @RequestParam(value = "galleryImageUrls", required = false) List<String> galleryImageUrls,
                         @RequestParam(value = "resourceType", required = false) List<String> resourceTypes,
                         @RequestParam(value = "resourceName", required = false) List<String> resourceNames,
                         @RequestParam(value = "resourceLink", required = false) List<String> resourceLinks) {
        CarStereo existing = carStereoRepository.findCarStereoByName(name);
        if(existing == null) {
            throw new ResourceNotFoundException("Car stereo not found: " + name);
        }
        
        existing.setName(carStereo.getName());
        existing.setYear(carStereo.getYear());
        existing.setMinPrice(carStereo.getMinPrice());
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

        if (resourceTypes != null && resourceNames != null && resourceLinks != null) {
            existing.getResources().clear();
            for (int i = 0; i < resourceTypes.size(); i++) {
                String type = resourceTypes.get(i);
                String rName = resourceNames.get(i);
                String link = resourceLinks.get(i);
                if (rName != null && !rName.trim().isEmpty() && link != null && !link.trim().isEmpty()) {
                    existing.getResources().add(new Resource(existing, type, rName, link));
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
        model.addAttribute("resourceTypes", Resource.TYPES.keySet());
        return "page/carStereoEdit";
    }

    @PostMapping("/carStereo/add")
    String createCarStereo(@ModelAttribute CarStereo carStereo,
                           @RequestParam(value = "brands", required = false) List<Long> brandIds,
                           @RequestParam(value = "sizes", required = false) List<Long> sizeIds,
                           @RequestParam(value = "displays", required = false) List<Long> displayIds,
                           @RequestParam(value = "inputs", required = false) List<Long> inputIds,
                           @RequestParam(value = "galleryImageUrls", required = false) List<String> galleryImageUrls,
                           @RequestParam(value = "resourceType", required = false) List<String> resourceTypes,
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

        List<Resource> resources = new ArrayList<>();
        if (resourceTypes != null && resourceNames != null && resourceLinks != null) {
            for (int i = 0; i < resourceTypes.size(); i++) {
                String type = resourceTypes.get(i);
                String rName = resourceNames.get(i);
                String link = resourceLinks.get(i);
                if (rName != null && !rName.trim().isEmpty() && link != null && !link.trim().isEmpty()) {
                    resources.add(new Resource(carStereo, type, rName, link));
                }
            }
        }
        carStereo.setResources(resources);

        carStereoRepository.save(carStereo);

        return "redirect:/carStereo/" + carStereo.getName();
    }
}
