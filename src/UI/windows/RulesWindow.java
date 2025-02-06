package src.UI.windows;

import src.UI.UIHandler;
import src.enums.ColorPalette;
import src.enums.MenuAction;

import javax.swing.*;
import java.awt.*;

/**
 * @author Waldemar Justus
 * @version 04.02.2025
 */

public class RulesWindow {
    UIHandler uiHandler;
    private JFrame window;

    public RulesWindow(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public JFrame getWindow() {
        return window;
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public void createRulesWindow() {

        JPanel rulePanel = new JPanel();
        rulePanel.setBackground(ColorPalette.RED.getColor());
        rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.PAGE_AXIS));

        JLabel ruleImg = uiHandler.createImageLabel(".//resources//RULES.jpg");
        ruleImg.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JButton btnBack = uiHandler.createButtons(MenuAction.BACK.getActionCommand(), MenuAction.BACK.getLabel(),
                0, 0, 150, 50, true);
        btnBack.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        rulePanel.add(Box.createVerticalGlue());
        rulePanel.add(ruleImg);
        rulePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rulePanel.add(btnBack);
        rulePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        window.setContentPane(rulePanel);
        window.revalidate();
        window.repaint();
    }
}
