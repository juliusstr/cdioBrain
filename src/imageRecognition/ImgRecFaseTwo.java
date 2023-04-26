package imageRecognition;

import misc.Ball;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImgRecFaseTwo {

    private VideoCapture capture;
    private Mat frame;
    private SimpleBlobDetector blobDetec;
    private SimpleBlobDetector_Params params;
    private MatOfKeyPoint keypoints;

    public ImgRecFaseTwo() {


        // Create a new VideoCapture object to get frames from the webcam
        capture = new VideoCapture(1);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);
        System.err.println("Webcam loaded");

        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }
        System.err.println("Webcam opened");
        params = new SimpleBlobDetector_Params();
        params.set_filterByColor(false);
        params.set_minArea(60);
        params.set_maxArea(170);
        //min distance
        //params.set_minDistBetweenBlobs(10);

        //params.set_minConvexity();
        params.set_minConvexity(0.93F);
        params.set_maxConvexity(1);
        params.set_minThreshold(0.3F);


        //params.set
        blobDetec = SimpleBlobDetector.create(params);
        keypoints = new MatOfKeyPoint();

    }
    public List<Ball> captureBalls(){
        // Continuously capture frames from the webcam and display them on the screen
        frame = new Mat();
        // Read a new frame from the webcam
        capture.read(frame);

        System.err.println("Frame captured");


        // Apply some image processing to the frame (optional)
        //Imgproc.resize(frame, frame, new Size(1280, 960));

        capture.read(frame);
        //Detect the balls, and but them into MatOfKeyPoints keypoints
        blobDetec.detect(frame, keypoints);
        //List of balls
        List<Ball> balls = new ArrayList<>();
        List<KeyPoint> keypointList = new ArrayList<>();
        if(keypoints.get(0,0) != null) {
            //making keypoints into a list
            keypointList = keypoints.toList();
            //For each on the keypoints
            for(KeyPoint keypoint : keypointList){
                double[] colorDoubleArray = frame.get((int) keypoint.pt.y, (int) keypoint.pt.x);
                int b = (int) colorDoubleArray[0]; // blue value
                int g = (int) colorDoubleArray[1]; // green value
                int r = (int) colorDoubleArray[2]; // red value
                balls.add(new Ball((int) keypoint.pt.x, (int) keypoint.pt.y, 0, new Color(r, g, b), true));
            }
        }
        return balls;
    }

    public void destroy(){
        // Release the VideoCapture object and destroy the window
        capture.release();
        //HighGui.destroyAllWindows();
    }
}
