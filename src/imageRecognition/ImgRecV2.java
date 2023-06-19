package imageRecognition;


import Client.StandardSettings;
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

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.opencv.features2d.Features2d.drawKeypoints;

public class ImgRecV2 {
    public static void main(String[] args) throws InterruptedException {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create a new VideoCapture object to get frames from the webcam
        VideoCapture capture = new VideoCapture(StandardSettings.VIDIO_CAPTURE_INDEX);
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
        MatOfKeyPoint keypointsAll = new MatOfKeyPoint();
        ArrayList<KeyPoint> keypointList = new ArrayList<>();

        Scalar colorRed = new Scalar( 0, 0, 255 );

        // Continuously capture frames from the webcam and display them on the screen
        while (true) {
            // Read a new frame from the webcam
            capture.read(frame);
            ArrayList<Mat> channels = new ArrayList<>();
            Core.split(frame, channels);
            List<KeyPoint> combinedKeypoints = new ArrayList<>();
            //Detect the balls, and but them into MatOfKeyPoints keypoints
            blobDetec.detect(frame, keypoints);
            if(keypoints.get(0,0) != null)
                combinedKeypoints.addAll(keypoints.toList());
            blobDetec.detect(channels.get(2), keypoints);
            if(keypoints.get(0,0) != null)
                combinedKeypoints.addAll(keypoints.toList());
            keypoints.fromList(combinedKeypoints);
            //List of balls
            List<Ball> balls = new ArrayList<>();
            //For each on the keypoints
            for(KeyPoint keypoint : keypointList){
                double[] colorDoubleArray = frame.get((int) keypoint.pt.y, (int) keypoint.pt.x);
                int b = (int) colorDoubleArray[0]; // blue value
                int g = (int) colorDoubleArray[1]; // green value
                int r = (int) colorDoubleArray[2]; // red value
                balls.add(new Ball((int) keypoint.pt.x, (int) keypoint.pt.y, 0, new Color(r, g, b), true, PrimitiveBall.Status.UNKNOWN,0, Ball.Type.UNKNOWN));
            }
            ArrayList<Ball> ballsToRemove =new ArrayList<>();
            for (Ball b1: balls) {
                if(ballsToRemove.contains(b1))
                    continue;
                for (Ball b2: balls) {
                    if(b1 == b2)
                        continue;
                    if(ballsToRemove.contains(b2))
                        continue;
                    double dis = sqrt((pow((b1.getxPos() - b2.getxPos()), 2) + pow((b1.getyPos() - b2.getyPos()), 2)));
                    if(dis < 0)
                        dis*=-1;
                    if(dis < 5)
                        ballsToRemove.add(b2);
                }
            }
            for (Ball b: ballsToRemove) {
                balls.remove(b);
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