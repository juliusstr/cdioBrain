package Gui;

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

    private Mat image;

    private JFrame imageFrame = null;

    private  JLabel imageLabel = null;

    public boolean running = true;

    public DataView(Mat m, ArrayList<Ball> balls, Boundry b, Cross c){
        cross = c;
        boundry = b;
        this.balls = balls;
        image = m.clone();
        imageFrame = new JFrame("Image");
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Load an image
        imageLabel = new JLabel(getIcon(m.clone()));
        // Add the image label to the frame
        imageFrame.getContentPane().add(imageLabel);
        //frame.add(imageLabel);
        imageFrame.pack();
        imageFrame.setVisible(true);
        updateImage(m.clone());
        setupMenu();
    }

    private void showImage(ImageIcon icon){
        imageLabel.setIcon(icon);
    }

    private void updateImage(Mat mat){
        if(ballOn){

            for (Ball b: balls) {
                org.opencv.core.Point center = new org.opencv.core.Point((int)b.getxPos()*2, (int)b.getyPos()*2);
                Imgproc.circle(mat, center, 6, new Scalar(0,255,0), 3);
            }

            if(zoneOn){

                for (Ball b: balls) {
                    org.opencv.core.Point center = new org.opencv.core.Point((int)b.getxPos()*2, (int)b.getyPos()*2);
                    Imgproc.circle(mat, center, (int)b.getSafetyZone().radius, new Scalar(255,0,0,128), 3);
                }

            }
            if(dangerZoneOn){

                for (Ball b: balls) {
                    org.opencv.core.Point center = new org.opencv.core.Point((int)b.getxPos()*2, (int)b.getyPos()*2);
                    Imgproc.circle(mat, center, (int)b.getCriticalZone().radius, new Scalar(0,0,255), 3);
                }

            }
            if(lineUpOn){

                for (Ball b: balls) {
                    Vector2Dv1 l = b.getLineUpPoint();
                    org.opencv.core.Point center = new org.opencv.core.Point((int)l.x*2, (int)l.y*2);
                    Imgproc.circle(mat, center, 4, new Scalar(0,255,0,128), 3);
                }

            }
        }
        if(boundryOn){

            for (Line l: boundry.bound) {
                Imgproc.line(mat, new Point(l.p1.x*2, l.p1.y*2), new Point(l.p2.x*2, l.p2.y*2), new Scalar(255,0,0), 3);
            }
        }
        if(crossOn){

            for (Line l: cross.crossLines) {
                Imgproc.line(mat, new Point(l.p1.x*2, l.p1.y*2), new Point(l.p2.x*2, l.p2.y*2), new Scalar(255,0,0), 3);
            }

            if(zoneOn){

                for (Zone z: cross.getCriticalZones()) {
                    Zone safe = z.getNewSafetyZoneFromCriticalZone();
                    Vector2Dv1 p = safe.pos;
                    org.opencv.core.Point center = new org.opencv.core.Point((int)p.x*2, (int)p.y*2);
                    Imgproc.circle(mat, center, (int)safe.radius, new Scalar(0,0,255), 3);
                }

            }
            if(dangerZoneOn){

                for (Zone z: cross.getCriticalZones()) {
                    Vector2Dv1 p = z.pos;
                    org.opencv.core.Point center = new org.opencv.core.Point((int)p.x*2, (int)p.y*2);
                    Imgproc.circle(mat, center, (int)z.radius, new Scalar(0,0,255), 3);
                }

            }
        }
        showImage(getIcon(mat));
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
                updateImage(image.clone());
            }
        });
        boundryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(boundryOn)
                    boundryOn = false;
                else
                    boundryOn = true;
                updateImage(image.clone());
            }
        });
        crossBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(crossOn)
                    crossOn = false;
                else
                    crossOn = true;
                updateImage(image.clone());
            }
        });
        zoneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(zoneOn)
                    zoneOn = false;
                else
                    zoneOn = true;
                updateImage(image.clone());
            }
        });
        dzoneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dangerZoneOn)
                    dangerZoneOn = false;
                else
                    dangerZoneOn = true;
                updateImage(image.clone());
            }
        });
        pointBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lineUpOn)
                    lineUpOn = false;
                else
                    lineUpOn = true;
                updateImage(image.clone());
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
