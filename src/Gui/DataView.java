package Gui;

import Gui.Image.GuiImage;
import misc.*;
import misc.ball.Ball;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class DataView {
    private static int WIDTH = 200;
    private static int HEIGHT = 500;

    private static boolean ballOn = false;

    private static boolean crossOn = false;

    private static boolean boundryOn = false;

    private static boolean dangerZoneOn = false;

    private static boolean zoneOn = false;
    private static boolean lineUpOn = false;

    private static ArrayList<Ball> balls = null;

    private static Cross cross = null;

    private static Boundry boundry = null;

    private GuiImage image;
    private Mat cleanImage;

    private JFrame imageFrame = null;

    private  JLabel imageLabel = null;

    public boolean running = true;

    public DataView(Mat m, ArrayList<Ball> balls, Boundry b, Cross c){
        cross = c;
        boundry = b;
        this.balls = balls;
        image = new GuiImage(m);
        cleanImage = m;
        imageFrame = new JFrame("Image");
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Load an image
        imageLabel = new JLabel(image.getIcon());
        // Add the image label to the frame
        imageFrame.getContentPane().add(imageLabel);
        //frame.add(imageLabel);
        imageFrame.pack();
        imageFrame.setVisible(true);
        updateImage();
        setupMenu();
    }

    private void showImage(ImageIcon icon){
        imageLabel.setIcon(icon);
    }

    private void updateImage(){
        image = new GuiImage(cleanImage);
        if(ballOn){
            for (Ball b: balls) {
                image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 6, Color.GREEN, 3), false);
            }
            if(zoneOn){

                for (Ball b: balls) {
                    image.Draw(new GuiImage.GuiCircle(b.getPosVector(), (int)b.getSafetyZone().radius, Color.BLUE, 3), false);
                }

            }
            if(dangerZoneOn){

                for (Ball b: balls) {
                    image.Draw(new GuiImage.GuiCircle(b.getPosVector(), (int)b.getCriticalZone().radius, Color.RED, 3), false);
                }

            }
            if(lineUpOn){

                for (Ball b: balls) {
                    image.Draw(new GuiImage.GuiCircle(b.getLineUpPoint(), 4, Color.RED, 2), false);
                }

            }
        }
        if(boundryOn){

            for (Line l: boundry.bound) {
                image.Draw(new GuiImage.GuiLine(l.p1, l.p2, Color.blue, 3), false);
            }
        }
        if(crossOn){

            for (Line l: cross.crossLines) {
                image.Draw(new GuiImage.GuiLine(l.p1, l.p2, Color.blue, 3), false);
            }

            if(zoneOn){

                for (Zone z: cross.getCriticalZones()) {
                    Zone safe = z.getNewSafetyZoneFromCriticalZone();
                    image.Draw(new GuiImage.GuiCircle(safe.pos, (int)safe.radius, Color.BLUE, 3), false);
                }

            }
            if(dangerZoneOn){

                for (Zone z: cross.getCriticalZones()) {
                    image.Draw(new GuiImage.GuiCircle(z.pos, (int)z.radius, Color.RED, 3), false);
                }

            }
        }
        image.update();
        imageLabel.setIcon(image.getIcon());
    }

    private void setupMenu(){
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Panel
        JPanel jPanel = new JPanel();
        jPanel.setSize(WIDTH,HEIGHT);
        jPanel.setLayout(new GridLayout(7,1));
        JButton ballBtn = new JButton("Toggle balls");
        jPanel.add(ballBtn);
        JButton boundryBtn = new JButton("Toggle boundry");
        jPanel.add(boundryBtn);
        JButton crossBtn = new JButton("Toggle cross");
        jPanel.add(crossBtn);
        JButton zoneBtn = new JButton("Toggle safety zones");
        jPanel.add(zoneBtn);
        JButton dzoneBtn = new JButton("Toggle Critical zones");
        jPanel.add(dzoneBtn);
        JButton pointBtn = new JButton("Toggle line up point");
        jPanel.add(pointBtn);
        JButton closeBtn = new JButton("Close");
        jPanel.add(closeBtn);


        ballBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ballOn)
                    ballOn = false;
                else
                    ballOn = true;
                updateImage();
            }
        });
        boundryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(boundryOn)
                    boundryOn = false;
                else
                    boundryOn = true;
                updateImage();
            }
        });
        crossBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(crossOn)
                    crossOn = false;
                else
                    crossOn = true;
                updateImage();
            }
        });
        zoneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(zoneOn)
                    zoneOn = false;
                else
                    zoneOn = true;
                updateImage();
            }
        });
        dzoneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dangerZoneOn)
                    dangerZoneOn = false;
                else
                    dangerZoneOn = true;
                updateImage();
            }
        });
        pointBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lineUpOn)
                    lineUpOn = false;
                else
                    lineUpOn = true;
                updateImage();
            }
        });
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
                imageFrame.dispose();
                frame.dispose();
            }
        });

        //Show jFrame
        frame.add(jPanel);

        frame.setSize(WIDTH,HEIGHT);
        frame.setVisible(true);

    }


}
