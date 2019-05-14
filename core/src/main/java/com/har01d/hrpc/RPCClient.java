package com.har01d.hrpc;

import com.har01d.hrpc.core.Message;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RPCClient {
    private static final Logger logger = Logger.getLogger(RPCClient.class.getName());
    private static final int DEFAULT_PORT = 9807;
    private String server = "localhost";
    private int port = DEFAULT_PORT;

    private Map<Class<?>, Object> stubs = new HashMap<>();

    public RPCClient() {
    }

    public RPCClient(String server) {
        this.server = server;
    }

    public RPCClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public <T> T getService(Class<T> clazz) {
        Object stub = stubs.computeIfAbsent(clazz, k -> createStub(clazz));
        return (T) stub;
    }

    private Object createStub(Class<?> clazz) {
        InvocationHandler handler = (proxy, method, args) -> {
            Message message = new Message();
            message.setService(clazz.getName());
            message.setMethod(method.getName());
            message.setArguments(args);

            Socket socket = new Socket(server, port);
            try (InputStream is = socket.getInputStream();
                 OutputStream os = socket.getOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(os)) {
                oos.writeObject(message);
                oos.flush();

                ObjectInputStream ois = new ObjectInputStream(is);
                return ois.readObject();
            }
        };

        logger.fine("Create stub " + clazz.getName());
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }

}
