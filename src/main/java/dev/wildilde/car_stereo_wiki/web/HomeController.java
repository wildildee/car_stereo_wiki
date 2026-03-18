package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
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

    private static final int PAGE_SIZE = 100;

    private final CarStereoRepository carStereoRepository;

    public HomeController(CarStereoRepository carStereoRepository) {
        this.carStereoRepository = carStereoRepository;
    }

    @GetMapping("/")
    String home(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String query, Authentication authentication) {
        // Get all car stereos
        Page<CarStereo> stereos = carStereoRepository.findAll(PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("pagedCarStereos",stereos);
        model.addAttribute("query", query);
        
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        return "page/home";
    }
}
