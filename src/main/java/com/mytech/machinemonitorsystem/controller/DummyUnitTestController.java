package com.mytech.machinemonitorsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyUnitTestController {
    @GetMapping("/api/v1/dummy/illegal-argument")
    public void triggerIllegalArgumentException(){
        throw new IllegalArgumentException("This is a dummy IllegalArgumentException for testing purposes.");
    }

    @GetMapping("/api/v1/dummy/error")
    public String triggerUnexpectedException() throws Exception {
        throw new Exception("Unexpected error in DummyErrorController");
    }
}
