package Gui;

import Client.StandardSettings;
import Gui.Image.GuiImage;
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
    private GuiImage image;

    public ImageClick(GuiImage image){
        this.image = (GuiImage) image.clone();
    }

    public void draw(ArrayList<Vector2Dv1> circles){

    }

    public void drawBall(Vector2Dv1 circle){

    }

    public void drawPoint(int x, int y){
        image.Draw(new GuiImage.GuiCircle(new Vector2Dv1(x,y), 2, Color.BLUE, 2), true);
    }

    public void setImage(GuiImage image){
        this.image = image;
    }
    private int amount;
    private void createAndShowGUI(String title, int amount, ArrayList<Vector2Dv1> pos, ArrayList<Color> color) {
        JFrame frame = new JFrame(title + " (click 1 out of " + amount + ")");
        this.amount = amount;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image
        JLabel imageLabel = new JLabel(this.image.getIcon());

        // Create a custom mouse adapter to handle mouse click events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                GuiImage.GuiPixel pixel = image.getPixel(x,y);
                color.add(pixel.getColor());
                pos.add(pixel.getVector());
                drawPoint(x, y);
                imageLabel.setIcon(image.getIcon());
                amount--;
                frame.setTitle(title + "(" + (pos.size()+1) + " out of " + (pos.size()+this.amount) + ")");
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
