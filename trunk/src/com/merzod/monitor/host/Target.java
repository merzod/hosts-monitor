package com.merzod.monitor.host;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author opavlenko
 */
@Root
public class Target {
    @Attribute (required = false)
    private Protocol protocol = Protocol.ICMP_TCP;
    @Attribute
    private String host;
    @Attribute (required = false)
    private String listener;
    @Attribute (required = false)
    private int port = 0;
    private long lastFailed;
    @Attribute (required = false)
    private String name;
    private Result result;

    public Target() {
    }

    public Target(Protocol protocol, String host, String listener) {
        this.protocol = protocol;
        this.host = host;
        this.listener = listener;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getListener() {
        return listener;
    }

    public long getLastFailed() {
        return lastFailed;
    }

    public void setLastFailed(long lastFailed) {
        this.lastFailed = lastFailed;
    }

    public String getName() {
        if(name != null && !name.isEmpty())
            return name;
        return getHost();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(protocol.toString());
        string.append(": ").append(name);
        string.append(": ").append(host);
        if(protocol == Protocol.SOCKET) {
            string.append(":").append(port);
        }
        return string.toString();
    }

    public static enum Protocol {
        ICMP_TCP,
        SOCKET,
    }

}
