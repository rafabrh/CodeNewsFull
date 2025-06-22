package org.codenews.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class LandingController {

    @GetMapping("/")
    public String landing() {
        return "subscribe";
    }
}
