package com.harishkannarao.springboot.graphqlwebmvc.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @QueryMapping(name = "greeting")
    public String handleGreeting(
            @Argument(name = "name")
            String inputName) {
        return "Hello, " + inputName + "!";
    }
}
