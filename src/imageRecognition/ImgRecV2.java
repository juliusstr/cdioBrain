package imageRecognition;


import Client.standardSettings;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import org.opencv.core.*;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.features2d.Features2d.drawKeypoints;

public class ImgRecV2 {
    public static void main(String[] args) throws InterruptedException {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create a new VideoCapture object to get frames from the webcam
        VideoCapture capture = new VideoCapture(standardSettings.videoCaptureIndex);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);
        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }

        // Create a Mat object to store the current frame from the webcam
        Mat frame = new Mat();
        // Create a new window to display the webcam feed
        HighGui.namedWindow("Webcam Feed");
        //HighGui.namedWindow("Procesed Feed");
        ImgRecParams parameters = new ImgRecParams();
        SimpleBlobDetector_Params params = parameters.getParams();
        //params.set
        SimpleBlobDetector blobDetec = SimpleBlobDetector.create(params);


        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        List<KeyPoint> keypointList = new ArrayList<>();

        Scalar colorRed = new Scalar( 0, 0, 255 );

        // Continuously capture frames from the webcam and display them on the screen
        while (true) {
            // Read a new frame from the webcam
            capture.read(frame);
            //Detect the balls, and but them into MatOfKeyPoints keypoints
            blobDetec.detect(frame, keypoints);
            //List of balls
            List<Ball> balls = new ArrayList<>();
            if(keypoints.get(0,0) != null) {
                //making keypoints into a list
                keypointList = keypoints.toList();
                //For each on the keypoints
                for(KeyPoint keypoint : keypointList){
                    double[] colorDoubleArray = frame.get((int) keypoint.pt.y, (int) keypoint.pt.x);
                    int b = (int) colorDoubleArray[0]; // blue value
                    int g = (int) colorDoubleArray[1]; // green value
                    int r = (int) colorDoubleArray[2]; // red value
                    balls.add(new Ball((int) keypoint.pt.x, (int) keypoint.pt.y, 0, new Color(r, g, b), true, PrimitiveBall.Status.UNKNOWN,0, Ball.Type.UKNOWN));
                }
            }

            // Display the current frame on the screen
            drawKeypoints(frame, keypoints, frame, colorRed, 1);
            HighGui.imshow("Webcam Feed", frame);
            //HighGui.imshow("Procesed Feed", grayImage);
            //Thread.sleep(1000);
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