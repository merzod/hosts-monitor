package com.merzod.monitor.host.mail;

/**
 * @author opavlenko
 */
public interface MailSender {
    public void send(String to, String body, String subject);
}
