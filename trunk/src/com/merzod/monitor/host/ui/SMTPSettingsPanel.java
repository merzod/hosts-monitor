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
public class SMTPSettingsPanel extends JPanel implements ActionListener {
    private JTextField host;
    private JTextField user;
    private JTextField pass;
    private JTextField subject;
    private JTextField from;
    private JButton save;

    public SMTPSettingsPanel() {
        super(new GridBagLayout());
        init();
        setData();
    }

    private void init() {
        Dimension size = new Dimension(150, 20);
        host = new JTextField();
        host.setPreferredSize(size);
        user = new JTextField();
        user.setPreferredSize(size);
        pass = new JPasswordField();
        pass.setPreferredSize(size);
        subject = new JTextField();
        subject.setPreferredSize(size);
        from = new JTextField();
        from.setPreferredSize(size);
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
        add(new JLabel("SMTP Host"), c);
        c.gridx = 1;
        c.gridy = 0;
        add(host, c);
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Username"), c);
        c.gridx = 1;
        c.gridy = 1;
        add(user, c);
        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Password"), c);
        c.gridx = 1;
        c.gridy = 2;
        add(pass, c);
        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Subject"), c);
        c.gridx = 1;
        c.gridy = 3;
        add(subject, c);
        c.gridx = 0;
        c.gridy = 4;
        add(new JLabel("From"), c);
        c.gridx = 1;
        c.gridy = 4;
        add(from, c);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 6;
        c.weightx = 1;
        add(new JPanel(), c);
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 0;
        c.weightx = 0;
        c.gridwidth = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buttons, c);
    }

    private void setData() {
        host.setText(Config.getInstance().getSmtp().getHost());
        user.setText(Config.getInstance().getSmtp().getUser());
        pass.setText(Config.getInstance().getSmtp().getPassword());
        subject.setText(Config.getInstance().getSmtp().getSubject());
        from.setText(Config.getInstance().getSmtp().getFrom());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Config.getInstance().getSmtp().setHost(host.getText());
        Config.getInstance().getSmtp().setUser(user.getText());
        Config.getInstance().getSmtp().setPassword(pass.getText());
        Config.getInstance().getSmtp().setSubject(subject.getText());
        Config.getInstance().getSmtp().setFrom(from.getText());
        try {
            Config.dump();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
