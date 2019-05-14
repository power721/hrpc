package com.har01d.example;

import com.har01d.hrpc.RPCServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        RPCServer server = new RPCServer();
        server.start();
    }
}
