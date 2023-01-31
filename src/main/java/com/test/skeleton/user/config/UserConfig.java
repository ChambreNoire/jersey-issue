/*
 * Copyright (c) 2023. CAST Software - Highlight Team. All rights reserved.
 */

package com.test.skeleton.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.test.skeleton.user.dao.UserDao;

@Configuration
public class UserConfig {

    @Bean
    public UserDao userDao() {
        return new UserDao();
    }
}
