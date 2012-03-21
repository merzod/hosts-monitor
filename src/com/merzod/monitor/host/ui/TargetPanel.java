package com.merzod.monitor.host.ui;

import com.merzod.monitor.host.Target;
import com.merzod.monitor.host.xml.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author opavlenko
 */
public class TargetPanel extends ListPanel implements ActionListener {
    private JComboBox protocol;
    private JTextField host;
    private JTextField port;
    private JTextField name;
    private JTextField email;

    private JButton save;
    private JButton add;
    private JButton delete;
    
    private Target target;
    
    public TargetPanel() {
        super(new TargetsListModel());
    }

    @Override
    public JComponent getRightPane() {
        Dimension size = new Dimension(150, 20);
        // name
        name = new JTextField();
        name.setPreferredSize(size);
        // protocol
        protocol = new JComboBox(Target.Protocol.values());
        protocol.setPreferredSize(size);
        protocol.setSelectedItem(Target.Protocol.SOCKET);
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
        // host
        host = new JTextField();
        host.setPreferredSize(size);
        // port
        port = new JTextField();
        port.setPreferredSize(size);
        // email
        email = new JTextField();
        email.setPreferredSize(size);

        // buttons
        add = new JButton("Add");
        save = new JButton("Save");
        delete = new JButton("Delete");
        add.addActionListener(this);
        save.addActionListener(this);
        delete.addActionListener(this);
        save.setEnabled(false);
        delete.setEnabled(false);
        JPanel buttons = new JPanel();
        buttons.setBorder(new TitledBorder(""));
        buttons.add(add);
        buttons.add(save);
        buttons.add(delete);

        // layout
        JPanel central = new JPanel(new GridBagLayout());
        central.setBorder(new TitledBorder(""));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(2,2,2,2);
        central.add(new JLabel("Name"), c);
        c.gridx = 1;
        c.gridy = 0;
        central.add(name, c);
        c.gridx = 0;
        c.gridy = 1;
        central.add(new JLabel("Protocol"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        central.add(protocol, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        central.add(new JLabel("Host"), c);
        c.gridx = 1;
        c.gridy = 2;
        central.add(host, c);
        c.gridx = 0;
        c.gridy = 3;
        central.add(new JLabel("Port"), c);
        c.gridx = 1;
        c.gridy = 3;
        central.add(port, c);
        c.gridx = 0;
        c.gridy = 4;
        central.add(new JLabel("Email"), c);
        c.gridx = 1;
        c.gridy = 4;
        central.add(email, c);

        JPanel main = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        main.add(central, c);
        c.gridx = 0;
        c.gridy = 1;
        main.add(buttons, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.gridheight = 3;
        main.add(new JPanel(), c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.gridheight = 0;
        c.gridwidth = 1;
        c.weighty = 1;
        main.add(new JPanel(), c);

        return main;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        target = (Target) list.getSelectedValue();
        if(target == null) {
            // lost selection
            reset();
        } else {
            name.setText(target.getName());
            host.setText(target.getHost());
            port.setText(String.valueOf(target.getPort()));
            protocol.setSelectedItem(target.getProtocol());
            email.setText(target.getListener());
            save.setEnabled(true);
            delete.setEnabled(true);
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        if(listModel.getSize() == 0) {
            reset();
        }
    }

    private void reset() {
        name.setText("");
        host.setText("");
        port.setText("");
        protocol.setSelectedItem(0);
        email.setText("");
        save.setEnabled(false);
        delete.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == add) {
            Target t = new Target();
            updateTarget(t);
            Config.getInstance().addTarget(t);
        } else if (e.getSource() == save) {
            updateTarget(target);
        } else if (e.getSource() == delete) {
            int res = JOptionPane.showConfirmDialog(this, "Going to delete target: " + target, "Delete", JOptionPane.OK_CANCEL_OPTION);
            if(res == JOptionPane.OK_OPTION) {
                Config.getInstance().deleteTarget(target);
            }
        }
        // this will refresh the list
        ((TargetsListModel)listModel).update();
        try {
            Config.dump();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean check() {
        if(host.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Host is empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(protocol.getSelectedItem() == Target.Protocol.SOCKET) {
            try {
                Integer.parseInt(port.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid port: "+port.getText(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }    

    private void updateTarget(Target t) {
        if(check()) {
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
}
