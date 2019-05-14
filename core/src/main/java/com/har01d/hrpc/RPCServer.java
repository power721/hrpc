package com.har01d.hrpc;

import com.har01d.hrpc.util.ClassFinder;

import java.io.IOException;
import java.util.*;

public class RPCServer {
    private static final int DEFAULT_PORT = 9807;
    private int port = DEFAULT_PORT;
    private boolean isRunning = false;
    private Map<String, Class<?>> registration = new HashMap<String, Class<?>>();

    public RPCServer() {
    }

    public RPCServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        scanService();
    }

    private void scanService() throws IOException {
        ClassFinder finder = new ClassFinder();
        List<Class<?>> classes = finder.scanClasses();
        Set<Class<?>> services = new HashSet<Class<?>>();

        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
            RPC rpc = clazz.getAnnotation(RPC.class);
            if (rpc != null) {
                services.add(clazz);
            }
        }

        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                continue;
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> intf : interfaces) {
                if (services.contains(intf)) {
                    System.out.println("add service implementation: " + clazz.getName() + " -> " + intf.getName());
                    registration.put(intf.getName(), clazz);
                }
            }
        }
    }

    public void run() {
        while (isRunning) {

        }
    }

    public void stop() {
        isRunning = false;
    }
}
