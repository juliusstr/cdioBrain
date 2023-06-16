package Gui;

import Client.StandardSettings;
import Gui.Image.GuiImage;
import misc.Vector2Dv1;
import misc.ball.Ball;
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
    private static GuiImage image;
    private static Mat cleanMat;

    public ImageClick(GuiImage image){
        this.image = (GuiImage) image.clone();
        cleanMat = image.getMat();
    }

    public void drawBalls(ArrayList<Ball> balls){
        for (Ball b: balls) {
            if(balls.get(balls.size()-1) == b)
                image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 16, Color.RED, 3), true);
            else
                image.Draw(new GuiImage.GuiCircle(b.getPosVector(), 16, Color.RED, 3), false);
        }
    }
    public void drawBallsVec(ArrayList<Vector2Dv1> balls){
        for (Vector2Dv1 v: balls) {
                image.Draw(new GuiImage.GuiCircle(v, 16, Color.RED, 3), false);
        }
        image.update();
    }

    public static void drawPoint(int x, int y){
        image.Draw(new GuiImage.GuiCircle(new Vector2Dv1(x,y), 2, Color.BLUE, 2), true);
    }

    public void setImage(GuiImage image){
        this.image = (GuiImage) image.clone();
        cleanMat = image.getMat();
    }
    private static int amount;
    private static String title;
    private static ArrayList<Vector2Dv1> pos;
    private static ArrayList<Color> color;
    private static boolean colorbool;
    private static JTable jt = null;
    public void run(String title, int amount, ArrayList<Vector2Dv1> pos, ArrayList<Color> color, JTable jt, boolean colorbool) {
        image = new GuiImage(cleanMat);
        this.title = title;
        this.amount = amount;
        this.pos = pos;
        this.color = color;
        this.jt = jt;
        this.colorbool = colorbool;
        SwingUtilities.invokeLater(ImageClick::createAndShowGUI);
    }
    public void run(String title, int amount, ArrayList<Vector2Dv1> pos, ArrayList<Color> color, boolean colorbool, ArrayList<Vector2Dv1> balls) {
        image = new GuiImage(cleanMat);
        this.title = title;
        this.amount = amount;
        this.pos = pos;
        this.color = color;
        this.colorbool = colorbool;
        drawBallsVec(balls);
        SwingUtilities.invokeLater(ImageClick::createAndShowGUI);
    }
    public void run(String title, int amount, ArrayList<Vector2Dv1> pos, ArrayList<Color> color, boolean colorbool) {
        image = new GuiImage(cleanMat);
        this.title = title;
        this.amount = amount;
        this.pos = pos;
        this.color = color;
        this.colorbool = colorbool;
        SwingUtilities.invokeLater(ImageClick::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame(title + " (click 1 out of " + amount + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image
        JLabel imageLabel = new JLabel(image.getIcon());

        // Create a custom mouse adapter to handle mouse click events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                GuiImage.GuiPixel pixel = image.getPixel(x,y);
                color.add(pixel.getColor());
                pos.add(pixel.getVector());
                drawPoint(pixel.x, pixel.y);
                imageLabel.setIcon(image.getIcon());
                amount--;
                frame.setTitle(title + "(" + (pos.size()+1) + " out of " + (pos.size()+amount) + ")");
                if(amount == 0){
                    int i = 0;
                    if(jt != null && colorbool){
                        for (Color c: color) {
                            jt.getModel().setValueAt(String.valueOf(c.getRed()), i, 1);
                            jt.getModel().setValueAt(String.valueOf(c.getGreen()), i, 2);
                            jt.getModel().setValueAt(String.valueOf(c.getBlue()), i, 3);
                            i++;
                        }
                        BallClassifierPhaseTwo.UpdateColor(color);
                    } else if(jt != null) {
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
