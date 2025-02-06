package src;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Alte Version der Handerkennung (nicht mehr genutzt)
 */
public class KonturDemo {
    public static void Kontur() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String file ="testBild.png";
        Mat src = Imgcodecs.imread(file);
        Mat cannyEdges = new Mat();
        Mat hierarchey = new Mat();
        //Das Bild in binär umwandeln
        Mat gray = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Mat binary = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
        Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY_INV);

        //Kontur finden
        List<MatOfPoint> konturL = new ArrayList<>();

        Size size = new Size(3,3);
        Imgproc.GaussianBlur(src,src,size,2);
        Imgproc.Canny(src, cannyEdges, 90, 100);

        Imgproc.findContours(cannyEdges, konturL, hierarchey, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);

        //Zeichnung der Kontur
        Scalar farbeKontur = new Scalar(0, 255, 255);
        Imgproc.drawContours(src, konturL, -1, farbeKontur, 1, Imgproc.LINE_8,
                hierarchey, 1, new Point() ) ;

        //Fenster erzeugen
        HighGui.imshow("Kontur", src);
        HighGui.waitKey();

        //Sonst geht das Programm nicht automatisch aus
        HighGui.destroyAllWindows();
        HighGui.waitKey();

    }
}
