package com.harishkannarao.springboot.graphqlwebmvc.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomAuthenticationResolver implements AuthenticationResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsResolver userDetailsResolver;

    @Autowired
    public CustomAuthenticationResolver(UserDetailsResolver userDetailsResolver) {
        this.userDetailsResolver = userDetailsResolver;
    }

    public Optional<Authentication> resolve(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<String> token = extractToken(authHeader);
        Optional<UserDetails> userDetails = token.flatMap(userDetailsResolver::resolve);
        return userDetails.map(user -> new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private Optional<String> extractToken(String authHeader) {
        if (Objects.nonNull(authHeader) && authHeader.startsWith(BEARER_PREFIX)){
            return Optional.of(authHeader.substring(7));
        } else {
            return Optional.empty();
        }
    }
}
