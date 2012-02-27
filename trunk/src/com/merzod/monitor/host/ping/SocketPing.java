package com.merzod.monitor.host.ping;

import com.merzod.monitor.host.Target;

import java.net.Socket;

/**
 * @author opavlenko
 */
public class SocketPing implements Ping {
    @Override
    public void ping(Target target) throws Exception {
        new Socket(target.getHost(), target.getPort());
    }
}
