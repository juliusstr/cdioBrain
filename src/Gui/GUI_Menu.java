package Gui;

import javax.swing.*;
import java.awt.*;

public class GUI_Menu {

    public static void main(String[] args) {
        setUpMenu();
    }
    public static void setUpMenu(){
        JFrame jFrame = new JFrame("Calibrate menu");
        jFrame.setSize(400,400);


        //Labels
        JLabel labelCorners = new JLabel("Corner calibrate Label");


        //Buttons
        JButton jButton1 = new JButton("Choose corners Button");
        jButton1.setBounds(50,50, 95,30);

        //add text fields
        jFrame.add(labelCorners);

        //add buttons
        jFrame.add(jButton1);


        //Show jFrame
        jFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        jFrame.setVisible(true);


    }
}
