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

    /**
     * The result of monitoring is in Map <Target, Result>, for sending its needed to reformat the result
     * to Map <Email, List<Result>>
     * @return
     */
    private Map<String, List<Result>> reformatResult() {
        // key - email, value - list of results
        Map<String, List<Result>> results = new HashMap<String, List<Result>>() {
            @Override
            public List<Result> get(Object key) {
                List<Result> list = super.get(key);
                if(list == null) {
                    list = new ArrayList<Result>();
                    put((String) key, list);
                }
                return list;
            }
        };
        // all failed message will be send to root and target listener if there is any
        // get root's email
        String rootEmail = Config.getInstance().getListener();
        for(Target target : table.keySet()) {
            Result result = table.get(target);
            if(result.getState() != Result.State.SUCCESS) {
                String email = target.getListener();
                // add result to target's listener and to root listener
                results.get(email).add(result);
                results.get(rootEmail).add(result);
            }
        }
        return results;
    }

    /**
     * Will send the result by mail to corresponding listeners
     */
    void printResult() {
        log.info("Start Monitor " + this);
        Map<String, List<Result>> result = reformatResult();
        // if any failed - send email to listeners
        for(String email : result.keySet()) {
            // construct body from the List of Results
            StringBuilder body = new StringBuilder();
            List<Result> list = result.get(email);
            // keep first result for subject
            Result first = null;
            for(Result res : list) {
                if(first == null) first = res;
                body.append(getMessage(res));
            }
            mailSender.send(email, body.toString(), first == null ? null : first.getTarget().toString());
        }

        log.info("Stop Monitor " + this);
    }

    /**
     * Construct the message from result
     * @param result to process
     * @return the String message
     */
    String getMessage(Result result) {
        StringBuilder error = new StringBuilder();
        error.append(result.getTarget()).append("\t").append(result).append("\n");
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
                    result = new Result(target, "within " + sec + " sec");
                } catch (Exception e) {
                    result = new Result(target, e);
                }
            } else {
                result = new Result(target, new Exception("Unsupported protocol: " + target.getProtocol()));
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
