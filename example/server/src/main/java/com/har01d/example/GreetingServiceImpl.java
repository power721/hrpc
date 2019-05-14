package com.har01d.example;

public class GreetingServiceImpl implements GreetingService {
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
