package com.test.skeleton.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenAuthEntryPoint.class);

    @Override
    public void commence(final HttpServletRequest req, final HttpServletResponse resp, final AuthenticationException authException)
        throws IOException, ServletException {

        LOGGER.error("Unauthorized error: {}", authException.getMessage());

        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", req.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getOutputStream(), body);
    }
}
