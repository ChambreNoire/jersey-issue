/*
 * Copyright (c) 2023. CAST Software - Highlight Team. All rights reserved.
 */

package com.test.skeleton.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.test.skeleton.security.jwt.JwtTokenAuthEntryPoint;
import com.test.skeleton.security.jwt.JwtTokenAuthFilter;

import static java.util.Collections.singletonList;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_PATTERNS;

    static {
        PUBLIC_PATTERNS = new String[]{
            "/login",
            "/login2"
        };
    }

    private final JwtTokenAuthFilter jwtTokenAuthFilter;
    private final JwtTokenAuthEntryPoint unauthorizedHandler;

    @Autowired
    public SecurityConfigurationAdapter(final JwtTokenAuthFilter jwtTokenAuthFilter, final JwtTokenAuthEntryPoint unauthorizedHandler) {
        this.jwtTokenAuthFilter = jwtTokenAuthFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//.requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .cors()
                .configurationSource(corsConfig()).and()
            .csrf()
                .ignoringAntMatchers("/login", "/login2")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler).and()
            .authorizeRequests()
                .antMatchers(PUBLIC_PATTERNS).permitAll()
                .anyRequest().authenticated().and()
            .addFilterBefore(jwtTokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers()
                .xssProtection().and()
                .contentSecurityPolicy("script-src 'self';require-trusted-types-for 'script';object-src 'none';");
    }

    private CorsConfigurationSource corsConfig() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(singletonList("http://localhost:9000"));
            config.setAllowedMethods(singletonList("*"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(singletonList("*"));
            config.setExposedHeaders(singletonList("Authorization"));
            config.setMaxAge(3600L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return config;
        };
    }
}
