package com.test.skeleton.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.test.skeleton.security.api.AuthResource;
import com.test.skeleton.user.api.UserResource;

import javax.ws.rs.ext.ContextResolver;

@Configuration
public class JerseyConfig {

    @Bean
    public org.glassfish.jersey.server.ResourceConfig resourceConfig(ObjectMapper objectMapper) {

        return new org.glassfish.jersey.server.ResourceConfig()
            .property(ServletProperties.FILTER_FORWARD_ON_404, true)
            .register((ContextResolver<ObjectMapper>) aClass -> objectMapper)
            .register(DeclarativeLinkingFeature.class)
            .register(JacksonFeature.class)
            .register(HttpMethodOverrideFilter.class)
            .register(AuthResource.class)
            .register(UserResource.class);
    }
}
