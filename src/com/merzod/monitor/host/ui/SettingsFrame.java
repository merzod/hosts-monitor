package com.merzod.monitor.host.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author opavlenko
 */
public class SettingsFrame extends JFrame {
    
    private JTabbedPane tabbedPane;

    public SettingsFrame() throws HeadlessException {
        super("Targets");
        init();
    }

    private void init() {
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Common", new CommonSettingsPanel());
        tabbedPane.add("SMTP", new SMTPSettingsPanel());
        tabbedPane.add("Targets", new TargetPanel());

        setContentPane(tabbedPane);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }


}
