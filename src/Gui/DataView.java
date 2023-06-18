package Gui;

import Client.StandardSettings;
import Gui.Image.GuiImage;
import exceptions.NoWaypointException;
import misc.*;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.PrimitiveBall;
import org.opencv.core.*;
import org.opencv.core.Point;
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
    private static int WIDTH = 400;
    private static int HEIGHT = 500;

    private static boolean ballOn = false;

    private static boolean crossOn = false;

    private static boolean boundryOn = false;

    private static Robotv1 robot = null;
    private static boolean dangerZoneOn = false;

    private static boolean zoneOn = false;
    private static boolean lineUpOn = false;

    private static boolean robotOn = false;
    private static boolean robotNonScaleOn = false;
    private static ArrayList<Ball> balls = null;
    private static ArrayList<Ball> rballs = null;

    private static Cross cross = null;

    private static Boundry boundry = null;

    private static GuiImage image;
    private static Mat cleanImage;

    private static JFrame imageFrame = null;

    private static JLabel imageLabel = null;

    public static boolean running = true;

    public static void main(String[] args) {

        ArrayList<Vector2Dv1> boundryList = new ArrayList<>();
        ArrayList<Ball> ball_list = new ArrayList<>();
        robot = new Robotv1(416.0, 254.0, new Vector2Dv1(0.5481603730984362));


        //SET TEST DATA
        cross = new Cross(new Vector2Dv1(301.0,205.0), new Vector2Dv1(306.0,180.0));
        boundryList.add(new Vector2Dv1(516.0,56.0));
        boundryList.add(new Vector2Dv1(516.0,56.0));
        boundryList.add(new Vector2Dv1(516.0,56.0));
        boundryList.add(new Vector2Dv1(516.0,56.0));
        ball_list.add(new Ball(new Vector2Dv1(128.0,91.0), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(155.0,37.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(174.0,99.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(212.0,224.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(200.0,272.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(87.0,318.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(421.0,108.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(494.0,134.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(510.0,104.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(454.0,336.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(294.0,187.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));


        boundry = new Boundry(boundryList);

        try {
            BallClassifierPhaseTwo.ballSetPlacement(ball_list, boundry, cross);
        } catch (NoWaypointException e) {
            throw new RuntimeException(e);
        }
        balls = ball_list;
        rballs = new ArrayList<>();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("test_img/WIN_20230315_10_32_53_Pro.jpg");
        image = new GuiImage(tImage);
        cleanImage = image.getMat().clone();
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

    public DataView(Mat m, ArrayList<Ball> balls, Boundry b, Cross c, Robotv1 r, ArrayList<Ball> rballs){
        robot = r;
        cross = c;
        boundry = b;
        this.balls = balls;
        this.rballs = rballs;
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

    private static void updateImage(){
        image = new GuiImage(cleanImage);
        if(ballOn){
            for (Ball b: balls) {
                if(b.getColor().equals(BallClassifierPhaseTwo.ORANGE))
                    image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 3, Color.BLUE, 3), false);
                else if(rballs.contains(b))
                    image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 3, Color.RED, 3), false);
                else
                    image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 3, Color.GREEN, 3), false);
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
                    image.Draw(new GuiImage.GuiCircle(b.getPickUpPoint(), 2, Color.RED, 2), false);
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
        if(robotOn){
            image.Draw(new GuiImage.GuiCircle(robot.aScale.getPosVector(), 1, Color.GREEN, 10), false);
            image.Draw(new GuiImage.GuiCircle(robot.bScale.getPosVector(), 1, Color.BLACK, 10), false);
        }
        if(robotNonScaleOn){
            image.Draw(new GuiImage.GuiCircle(robot.aUnScale.getPosVector(), 1, Color.GREEN, 10), false);
            image.Draw(new GuiImage.GuiCircle(robot.bUnScale.getPosVector(), 1, Color.BLACK, 10), false);
        }
        image.update();
        imageLabel.setIcon(image.getIcon());
    }

    private static void setupMenu(){
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Panel
        JPanel jPanel = new JPanel();
        jPanel.setSize(WIDTH,HEIGHT);
        jPanel.setLayout(new BorderLayout());

        JPanel bpanel = new JPanel();
        bpanel.setSize(WIDTH,(int)(HEIGHT*0.8));
        bpanel.setLayout(new GridLayout(4,2));

        JButton ballBtn = new JButton("Toggle balls");
        bpanel.add(ballBtn);
        JButton boundryBtn = new JButton("Toggle boundry");
        bpanel.add(boundryBtn);
        JButton crossBtn = new JButton("Toggle cross");
        bpanel.add(crossBtn);
        JButton zoneBtn = new JButton("Toggle safety zones");
        bpanel.add(zoneBtn);
        JButton dzoneBtn = new JButton("Toggle Critical zones");
        bpanel.add(dzoneBtn);
        JButton pointBtn = new JButton("Toggle line up point");
        bpanel.add(pointBtn);
        JButton robotBtn = new JButton("Toggle Robot");
        bpanel.add(robotBtn);
        JButton robotScaleBtn = new JButton("Toggle robot no scale");
        bpanel.add(robotScaleBtn);

        JPanel ipanel = new JPanel();
        ipanel.setSize(WIDTH,(int)(HEIGHT*0.2));
        ipanel.setLayout(new GridLayout(5,1));

        JLabel ballColor = new JLabel("balls color: Green");
        JLabel rballColor = new JLabel("required balls color: Red");
        JLabel oballColor = new JLabel("orange ball color: Blue");

        JButton closeBtn = new JButton("Close");
        ipanel.add(ballColor);
        ipanel.add(rballColor);
        ipanel.add(oballColor);
        ipanel.add(closeBtn);
        jPanel.add(bpanel, BorderLayout.CENTER);
        jPanel.add(ipanel, BorderLayout.PAGE_END);

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
        robotBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(robotOn)
                    robotOn = false;
                else
                    robotOn = true;
                updateImage();
            }
        });
        robotScaleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(robotNonScaleOn)
                    robotNonScaleOn = false;
                else
                    robotNonScaleOn = true;
                updateImage();
            }
        });

        //Show jFrame
        frame.add(jPanel);

        frame.setSize(WIDTH,HEIGHT);
        frame.setVisible(true);

    }


}
