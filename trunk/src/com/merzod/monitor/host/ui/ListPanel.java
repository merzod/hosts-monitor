package com.merzod.monitor.host.ui;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * @author opavlenko
 */
public abstract class ListPanel extends JSplitPane implements ListSelectionListener, ListDataListener {
    protected JList list;
    protected final ListModel listModel;

    public ListPanel(ListModel listModel) {
        this.listModel = listModel;
        init();
    }

    private void init() {
        list = new JList();
        list.setModel(listModel);
        listModel.addListDataListener(this);
        list.addListSelectionListener(this);
        list.setCellRenderer(new TargetListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLeftComponent(new JScrollPane(list));
        setRightComponent(getRightPane());
        setDividerLocation(100);

    }

    public abstract JComponent getRightPane();

    @Override
    public void intervalAdded(ListDataEvent e) {
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
    }
}
