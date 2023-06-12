package Gui;

import misc.Cross;
import misc.Vector2Dv1;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI_Menu {

    private static ArrayList<Vector2Dv1> boundryPos = null;

    private static ArrayList<Vector2Dv1> crossPos = null;
    private static ArrayList<Vector2Dv1> balls = null;
    private static ArrayList<Color> robotColor = null;

    private static GuiData guiData = null;


    private static Mat image;

    public GUI_Menu(Mat m, ArrayList<Color> rc, ArrayList<Vector2Dv1> bp, ArrayList<Vector2Dv1> cp, ArrayList<Vector2Dv1> balls, GuiData gd){
        image = m;
        robotColor = rc;
        boundryPos = bp;
        crossPos = cp;
        this.balls = balls;
        guiData = gd;
        setUpMenu();
    }
    /*public static void main(String[] args) {
        setUpMenu();
    }*/
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

        // button ActionListener
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                boundryPos.clear();
                new ImageClick(4, image, "Choose boundry corners", boundryPos, c);
            }
        });/*
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                crossPos.clear();
                new ImageClick(4, image, "Choose Cross", crossPos, c);
            }
        });
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                balls.clear();
                new ImageClick(4, image, "Choose balls(orange first)", balls, c);
            }
        });
        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Vector2Dv1> v = new ArrayList<>();
                robotColor.clear();
                new ImageClick(4, image, "Choose robot colors", v, robotColor);
            }
        });*/
        //Show jFrame
        jFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        jFrame.setVisible(true);


    }
}
