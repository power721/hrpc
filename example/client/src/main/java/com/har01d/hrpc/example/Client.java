package com.har01d.hrpc.example;

import com.har01d.hrpc.RPCClient;

public class Client {
    public static void main(String[] args) {
        RPCClient stub = new RPCClient("localhost");
        GreetingService greetingService = stub.getService(GreetingService.class);
        String result = greetingService.hello("Harold");
        System.out.println(result);
    }
}
