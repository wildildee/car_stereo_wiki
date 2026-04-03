package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 20;

    private final CarStereoRepository carStereoRepository;
    private final TagRepository tagRepository;

    public HomeController(CarStereoRepository carStereoRepository, TagRepository tagRepository) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/")
    String home(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String query, Authentication authentication) {
        // Get recently modified car stereos
        Page<CarStereo> stereos = carStereoRepository.findAllByOrderByLastModifiedDesc(PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("pagedCarStereos",stereos);
        model.addAttribute("query", query);
        
        // Add statistics for the welcome section
        model.addAttribute("totalStereos", carStereoRepository.count());
        model.addAttribute("totalBrands", tagRepository.findAllByType("brand").size());
        
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        return "page/home";
    }
}
