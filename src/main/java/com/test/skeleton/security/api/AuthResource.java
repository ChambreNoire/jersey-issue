package com.test.skeleton.security.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;

import com.test.skeleton.security.jwt.JwtTokenUtils;
import com.test.skeleton.security.model.LoginRequest;
import com.test.skeleton.security.service.UserDetailsImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Singleton
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Inject
    public AuthResource(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @POST
    @Path("/login")
    public Response login(@RequestBody LoginRequest request) {
        try {
            org.springframework.security.core.Authentication authentication = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                    )
                );

            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            return Response.ok()
                .cookie(new NewCookie(
                    "ACCESS-TOKEN",
                    jwtTokenUtils.generateJwtToken(user),
                    "/", null, null,
                    NewCookie.DEFAULT_MAX_AGE,
                    false, true
                ))
                .build();

        } catch (BadCredentialsException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
