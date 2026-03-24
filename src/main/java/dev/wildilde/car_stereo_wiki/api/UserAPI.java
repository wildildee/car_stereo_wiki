package dev.wildilde.car_stereo_wiki.api;

import dev.wildilde.car_stereo_wiki.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserAPI {

    private final UserRepository userRepository;

    public UserAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("name", principal.getAttribute("login"));

        // Get the user from the database and check if they are an admin
        String id = principal.getAttribute("id").toString();
        userRepository.findById(id).ifPresent(user -> {
            response.put("admin", user.isAdmin());
        });

        return response;
    }
}
