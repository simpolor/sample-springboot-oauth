package io.simpolor.resource.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    @GetMapping("/list")
    public String list(){
        return "list";
    }

    @GetMapping("/detail")
    public String detail(){
        return "detail";
    }

    // @PreAuthorize("hasRole('ROLE_USER')")
    @Secured("ROLE_USER")
    @GetMapping("/register")
    public String register(){
        return "register";
    }
}
