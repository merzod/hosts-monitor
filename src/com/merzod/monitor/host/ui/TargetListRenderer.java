package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.Result;
import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.tray.TrayUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author opavlenko
 */
public class TargetListRenderer extends JPanel implements ListCellRenderer {
    private JLabel label;
    private ImageIcon ok, failed;

    public TargetListRenderer() {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        label = new JLabel();
        add(label, c);

        ok = new ImageIcon(TrayUtils.State.on.getFile());
        failed = new ImageIcon(TrayUtils.State.red.getFile());
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Target t = (Target) value;
        label.setText(t.getName());
        if(t.getResult() == null || t.getResult().getState() == Result.State.SUCCESS) {
            label.setIcon(ok);
        } else {
            label.setIcon(failed);
        }

        if(isSelected) {
            label.setForeground(Color.WHITE);
            setBackground(Color.DARK_GRAY);
        } else {
            label.setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        }

        if(!t.isEnabled()) {
            label.setForeground(Color.GRAY);
        }
        return this;
    }
}
