package com.merzod.monitor.host.ping;

import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.xml.Config;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author opavlenko
 */
public class TcpPing implements Ping {
    
    public void ping(Target target) throws IOException {
        InetAddress address = InetAddress.getByName(target.getHost());
        address.isReachable(Config.getInstance().getTcpTimeout());
    }
}
