package com.har01d.example;

import com.har01d.hrpc.core.RPC;

@RPC
public interface GreetingService {
    public String hello(String name);
}
