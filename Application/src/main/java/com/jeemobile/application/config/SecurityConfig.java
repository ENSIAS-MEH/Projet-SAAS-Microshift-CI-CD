package com.jeemobile.application.config;

import com.jeemobile.application.security.CustomAuthenticationFailureHandler;
import com.jeemobile.application.security.CustomAuthenticationSuccessHandler;
import com.jeemobile.application.security.CustomUserDetailsService;
import com.jeemobile.application.service.AuthenticationService;
import com.jeemobile.application.service.HistoriqueService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;
    private final HistoriqueService historiqueService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                           AuthenticationService authenticationService,
                           HistoriqueService historiqueService,
                           PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationService = authenticationService;
        this.historiqueService = historiqueService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**", "/static/**", "/bootstrap/**", "/dist/**", "/plugins/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin((FormLoginConfigurer<HttpSecurity> form) -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(new CustomAuthenticationSuccessHandler(authenticationService, historiqueService))
                .failureHandler(new CustomAuthenticationFailureHandler(authenticationService))
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }
}