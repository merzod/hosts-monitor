package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.Target;

import javax.swing.*;
import java.awt.*;

/**
 * @author opavlenko
 */
public class TargetListRenderer extends JLabel implements ListCellRenderer {
    public TargetListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(((Target) value).getName());
        if(isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(Color.BLACK);
        }
        return this;
    }
}
