package com.har01d.hrpc;

import com.har01d.hrpc.core.Message;
import com.har01d.hrpc.core.RPC;
import com.har01d.hrpc.exception.ServerException;
import com.har01d.hrpc.util.ClassFinder;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class RPCServer {
    private static final int DEFAULT_PORT = 9807;
    private int port = DEFAULT_PORT;
    private boolean isRunning = false;
    private Map<String, Class<?>> registration = new HashMap<>();
    private Map<String, Object> services = new HashMap<>();

    public RPCServer() {
    }

    public RPCServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        scanService();
        run();
    }

    private void scanService() throws IOException {
        ClassFinder finder = new ClassFinder();
        List<Class<?>> classes = finder.scanClasses();
        Set<Class<?>> services = new HashSet<>();

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

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (isRunning) {
            Socket socket = serverSocket.accept();
            handleSocket(socket);
        }
    }

    private void handleSocket(Socket socket) {
        try (InputStream is = socket.getInputStream();
             ObjectInputStream ois = new ObjectInputStream(is);
             OutputStream os = socket.getOutputStream()) {
            Message message = (Message) ois.readObject();
            handleRequest(message, os);
        } catch (ServerException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            // TODO:
        } catch (ClassNotFoundException e) {
            // TODO:
        }
    }

    private void handleRequest(Message message, OutputStream os) throws IOException {
        Class<?> clazz = registration.get(message.getService());
        if (clazz == null) {
            throw new ServerException("Invalid service " + message.getService());
        }

        Object service = services.get(message.getService());
        if (service == null) {
            try {
                service = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ServerException("Cannot create service " + message.getService());
            }
            services.put(message.getService(), service);
        }

        Method method;
        try {
            Object[] arguments = message.getArguments();
            Class<?>[] parameterTypes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; ++i) {
                parameterTypes[i] = arguments[i].getClass();
            }
            method = clazz.getMethod(message.getMethod(), parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new ServerException("Invalid method " + message.getService() + "." + message.getMethod());
        }

        Object result;
        try {
            result = method.invoke(service, message.getArguments());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServerException("Cannot invoke method " + message.getService() + "." + message.getMethod());
        }

        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(result);
    }

    public void stop() {
        isRunning = false;
    }
}
