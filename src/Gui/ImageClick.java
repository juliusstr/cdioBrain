package Gui;

import Client.StandardSettings;
import misc.Vector2Dv1;
import misc.ball.BallClassifierPhaseTwo;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageClick {

    public static ArrayList<Vector2Dv1> pos = null;
    public static ArrayList<Color> color = null;

    private static int amount = -1;
    private static BufferedImage imageBuffered = null;

    private static ImageIcon icon = null;

    private static String title = "";

    private static JTable jt = null;

    private static Boolean colorbool = false;
    private static Boolean startup = true;

    public ImageClick(int amount, Mat mat, String title, ArrayList<Vector2Dv1> v, ArrayList<Color> c, JTable jt, Boolean color){
        this.pos = v;
        this.color = c;
        this.amount = amount;
        this.title = title;
        this.jt = jt;
        this.colorbool = color;

        // Convert Mat to MatOfByte
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);

        // Create an InputStream from the MatOfByte
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);

        // Read the image using ImageIO
        try {
            imageBuffered = ImageIO.read(in);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("ERROR");
            imageBuffered = null;
        }
        icon = new ImageIcon(imageBuffered);
        run();

    }
    public ImageClick(int amount, Mat mat, String title, ArrayList<Vector2Dv1> v){
        this.pos = v;
        this.color = new ArrayList<>();
        this.amount = amount;
        this.title = title;
        this.jt = new JTable();
        this.colorbool = false;
        this.startup = false;

        for (Vector2Dv1 v1: v) {
            org.opencv.core.Point center = new org.opencv.core.Point((int)v1.x*2, (int)v1.y*2);
            int radius = 16;
            Imgproc.circle(mat, center, radius, new Scalar(0, 0, 255), 3);
        }
        v.clear();
        // Convert Mat to MatOfByte
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);

        // Create an InputStream from the MatOfByte
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);

        // Read the image using ImageIO
        try {
            imageBuffered = ImageIO.read(in);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("ERROR");
            imageBuffered = null;
        }



        icon = new ImageIcon(imageBuffered);
        run();

    }
    public void run() {
        SwingUtilities.invokeLater(ImageClick::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame(title + " (click 1 out of " + amount + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image
        JLabel imageLabel = new JLabel(icon);

        // Create a custom mouse adapter to handle mouse click events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                color.add(new Color(imageBuffered.getRGB(x, y)));
                if(!StandardSettings.SPEED_BOOT) {
                    if (x % 2 < 0)
                        x--;
                    x /= 2;
                    if (y % 2 < 0)
                        y--;
                    y /= 2;
                }
                pos.add(new Vector2Dv1(x,y));
                /*Color mycolor = null;
                mycolor = new Color(imageBuffered.getRGB(x, y));
                System.out.println("Red " + mycolor.getRed() + "Green" + mycolor.getGreen() + "Blue" + mycolor.getBlue());
                System.out.println("Clicked at position: (" + x + ", " + y + ")");
                System.out.println(imageBuffered.getHeight() + "width " + imageBuffered.getWidth());
                System.out.println(imageLabel.getHeight() + "width " + imageLabel.getWidth());*/
                amount--;
                frame.setTitle(title + "(" + (pos.size()+1) + " out of " + (pos.size()+amount) + ")");
                if(amount == 0){
                    int i = 0;
                    if(startup && colorbool){
                        for (Color c: color) {
                            jt.getModel().setValueAt(String.valueOf(c.getRed()), i, 1);
                            jt.getModel().setValueAt(String.valueOf(c.getGreen()), i, 2);
                            jt.getModel().setValueAt(String.valueOf(c.getBlue()), i, 3);
                            i++;
                        }
                        BallClassifierPhaseTwo.UpdateColor(color);
                    } else if(startup) {
                        for (Vector2Dv1 v: pos) {
                            jt.getModel().setValueAt(String.valueOf(v.x),i,1);
                            jt.getModel().setValueAt(String.valueOf(v.y),i,2);
                            i++;
                        }
                    }
                    frame.dispose();
                }
            }
        };

        // Add the mouse adapter to the image label
        imageLabel.addMouseListener(mouseAdapter);

        // Add the image label to the frame
        frame.getContentPane().add(imageLabel);
        //frame.add(imageLabel);
        frame.pack();
        frame.setVisible(true);
    }
}
