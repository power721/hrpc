package com.har01d.hrpc.example.service;

import com.har01d.hrpc.example.GreetingService;

public class GreetingServiceImpl implements GreetingService {
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
