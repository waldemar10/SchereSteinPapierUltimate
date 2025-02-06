package src.UI;

import org.slf4j.LoggerFactory;
import src.HandDetection;
import src.enums.ColorPalette;
import src.enums.WindowType;
import src.interfaces.ThresholdUpdateListener;
import src.UI.windows.CalibrationWindow;
import src.UI.windows.MainWindow;
import src.UI.windows.RulesWindow;

import javax.swing.*;
import java.awt.*;

/**
 * @author Waldemar Justus
 * @version 04.02.2025
 */

public class UIHandler {
    public static final Font FONT = new Font("Arial", Font.PLAIN, 14);
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UIHandler.class);
    private static JFrame window;
    private final MainWindow mainWindow;
    private final CalibrationWindow calibrationWindow;
    private final RulesWindow rulesWindow;
    private final ThresholdUpdateListener thresholdUpdateListener;
    private final MenuEventHandler menuEventHandler;


    HandDetection handDetection;

    public UIHandler(ThresholdUpdateListener thresholdUpdateListener,
                     HandDetection handDetection) {

        this.handDetection = handDetection;
        this.thresholdUpdateListener = thresholdUpdateListener;

        rulesWindow = new RulesWindow(this);
        mainWindow = new MainWindow(this, handDetection);
        calibrationWindow = new CalibrationWindow(this,handDetection);

        menuEventHandler = new MenuEventHandler(this);
        initializeAllWindows();
    }

    public static JFrame getWindow() {
        return window;
    }

    public void initializeUI(String title, int width, int height, WindowType windowType) {
        createWindow(title, width, height, windowType);
    }

    private void createWindow(String title, int width, int height, WindowType windowType) {
        window = new JFrame(title);
        window.setSize(width, height);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        switch (windowType) {
            case CALIBRATION:
                calibrationWindow.setWindow(window);
                calibrationWindow.createCalibrationWindow(thresholdUpdateListener);
                break;
            case MAIN:
                mainWindow.setWindow(window);
                mainWindow.createMainWindow(width, height);
                break;
            case RULES:
                rulesWindow.setWindow(window);
                rulesWindow.createRulesWindow();
                break;
            default:
                break;
        }

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(".//resources//PapierBild.jpg");
            window.setIconImage(icon);
        } catch (Exception e) {
            logger.error("Error loading icon", e);
        }

        window.setVisible(false);
    }

    public JButton createButtons(String name, String btnText, int x, int y, int btnWidth, int btnHeight, boolean isVisible) {
        JButton btn = new JButton();
        btn.setName(name);
        btn.setActionCommand(name);
        btn.setText(btnText);
        btn.setBounds(x, y, btnWidth, btnHeight);
        btn.setBackground(ColorPalette.WHITE.getColor());
        btn.setFont(FONT);
        btn.setForeground(ColorPalette.BLACK.getColor());
        btn.setHorizontalAlignment(JLabel.CENTER);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.addActionListener(menuEventHandler);
        btn.setVisible(isVisible);
        return btn;
    }

    public void showWindow(WindowType windowType) {
        setAllWindowsInvisible();

        switch (windowType) {
            case CALIBRATION:
                calibrationWindow.getWindow().setVisible(true);
                break;
            case MAIN:
                mainWindow.getWindow().setVisible(true);
                break;
            case RULES:
                rulesWindow.getWindow().setVisible(true);
                break;
            default:
                break;
        }
    }

    private void initializeAllWindows() {
        initializeUI(WindowType.CALIBRATION.getWindowTitle(), 1000, 700, WindowType.CALIBRATION);
        initializeUI(WindowType.MAIN.getWindowTitle(), 1408, 768, WindowType.MAIN);
        initializeUI(WindowType.RULES.getWindowTitle(), 400, 700, WindowType.RULES);
    }

    private void setAllWindowsInvisible() {
        mainWindow.getWindow().setVisible(false);
        rulesWindow.getWindow().setVisible(false);
        calibrationWindow.getWindow().setVisible(false);
    }

    public JLabel createImageLabel(String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            return new JLabel(icon);
        } catch (Exception e) {
            logger.error("Error loading image", e);
            return new JLabel("No image found at path: " + imagePath);
        }
    }

}