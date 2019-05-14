package com.har01d.hrpc.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassFinder {

    public List<Class<?>> scanClasses() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }

        List<Class<?>> classes = new ArrayList<>();
        Enumeration<URL> enumeration = cl.getResources("");
        while (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            File root = new File(url.getPath());
            scanClasses(root, root, cl, classes);
        }

        return classes;
    }

    private void scanClasses(File root, File directory, ClassLoader loader, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanClasses(root, file, loader, classes);
                } else {
                    if (file.getName().endsWith(".class")) {
                        String path = file.getAbsolutePath().replace(".class", "");
                        path = path.substring(root.getAbsolutePath().length());
                        if (path.startsWith("/")) {
                            path = path.substring(1);
                        }

                        String className = path.replace(File.separator, ".");
                        try {
                            Class<?> clazz = Class.forName(className, false, loader);
                            classes.add(clazz);
                        } catch (ClassNotFoundException | SecurityException e) {
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

}
