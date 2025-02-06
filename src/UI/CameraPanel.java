package src.UI;

import src.HandDetection;

import javax.swing.*;
import java.awt.*;

/**
 * @author Waldemar Justus
 * @version 04.02.2025
 */

public class CameraPanel extends JPanel {
    private final HandDetection handDetection;

    public CameraPanel(HandDetection handDetection) {
        this.handDetection = handDetection;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (handDetection.isCameraReady()) {
            handDetection.paint(g);
        }
    }
}
