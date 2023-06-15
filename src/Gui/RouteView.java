package Gui;

import misc.Vector2Dv1;
import misc.ball.Ball;
import nav.WaypointGenerator;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import routePlaner.Route;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class RouteView {

    public RouteView(ArrayList<ArrayList<Vector2Dv1>> routes, Mat mat){
        Mat m2 = mat.clone();
        int i = 1;
        for (ArrayList<Vector2Dv1> route: routes) {
            int j = 1;
            if(routes.size() > i){
                System.out.println("Line p1: " + route.get(route.size()-1).x*2 + ", " + route.get(route.size()-1).y*2 + " P2: " + routes.get(i).get(0).x*2 + ", " + routes.get(i).get(0).y*2);
                Imgproc.line(m2, new Point(route.get(route.size()-1).x*2, route.get(route.size()-1).y*2), new Point(routes.get(i).get(0).x*2, routes.get(i).get(0).y*2), new Scalar(0,0,255), 3);
            }
            for (Vector2Dv1 waypoint: route) {
                if(route.size() > j){
                    Imgproc.line(m2, new Point(waypoint.x*2, waypoint.y*2), new Point(route.get(j).x*2, route.get(j).y*2), new Scalar(0,255,0), 2);
                }
                org.opencv.core.Point center = new org.opencv.core.Point((int)waypoint.x*2, (int)waypoint.y*2);
                Imgproc.circle(m2, center, 6, new Scalar(0,255,0), 3);
                j++;
            }
            i++;
        }
        show(m2);
    }

    private void show(Mat mat){
        JFrame imageFrame = new JFrame("Image");
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Load an image
        JLabel imageLabel = new JLabel(getIcon(mat));
        // Add the image label to the frame
        imageFrame.getContentPane().add(imageLabel);
        //frame.add(imageLabel);
        imageFrame.pack();
        imageFrame.setVisible(true);
    }

    private ImageIcon getIcon(Mat mat){
        // Convert Mat to MatOfByte
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);

        // Create an InputStream from the MatOfByte
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage imageBuffered = null;
        // Read the image using ImageIO
        try {
            imageBuffered = ImageIO.read(in);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("ERROR");
            imageBuffered = null;
        }
        return new ImageIcon(imageBuffered);
    }
}
