package dev.wildilde.car_stereo_wiki;

import dev.wildilde.car_stereo_wiki.repository.UserRepository;
import dev.wildilde.car_stereo_wiki.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {
        return http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/error", "/css/**", "/js/**", "/carStereo/{name}", "/api/user", "/search").permitAll()
                        .requestMatchers("/carStereo/add", "/carStereo/*/edit", "/api/tag/add").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(l -> l.
                        logoutSuccessUrl("/").permitAll()
                )
                .csrf(withDefaults())
                .oauth2Login(o -> o
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(u -> u.userService(oAuth2UserService))
                )
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(UserRepository userRepository) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return (userRequest) -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            String id = oAuth2User.getAttribute("id").toString();

            // If not in the database, create a new user
            Optional<User> userOpt = userRepository.findById(id);
            User user;
            if (userOpt.isEmpty()) {
                // Always false (not admin), administrators are added manually
                user = new User(id, false);
                userRepository.save(user);
            } else {
                user = userOpt.get();
            }

            Collection<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
            if (user.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
        };
    }
}
