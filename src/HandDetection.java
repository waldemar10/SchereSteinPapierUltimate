package src;

import org.opencv.core.Core;

import java.util.*;

import org.opencv.core.*;
import org.opencv.core.Point;

import java.awt.Graphics;

import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.List;

import org.opencv.videoio.VideoCapture;
import src.UI.CameraHandler;
import src.UI.windows.MainWindow;
import src.enums.ColorScalarPalette;
import src.enums.GameStatus;
import src.interfaces.ThresholdUpdateListener;

import static org.opencv.imgproc.Imgproc.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects hand gestures using OpenCV. The detection is based on basic image processing techniques.
 *
 * @author Waldemar Justus
 * @version 02.02.2025
 */
public class HandDetection implements ThresholdUpdateListener {
    private static final Logger logger = LoggerFactory.getLogger(HandDetection.class);
    private static final long GESTURE_HOLD_TIME = 2000; // 2 seconds
    private static final long MIN_CONTOUR_AREA = 2000;
    private static final long MAX_THRESHOLD_VALUE = 255;
    private static final int CANNY_THRESHOLD_1 = 250;
    private static final int CANNY_THRESHOLD_2 = 255;
    private static final int CANNY_APERTURE_SIZE = 3;
    private static final int DILATE_ITERATIONS = 2;
    private static final int CONTOUR_DEFECT_THRESHOLD = 20;
    private static final int CONTOUR_THICKNESS = 2;
    private static int thresholdValue = 0;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Map<String, Gesture> gestures;
    private long gestureDetectionStartTime;
    private long gestureHoldRemainingTime;


    private final Mat capturedFrame;
    private final Mat grayscaleFrame;
    private final Mat thresholdFrame;
    private final Mat edgeDetectedFrame;
    VideoCapture camera;
    private String currentGesture = "";
    private int contourDefectCount = 0;
    private MatOfPoint largestContour = new MatOfPoint();
    public HandDetection() {
        try {
            camera = new VideoCapture(0);
            capturedFrame = new Mat();
            grayscaleFrame = new Mat();
            thresholdFrame = new Mat();
            edgeDetectedFrame = new Mat();
            gestures = new HashMap<>();
            addGesture();



        } catch (Exception e) {
            logger.error("Error initializing HandDetection", e);
            throw e;
        }
    }

    /**
     * Find the largest contour
     *
     * @param contour The contours of the current frame.
     */
    private void setLargestContour(List<MatOfPoint> contour) {
        if (contour.isEmpty()) return;

        int largestContourIndex = 0;
        double biggerRes = 0;
        int contourNumber = contour.size();
        for (int i = 0; i < contourNumber; i++) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.get(i).toArray());
            double res = Imgproc.arcLength(contour2f, false);
            if (res > biggerRes) {
                biggerRes = res;
                largestContourIndex = i;
            }
        }
        largestContour = contour.get(largestContourIndex);
    }

    /**
     * Paints the current frame
     *
     * @param graphics The graphics object.
     */
    public void paint(Graphics graphics) {
        if (!existsGraphicsObject(graphics) || !isCameraReady() || !isFrameReady()) return;

        processCapturedFrame(capturedFrame);
        graphics.drawImage(CameraHandler.Mat2BufferedImage(capturedFrame),0,0,null);
    }

    private void addGesture() {
        gestures.put("Stone", new Gesture("Stone", 1, 3,
                0.028, 1.0, 0, 640));
        gestures.put("Scissor", new Gesture("Scissor", 4, 4, 0.0095,
                0.02, 0, 0));
        gestures.put("Paper", new Gesture("Paper", 4, 9, 0.025,
                0, 650, 0));
    }

    private boolean existsGraphicsObject(Graphics graphics) {
        if (graphics != null) {
            return true;
        }
        logger.error("Graphics is null");
        return false;
    }

    public boolean isCameraReady() {
        try {
            return camera.isOpened();
        } catch (Exception e) {
            logger.error("Error checking if camera is ready", e);
            return false;
        }
    }

    private boolean isFrameReady() {
        try {
            camera.read(capturedFrame);
            if (!capturedFrame.empty()) {
                return true;
            }
            logger.error("Captured frame is empty");
        } catch (Exception e) {
            logger.error("Error while capturing frame", e);
        }
        return false;
    }

    /**
     * Image processes the current frame
     *
     * @param frame The current frame.
     */
    private void processCapturedFrame(Mat frame) {
        try {
            setupImageProcessing(frame, grayscaleFrame, thresholdFrame, edgeDetectedFrame);
            List<MatOfPoint> contours = findContoursInFrame();
            if (contours.isEmpty()) return;

            setLargestContour(contours);
            double largestContourArea = contourArea(largestContour);
            if (largestContourArea >= MIN_CONTOUR_AREA) {
                detectAndValidateGesture(frame, contours, largestContourArea);
            }
        }
        catch (Exception e) {
            logger.error("Error while processing frame", e);
        }
    }

    /**
     * Find the contours in the current frame
     *
     * @return A list of contours
     */
    private List<MatOfPoint> findContoursInFrame() {
        List<MatOfPoint> contours = new ArrayList<>();
        findContours(edgeDetectedFrame, contours, new Mat(), RETR_EXTERNAL,
                CHAIN_APPROX_SIMPLE);
        return contours;
    }

    /**
     * Handles the gesture detection.
     *
     * @param frame       The current frame.
     * @param contours    The contours of the current frame.
     * @param contourArea The area of the largest contour.
     */
    private void detectAndValidateGesture(Mat frame, List<MatOfPoint> contours, double contourArea) {
        String detectedGesture = checkGesture(calculateConvexHull(contours), contourArea);
        displayGestureRecognitionResult(frame, detectedGesture, checkGestureHoldDuration(detectedGesture));
        showGestureHoldProgress(frame);
    }

    @Override
    public void onThresholdUpdated(int thresholdValue) {
        HandDetection.thresholdValue = thresholdValue;
        updateThreshold();
    }

    private void updateThreshold() {
        if (grayscaleFrame == null || thresholdFrame == null) return;
        threshold(grayscaleFrame, thresholdFrame, thresholdValue, MAX_THRESHOLD_VALUE, THRESH_BINARY);
    }

    /**
     * Creates the convex hull of the largest contour.
     *
     * @param contours The contours of the current frame.
     * @return The convex hull of the largest contour.
     */
    private MatOfPoint calculateConvexHull(List<MatOfPoint> contours) {
        if (largestContour == null || largestContour.empty()) {
            return new MatOfPoint();
        }

        // Create the convex hull
        MatOfInt convexHullIndices = new MatOfInt();
        Imgproc.convexHull(largestContour, convexHullIndices, false);

        // Convert the indices to points
        List<Point> hullPoints = new ArrayList<>();
        List<Integer> indices = convexHullIndices.toList();
        for (int index : indices) {
            hullPoints.add(largestContour.toList().get(index));
        }

        // Convert the points to MatOfPoint
        MatOfPoint convexHull = new MatOfPoint();
        convexHull.fromList(hullPoints);
        // Draw the convex hull
        processContourDefects(contours, convexHullIndices, edgeDetectedFrame, capturedFrame);
        drawHandContours(contours, Collections.singletonList(convexHull), capturedFrame);

        return convexHull;
    }

    /**
     * Sets up the image processing.
     *
     * @param frame  The current frame.
     * @param gray   The grayscale version of the current frame.
     * @param thresh The binary "threshold" version of the current frame.
     * @param dst    The edge detected version of the current frame.
     */
    private void setupImageProcessing(Mat frame, Mat gray, Mat thresh, Mat dst) {
        try {
            // Scale the frame
            resize(frame, frame, new Size(384, 288));
            // Convert the frame to grayscale
            cvtColor(frame, gray, COLOR_BGR2GRAY);
            // Gauss-filter the frame
            GaussianBlur(gray, gray, new Size(25, 25), 0);
            // Binary threshold the frame
            threshold(gray, thresh, thresholdValue, MAX_THRESHOLD_VALUE, THRESH_BINARY);
            // Canny edge detection
            Canny(thresh, dst, CANNY_THRESHOLD_1, CANNY_THRESHOLD_2, CANNY_APERTURE_SIZE, false);
            // Dilate the frame
            dilate(dst, dst, new Mat(), new Point(-1, -1), DILATE_ITERATIONS);
        }
        catch (Exception e) {
            logger.error("Error while setting up image processing", e);
        }
    }

    /**
     * Sets the gestures text.
     *
     * @param frame             The current frame.
     * @param recognizedGesture The recognized gesture.
     * @param isRecognized      True if the gesture is recognized, false otherwise.
     */
    private void displayGestureRecognitionResult(Mat frame, String recognizedGesture, boolean isRecognized) {
        Point textPoint = new Point(10, 25);

        Scalar defaultColor = ColorScalarPalette.BLUE.get();
        Scalar recognizedColor = ColorScalarPalette.GREEN.get();
        Scalar gesturesColor = isRecognized ? recognizedColor : defaultColor;

        putText(frame, recognizedGesture, textPoint, 2, 1, gesturesColor, 1);

        if (isRecognized && !recognizedGesture.isEmpty() && GameStructure.getGameStatus().equals(GameStatus.RUNNING)) {
            MainWindow.updateAnnouncementText(recognizedGesture);
            GameStructure.selectedGesture = recognizedGesture;
            GameStructure.checkGestures(recognizedGesture, GameStructure.generateComputerGesture());
        }
    }

    /**
     * Checks the gesture.
     *
     * @param convexHullMatOfPoint The convex hull of the largest contour.
     * @param contourArea          The area of the largest contour.
     * @return The recognized gesture.
     */
    private String checkGesture(MatOfPoint convexHullMatOfPoint, double contourArea) {

        String detectedGesture = "";

        MatOfPoint2f contour2f = new MatOfPoint2f(largestContour.toArray());
        MatOfPoint2f hull2f = new MatOfPoint2f(convexHullMatOfPoint.toArray());
        double arcHull = arcLength(hull2f, true);
        double arc = arcLength(contour2f, true);

        double circularity = getCircularity(contourArea, arc);

        if (gestures.get("Stone").isDetectedStone(contourDefectCount, circularity, arcHull)) {
            detectedGesture = "Stone";
        } else if (gestures.get("Scissor").isDetectedScissor(contourDefectCount, circularity)) {
            detectedGesture = "Scissor";
        } else if (gestures.get("Paper").isDetectedPaper(contourDefectCount, circularity, arcHull)) {
            detectedGesture = "Paper";
        }

        if (detectedGesture.isEmpty()) {
            resetValidateTimer();
            return detectedGesture;
        }
        if (!detectedGesture.equals(currentGesture)) {
            currentGesture = detectedGesture;
            gestureDetectionStartTime = System.currentTimeMillis();
        }
        return detectedGesture;
    }

    /**
     * Validates the gesture.
     *
     * @param detectedGesture The recognized gesture.
     * @return True if the gesture was held for x seconds, false and resets the timer otherwise.
     */
    private boolean checkGestureHoldDuration(String detectedGesture) {

        if (detectedGesture.equals(currentGesture)) {
            return calculateRemainingGestureTime() <= 0;
        }

        resetValidateTimer();
        return false;
    }

    /**
     * Resets the gesture timer.
     */
    void resetValidateTimer() {
        currentGesture = "";
        gestureDetectionStartTime = 0;
        gestureHoldRemainingTime = GESTURE_HOLD_TIME;
    }

    /**
     * Calculates the remaining gesture time. The user has to hold the gesture for x seconds.
     *
     * @return The remaining gesture time.
     */
    double calculateRemainingGestureTime() {
        long currentTime = System.currentTimeMillis();
        gestureHoldRemainingTime = (GESTURE_HOLD_TIME - (currentTime - gestureDetectionStartTime));
        return gestureHoldRemainingTime;
    }

    /**
     * Displays the progress bar
     *
     * @param frame The current frame.
     */
    private void showGestureHoldProgress(Mat frame) {
        if (gestureHoldRemainingTime > 0) {
            double progress = 1.0 - ((double) gestureHoldRemainingTime / GESTURE_HOLD_TIME);
            int barWidth = 200;
            int barHeight = 10;
            Point barStart = new Point(50, 40);
            Point barEnd = new Point(50 + (int) (barWidth * progress), 40 + barHeight);
            Scalar barColor = ColorScalarPalette.GREEN.get();
            rectangle(frame, barStart, barEnd, barColor, -1);
        }
    }

    void drawHandContours(List<MatOfPoint> contour, List<MatOfPoint> convexHullMatOfPointArrayList, Mat frame) {
        if (contour.isEmpty()) return;

        drawContours(frame, contour, -1, ColorScalarPalette.MAGENTA.get(), CONTOUR_THICKNESS);
        drawContours(frame, convexHullMatOfPointArrayList, -1,
                ColorScalarPalette.CYAN.get(), CONTOUR_THICKNESS);
    }

    /**
     * Calculates the center of the largest contour
     *
     * @param dst The destination image.
     * @return The center of the largest contour.
     */
    private Point calculateCenter(Mat dst) {
        Moments m = moments(dst);
        int xAchse = (int) Math.round(m.m10 / m.m00);
        int yAchse = (int) Math.round(m.m01 / m.m00);
        return new Point(xAchse, yAchse);
    }

    void drawContourDefects(Mat frame, Point endPoint, Point midPoint) {
        byte radius = 10;

        circle(frame, endPoint, radius, ColorScalarPalette.ORANGE.get(), -1);
        circle(frame, midPoint, radius, ColorScalarPalette.ORANGE.get(), -1);

        putText(frame, String.valueOf(contourDefectCount), endPoint, 2, 1,
                ColorScalarPalette.ORANGE.get(), 1);

        line(frame, endPoint, midPoint, ColorScalarPalette.ORANGE.get(), 1);
    }

    /**
     * Processes the contour defects
     *
     * @param contour            The contours of the current frame.
     * @param convexHullMatOfInt The convex hull of the largest contour.
     * @param dst                The destination image.
     * @param frame              The current frame.
     */
    private void processContourDefects(List<MatOfPoint> contour, MatOfInt convexHullMatOfInt, Mat dst, Mat frame) {
        if(largestContour == null || largestContour.empty()) {
            return;
        }

        List<MatOfInt4> contourDefects = new ArrayList<>();
        for (int i = 0; i < contour.size(); i++) {
            contourDefects.add(new MatOfInt4());
            convexityDefects(largestContour, convexHullMatOfInt, contourDefects.get(i));
            Mat nativeMat = contourDefects.get(i);

            if (nativeMat.rows() > 0) {
                processDefects(contourDefects.get(i).toList(), largestContour.toArray(), dst, frame);
            }
        }
    }

    /**
     * Processes the contour defects
     *
     * @param intList The contour defects.
     * @param data    The data of the largest contour.
     * @param dst     The destination image.
     * @param frame   The current frame.
     */
    private void processDefects(List<Integer> intList, Point[] data, Mat dst, Mat frame) {
        contourDefectCount = 0;
        for (int j = 0; j < intList.size(); j += 4) {
            Point endPoint = data[intList.get(j + 1)];
            Point midPoint = calculateCenter(dst);
            byte depthPosition = 3;
            int depth = intList.get(j + depthPosition);
            int depthScale = 256;
            int scaledDepth = depth / depthScale;
            if (scaledDepth > CONTOUR_DEFECT_THRESHOLD) {
                contourDefectCount++;
                drawContourDefects(frame, endPoint, midPoint);
            }
        }
    }

    /**
     * Calculates the circularity
     *
     * @param contourArea The area of the contour.
     * @param arc         The arc length of the contour.
     * @return The circularity.
     */
    double getCircularity(double contourArea, double arc) {
        return (4 * Math.PI * contourArea) / (arc * arc);
    }
}
