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
        Config.load("config-test.xml");
        monitor = new HostMonitor();
        monitor.setMailSender(this);
        mails = new HashMap<String, String>();
    }

    // success only
    public void testPrintResultSuccess() throws Exception {
        monitor.table = new HashMap<Target, Result>();
        Target target = new Target(Target.Protocol.TCP, "host1", null);
        monitor.table.put(target, new Result(target, "success"));
        monitor.printResult();
        assertEquals("No mails should be send", 0, mails.size());
    }

    // failed report to root
    public void testPrintResultFailedToRoot() throws Exception {
        monitor.table = new HashMap<Target, Result>();
        Target target = new Target(Target.Protocol.TCP, "host1", null);
        monitor.table.put(target, new Result(target, new Exception("failed")));
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
        Result r1 = new Result(t1, new Exception("failed"));
        Result r2 = new Result(t2, new Exception("failed"));
        monitor.table.put(t1, r1);
        monitor.table.put(t2, r2);
        // do the job
        monitor.printResult();
        // check results
        assertEquals("Email should be send twice", 2, mails.size());
        String adminMail = mails.get(Config.getInstance().getListener());
        assertTrue("Root should receive both messages", adminMail.contains(monitor.getMessage(r1)));
        assertTrue("Root should receive both messages", adminMail.contains(monitor.getMessage(r2)));
        String testMail = mails.get(email);
        assertEquals(email + " should receive ", monitor.getMessage(r2), testMail);
    }

    public void testPrintResultDelay() throws Exception {
        // fill test results
        monitor.table = new HashMap<Target, Result>();
        Target target = new Target(Target.Protocol.TCP, "host1", null);
        monitor.table.put(target, new Result(target, new Exception("failed")));
        // process once and check that mail sent to root
        monitor.printResult();
        assertEquals("Email should be send to root only", 1, mails.size());
        assertEquals("Email should be send to root only", Config.getInstance().getListener(), mails.keySet().iterator().next());
        // process twice and check that mail wasn't sent
        mails.clear();
        monitor.printResult();
        assertEquals("Email shouldn't be send twice", 0, mails.size());
        // process one more time after skip interval passed (1 sec)
        Thread.sleep(1000);
        mails.clear();
        monitor.printResult();
        assertEquals("Email should be send to root only", 1, mails.size());
        assertEquals("Email should be send to root only", Config.getInstance().getListener(), mails.keySet().iterator().next());
    }

    @Override
    public void send(String to, String body, String subject) {
        if(to != null) {
            mails.put(to, body);
        }
    }
}
