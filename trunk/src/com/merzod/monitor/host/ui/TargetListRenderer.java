package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.Target;

import javax.swing.*;
import java.awt.*;

/**
 * @author opavlenko
 */
public class TargetListRenderer extends JPanel implements ListCellRenderer {
    private JLabel label = new JLabel();

    public TargetListRenderer() {
        super();
        this.add(label);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setText(((Target) value).getName());
        if(isSelected) {
            label.setForeground(Color.WHITE);
            setBackground(Color.GRAY);
        } else {
            label.setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        }
        return this;
    }
}
