package Test.Gui;

import Gui.DataView;
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

public class dataViewTest {

    @Test
    @DisplayName("test")
    void getTest(){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("test_img/WIN_20230315_10_32_53_Pro.jpg");
        Mat mat = imageIconToMat(tImage);

        DataView test = new DataView(mat, new ArrayList<>(), null, null);
        do{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }while(true);
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
