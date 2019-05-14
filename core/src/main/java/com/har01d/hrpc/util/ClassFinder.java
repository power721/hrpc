package com.har01d.hrpc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {

    public List<Class<?>> scanClasses() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }

        List<Class<?>> classes = new ArrayList<>();

        String classPath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            File file = new File(path);
            if (file.isDirectory()) {
                scanClassFile(file, file, cl, classes);
            } else if (path.toLowerCase().endsWith(".jar")) {
                if (path.contains("/jre/lib/")) { // skip jre
                    continue;
                }
                scanJarFile(file, cl, classes);
            }
        }

        return classes;
    }

    private void scanClassFile(File root, File directory, ClassLoader loader, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanClassFile(root, file, loader, classes);
            } else {
                if (file.getName().toLowerCase().endsWith(".class")) {
                    String className = getClassName(root, file);
                    try {
                        Class<?> clazz = Class.forName(className, false, loader);
                        classes.add(clazz);
                    } catch (ClassNotFoundException | SecurityException e) {
                        // ignore
                    }
                }
            }
        }
    }

    private String getClassName(File root, File file) {
        String path = file.getAbsolutePath();
        path = path.substring(root.getAbsolutePath().length());
        path = path.substring(0, path.length() - ".class".length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path.replace(File.separator, ".");
    }

    private void scanJarFile(File file, ClassLoader loader, List<Class<?>> classes) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().toLowerCase().endsWith(".class")) {
                    String className = getClassName(entry.getName());
                    try {
                        Class<?> clazz = Class.forName(className, false, loader);
                        classes.add(clazz);
                    } catch (ClassNotFoundException | NoClassDefFoundError | SecurityException e) {
                        // ignore
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private String getClassName(String path) {
        path = path.substring(0, path.length() - ".class".length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path.replace(File.separator, ".");
    }

}
