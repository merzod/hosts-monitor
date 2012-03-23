package com.merzod.monitor.host.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Properties;

/**
 * @author opavlenko
 */
@Root
public class SMTPConfig {
    @Element
    private String host;
    @Element (required = false)
    private String user = "";
    @Element (required = false)
    private String password = "";
    @Element
    private String subject;
    @Element
    private String from;
    @Attribute (required = false)
    private boolean enabled = true;

    private Properties props;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            Config.getInstance().enableMailing();
        } else {
            Config.getInstance().disableMailing();
        }
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Properties getProperties() {
        if(props == null) {
            props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.host", host);
            props.put("mail.user", user);
            props.put("mail.password", password);
        }
        return props;
    }
}
