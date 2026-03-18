package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.Tag;
import dev.wildilde.car_stereo_wiki.repository.TagRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping("/tag/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addTagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "page/tagAdd";
    }
}
