package io.simpolor.oauth2.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class OAuth2Controller {

    @GetMapping({"", "/"})
    public String getAuthorizationMessage() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/loginSuccess", "/hello"})
    public String loginSuccess(OAuth2AuthenticationToken authentication) {

        System.out.println("authentication = " + authentication);
        if(Objects.nonNull(authentication)){
            System.out.println("getAttributes = " + authentication.getPrincipal().getAttributes());
        }

        return "complete";
    }


    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "loginFailure";
    }

    @GetMapping("/role/_check")
    public String google() {
        return "user_role_check";
    }
}
