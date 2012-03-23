package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.tray.TrayMonitorListener;
import com.merzod.monitor.host.xml.Config;

/**
 * @author opavlenko
 */
public class HostMonitorListersManager {
    private static HostMonitorListersManager instance;
    MailMonitorListener mail;
    TrayMonitorListener tray;

    private HostMonitorListersManager() {
        mail = new MailMonitorListener();
        tray = new TrayMonitorListener();
    }

    public static synchronized HostMonitorListersManager getInstance() {
        if(instance == null) {
            instance = new HostMonitorListersManager();
        }
        return instance;
    }

    public void init() {
        if(Config.getInstance().getSmtp().isEnabled()) {
            enableMailing();
        }
        enableTraying();
    }
    
    public void enableMailing() {
        Config.getInstance().addHostMonitorListener(mail);
    }

    public void disableMailing() {
        Config.getInstance().removeHostMonitorListener(mail);
    }

    public void enableTraying() {
        Config.getInstance().addHostMonitorListener(tray);
    }

    public void disableTraying() {
        Config.getInstance().removeHostMonitorListener(tray);
    }
}
