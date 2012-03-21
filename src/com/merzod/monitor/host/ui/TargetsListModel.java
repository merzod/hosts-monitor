package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.xml.Config;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author opavlenko
 */
public class TargetsListModel implements ListModel {
    private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
    @Override
    public int getSize() {
        return Config.getInstance().getTargets().size();
    }

    @Override
    public Object getElementAt(int index) {
        return Config.getInstance().getTargets().get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void update() {
        for(ListDataListener listener : listeners) {
            listener.contentsChanged(new ListDataEvent(this, 1, 1, 1));
        }
    }
}
