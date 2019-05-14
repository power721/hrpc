package com.har01d.hrpc.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Message implements Serializable {
    private int version = 1;
    private String service;
    private String method;
    private Object[] arguments;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return version == message.version &&
                service.equals(message.service) &&
                method.equals(message.method) &&
                Arrays.equals(arguments, message.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, service, method);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }
}
