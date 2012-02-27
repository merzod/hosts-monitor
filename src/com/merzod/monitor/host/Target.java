package com.merzod.monitor.host;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author opavlenko
 */
@Root
public class Target {
    @Attribute (required = false)
    private Protocol protocol = Protocol.TCP;
    @Attribute
    private String host;
    @Attribute (required = false)
    private String listener;
    @Attribute (required = false)
    private int port = 0;

    public Target() {
    }

    public Target(Protocol protocol, String host, String listener) {
        this.protocol = protocol;
        this.host = host;
        this.listener = listener;
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

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(protocol.toString());
        string.append(": ").append(host);
        if(protocol == Protocol.SOCKET) {
            string.append(":").append(port);
        }
        return string.toString();
    }

    public static enum Protocol {
        TCP,
        SOCKET,
    }

}
