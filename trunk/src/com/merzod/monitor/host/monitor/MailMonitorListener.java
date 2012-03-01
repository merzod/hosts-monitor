package com.merzod.monitor.host.monitor;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.mail.MailSender;
import com.merzod.monitor.host.mail.MailSenderImpl;
import com.merzod.monitor.host.tray.TrayUtils;
import com.merzod.monitor.host.xml.Config;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author opavlenko
 */
public class MailMonitorListener implements IMonitorListener {
    private static final Logger log = Logger.getLogger(MailMonitorListener.class);
    private MailSender mailSender = new MailSenderImpl();
    private final DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    // for tests only to reset mail sender
    void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void monitorCycleFinished(Map<Target, Result> table) {
        Map<String, List<Result>> result = reformatResult(table);

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
            mailSender.send(email, first == null ? null : first.getTarget().toString(), body.toString());
        }

        log.info("Stop Monitor " + this);
    }

    /**
     * The result of monitoring is in Map <Target, Result>, for sending its needed to reformat the result
     * to Map <Email, List<Result>>
     * @return
     */
    private Map<String, List<Result>> reformatResult(Map<Target, Result> table) {
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
        long skip = Config.getInstance().getSkipInterval();
        long now = new Date().getTime();
        for(Target target : table.keySet()) {
            Result result = table.get(target);
            if(result.getState() != Result.State.SUCCESS) {
                // if target failed long enough ago - send email
                long passed = now - target.getLastFailed();
                boolean skp = passed > skip;
                String lastFailed;
                if(target.getLastFailed() == 0) {
                    lastFailed = "Never";
                } else {
                    lastFailed = format.format(new Date(target.getLastFailed()));
                }
                // email state, in case of skipping - on info level, otherwise on debug
                String message = target + " last time failed at: " + lastFailed + " passed: " + passed + " ms skip: " + !skp;
                if(!skp) {
                    log.info(message);
                } else {
                    log.debug(message);
                }
                if(skp) {
                    TrayUtils.getInstance().displayError(target.toString());
                    target.setLastFailed(now);
                    String email = target.getListener();
                    // add result to target's listener and to root listener
                    results.get(email).add(result);
                    results.get(rootEmail).add(result);
                }
            }
        }
        return results;
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
}
