package com.merzod.monitor.host.tray;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Utils for System Tray
 * @author opavlenko
 */
public class TrayUtils {

    private static final Logger log = Logger.getLogger(TrayUtils.class);
    private static TrayUtils instance;
    private boolean isSupported = true;
    private TrayIcon icon;

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
            log.debug("settings icon: " + state);
            try {
                if(icon == null) {
                    createTrayIcon();
                    SystemTray tray = SystemTray.getSystemTray();
                    tray.add(icon);
                } else {
                    icon.setImage(createImage(state));
                    icon.getImage().flush();
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
        icon = new TrayIcon(createImage(State.on), "HostMonitor");
        PopupMenu menu = new PopupMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(exit);
        icon.setPopupMenu(menu);
    }

    private Image createImage(State state) throws IOException {
        return ImageIO.read(new File(state.getFile()));
    }

    /**
     * Possible Icons of System Tray Item
     */
    public static enum State {
        on("icons/comp.gif"),
        run("icons/comp_run.gif");


        private final String file;

        State(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }
}
