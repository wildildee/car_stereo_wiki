package dev.wildilde.car_stereo_wiki.web;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import dev.wildilde.car_stereo_wiki.entity.CarStereoComment;
import dev.wildilde.car_stereo_wiki.entity.User;
import dev.wildilde.car_stereo_wiki.repository.CarStereoCommentRepository;
import dev.wildilde.car_stereo_wiki.repository.CarStereoRepository;
import dev.wildilde.car_stereo_wiki.repository.UserRepository;
import dev.wildilde.car_stereo_wiki.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
public class CommentController {

    private final CarStereoCommentRepository commentRepository;
    private final CarStereoRepository carStereoRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    public CommentController(CarStereoCommentRepository commentRepository,
                             CarStereoRepository carStereoRepository,
                             UserRepository userRepository,
                             FileService fileService) {
        this.commentRepository = commentRepository;
        this.carStereoRepository = carStereoRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    @PostMapping("/carStereo/{id}/comment")
    public String addComment(@RequestParam("id") Long carStereoId,
                             @RequestParam("text") String text,
                             @RequestParam(value = "photo", required = false) MultipartFile photo,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oauth2User.getAttribute("id").toString();

        Optional<CarStereo> carStereoOpt = carStereoRepository.findById(carStereoId);
        if (carStereoOpt.isEmpty()) {
            throw new ResourceNotFoundException("Car stereo not found");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            // Should not happen as user is logged in
            return "redirect:/";
        }

        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoUrl = fileService.uploadFile(photo, "comment");
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload photo: " + e.getMessage());
                return "redirect:/carStereo/" + carStereoOpt.get().getName();
            }
        }

        CarStereoComment comment = new CarStereoComment(text, photoUrl, carStereoOpt.get(), userOpt.get());
        commentRepository.save(comment);

        redirectAttributes.addFlashAttribute("message", "Comment submitted for review!");
        return "redirect:/carStereo/" + carStereoOpt.get().getName();
    }

    @PostMapping("/comment/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        Optional<CarStereoComment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Comment not found");
        }

        CarStereoComment comment = commentOpt.get();
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oauth2User.getAttribute("id").toString();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (comment.getUser().getId().equals(userId) || isAdmin) {
            commentRepository.delete(comment);
            redirectAttributes.addFlashAttribute("message", "Comment deleted.");
        } else {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this comment.");
        }

        return "redirect:/carStereo/" + comment.getCarStereo().getName();
    }
}
