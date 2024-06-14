package com.harishkannarao.springboot.graphqlwebmvc.security.resolver;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserDetailsResolver {

    public Optional<UserDetails> resolve(String token) {
        if ("user-token".equals(token)) {
            return Optional.of(
                    User.builder()
                            .username("user-name-1")
                            .password("")
                            .disabled(false)
                            .accountExpired(false)
                            .credentialsExpired(false)
                            .accountLocked(false)
                            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                            .build()
            );
        } else if ("admin-token".equals(token)) {
            return Optional.of(
                    User.builder()
                            .username("admin-name-1")
                            .password("")
                            .disabled(false)
                            .accountExpired(false)
                            .credentialsExpired(false)
                            .accountLocked(false)
                            .authorities(List.of(
                                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                                    new SimpleGrantedAuthority("ROLE_ROOT_ADMIN")
                            ))
                            .build()
            );
        } else {
            return Optional.empty();
        }
    }
}
