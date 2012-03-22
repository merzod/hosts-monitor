package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.ping.SocketPing;
import com.merzod.monitor.host.ping.Ping;
import com.merzod.monitor.host.ping.TcpPing;
import com.merzod.monitor.host.tray.TrayUtils;
import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author opavlenko
 */
public class HostMonitor implements Monitor {

    private static final Logger log = Logger.getLogger(HostMonitor.class);
    Map<Target, Result> table;
    private Map<Target.Protocol, Ping> pings;
    private int runThreads = 0;
    private List<IMonitorListener> listeners;

    public HostMonitor() {
        table = Collections.synchronizedMap(new HashMap<Target, Result>());
        pings = new HashMap<Target.Protocol, Ping>();
        listeners = new ArrayList<IMonitorListener>();
        registerPings();
        TrayUtils.getInstance().setIcon(TrayUtils.State.on);
    }

    private void registerPings() {
        pings.put(Target.Protocol.ICMP_TCP, new TcpPing());
        pings.put(Target.Protocol.SOCKET, new SocketPing());
    }

    public void addListener(IMonitorListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IMonitorListener listener) {
        listeners.remove(listener);
    }

    void notifyListeners() {
        for(IMonitorListener listener : listeners) {
            listener.monitorCycleFinished(table);
        }
    }

    public void run() {
        // reset success flag
        TrayUtils.getInstance().setIcon(TrayUtils.State.run);
        log.info("Start Monitor " + this);
        for (Target target : Config.getInstance().getTargets()) {
            new Thread(new PingThread(target)).start();
        }

        // start print result thread. it will wait until all the PingThreads to finish
        // and will print the monitoring result
        new Thread(new Runnable() {
            public void run() {
                synchronized (HostMonitor.this) {
                    while (runThreads != 0) {
                        try {
                            HostMonitor.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    notifyListeners();
                }
            }
        }).start();
    }

    private synchronized Ping getPing(Target.Protocol protocol) {
        return pings.get(protocol);
    }

    class PingThread implements Runnable {
        private final Logger log = Logger.getLogger(PingThread.class);

        private final Target target;

        PingThread(Target target) {
            this.target = target;
        }

        public void run() {
            synchronized (HostMonitor.this) {
                runThreads++;
                HostMonitor.this.notifyAll();
            }

            log.debug("Start PingThread for " + target);
            Ping ping = getPing(target.getProtocol());
            Result result;
            if (ping != null) {
                try {
                    long start = new Date().getTime();
                    ping.ping(target);
                    long stop = new Date().getTime();
                    double sec = (double) (stop - start) / 1000;
                    result = new Result(target, "within " + sec + " sec");
                } catch (Exception e) {
                    result = new Result(target, e);
                }
            } else {
                result = new Result(target, new Exception("Unsupported protocol: " + target.getProtocol()));
            }
            target.setResult(result);
            table.put(target, result);
            // yell about the result in case of failed, otherwise just debug
            String message = "Stop PingThread for " + target + " result " + result;
            if(result.getState() == Result.State.FAILED) {
                log.error(message);
            } else {
                log.debug(message);
            }

            synchronized (HostMonitor.this) {
                runThreads--;
                HostMonitor.this.notifyAll();
            }
        }
    }
}
