package com.merzod.monitor.host.xml;

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

    private Properties props;

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
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
