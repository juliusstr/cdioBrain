package imageRecognition;

import exceptions.BadDataException;
import imageRecognition.ImgRecObstacle;
import misc.Vector2Dv1;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ImgRecObstacleRunnable {
    public static void main(String[] args) throws IOException, BadDataException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = new Mat(); //Imgcodecs.imread("test_img/WIN_20230315_10_32_53_Pro.jpg");

        VideoCapture capture;
        capture = new VideoCapture(2);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);

        ImgRecObstacle imgRecObstacle = new ImgRecObstacle();
        capture.read(input);
        imgRecObstacle.findeObstacle(input);


        HighGui.namedWindow("Plot");

        // Create Mat object to store plot
        int width = 2500;
        int height = 1500;
        Mat plot = new Mat(new Size(width, height), CvType.CV_8UC3);

        // Generate list of random points
        ArrayList<java.awt.Point> points = new ArrayList<>();
        points.addAll(imgRecObstacle.cross.crossPoint);
        points.addAll(imgRecObstacle.boundry.points);

        // Draw points on plot
        for (Point p : points) {
            org.opencv.core.Point point = new org.opencv.core.Point(p.x,p.y);
            Imgproc.circle(plot, point, 7, new Scalar(0, 0, 255), -1);
        }
        Imgproc.resize(plot, plot, new Size(1280, 720));
        // Show plot on HighGui frame
        HighGui.imshow("Plot", plot);

        // Wait for user to close frame
        HighGui.waitKey();
    }
}