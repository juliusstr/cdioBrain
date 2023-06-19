package imageRecognition;

import Client.StandardSettings;
import exceptions.BadDataException;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.videoio.Videoio.CAP_DSHOW;

public class ImgRecFaseTwo {

    private VideoCapture capture;
    private Mat frame;
    public Mat frameGUI;
    private SimpleBlobDetector blobDetec;
    private MatOfKeyPoint keypoints;

    public ImgRecObstacle imgRecObstacle;

    public Mat getFrame(){
        return frame;
    }

    public ImgRecFaseTwo() {
        // Create a new VideoCapture object to get frames from the webcam
        if(!StandardSettings.SPEED_BOOT) {
            System.err.println("loading webcam");
            capture = new VideoCapture(StandardSettings.VIDIO_CAPTURE_INDEX);
            System.err.println("changing frame size for GUI clicker");
            capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
            capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);
            frameGUI = new Mat();
            capture.read(frameGUI);
            capture.release();
        }
        System.err.println("loading webcam");
        capture = new VideoCapture(StandardSettings.VIDIO_CAPTURE_INDEX);
        System.err.println("changing frame size");
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);
        System.err.println("Webcam loaded");

        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }
        System.err.println("Webcam opened");
        ImgRecParams parameters = new ImgRecParams();
        SimpleBlobDetector_Params params = parameters.getParams();


        //params.set
        blobDetec = SimpleBlobDetector.create(params);
        keypoints = new MatOfKeyPoint();
        frame = new Mat();
        capture.read(frame);
        if(StandardSettings.SPEED_BOOT){
            frameGUI = frame.clone();
        }
        imgRecObstacle = new ImgRecObstacle();
        try {
            imgRecObstacle.findeObstacle(frame);
        } catch (BadDataException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Ball> captureBalls(){
        // Continuously capture frames from the webcam and display them on the screen
        frame = new Mat();
        // Read a new frame from the webcam
        capture.read(frame);

        System.err.println("Frame captured");


        // Apply some image processing to the frame (optional)
        //Imgproc.resize(frame, frame, new Size(1280, 960));

        //Detect the balls, and but them into MatOfKeyPoints keypoints
        blobDetec.detect(frame, keypoints);
        //List of balls
        ArrayList<Ball> balls = new ArrayList<>();
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
                balls.add(new Ball((int) keypoint.pt.x, (int) keypoint.pt.y, 0, new Color(r, g, b), true, PrimitiveBall.Status.UNKNOWN,0, Ball.Type.UNKNOWN));
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
