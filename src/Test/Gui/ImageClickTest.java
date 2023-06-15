package Test.Gui;

import Gui.GUI_Menu;
import misc.Boundry;
import misc.Cross;
import misc.Vector2Dv1;
import misc.ball.BallClassifierPhaseTwo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

public class ImageClickTest {

    @Test
    @DisplayName("test")
    void menuTest(){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("C:\\Users\\charl\\IdeaProjects\\cdioBrain\\test_img\\colorpickerTest.JPEG");
        Mat mat = imageIconToMat(tImage);

        ArrayList<Vector2Dv1> boundryConorsGUI = new ArrayList<>();
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));

        ArrayList<Vector2Dv1> crossPosGUI = new ArrayList<>();
        crossPosGUI.add(new Vector2Dv1(1,1));
        crossPosGUI.add(new Vector2Dv1(1,1));
        crossPosGUI.add(new Vector2Dv1(1,1));
        crossPosGUI.add(new Vector2Dv1(1,1));

        ArrayList<Vector2Dv1> ballsGUI = new ArrayList<>();
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));
        ballsGUI.add(new Vector2Dv1(1,1));

        ArrayList<Color> robotColorsGUI = new ArrayList<>();
        robotColorsGUI.add(Color.BLACK);
        robotColorsGUI.add(Color.GREEN);
        ArrayList<Vector2Dv1> robotPos = new ArrayList<>();
        robotPos.add(new Vector2Dv1(1,1));
        robotPos.add(new Vector2Dv1(1,1));
        System.out.println("1. sort " + BallClassifierPhaseTwo.BLACK + "\n");
        System.out.println("1. green" + BallClassifierPhaseTwo.GREEN + "\n");
        GuiData gd = new GuiData();

        ArrayList<Vector2Dv1> caliGUI = new ArrayList<>();
        caliGUI.add(new Vector2Dv1(1,1));
        caliGUI.add(new Vector2Dv1(1,1));

        new GUI_Menu(mat, robotColorsGUI, boundryConorsGUI, crossPosGUI, ballsGUI, gd,caliGUI,robotPos);

        Boundry boundry = new Boundry(boundryConorsGUI);
        Cross cross = new Cross(boundryConorsGUI.get(0), boundryConorsGUI.get(1));



        /*Robot Robot = new Robot();
        Boundry boundry = new Boundry();

        robotColorsGUI.addAll(robotColorsGUI);*/
        //ArrayList<Ball> routeBalls = new ArrayList<>();





        while(boundryConorsGUI.size() < 5){
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(boundryConorsGUI.size());
        }
        System.out.println(boundryConorsGUI);
    }
    @Test
    @DisplayName("test")
    void getTest(){
/*
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ArrayList<Vector2Dv1> v = new ArrayList<>();
        ArrayList<Color> c = new ArrayList<>();
        ImageIcon tImage = new ImageIcon("C:\\Users\\John\\Desktop\\input1.jpg");
        Mat mat = imageIconToMat(tImage);

        ImageClick test = new ImageClick(4, mat, "Test", v, c);

        while(v.size() < 4){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(v.size());
        }*/
    }
    public static Mat imageIconToMat(ImageIcon imageIcon) {
        // Convert ImageIcon to BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                imageIcon.getIconWidth(),
                imageIcon.getIconHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );
        Image image = imageIcon.getImage();
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

        // Convert BufferedImage to Mat
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);

        return mat;
    }
}
