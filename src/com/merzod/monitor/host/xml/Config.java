package com.merzod.monitor.host.xml;

import com.merzod.monitor.host.Target;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.List;

/**
 * @author opavlenko
 */
@Root
public class Config {
    @Element
    private int interval;
    @Element
    private int tcpTimeout;
    @ElementList
    private List<Target> targets;
    @Element
    private SMTPConfig smtp;
    @Element (required = false)
    private String listener;
    @Element
    private long skipInterval;

    private static Config me;
    public static final String file = "config.xml";

    private Config() {}

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTcpTimeout(int tcpTimeout) {
        this.tcpTimeout = tcpTimeout;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public void setSkipInterval(long skipInterval) {
        this.skipInterval = skipInterval;
    }

    public int getInterval() {
        return interval * 1000;
    }

    public int getIntervalSec() {
        return interval;
    }

    public int getTcpTimeout() {
        return tcpTimeout * 1000;
    }

    public int getTcpTimeoutSec() {
        return tcpTimeout;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public SMTPConfig getSmtp() {
        return smtp;
    }

    public String getListener() {
        return listener;
    }

    public long getSkipInterval() {
        return skipInterval * 1000;
    }

    public long getSkipIntervalSec() {
        return skipInterval;
    }

    public static Config getInstance() {
        if(me == null) {
            throw new RuntimeException("Config hasn't been loaded");
        }
        return me;
    }

    public static void load() throws Exception {
        load(file);
    }
    
    public static void load(String file) throws Exception {
        Serializer serializer = new Persister();
        File cfgFile = new File(file);
        me = serializer.read(Config.class, cfgFile);
    }

    public static void dump() throws Exception {
        dump(file);
    }

    public static void dump(String file) throws Exception {
        Serializer serializer = new Persister();
        File cfgFile = new File(file);
        serializer.write(Config.getInstance(), cfgFile);
    }

    public void addTarget(Target target) {
        targets.add(target);
    }

    public void deleteTarget(Target target) {
        targets.remove(target);
    }

}
