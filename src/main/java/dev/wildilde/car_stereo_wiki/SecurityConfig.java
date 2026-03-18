package dev.wildilde.car_stereo_wiki;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                )
                .build();
    }
}
