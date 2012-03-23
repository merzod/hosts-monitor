package com.merzod.monitor.host;

import com.merzod.monitor.host.monitor.HostMonitor;
import com.merzod.monitor.host.monitor.HostMonitorListersManager;
import com.merzod.monitor.host.monitor.MailMonitorListener;
import com.merzod.monitor.host.monitor.Monitor;
import com.merzod.monitor.host.tray.TrayMonitorListener;
import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import javax.swing.*;
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
            // load config form file
            Config.load();
            // create the monitor
            HostMonitor monitor = new HostMonitor();
            // store monitor in config
            Config.getInstance().setMonitor(monitor);
            // init listeners
            HostMonitorListersManager.getInstance().init();
            // start monitoring
            timer.schedule(getTask(monitor), 0, Config.getInstance().getInterval());
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
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available
        }
        new Starter();
//        try {
//            Config.load();
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        new SettingsFrame();
    }
}
