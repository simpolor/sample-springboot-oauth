package io.simpolor.client.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AccessDeniedPageController {

    private final String ERROR_403_PATH = "/error/403";

    @GetMapping(value = "/access-denied-page")
    public ModelAndView handleError(ModelAndView mav, HttpServletRequest request) {

        mav.setViewName(ERROR_403_PATH);
        return mav;
    }
}
