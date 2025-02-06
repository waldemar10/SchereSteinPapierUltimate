package src.UI;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraHandler {
    private static final Logger logger = LoggerFactory.getLogger(CameraHandler.class);

    public static BufferedImage Mat2BufferedImage(Mat src) {
        if (src.empty()) return null;

        MatOfByte matByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", src, matByte);
        byte[] byteArray = matByte.toArray();
        InputStream input = new ByteArrayInputStream(byteArray);

        try {
            return ImageIO.read(input);
        } catch (IOException e) {
            logger.error("Error while reading image", e);
            return null;
        }
    }
    public static void startCamera(CameraPanel cameraPanel) {
        Timer timer = new Timer(30, e -> cameraPanel.repaint());
        timer.start();
    }

    public static void stopCamera(CameraPanel cameraPanel) {
        Timer timer = new Timer(30, e -> cameraPanel.repaint());
        timer.stop();
    }
}
