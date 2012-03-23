package com.merzod.monitor.host.tray;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.monitor.IMonitorListener;
import com.merzod.monitor.host.xml.Config;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author opavlenko
 */
public class TrayMonitorListener implements IMonitorListener {
    @Override
    public void monitorCycleFinished(Map<Target, Result> table) {
        boolean anyFailed = false;
        boolean anySuccess = false;
        List<MenuItem> menu = new ArrayList<MenuItem>();
        for(Target target : table.keySet()) {
            Result result = table.get(target);
            MenuItem item = new MenuItem(target.getName());
            if(result.getState() == Result.State.SUCCESS) {
                // success item
                item.setEnabled(true);
                anySuccess = true;
            } else {
                // failed item
                item.setEnabled(false);
                anyFailed = true;
                if(Config.getInstance().isEnableTrayNotifications()) {
                    long now = new Date().getTime();
                    long passed = now - target.getLastFailed();
                    if(passed > Config.getInstance().getSkipInterval()) {
                        TrayUtils.getInstance().displayError(target.toString());
                    }
                }
            }
            menu.add(item);
        }
        TrayUtils.State state;
        if(!anyFailed) {
            // all targets success, or no targets at all
            state = TrayUtils.State.on;
        } else if(anySuccess) {
            // some success, some failed
            state = TrayUtils.State.yel;
        } else {
            // all failed
            state = TrayUtils.State.red;
        }
        TrayUtils.getInstance().setIcon(state);
        TrayUtils.getInstance().addTargets(menu);
    }
}
