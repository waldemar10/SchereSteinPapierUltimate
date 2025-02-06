package src.UI.windows;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import src.*;
import src.UI.CameraHandler;
import src.UI.CameraPanel;
import src.UI.UIHandler;
import src.enums.ColorPalette;
import src.enums.MenuAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static src.UI.UIHandler.FONT;

/**
 * @author Waldemar Justus
 * @version 04.02.2025
 */

public class MainWindow {
    private static final int FPS_ANIMATOR = 60;
    static UIHandler uiHandler;
    private final CameraPanel cameraPanel;
    public static ArrayList<JButton> allButtons;
    HandDetection handDetection;
    private JFrame window;
    static JLabel announcement;
    public final static String GREETING_TEXT = "Willkommen bei Schere Stein Papier Ultimate!";
    public MainWindow(UIHandler uiHandler, HandDetection handDetection) {
        MainWindow.uiHandler = uiHandler;
        allButtons = new ArrayList<>();
        announcement = new JLabel();
        this.handDetection = handDetection;
        this.cameraPanel = new CameraPanel(handDetection);
    }

    public JFrame getWindow() {
        return window;
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public void createMainWindow(int width, int height) {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // OpenGL Canvas für das Rendering
        GLCanvas canvas = new StartRendererPP(capabilities);
        FPSAnimator animator = new FPSAnimator(canvas, FPS_ANIMATOR, true);

        window.setPreferredSize(new Dimension(width, height));
        window.setLayout(new BorderLayout());

        announcement = announcementText(GREETING_TEXT);

        announcement.setPreferredSize(new Dimension(width, 50));
        window.add(announcement, BorderLayout.NORTH);

        JPanel glPanel = new JPanel(new BorderLayout());
        canvas.setPreferredSize(new Dimension(999, 768));
        glPanel.add(canvas, BorderLayout.CENTER);
        window.add(glPanel, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setPreferredSize(new Dimension(408, height));
        menuPanel.setBackground(ColorPalette.DARK_GRAY.getColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;


        initializeMainWindowButtons();

        if (!allButtons.isEmpty()) {
            for (JButton btn : allButtons) {
                menuPanel.add(btn, gbc);
                gbc.gridy++;
            }
        }

        JPanel cameraPanelContainer = new JPanel(new BorderLayout());

        cameraPanel.setPreferredSize(new Dimension(340, 280));
        cameraPanel.setMinimumSize(new Dimension(340, 280));
        cameraPanel.setMaximumSize(new Dimension(340, 280));
        cameraPanelContainer.add(cameraPanel, BorderLayout.CENTER);
        cameraPanelContainer.setPreferredSize(new Dimension(340, 280));

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        menuPanel.add(cameraPanelContainer, gbc);
        CameraHandler.startCamera(cameraPanel);


        window.add(menuPanel, BorderLayout.EAST);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    if (animator.isStarted()) animator.stop();
                    System.exit(0);
                }).start();
            }
        });

        window.pack();
        window.setLocationRelativeTo(null);
        animator.start();
    }

    public void initializeMainWindowButtons() {
        allButtons.add(uiHandler.createButtons(MenuAction.START.getActionCommand(), MenuAction.START.getLabel(),
                1134, 30, 150, 50, true));
        allButtons.add(uiHandler.createButtons(MenuAction.CALIBRATION.getActionCommand(), MenuAction.CALIBRATION.getLabel(),
                1134, 110, 150, 50, true));
        allButtons.add(uiHandler.createButtons(MenuAction.RULES.getActionCommand(), MenuAction.RULES.getLabel(),
                1134, 190, 150, 50, true));
        allButtons.add(uiHandler.createButtons(MenuAction.QUIT.getActionCommand(), MenuAction.QUIT.getLabel(),
                1134, 318, 150, 50, true));
        allButtons.add(uiHandler.createButtons(MenuAction.RESET.getActionCommand(), MenuAction.RESET.getLabel(),
                1084, 50, 250, 80, false));
        allButtons.add(uiHandler.createButtons(MenuAction.NEW_ROUND.getActionCommand(), MenuAction.NEW_ROUND.getLabel(),
                1134, 190, 150, 50, false));
        allButtons.add(uiHandler.createButtons(MenuAction.BACK.getActionCommand(), MenuAction.BACK.getLabel(),
                1134, 318, 150, 50, false));
    }

    public static JLabel announcementText(String text){
        JLabel ancText = new JLabel(text, SwingConstants.CENTER);
        ancText.setFont(FONT);
        ancText.setOpaque(true);
        ancText.setBackground(ColorPalette.DARK_GRAY.getColor());
        ancText.setForeground(Color.WHITE);
        return ancText;
    }
    public static void updateAnnouncementText(String newText) {
        announcement.setText(newText);
        announcement.revalidate();
        announcement.repaint();
    }
}
