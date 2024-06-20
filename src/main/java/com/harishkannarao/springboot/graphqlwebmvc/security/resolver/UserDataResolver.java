package com.harishkannarao.springboot.graphqlwebmvc.security.resolver;

import com.harishkannarao.springboot.graphqlwebmvc.security.dto.UserData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDataResolver {

    public Optional<UserData> resolve(String username) {
        if ("user-name-1".equals(username)) {
            return Optional.of(new UserData("userFirstName", "userLastName"));
        } else {
            return Optional.empty();
        }
    }
}
