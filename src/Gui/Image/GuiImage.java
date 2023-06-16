package Gui.Image;

import misc.Boundry;
import misc.Vector2Dv1;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.opencv.imgproc.Imgproc.INTER_CUBIC;

public class GuiImage implements Cloneable{

    private final int HEIGHT = 720;
    private final int WIDTH = 1280;
    private static final int ADJUST = 2;

    private Mat mat;

    private BufferedImage bufferedImage;

    private ImageIcon icon;

    public GuiImage(ImageIcon icon){
        update(icon);
        setSize();
    }

    public GuiImage(BufferedImage bufferedImage){
        update(bufferedImage);
        setSize();
    }
    public GuiImage(Mat mat){
        update(mat);
        setSize();
    }

    private void setSize(){
        Imgproc.resize(mat,mat, new Size(WIDTH, HEIGHT), 0, 0, INTER_CUBIC);
        update();
    }

    private Mat getMat(ImageIcon icon){
        return getMat(getBufferedImage(icon));
    }

    private Mat getMat(BufferedImage bufferedImage){
        // Convert BufferedImage to Mat
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
    public Mat getMat(){
        return this.mat;
    }

    private ImageIcon getIcon(Mat mat){
        return getIcon(getBufferedImage(mat));
    }
    private ImageIcon getIcon(BufferedImage bufferedImage){
        return new ImageIcon(bufferedImage);
    }
    public ImageIcon getIcon(){
        return this.icon;
    }

    private BufferedImage getBufferedImage(ImageIcon icon){
        // Convert ImageIcon to BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );
        Image image = icon.getImage();
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }
    private BufferedImage getBufferedImage(Mat mat){
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
        return imageBuffered;
    }

    public BufferedImage getBufferedImage(){
        return this.bufferedImage;
    }

    private void update(Mat mat){
        Mat m = mat.clone();
        this.mat = m;
        this.bufferedImage = getBufferedImage(m);
        this.icon = getIcon(m);
    }

    private void update(BufferedImage bufferedImage){
        Mat m = getMat(bufferedImage);
        this.mat = m;
        this.bufferedImage = getBufferedImage(m);
        this.icon = getIcon(m);
    }

    private void update(ImageIcon icon){
        Mat m = getMat(icon);
        this.mat = m;
        this.bufferedImage = getBufferedImage(m);
        this.icon = getIcon(m);
    }

    public static class GuiCircle{

        private Scalar color;

        private org.opencv.core.Point pos;
        private int radius;
        private int size;

        public GuiCircle(Vector2Dv1 v, int radius , Color color, int size){
            this.size = size;
            this.radius = radius;
            pos = new org.opencv.core.Point((int)v.x*ADJUST, (int)v.y*ADJUST);
            this.color = new Scalar(color.getBlue(), color.getGreen(), color.getRed());
        }

    }

    public static class GuiLine{

        private Scalar color;

        private org.opencv.core.Point p1;

        private org.opencv.core.Point p2;
        private int size;

        public GuiLine(Vector2Dv1 v1, Vector2Dv1 v2, Color color, int size){
            this.size = size;
            p1 = new org.opencv.core.Point((int)v1.x*ADJUST, (int)v1.y*ADJUST);
            p2 = new org.opencv.core.Point((int)v2.x*ADJUST, (int)v2.y*ADJUST);
            this.color = new Scalar(color.getBlue(), color.getGreen(), color.getRed());
        }

    }

    public class GuiPixel{

        public int x, y;

        public Color color;

        public GuiPixel(int x, int y, Color color){
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public int getX(){ return x;}
        public int getY(){ return y;}
        public Color getColor(){ return color;}

        public Vector2Dv1 getVector(){ return new Vector2Dv1(x,y);}

    }

    public void Draw(GuiCircle c, boolean update){
        Imgproc.circle(mat, c.pos, c.radius, c.color, c.size);
        if(update)
            update(this.mat);
    }

    public void Draw(GuiLine l, boolean update){
        Imgproc.line(mat, l.p1, l.p2, l.color, l.size);
        if(update)
            update(this.mat);
    }

    public void update(){ update(mat); }

    public GuiPixel getPixel(int x, int y){
        Color c = new Color(bufferedImage.getRGB(x, y));
        if (x % 2 < 0)
            x--;
        x /= 2;
        if (y % 2 < 0)
            y--;
        y /= 2;
        return new GuiPixel(x,y, c);
    }
    public GuiImage clone() {
        return new GuiImage(mat);
    }
}
