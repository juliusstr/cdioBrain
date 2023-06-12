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
    private static int WIDTH = 500;
    private static int HEIGHT = 500;

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
    public static void setUpMenu(){
        JFrame jFrame = new JFrame("Calibrate menu");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Panel
        JPanel jPanel = new JPanel();
        jPanel.setSize(WIDTH,HEIGHT);
        jPanel.setLayout(new GridLayout(5,1));

        //Labels
        JLabel labelCorners = new JLabel("Corner calibrate to", SwingConstants.LEFT);

        // Tables
        String[][] data = getCornerInfo();
        String[] columnNames = {"Corner","X position","Y position"};
        JTable jTableCorner = new JTable(data,columnNames);
        JScrollPane sp=new JScrollPane(jTableCorner);

        //Buttons
        JButton jButton1 = new JButton("Choose corners Button");

        //add labels
        jPanel.add(labelCorners);
        jPanel.add(sp);

        //add buttons
        jPanel.add(jButton1);



        // button ActionListener
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                boundryPos.clear();
                new ImageClick(4, image, "Choose boundry corners", boundryPos, c);
            }
        });
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                boundryPos.clear();
                new ImageClick(4, image, "Choose boundry corners", boundryPos, c);
            }
        });
        /*
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
        jFrame.add(jPanel);

        jFrame.setSize(WIDTH,HEIGHT);
        jFrame.setVisible(true);


    }

    public static String[][] getCornerInfo(){
        String[][] data = {
                {"Corner A",String.valueOf(boundryPos.get(0).x),String.valueOf(boundryPos.get(0).x)},
                {"Corner B",String.valueOf(boundryPos.get(1).x),String.valueOf(boundryPos.get(1).x)},
                {"Corner C",String.valueOf(boundryPos.get(2).x),String.valueOf(boundryPos.get(2).x)},
                {"Corner D",String.valueOf(boundryPos.get(3).x),String.valueOf(boundryPos.get(3).x)}
        };
        return data;
    }
}
