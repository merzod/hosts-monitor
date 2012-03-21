package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.xml.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author opavlenko
 */
public class CommonSettingsPanel extends JPanel implements ActionListener {
    private JSpinner interval;
    private JSpinner skipInterval;
    private JSpinner tcpTimeout;
    private JTextField email;
    private JButton save;

    public CommonSettingsPanel() {
        super(new GridBagLayout());
        init();
        setData();
    }

    private void init() {
        Dimension size = new Dimension(150, 20);
        interval = new JSpinner();
        interval.setPreferredSize(size);
        skipInterval = new JSpinner();
        skipInterval.setPreferredSize(size);
        tcpTimeout = new JSpinner();
        tcpTimeout.setPreferredSize(size);
        email = new JTextField();
        email.setPreferredSize(size);
        save = new JButton("Save");
        save.addActionListener(this);
        JPanel buttons = new JPanel();
        buttons.setBorder(new TitledBorder(""));
        buttons.add(save);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(2,2,2,2);
        add(new JLabel("Interval"), c);
        c.gridx = 1;
        c.gridy = 0;
        add(interval, c);
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Skip Interval"), c);
        c.gridx = 1;
        c.gridy = 1;
        add(skipInterval, c);
        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Tcp Timeout"), c);
        c.gridx = 1;
        c.gridy = 2;
        add(tcpTimeout, c);
        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Email"), c);
        c.gridx = 1;
        c.gridy = 3;
        add(email, c);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 5;
        c.weightx = 1;
        add(new JPanel(), c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 0;
        c.weightx = 0;
        c.gridwidth = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buttons, c);
    }
    
    private void setData() {
        interval.setValue(Config.getInstance().getIntervalSec());
        skipInterval.setValue(Config.getInstance().getSkipIntervalSec());
        tcpTimeout.setValue(Config.getInstance().getTcpTimeoutSec());
        email.setText(Config.getInstance().getListener());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Config.getInstance().setInterval(Integer.parseInt(interval.getValue().toString()));
        Config.getInstance().setSkipInterval(Integer.parseInt(skipInterval.getValue().toString()));
        Config.getInstance().setTcpTimeout(Integer.parseInt(tcpTimeout.getValue().toString()));
        Config.getInstance().setListener(email.getText());
        try {
            Config.dump();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
