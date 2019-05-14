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

}
