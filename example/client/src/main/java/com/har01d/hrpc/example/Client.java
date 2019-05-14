package com.har01d.hrpc.example;

import com.har01d.hrpc.RPCClient;

public class Client {
    public static void main(String[] args) {
        RPCClient stub = new RPCClient();
        GreetingService greetingService = stub.getService(GreetingService.class);

        String name = "Harold";
        if (args.length > 0) {
            name = args[0];
        }

        System.out.println(greetingService.hello("world"));
        System.out.println(greetingService.hello(name));
    }
}
