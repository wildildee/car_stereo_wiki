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
public class SearchController {

    private static final int PAGE_SIZE = 100;

    private final CarStereoRepository carStereoRepository;

    public SearchController(CarStereoRepository carStereoRepository) {
        this.carStereoRepository = carStereoRepository;
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "") String query,
                         @RequestParam(defaultValue = "") String brand,
                         @RequestParam(defaultValue = "") String year,
                         @RequestParam(defaultValue = "") String size,
                         @RequestParam(defaultValue = "") String display,
                         @RequestParam(defaultValue = "") String input,
                         Authentication authentication) {

        Integer yearValue = year.isBlank() ? null : Integer.valueOf(year);

        Page<CarStereo> stereos = carStereoRepository.search(
                query,
                brand,
                yearValue,
                size,
                display,
                input,
                PageRequest.of(page, PAGE_SIZE)
        );

        model.addAttribute("pagedCarStereos", stereos);
        model.addAttribute("query", query);
        model.addAttribute("brand", brand);
        model.addAttribute("year", yearValue);
        model.addAttribute("size", size);
        model.addAttribute("display", display);
        model.addAttribute("input", input);

        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "page/search";
    }
}
