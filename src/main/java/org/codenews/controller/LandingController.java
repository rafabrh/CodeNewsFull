package org.codenews.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class LandingController {

    @GetMapping("/")
    public String landing() {
        return "subscribe";
    }
}
