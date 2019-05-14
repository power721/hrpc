package com.har01d.hrpc;

public class RPCServer {
    private static final int DEFAULT_PORT = 9807;
    private int port = DEFAULT_PORT;
    private boolean isRunning = false;

    public RPCServer() {
    }

    public RPCServer(int port) {
        this.port = port;
    }

    public void start() {

    }

    private void scanService() {

    }

    private void registerService() {

    }

    public void run() {
        while (isRunning) {

        }
    }

    public void stop() {
        isRunning = false;
    }
}
