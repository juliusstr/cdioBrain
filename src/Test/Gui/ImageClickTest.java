package Test.Gui;

import Gui.Pixel;
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
    void getTest(){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ArrayList<Pixel> testArray = new ArrayList<>();
        ImageIcon tImage = new ImageIcon("C:\\Users\\John\\Desktop\\input1.jpg");
        Mat mat = imageIconToMat(tImage);

        ImageClick test = new ImageClick(4, mat, "Test", testArray);
        test.run();
        while(testArray.size() < 4){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(testArray.size());
        }
        System.out.println("Test");
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
