package com.test.skeleton.security.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.skeleton.security.jwt.JwtTokenUtils;
import com.test.skeleton.security.model.LoginRequest;
import com.test.skeleton.security.service.UserDetailsImpl;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("")
public class Auth2Resource {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Inject
    public Auth2Resource(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/login2")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            org.springframework.security.core.Authentication authentication = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                    )
                );

            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            Cookie cookie = new Cookie("ACCESS-TOKEN", jwtTokenUtils.generateJwtToken(user));
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(-1);

            response.addCookie(cookie);

            return ResponseEntity.ok().build();

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
