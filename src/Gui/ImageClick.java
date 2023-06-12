package Gui;

import misc.Vector2Dv1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

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

    public static ArrayList<Pixel> pixels = null;

    private static int amount = -1;
    private static BufferedImage imageBuffered = null;

    private static ImageIcon icon = null;

    private static String title = "";

    public ImageClick(int amount, Mat mat, String title, ArrayList<Pixel> pixels){
        this.pixels = pixels;
        this.amount = amount;
        this.title = title;
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
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image
        JLabel imageLabel = new JLabel(icon);

        // Create a custom mouse adapter to handle mouse click events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                pixels.add(new Pixel(new Vector2Dv1(x,y), new Color(imageBuffered.getRGB(x, y))));
                Color mycolor = null;
                mycolor = new Color(imageBuffered.getRGB(x, y));
                System.out.println("Red " + mycolor.getRed() + "Green" + mycolor.getGreen() + "Blue" + mycolor.getBlue());
                System.out.println("Clicked at position: (" + x + ", " + y + ")");
                amount--;
                if(amount == 0){
                    frame.dispose();
                }
            }
        };

        // Add the mouse adapter to the image label
        imageLabel.addMouseListener(mouseAdapter);

        // Add the image label to the frame
        frame.getContentPane().add(imageLabel);

        frame.pack();
        frame.setVisible(true);
    }
}
