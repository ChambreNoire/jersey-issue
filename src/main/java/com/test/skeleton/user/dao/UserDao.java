/*
 * Copyright (c) 2023. CAST Software - Highlight Team. All rights reserved.
 */

package com.test.skeleton.user.dao;

import com.test.skeleton.user.domain.UserDocument;

import java.util.Collections;
import java.util.Optional;

public class UserDao {

    public Optional<UserDocument> findByEmail(final String email) {
        if ("user@test.com".equals(email)) {
            return Optional.of(new UserDocument(
                "id1",
                "user@test.com",
                "$2y$10$7STIpd9G2.rmHW7esGvfQuOkwmbaUStxF/gZdtbQinkaA2pEowdLe", // 'password' bcrypted
                Collections.singleton("USER")
            ));
        }
        return Optional.empty();
    }
}
