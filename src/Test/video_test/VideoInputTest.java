package Test.video_test;

import imageRecognition.ImgRecParams;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.opencv.features2d.Features2d.drawKeypoints;
import static org.opencv.highgui.HighGui.imshow;

public class VideoInputTest {
    VideoCapture capture;
    @Test
    @DisplayName("Test if input is working")
    void videoInputTest(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.out.println(System.getProperty("java.library.path"));
        //URL url = VideoInputTest.class.getClass().getResource("vid_cap_v1.mp4");
        //String filename = "/home/ulle/Dropbox/dtu_current/62410_CDIO/code/cdioBrain/cdioBrain/test_videos/video_capture/cdio/vid_cap_v1.mp4";
        String filename = "test_videos/video_capture/cdio/vid_cap_v1.mp4";
        capture = new VideoCapture(filename);
        assertEquals(true, capture.isOpened());
    }
    @Test
    @DisplayName("Test of ball capture of video v2.0")
    void videov2_1Test(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filename = "test_videos/video_capture/cdio/vid_cap_v2.0.mp4";
        capture = new VideoCapture(filename);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }
        // Create a Mat object to store the current frame from the webcam
        Mat frame = new Mat();
        // Create a new window to display the webcam feed
        //HighGui.namedWindow("Webcam Feed");
        //HighGui.namedWindow("Procesed Feed");

        ImgRecParams parameters = new ImgRecParams();
        SimpleBlobDetector_Params params = parameters.getParams();
        //params.set
        SimpleBlobDetector blobDetec = SimpleBlobDetector.create(params);
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        List<KeyPoint> keypointList = new ArrayList<>();
        // Continuously capture frames from the webcam and display them on the screen
        while (capture.read(frame)) {
            //Detect the balls, and but them into MatOfKeyPoints keypoints
            blobDetec.detect(frame, keypoints);
            //List of balls
            List<Ball> balls = new ArrayList<>();
            if(keypoints.get(0,0) != null) {
                //making keypoints into a list
                keypointList = keypoints.toList();

                //assertEquals(3, keypointList.size());
                //For each on the keypoints
                for(KeyPoint keypoint : keypointList){
                    double[] colorDoubleArray = frame.get((int) keypoint.pt.y, (int) keypoint.pt.x);
                    int b = (int) colorDoubleArray[0]; // blue value
                    int g = (int) colorDoubleArray[1]; // green value
                    int r = (int) colorDoubleArray[2]; // red value
                    balls.add(new Ball((int) keypoint.pt.x, (int) keypoint.pt.y, 0, new Color(r, g, b), true, PrimitiveBall.Status.UNKNOWN,0, Ball.Type.UKNOWN));
                }
            }
        }
        // Release the VideoCapture object and destroy the window
        capture.release();
    }

}






