package imageRecognition;

import Client.StandardSettings;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.imageio.ImageIO;




public class ColorPicker {
    static int x=0,y=0;
    static BufferedImage img = null;

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String filePath = "test_img/colorpickerTest.JPEG";

        updatePicture(filePath);
        System.err.println("Update picture done");


        addPicToJFrame(img);

    }

    public static void updatePicture(String filePath){


        Imgcodecs imgcodecs = new Imgcodecs();

        VideoCapture capture = new VideoCapture(StandardSettings.VIDIO_CAPTURE_INDEX);

        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 360);
        // Check if the VideoCapture object was successfully initialized
        if (!capture.isOpened()) {
            System.err.println("Failed to open webcam!");
            System.exit(-1);
        }

        // Create a Mat object to store the current frame from the webcam
        Mat frame = new Mat();

        System.err.println("Capture frame");
        capture.read(frame);


        System.err.println("write to filepath");
        imgcodecs.imwrite(filePath, frame);

        File file = new File(filePath);
        try {

            img = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        capture.release();

    }

    public static void addPicToJFrame(BufferedImage img){
        JFrame f = new JFrame("Click on point to sample colors");
        ImageIcon icon = new ImageIcon(img);

        Mouse mouse = new Mouse();
        f.addMouseListener(mouse);

        f.add(new JLabel(icon));
        f.pack();
        f.setVisible(true);


    }
    public static Color getColor(int x, int y, BufferedImage img){
        Color mycolor = null;
        mycolor = new Color(img.getRGB(x, y));
        System.out.println("Red " + mycolor.getRed() + "Green" + mycolor.getGreen() + "Blue" + mycolor.getBlue());
        return mycolor;
    }
    static class Mouse implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println(("Mouse X Y: ("+e.getX()+", "+e.getY() +")"));
            //System.out.println(("X Y color: ("++", "+e.getY() +")"));
            x=e.getX();
            y=e.getY();

            getColor(x,y, img);


        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
