package com.freshco.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Temp {

    @GetMapping("/")
    public String temp() {
        return "Gulshan";
    }

}
