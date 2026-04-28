package com.dbb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Explicit origins (wildcard "*" is NOT allowed with credentials=true)
        config.setAllowedOriginPatterns(List.of(
            "https://rainbow-centaur-be0f7d.netlify.app",
            "https://*.netlify.app",
            "https://*.onrender.com",
            "http://localhost:*"
        ));

        // All methods including OPTIONS for preflight
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // All headers
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies / Authorization header
        config.setAllowCredentials(true);

        // Cache preflight for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
