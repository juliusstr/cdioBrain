package Test.Gui;

import misc.Vector2Dv1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Gui.ImageClickPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ImageClickPositionTest {

    @Test
    @DisplayName("test")
    void getTest(){
        ArrayList<Vector2Dv1> testArray = new ArrayList<>();
        ImageIcon tImage = new ImageIcon("C:\\Users\\John\\Desktop\\input1.jpg");
        ImageClickPosition test = new ImageClickPosition(4, tImage, "Test", testArray);
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
}
