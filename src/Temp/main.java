package Temp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Subdiv2D;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
public class main {
    public static void main(String[] args) throws IOException {
        HighGui.namedWindow("Webcam Feed");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        while(true){

            // Load the input image
            Mat input = Imgcodecs.imread("C:\\Users\\juliu\\Desktop\\WIN_20230315_10_32_09_Pro.jpg");
            Imgproc.resize(input, input, new Size(512, 384));;
            // Convert the image to HSV color space
            Mat hsv = new Mat();
            Imgproc.GaussianBlur(input, input, new Size(9, 9), 0, 0);

            // Define the range of orange color in HSV
            Scalar lowerOrange = new Scalar(staVal.arg0, staVal.arg1, staVal.arg2);
            Scalar upperOrange = new Scalar(staVal.arg3, staVal.arg4, staVal.arg5);
            Core.inRange(input, lowerOrange, upperOrange, input); //hsv

            // Apply morphology operations to remove noise and fill gaps
            Mat kernel = Imgproc.getStructuringElement((staVal.arg9 == 0 ? Imgproc.MORPH_ELLIPSE : (staVal.arg9 == 1 ? Imgproc.MORPH_RECT : Imgproc.MORPH_CROSS)) , new Size(staVal.arg7, staVal.arg8));
            Imgproc.morphologyEx(input, input, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(input, input, Imgproc.MORPH_CLOSE, kernel);

            /*
            // Find contours of the orange regions
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(input, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            // Loop through the contours and filter out small or non-rectangular ones
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                Rect rect = Imgproc.boundingRect(contour);
                double aspectRatio = (double) rect.width / rect.height;
                if (area > 1000 && aspectRatio > 0.5 && aspectRatio < 2.0) {
                    // Draw a rectangle around the orange region
                    Imgproc.rectangle(input, rect, new Scalar(0, 255, 0), 2);
                }
            }*/

            Mat dots = new Mat();
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(input, contours, dots, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));

            System.out.println("Test");
            for (MatOfPoint contour : contours) {
                Rect rect = Imgproc.boundingRect(contour);
                System.out.println("x: " + (rect.x + rect.width/2) + ", y: " + (rect.y + rect.height/2));
                Imgproc.rectangle(input, rect, new Scalar(255, 255, 0), 2);
            }
            //Imgproc.GaussianBlur(input, input, new Size(21, 21), 0, 0);

            // Display the current frame on the screen
            HighGui.imshow("Webcam Feed", input);

            // Wait for a key press to exit the program
            if (HighGui.waitKey(1) == 27) {
                break;
            }
        }
        HighGui.destroyAllWindows();


       /* Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);

        // Define the range of orange color in HSV
        Scalar lowerOrange = new Scalar(0, 70, 75);
        Scalar upperOrange = new Scalar(20, 255, 255);

        // Threshold the image to get the orange regions
        Mat mask = new Mat();
        Core.inRange(hsv, lowerOrange, upperOrange, mask);

        // Apply morphology operations to remove noise and fill gaps
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        // Find contours of the orange regions
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Loop through the contours and filter out small or non-rectangular ones
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            Rect rect = Imgproc.boundingRect(contour);
            double aspectRatio = (double) rect.width / rect.height;
            if (area > 1000 && aspectRatio > 0.5 && aspectRatio < 2.0) {
                // Draw a rectangle around the orange region
                Imgproc.rectangle(input, rect, new Scalar(0, 255, 0), 2);
            }
        }
*/
        // Display the result
        //Imgcodecs.imwrite("output.jpg", input);
       /* // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create a new VideoCapture object to get frames from the webcam
        VideoCapture capture = new VideoCapture(1);

        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }

        // Create a Mat object to store the current frame from the webcam
        Mat frame = new Mat();

        // Create a new window to display the webcam feed
        HighGui.namedWindow("Webcam Feed");

        // Continuously capture frames from the webcam and display them on the screen
        while (true) {
            // Read a new frame from the webcam
            capture.read(frame);

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
            Imgproc.HoughCircles(grayImage, circles, Imgproc.HOUGH_GRADIENT_ALT, 1, 1, 200, 0.7, 5, 100);

            // Draw the circles on the image
            for (int i = 0; i < circles.cols(); i++) {
                double[] circle = circles.get(0, i);
                Point center = new Point(Math.round(circle[0]), Math.round(circle[1]));
               System.out.println("x: " + center.x + "y: " + center.y);
                int radius = (int) Math.round(circle[2]);
                Imgproc.circle(frame, center, radius, new Scalar(0, 0, 255), 3);
             }
            // Display the current frame on the screen
            HighGui.imshow("Webcam Feed", frame);

            // Wait for a key press to exit the program
            if (HighGui.waitKey(1) == 27) {
                break;
            }
        }

        // Release the VideoCapture object and destroy the window
        capture.release();
        HighGui.destroyAllWindows();
    */
    }
}