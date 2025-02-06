package src.UI.windows;

import src.*;
import src.UI.CameraHandler;
import src.UI.CameraPanel;
import src.UI.UIHandler;
import src.enums.MenuAction;
import src.interfaces.ThresholdUpdateListener;

import javax.swing.*;
import java.awt.*;

import static src.UI.UIHandler.FONT;

/**
 * @author Waldemar Justus
 * @version 04.02.2025
 */
public class CalibrationWindow extends JFrame {
    UIHandler uiHandler;

    private static JFrame window;
    private final CameraPanel cameraPanel;
    HandDetection handDetection;
    public CalibrationWindow(UIHandler uiHandler, HandDetection handDetection) {
        this.uiHandler = uiHandler;
        this.handDetection = handDetection;
        this.cameraPanel = new CameraPanel(handDetection);
    }


    private static JLabel getJLabel() {
        JLabel instruction = new JLabel(
                "<html>" +
                        "<div>" +
                        "<h2>Kalibrierung der Hand</h2>" +
                        "<p>Für eine erfolgreiche Gestenerkennung muss zuvor eine Kalibrierung durchgeführt werden. </p>" +
                        "<p>Benutze den Threshold Schieberegler, um die Kontur der Hand vollständig zu erfassen.</p>" +
                        "<p>Wenn alle Gesten richtig erkannt werden, klicke auf <b>'Bestätigen'</b>.</p>" +
                        "<p><b>Wichtig: </b>Der Hintergrund sollte weiß sein und nur die Hand sollte zu sehen sein.</p>" +
                        "</div>" +
                        "</html>"
        );
        instruction.setFont(FONT);
        instruction.setBounds(30, 0, 400, 200);
        return instruction;
    }

    private static JSlider getJSlider(ThresholdUpdateListener thresholdUpdateListener) {
        JSlider sliderThreshValue = new JSlider(0, 255, 0);
        sliderThreshValue.setMajorTickSpacing(50);
        sliderThreshValue.setMinorTickSpacing(10);
        sliderThreshValue.setPaintTicks(true);
        sliderThreshValue.setPaintLabels(true);

        sliderThreshValue.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (thresholdUpdateListener != null) {
                thresholdUpdateListener.onThresholdUpdated(source.getValue());
            }
        });
        return sliderThreshValue;
    }

    public JFrame getWindow() {
        return window;
    }

    public void setWindow(JFrame window) {
        CalibrationWindow.window = window;
    }

    public void createCalibrationWindow(ThresholdUpdateListener thresholdUpdateListener) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton btnSubmit = uiHandler.createButtons(MenuAction.SUBMIT.getActionCommand(), MenuAction.SUBMIT.getLabel(),
                0, 0, 150, 50, true);

        JLabel instruction = getJLabel();

        JLabel schereLabel = uiHandler.createImageLabel(".//resources//SchereBild.jpg");
        JLabel papierLabel = uiHandler.createImageLabel(".//resources//PapierBild.jpg");
        JLabel steinLabel = uiHandler.createImageLabel(".//resources//SteinBild.jpg");

        JPanel sliderPanel = new JPanel(new BorderLayout());
        JSlider sliderThreshValue = getJSlider(thresholdUpdateListener);
        sliderPanel.add(new JLabel("Threshold Value"), BorderLayout.NORTH);
        sliderPanel.add(sliderThreshValue, BorderLayout.CENTER);

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(instruction, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnSubmit, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(schereLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(papierLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        panel.add(steinLabel, gbc);

        if (cameraPanel != null) {

            cameraPanel.setMinimumSize(new Dimension(440, 280));
            cameraPanel.setMaximumSize(new Dimension(440, 280));
            cameraPanel.setPreferredSize(new Dimension(440, 280));

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.gridheight = 3;
            panel.add(cameraPanel, gbc);

            CameraHandler.startCamera(cameraPanel);
        }


        window.setContentPane(panel);
        window.revalidate();
        window.repaint();
    }

}

