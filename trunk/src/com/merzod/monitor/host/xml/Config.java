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

    private static Config me;
    public static final String file = "config.xml";

    private Config() {}

    public int getInterval() {
        return interval;
    }

    public int getTcpTimeout() {
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

    public static Config getInstance() {
        if(me == null) {
            throw new RuntimeException("Config hasn't been loaded");
        }
        return me;
    }
    
    public static void load() throws Exception {
        Serializer serializer = new Persister();
        File cfgFile = new File(file);
        me = serializer.read(Config.class, cfgFile);
    }
}
