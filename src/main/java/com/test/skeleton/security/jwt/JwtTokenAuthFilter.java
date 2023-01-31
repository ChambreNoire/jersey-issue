package com.test.skeleton.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Priority;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.util.StringUtils.isEmpty;

@Priority(Priorities.AUTHENTICATION)
public class JwtTokenAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenAuthFilter(final UserDetailsService userDetailsService, final JwtTokenUtils jwtTokenUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse resp, final FilterChain chain)
        throws ServletException, IOException {

        if (req.getCookies() == null) {
            chain.doFilter(req, resp);
            return;
        }

        String token = Arrays.stream(req.getCookies())
            .filter(c -> "ACCESS-TOKEN".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);

        if (isEmpty(token) || !jwtTokenUtils.validateJwtToken(token)) {
            chain.doFilter(req, resp);
            return;
        }

        String username = jwtTokenUtils.getUserNameFromJwtToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, resp);
    }
}
