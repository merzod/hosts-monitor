package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.mail.MailSender;
import com.merzod.monitor.host.xml.Config;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author opavlenko
 */
public class TestHostMonitor extends TestCase implements MailSender{

    private HostMonitor monitor;
    private Map<String, String> mails;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Config.load();
        monitor = new HostMonitor();
        monitor.setMailSender(this);
        mails = new HashMap<String, String>();
    }

    // success only
    public void testPrintResultSuccess() throws Exception {
        monitor.table = new HashMap<Target, Result>();
        monitor.table.put(new Target(Target.Protocol.TCP, "host1", null), new Result("success"));
        monitor.printResult();
        assertEquals("No mails should be send", 0, mails.size());
    }

    // failed report to root
    public void testPrintResultFailedToRoot() throws Exception {
        monitor.table = new HashMap<Target, Result>();
        monitor.table.put(new Target(Target.Protocol.TCP, "host1", null), new Result(new Exception("failed")));
        monitor.printResult();
        assertEquals("Email should be send to root only", 1, mails.size());
        assertEquals("Email should be send to root only", Config.getInstance().getListener(), mails.keySet().iterator().next());
    }

    // failed report to both root and target listener
    public void testPrintResultFailed() throws Exception {
        final String email = "test@com";
        // fill test results
        monitor.table = new HashMap<Target, Result>();
        Target t1 = new Target(Target.Protocol.TCP, "host1", null);
        Target t2 = new Target(Target.Protocol.TCP, "host2", email);
        monitor.table.put(t1, new Result(new Exception("failed")));
        monitor.table.put(t2, new Result(new Exception("failed")));
        // do the job
        monitor.printResult();
        // check results
        assertEquals("Email should be send twice", 2, mails.size());
        String adminMail = mails.get(Config.getInstance().getListener());
        assertTrue("Root should receive both messages", adminMail.contains(monitor.getErrorMessage(t1)));
        assertTrue("Root should receive both messages", adminMail.contains(monitor.getErrorMessage(t2)));
        String testMail = mails.get(email);
        assertEquals(email + " should receive ", monitor.getErrorMessage(t2), testMail);
    }

    @Override
    public void send(String to, String body, String subject) {
        if(to != null) {
            mails.put(to, body);
        }
    }
}