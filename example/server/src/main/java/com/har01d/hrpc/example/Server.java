package com.har01d.hrpc.example;

import com.har01d.hrpc.RPCServer;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        RPCServer server = new RPCServer();
        server.start();
    }
}
