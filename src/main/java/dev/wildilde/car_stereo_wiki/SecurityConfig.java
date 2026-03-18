package dev.wildilde.car_stereo_wiki;

import dev.wildilde.car_stereo_wiki.repository.UserRepository;
import dev.wildilde.car_stereo_wiki.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {
        return http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/error", "/css/**", "/js/**", "/carStereo/**", "/api/user", "/search").permitAll()
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
            if(!userRepository.existsById(id)) {
                // Always false (not admin), administrators are added manually
                userRepository.save(new User(id, false));
            }

            return oAuth2User;
        };
    }
}
