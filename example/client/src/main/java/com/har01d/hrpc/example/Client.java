package com.har01d.hrpc.example;

import com.har01d.hrpc.RPCClient;

public class Client {
    public static void main(String[] args) {
        RPCClient stub = new RPCClient();
        GreetingService greetingService = stub.getService(GreetingService.class);

        System.out.println(greetingService.hello("Harold"));
        System.out.println(greetingService.hello("world"));
    }
}
