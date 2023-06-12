package Test.Gui;

import Gui.GUI_Menu;
import Gui.GuiData;
import misc.Vector2Dv1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Gui.ImageClick;
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
        ImageIcon tImage = new ImageIcon("C:\\Users\\John\\Desktop\\input1.jpg");
        Mat mat = imageIconToMat(tImage);
        ArrayList<Vector2Dv1> boundryConorsGUI = new ArrayList<>();
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        boundryConorsGUI.add(new Vector2Dv1(1,1));
        ArrayList<Vector2Dv1> crossPosGUI = new ArrayList<>();
        ArrayList<Vector2Dv1> ballsGUI = new ArrayList<>();
        ArrayList<Color> robotColorsGUI = new ArrayList<>();
        GuiData gd = new GuiData();
        new GUI_Menu(mat, robotColorsGUI, boundryConorsGUI, crossPosGUI, ballsGUI, gd);
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
        }
    }
    private static Mat imageIconToMat(ImageIcon imageIcon) {
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
