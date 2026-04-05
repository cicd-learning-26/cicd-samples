package com.testing.cicdapplication.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cicd")
@Slf4j
public class TestController {

    @GetMapping("/test")
    public String testEndpoint(){
        log.info("Inside TestController");
        return "Hello from TestController : Version " + "V1";
    }
}
