package imageRecognition;

import misc.Ball;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImgRecFaseOne {

    private VideoCapture capture;
    private Mat frame;

    public ImgRecFaseOne() {


        // Create a new VideoCapture object to get frames from the webcam
        System.err.println("Webcam loading");
        capture = new VideoCapture(1);
        System.err.println("Webcam loaded");

        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }
        System.err.println("Webcam opened");

    }
    public List<Ball> captureBalls(){
        // Continuously capture frames from the webcam and display them on the screen
        frame = new Mat();
        // Read a new frame from the webcam
        capture.read(frame);
        System.err.println("Frame captured");

        // Apply some image processing to the frame (optional)
        //Imgproc.resize(frame, frame, new Size(1280, 960));

        // Convert the image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayImage, grayImage, 200, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(9, 9), 0, 0);
        Imgproc.threshold(grayImage, grayImage, 200, 255, Imgproc.THRESH_BINARY_INV);

        // Apply Gaussian blur to the image to reduce noise
        //Imgproc.GaussianBlur(grayImage, grayImage, new Size(9, 9), 2, 2);

        // Apply HoughCircles to detect circles in the image
        Mat circles = new Mat();
        Imgproc.HoughCircles(grayImage, circles, Imgproc.HOUGH_GRADIENT_ALT, 2.5, 9, 200, 0.67, 4, 20);


        Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
        // Draw the circles on the image
        List<Ball> balls = new ArrayList<>();
        for (int i = 0; i < circles.cols(); i++) {
            double[] circle = circles.get(0, i);
            Point center = new Point(Math.round(circle[0]), Math.round(circle[1]));
            int radius = (int) Math.round(circle[2]);
            double[] colorDoubleArray = frame.get((int)center.y, (int)center.x);
            int b = (int) colorDoubleArray[0]; // blue value
            int g = (int) colorDoubleArray[1]; // green value
            int r = (int) colorDoubleArray[2]; // red value
            balls.add(new Ball((int) center.x, (int) center.y, radius, new Color(r,g,b), true));
            Imgproc.circle(frame, center, radius, new Scalar(0, 0, 255), 2);
            Imgproc.circle(grayImage, center, radius, new Scalar(0, 0, 255), 2);
            //System.out.println("  x: " + center.x + "  y: " + center.y + "  color: " + new Color(r,g,b).toString());
        }
        return balls;
    }

    public void destroy(){
        // Release the VideoCapture object and destroy the window
        capture.release();
        //HighGui.destroyAllWindows();
    }
}
