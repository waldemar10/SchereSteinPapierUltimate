package src.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.GameStructure;
import src.UI.windows.MainWindow;
import src.enums.GameStatus;
import src.enums.MenuAction;
import src.enums.WindowType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static src.UI.windows.MainWindow.GREETING_TEXT;


/**
 * Java class for handling menuPane events
 *
 * @author Waldemar Justus
 * @version 04.02.2025
 *
 */

public class MenuEventHandler implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(MenuEventHandler.class);

    static public boolean start =false;

    private final UIHandler uiHandler;
    public MenuEventHandler(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            MenuAction action = MenuAction.valueOf(command);
            String START_TEXT = "Spiel gestartet! Halte deine Geste für mindestens 2 Sekunden.";
            switch (action) {
                case START: {
                    GameStructure.setGameStatus(GameStatus.RUNNING);
                    MainWindow.allButtons.get(0).setVisible(false);
                    MainWindow.allButtons.get(1).setVisible(false);
                    MainWindow.allButtons.get(2).setVisible(false);
                    MainWindow.allButtons.get(3).setVisible(false);
                    MainWindow.allButtons.get(4).setVisible(true);
                    MainWindow.allButtons.get(5).setVisible(true);
                    MainWindow.allButtons.get(6).setVisible(true);
                    MainWindow.updateAnnouncementText(START_TEXT);
                    GameStructure.resetAnimation();
                    break;
                }
                case CALIBRATION: {
                    uiHandler.showWindow(WindowType.CALIBRATION);
                    break;
                }
                case RULES: {
                    uiHandler.showWindow(WindowType.RULES);
                    break;
                }
                case NEW_ROUND: {
                    GameStructure.setGameStatus(GameStatus.RUNNING);
                    MainWindow.updateAnnouncementText(START_TEXT);
                    GameStructure.resetAnimation();
                    break;
                }
                case RESET: {
                    GameStructure.setPlayerPoint(0);
                    GameStructure.setEnemyPoint(0);
                    MainWindow.updateAnnouncementText(START_TEXT);
                    GameStructure.setGameStatus(GameStatus.RUNNING);
                    GameStructure.resetAnimation();
                    break;
                }
                case BACK, SUBMIT: {
                    MainWindow.allButtons.get(0).setVisible(true);
                    MainWindow.allButtons.get(1).setVisible(true);
                    MainWindow.allButtons.get(2).setVisible(true);
                    MainWindow.allButtons.get(3).setVisible(true);
                    MainWindow.allButtons.get(4).setVisible(false);
                    MainWindow.allButtons.get(5).setVisible(false);
                    MainWindow.allButtons.get(6).setVisible(false);
                    uiHandler.showWindow(WindowType.MAIN);
                    MainWindow.updateAnnouncementText(GREETING_TEXT);
                    GameStructure.setGameStatus(GameStatus.IDLE);
                    break;
                }
                case QUIT: {
                    System.exit(0);
                    break;
                }
            }
        }catch (IllegalArgumentException ex){
            logger.error("MenuAction command not exist",ex);
        }
    }
}