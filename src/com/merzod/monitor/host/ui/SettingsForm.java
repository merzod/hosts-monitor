package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.xml.Config;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author merzod
 */
public class SettingsForm implements ActionListener {
    private JTabbedPane tab;
    private JPanel panel1;
    private JList list;
    private JTextField name;
    private JComboBox protocol;
    private JTextField host;
    private JTextField port;
    private JTextField email;
    private JButton delete;
    private JButton save;
    private JButton add;
    private JButton commonSave;
    private JSpinner interval;
    private JSpinner skipInterval;
    private JSpinner tcpTimeout;
    private JTextField commonEmail;
    private JButton smtpSave;
    private JTextField smtpHost;
    private JTextField user;
    private JPasswordField pass;
    private JTextField subject;
    private JTextField from;
    private JCheckBox enable;
    private JCheckBox targetEnable;
    private JCheckBox tray;

    private Target target;

    public SettingsForm() {
        super();
        init();
    }

    private void init() {
        initTargets();
        initCommon();
        initSmtp();
    }

    private void initSmtp() {
        smtpHost.setText(Config.getInstance().getSmtp().getHost());
        user.setText(Config.getInstance().getSmtp().getUser());
        pass.setText(Config.getInstance().getSmtp().getPassword());
        subject.setText(Config.getInstance().getSmtp().getSubject());
        from.setText(Config.getInstance().getSmtp().getFrom());
        enable.setSelected(Config.getInstance().getSmtp().isEnabled());
        smtpSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config.getInstance().getSmtp().setHost(smtpHost.getText());
                Config.getInstance().getSmtp().setUser(user.getText());
                Config.getInstance().getSmtp().setPassword(new String(pass.getPassword()));
                Config.getInstance().getSmtp().setSubject(subject.getText());
                Config.getInstance().getSmtp().setFrom(from.getText());
                Config.getInstance().getSmtp().setEnabled(enable.isSelected());
                dumpConfig();
            }
        });
    }

    private void initCommon() {
        interval.setValue(Config.getInstance().getIntervalSec());
        skipInterval.setValue(Config.getInstance().getSkipIntervalSec());
        tcpTimeout.setValue(Config.getInstance().getTcpTimeoutSec());
        commonEmail.setText(Config.getInstance().getListener());
        tray.setSelected(Config.getInstance().isEnableTrayNotifications());
        commonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config.getInstance().setInterval(Integer.parseInt(interval.getValue().toString()));
                Config.getInstance().setSkipInterval(Integer.parseInt(skipInterval.getValue().toString()));
                Config.getInstance().setTcpTimeout(Integer.parseInt(tcpTimeout.getValue().toString()));
                Config.getInstance().setListener(commonEmail.getText());
                Config.getInstance().setEnableTrayNotifications(tray.isSelected());
                dumpConfig();
            }
        });
    }

    private void initTargets() {
        targetEnable.setSelected(true);
        for(Target.Protocol prot : Target.Protocol.values()) {
            protocol.addItem(prot);
        }
        protocol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (protocol.getSelectedItem() == Target.Protocol.ICMP_TCP) {
                    port.setEnabled(false);
                } else {
                    port.setEnabled(true);
                }
            }
        });
        save.setEnabled(false);
        delete.setEnabled(false);

        TargetsListModel model = new TargetsListModel();
        list.setModel(model);
        model.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                if(list.getModel().getSize() == 0) {
                    reset();
                }
            }
        });
        list.setCellRenderer(new TargetListRenderer());
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                target = (Target) list.getSelectedValue();
                if(target == null) {
                    // lost selection
                    reset();
                } else {
                    targetEnable.setSelected(target.isEnabled());
                    name.setText(target.getName());
                    host.setText(target.getHost());
                    port.setText(String.valueOf(target.getPort()));
                    protocol.setSelectedItem(target.getProtocol());
                    email.setText(target.getListener());
                    save.setEnabled(true);
                    delete.setEnabled(true);
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Target t = new Target();
                updateTarget(t);
                Config.getInstance().addTarget(t);
                refresh();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTarget(target);
                refresh();
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Going to delete target: " + target, "Delete", JOptionPane.OK_CANCEL_OPTION);
                if(res == JOptionPane.OK_OPTION) {
                    Config.getInstance().deleteTarget(target);
                    refresh();
                }
            }
        });
    }

    private void refresh() {
        ((TargetsListModel)list.getModel()).update();
        dumpConfig();
    }

    private void dumpConfig() {
        try {
            Config.dump();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTarget(Target t) {
        if(check()) {
            t.setEnabled(targetEnable.isSelected());
            t.setName(name.getText());
            t.setHost(host.getText());
            if(port.isEnabled()) {
                t.setPort(Integer.parseInt(port.getText()));
            }
            t.setProtocol((Target.Protocol) protocol.getSelectedItem());
            if(email.getText().isEmpty()) {
                t.setListener(null);
            } else {
                t.setListener(email.getText());
            }
        }
    }

    private boolean check() {
        if(host.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Host is empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(protocol.getSelectedItem() == Target.Protocol.SOCKET) {
            try {
                Integer.parseInt(port.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid port: "+port.getText(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }


    private void reset() {
        name.setText("");
        host.setText("");
        port.setText("");
        protocol.setSelectedItem(0);
        email.setText("");
        save.setEnabled(false);
        delete.setEnabled(false);
        enable.setSelected(true);
    }

    public JPanel getContent() {
        return panel1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == save) {

        }
    }
}
