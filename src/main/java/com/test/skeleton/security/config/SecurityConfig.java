/*
 * Copyright (c) 2023. CAST Software - Highlight Team. All rights reserved.
 */

package com.test.skeleton.security.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.test.skeleton.security.jwt.JwtTokenAuthEntryPoint;
import com.test.skeleton.security.jwt.JwtTokenAuthFilter;
import com.test.skeleton.security.jwt.JwtTokenUtils;
import com.test.skeleton.security.service.UserDetailsServiceImpl;
import com.test.skeleton.user.dao.UserDao;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtTokenUtils jwtTokenUtils() {
        return new JwtTokenUtils();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService(final UserDao userDao) {
        return new UserDetailsServiceImpl(userDao);
    }

    @Bean
    public JwtTokenAuthEntryPoint unauthorizedHandler() {
        return new JwtTokenAuthEntryPoint();
    }

    @Bean
    public JwtTokenAuthFilter jwtTokenFilter(final UserDetailsService userDetailsService, final JwtTokenUtils jwtTokenUtils) {
        return new JwtTokenAuthFilter(userDetailsService, jwtTokenUtils);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TomcatContextCustomizer sameSiteCookiesConfig() {
        return context -> {
            final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            // SameSite
            cookieProcessor.setSameSiteCookies(SameSiteCookies.STRICT.getValue());
            context.setCookieProcessor(cookieProcessor);
        };
    }
}
