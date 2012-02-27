package com.merzod.monitor.host.ping;

import com.merzod.monitor.host.Target;

/**
 * @author opavlenko
 */
public interface Ping {
    void ping(Target target) throws Exception;
}
