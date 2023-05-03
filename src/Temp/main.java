package Temp;

import exceptions.BadDataException;
import imageRecognition.ImgRecObstacle;
import misc.Vector2Dv1;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.core.MatOfPoint3;
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

import java.awt.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
public class main {
    public static void main(String[] args) throws IOException, BadDataException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = Imgcodecs.imread("test_img/WIN_20230315_10_32_53_Pro.jpg");
        ImgRecObstacle imgRecObstacle = new ImgRecObstacle();
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