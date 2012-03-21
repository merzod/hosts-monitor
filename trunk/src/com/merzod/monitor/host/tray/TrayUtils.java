package com.merzod.monitor.host.tray;

import com.merzod.monitor.host.ui.SettingsForm;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Utils for System Tray
 * @author opavlenko
 */
public class TrayUtils {

    private static final Logger log = Logger.getLogger(TrayUtils.class);
    private static TrayUtils instance;
    private boolean isSupported = true;
    private TrayIcon icon;
    private Menu targets;
    
    public void addTargets(List<MenuItem> items) {
        targets.removeAll();
        for(MenuItem item : items) {
            targets.add(item);
        }
    }
    
    private TrayUtils() {
        if (!SystemTray.isSupported()) {
            log.warn("Tray is not supported");
            isSupported = false;
        }
    }

    public static TrayUtils getInstance() {
        if(instance == null)
            instance = new TrayUtils();
        return instance;
    }

    /**
     * Call to set or update Tray Icon
     * @param state State image to set
     */
    public void setIcon(State state) {
        if(isSupported) {
            log.debug("Settings icon: " + state);
            try {
                if(icon == null) {
                    createTrayIcon();
                    SystemTray tray = SystemTray.getSystemTray();
                    tray.add(icon);
                } else {
                    icon.setImage(createImage(state));
                    icon.setToolTip(getAltText(state));
                }
            } catch (IOException e) {
                log.error("Error while creating TrayIcon", e);
            } catch (AWTException e) {
                log.error("Error while creating TrayIcon", e);
            }
        }
    }
    
    public void displayError(String message) {
        displayMessage("Error", message, TrayIcon.MessageType.ERROR);
    }
    
    public void displayMessage(String title, String message, TrayIcon.MessageType type) {
        if(isSupported) {
            icon.displayMessage(title, message, type);
        }
    }

    /**
     * Create Tray Icon, called once. Construct the Tray Icon and Menu
     * @throws IOException in case of problems with image file
     */
    private void createTrayIcon() throws IOException {
        State st = State.on;
        icon = new TrayIcon(createImage(st), getAltText(st));
        PopupMenu menu = new PopupMenu();
        targets = new Menu("Targets");
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Stopped by user");
                System.exit(0);
            }
        });
        MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("SettingsForm");
                frame.setContentPane(new SettingsForm().getContent());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(400, 300);
                frame.setVisible(true);
            }
        });
        menu.add(targets);
        menu.add(settings);
        menu.addSeparator();
        menu.add(exit);
        icon.setPopupMenu(menu);
    }
    
    private String getAltText(State state) {
        return "HostMonitor - "+state.getAlt();
    }

    private Image createImage(State state) throws IOException {
        return ImageIO.read(new File(state.getFile()));
    }

    /**
     * Possible Icons of System Tray Item
     */
    public static enum State {
        on("icons/comp.gif", "Ok"),
        run("icons/comp_run.gif", "Running"),
        yel("icons/comp_yel.gif", "Some hosts are down"),
        red("icons/comp_red.gif", "All hosts are down"),
        ;

        private final String file;
        private final String alt;

        State(String file, String alt) {
            this.file = file;
            this.alt = alt;
        }

        public String getFile() {
            return file;
        }

        public String getAlt() {
            return alt;
        }
    }
}
