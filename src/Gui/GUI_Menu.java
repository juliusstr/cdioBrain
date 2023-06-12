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

    private static Integer boundryLong = 0;
    private static Integer boundryShort = 0;
    private static ArrayList<Vector2Dv1> crossPos = null;
    private static Integer crossLenght = 0;
    private static ArrayList<Vector2Dv1> balls = null;
    private static ArrayList<Color> robotColor = null;

    private static Integer robotLength = 0;

    private static Mat image;
    /*public GUI_Menu(Mat m, ArrayList<Color> rc, Integer rl, ArrayList<Vector2Dv1> bp, Integer bl, Integer bs, ArrayList<Vector2Dv1> cp, Integer cl, ArrayList<Vector2Dv1> balls){
        image = m;
        robotColor = rc;
        robotLength = rl;
        boundryPos = bp;
        boundryLong = bl;
        boundryShort = bs;
        crossPos = cp;
        crossLenght = cl;
        this.balls = balls;
    }*/
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

        // button ActionListener
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Pixel> p = new ArrayList<>();
                new ImageClick(4, image, "Choose boundry corners", p);

                while(p.size() < 4){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        throw new RuntimeException(e2);
                    }
                    System.out.println(p.size());
                }
                boundryPos.clear();
                for (Pixel pixel : p){
                    boundryPos.add(pixel.pos);
                }
            }
        });/*
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Pixel> p = new ArrayList<>();
                new ImageClick(2, image, "Choose Cross", p);

                while(p.size() < 2){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        throw new RuntimeException(e2);
                    }
                    System.out.println(p.size());
                }
                crossPos.clear();
                for (Pixel pixel : p){
                    crossPos.add(pixel.pos);
                }
            }
        });
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Pixel> p = new ArrayList<>();
                new ImageClick(11, image, "Choose balls(orange first)", p);

                while(p.size() < 11){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        throw new RuntimeException(e2);
                    }
                    System.out.println(p.size());
                }
                balls.clear();
                for (Pixel pixel : p){
                    balls.add(pixel.pos);
                }
            }
        });
        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Pixel> p = new ArrayList<>();
                new ImageClick(2, image, "Choose robot colors", p);
                while(p.size() < 2){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        throw new RuntimeException(e2);
                    }
                    System.out.println(p.size());
                }
                robotColor.clear();
                for (Pixel pixel : p){
                    robotColor.add(pixel.color);
                }
            }
        });*/
        //Show jFrame
        jFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        jFrame.setVisible(true);


    }
}
