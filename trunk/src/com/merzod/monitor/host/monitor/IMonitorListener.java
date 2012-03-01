package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;

import java.util.Map;

/**
 * @author opavlenko
 */
public interface IMonitorListener {
    void monitorCycleFinished(Map<Target, Result> table);
}
