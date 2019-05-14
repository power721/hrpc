package com.har01d.hrpc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        List<Class<?>> classes = scanClasses();
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
                    registration.put(intf.getName(), clazz);
                }
            }
        }
    }

    private List<Class<?>> scanClasses() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        Enumeration<URL> enumeration = cl.getResources("");
        while (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            File root = new File(url.getPath());
            scanClasses(root, root, classes);
        }

        return classes;
    }

    private void scanClasses(File root, File directory, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanClasses(root, file, classes);
                } else {
                    if (file.getName().endsWith(".class")) {
                        String path = file.getAbsolutePath().replace(".class", "");
                        path = path.substring(root.getAbsolutePath().length());
                        if (path.startsWith("/")) {
                            path = path.substring(1);
                        }

                        String className = path.replace(File.separator, ".");
                        try {
                            Class<?> clazz = Class.forName(className);
                            classes.add(clazz);
                        } catch (ClassNotFoundException e) {
                            // ignore
                        }
                    } else if (file.getName().endsWith(".jar")) {
                        // TODO:
                        System.err.println(file);
                    }
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
