package com.security.Security.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/public/welcome")
    public String welcome() {
        return "Welcome, Guest!";
    }

    @GetMapping("/secure/home")
    public String home() {
        return "Welcome to Secure Page!";
    }
}
