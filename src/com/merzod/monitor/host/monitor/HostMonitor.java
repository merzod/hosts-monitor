package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.mail.MailSender;
import com.merzod.monitor.host.mail.MailSenderImpl;
import com.merzod.monitor.host.ping.SocketPing;
import com.merzod.monitor.host.ping.Ping;
import com.merzod.monitor.host.ping.TcpPing;
import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author opavlenko
 */
public class HostMonitor implements Monitor {

    private static final Logger log = Logger.getLogger(HostMonitor.class);
    Map<Target, Result> table;
    private Map<Target.Protocol, Ping> pings;
    private int runThreads = 0;
    private MailSender mailSender = new MailSenderImpl();

    public HostMonitor() {
        table = Collections.synchronizedMap(new HashMap<Target, Result>());
        pings = new HashMap<Target.Protocol, Ping>();
        registerPings();
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void registerPings() {
        pings.put(Target.Protocol.TCP, new TcpPing());
        pings.put(Target.Protocol.SOCKET, new SocketPing());
    }

    public void run() {
        log.info("Start Monitor " + this);
        for (Target target : Config.getInstance().getTargets()) {
            new Thread(new PingThread(target)).start();
        }

        // start print result thread. it will wait until all the PingThreads to finish
        // and will print the monitoring result
        new Thread(new Runnable() {
            public void run() {
                synchronized (HostMonitor.this) {
                    while (runThreads != 0) {
                        try {
                            HostMonitor.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    printResult();
                }
            }
        }).start();
    }

    void printResult() {
        // the map of email bodies. the key - email, value - body
        Map<String, StringBuilder> results = new HashMap<String, StringBuilder>() {
            @Override
            public StringBuilder get(Object key) {
                // if the is no value for such key - create and put it
                StringBuilder sb = super.get(key);
                if (sb == null) {
                    sb = new StringBuilder();
                    put((String) key, sb);
                }
                return sb;
            }
        };
        // the map of email subjects
        Map<String, String> subjects = new HashMap<String, String>();
        // the 2 part of root's body        
        StringBuilder bodyFailed = new StringBuilder("Failed:\n");
        StringBuilder bodySuccess = new StringBuilder("Successful:\n");
        // root's subject
        String subject = null;
        for (Target target : table.keySet()) {
            Result result = table.get(target);
            if (result.getState() == Result.State.SUCCESS) {
                bodySuccess.append(target).append("\t").append(table.get(target)).append("\n");
            } else {
                // format error and set it to:
                // - bodyFailed (for mail listener)
                String error = getErrorMessage(target);
                bodyFailed.append(error);
                // - root's subject if there is no any
                if(subject == null) {
                    subject = error;
                }
                // - map of bodies with key target.getListener()
                String key = target.getListener();
                results.get(key).append(error);
                // - map of subjects with key target.getListener() if there is no any
                if(!subjects.containsKey(key)) {
                    subjects.put(key, error);
                }
            }
        }
        // if any failed - send email to listeners
        if (results.size() > 0) {
            // send mail with full report to main listener
            mailSender.send(Config.getInstance().getListener(), bodyFailed + "\n" + bodySuccess, subject);
            for (String to : results.keySet()) {
                // send short emails to all target listeners
                mailSender.send(to, results.get(to).toString(), subjects.get(to));
            }
        }

        log.info("Stop Monitor " + this);
    }
    
    String getErrorMessage(Target target) {
        StringBuilder error = new StringBuilder(target.toString());
        error.append("\t").append(table.get(target)).append("\n");
        return error.toString();
    }

    private synchronized Ping getPing(Target.Protocol protocol) {
        return pings.get(protocol);
    }

    class PingThread implements Runnable {
        private final Logger log = Logger.getLogger(PingThread.class);

        private final Target target;

        PingThread(Target target) {
            this.target = target;
        }

        public void run() {
            synchronized (HostMonitor.this) {
                runThreads++;
                HostMonitor.this.notifyAll();
            }

            log.debug("Start PingThread for " + target);
            Ping ping = getPing(target.getProtocol());
            Result result;
            if (ping != null) {
                try {
                    long start = new Date().getTime();
                    ping.ping(target);
                    long stop = new Date().getTime();
                    double sec = (double) (stop - start) / 1000;
                    result = new Result("within " + sec + " sec");
                } catch (Exception e) {
                    result = new Result(e);
                }
            } else {
                result = new Result(new Exception("Unsupported protocol: " + target.getProtocol()));
            }
            table.put(target, result);
            log.debug("Stop PingThread for " + target + " result " + result);

            synchronized (HostMonitor.this) {
                runThreads--;
                HostMonitor.this.notifyAll();
            }
        }
    }
}
