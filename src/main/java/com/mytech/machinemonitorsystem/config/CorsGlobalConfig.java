package com.mytech.machinemonitorsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CorsGlobalConfig {
    @Bean
    public CorsFilter corsFilter(CorsProperties corsProperties) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. Combine all possible origins
        List<String> allowedOrigins = new ArrayList<>();

        // Add everything from your properties file
        if (corsProperties.getAllowedOrigins() != null) {
            allowedOrigins.addAll(corsProperties.getAllowedOrigins());
        }

        // 2. Set the origins ONCE
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
