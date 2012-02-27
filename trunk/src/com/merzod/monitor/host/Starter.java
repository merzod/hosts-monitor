package com.merzod.monitor.host;

import com.merzod.monitor.host.monitor.HostMonitor;
import com.merzod.monitor.host.monitor.Monitor;
import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author opavlenko
 */
public class Starter {
    private final static Logger log = Logger.getLogger(Starter.class);
    private Timer timer;

    public Starter() {
        timer = new Timer("Starter");
        run();
    }

    public void run() {
        try {
            Config.load();
            timer.schedule(getTask(new HostMonitor()), 0, Config.getInstance().getInterval());
        } catch (Exception e) {
            log.fatal("Failed to start", e);
        }
    }

    public TimerTask getTask(final Monitor monitor) {
        return new TimerTask() {
            @Override
            public void run() {
                monitor.run();
            }
        };
    }

    public static void main(String[] args) {
        new Starter();
    }
}
