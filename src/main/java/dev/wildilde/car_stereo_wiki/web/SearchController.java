package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Controller
public class SearchController {

    private static final int PAGE_SIZE = 20;

    private final CarStereoRepository carStereoRepository;
    private final TagRepository tagRepository;

    public SearchController(CarStereoRepository carStereoRepository, TagRepository tagRepository) {
        this.carStereoRepository = carStereoRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "") String query,
                         @RequestParam(required = false) List<String> brand,
                         @RequestParam(required = false) List<String> year,
                         @RequestParam(required = false) List<String> size,
                         @RequestParam(required = false) List<String> display,
                         @RequestParam(required = false) List<String> input,
                         @RequestParam(defaultValue = "name") String sortBy,
                         @RequestParam(defaultValue = "asc") String sortDir,
                         Authentication authentication) {

        List<String> brands = brand == null ? Collections.emptyList() : brand.stream().flatMap(s -> Stream.of(s.split(","))).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toList());
        List<Integer> years = year == null ? Collections.emptyList() : year.stream().flatMap(s -> Stream.of(s.split(","))).map(String::trim).filter(s -> !s.isBlank()).map(Integer::valueOf).collect(Collectors.toList());
        List<String> sizes = size == null ? Collections.emptyList() : size.stream().flatMap(s -> Stream.of(s.split(","))).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toList());
        List<String> displays = display == null ? Collections.emptyList() : display.stream().flatMap(s -> Stream.of(s.split(","))).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toList());
        List<String> inputs = input == null ? Collections.emptyList() : input.stream().flatMap(s -> Stream.of(s.split(","))).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toList());

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Page<CarStereo> stereos = carStereoRepository.search(
                query,
                brands.isEmpty() ? null : brands,
                years.isEmpty() ? null : years,
                sizes.isEmpty() ? null : sizes,
                displays.isEmpty() ? null : displays,
                inputs.isEmpty() ? null : inputs,
                PageRequest.of(page, PAGE_SIZE, sort)
        );

        // Search filters
        model.addAttribute("query", query);
        model.addAttribute("brands", brands);
        model.addAttribute("years", years);
        model.addAttribute("sizes", sizes);
        model.addAttribute("displays", displays);
        model.addAttribute("inputs", inputs);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        // Search results
        model.addAttribute("pagedCarStereos", stereos);

        // Filter attributes
        model.addAttribute("allBrands", tagRepository.findAllByType("brand"));

        int minYearTag = carStereoRepository.findLowestYear();
        int maxYearTag = carStereoRepository.findHighestYear();

        System.out.println(minYearTag);
        System.out.println(maxYearTag);

        model.addAttribute("allYears", IntStream.rangeClosed(minYearTag, maxYearTag).boxed().collect(Collectors.toList()));

        model.addAttribute("allSizes", tagRepository.findAllByType("size"));
        model.addAttribute("allDisplays", tagRepository.findAllByType("display"));
        model.addAttribute("allInputs", tagRepository.findAllByType("input"));

        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "page/search";
    }
}
