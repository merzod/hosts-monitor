package com.merzod.monitor.host.mail;

import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author opavlenko
 */
public class MailSenderImpl implements MailSender {
    private static final Logger log = Logger.getLogger(MailSenderImpl.class);
    @Override
    public void send(String to, String subject, String body) {
        if(to != null) {
            log.debug("Sending email to " + to);
            Session session = Session.getInstance(Config.getInstance().getSmtp().getProperties());
            MimeMessage email = new MimeMessage(session);
            try {
                email.addRecipients(Message.RecipientType.TO, to);
                String subj = Config.getInstance().getSmtp().getSubject(); 
                if(subject != null && !subj.isEmpty()) {
                    subj += " "+subject;
                }
                log.info("Sending email to " + to+": "+subj);
                email.setSubject(subj);
                email.setText(body);
                email.setFrom(new InternetAddress(Config.getInstance().getSmtp().getFrom()));
                Transport.send(email);
            } catch (MessagingException e) {
                log.error("Failed to send email to " + to, e);
            }
        }
    }
}
