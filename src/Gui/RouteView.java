package Gui;

import Gui.Image.GuiImage;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class RouteView {

    private GuiImage image;
    public RouteView(ArrayList<ArrayList<Vector2Dv1>> routes, Mat mat){
        image = new GuiImage(mat);
        int i = 1;
        for (ArrayList<Vector2Dv1> route: routes) {
            int j = 1;
            if(routes.size() > i){
                image.Draw(new GuiImage.GuiLine(route.get(route.size()-1), routes.get(i).get(0), Color.RED, 3), false);
            }
            for (Vector2Dv1 waypoint: route) {
                if(route.size() > j){
                    image.Draw(new GuiImage.GuiLine(waypoint, route.get(j), Color.GREEN, 2), false);
                }
                image.Draw(new GuiImage.GuiCircle(waypoint, 6, Color.GREEN, 3), false);
                j++;
            }
            i++;
        }
        image.update();
        show();
    }

    private void show(){
        JFrame imageFrame = new JFrame("Image");
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Load an image
        JLabel imageLabel = new JLabel(image.getIcon());
        // Add the image label to the frame
        imageFrame.getContentPane().add(imageLabel);
        //frame.add(imageLabel);
        imageFrame.pack();
        imageFrame.setVisible(true);
    }
}
