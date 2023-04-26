package imageRecognition;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImgRec {
    public static void main(String[] args) {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create a new VideoCapture object to get frames from the webcam
        VideoCapture capture = new VideoCapture(2);


        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }

        // Create a Mat object to store the current frame from the webcam
        Mat frame = new Mat();

        // Create a new window to display the webcam feed
        HighGui.namedWindow("Webcam Feed");
        HighGui.namedWindow("Procesed Feed");


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
            Imgproc.HoughCircles(grayImage, circles, Imgproc.HOUGH_GRADIENT_ALT, 2.5, 9, 200, 0.67, 4, 20);


            Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
            // Draw the circles on the image
            for (int i = 0; i < circles.cols(); i++) {
                double[] circle = circles.get(0, i);
                Point center = new Point(Math.round(circle[0]), Math.round(circle[1]));
                System.out.println("x: " + center.x + "   y: " + center.y);
                int radius = (int) Math.round(circle[2]);
                Imgproc.circle(frame, center, radius, new Scalar(0, 0, 255), 2);
                Imgproc.circle(grayImage, center, radius, new Scalar(0, 0, 255), 2);
            }

            // Display the current frame on the screen
            HighGui.imshow("Webcam Feed", frame);
            HighGui.imshow("Procesed Feed", grayImage);

            // Wait for a key press to exit the program
            if (HighGui.waitKey(1) == 27) {
                break;
            }
        }

        // Release the VideoCapture object and destroy the window
        capture.release();
        HighGui.destroyAllWindows();

    }
}