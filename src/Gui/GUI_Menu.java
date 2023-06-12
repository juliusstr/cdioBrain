package Gui;

import javax.swing.*;
import java.awt.*;


public class GUI_Menu {
    private static int WIDTH = 500;
    private static int HEIGHT = 500;

    public static void main(String[] args) {
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
        //JLabel labelCorners = new JLabel("Corner calibrate to", SwingConstants.LEFT);

        // Tables
        String[][] data = getCornerInfo();
        String[] columnNames = {"Corner","X position","Y position"};
        JTable jTableCorner = new JTable(data,columnNames);
        //jTableCorner.setSize(400,400);

        JScrollPane sp=new JScrollPane(jTableCorner);
        //System.out.println(jTableCorner.getColumnName(0));
        //System.out.println(sp.getColumnHeader());
        //sp.setViewportView(jTableCorner);
        //jPanel.add(sp);
        jFrame.add(sp);




        //Buttons
        JButton jButton1 = new JButton("Choose corners Button");
        //jButton1.setBounds(50,100,95,30);



        //add labels
        //jPanel.add(labelCorners);

        jPanel.add(jTableCorner);

        //add buttons
        //jButton1.setVerticalAlignment(JButton.CENTER);
        //jFrame.add(jButton1);
        jPanel.add(jButton1);




        //Show jFrame
        jFrame.add(jPanel);

        jFrame.setSize(WIDTH,HEIGHT);
        jFrame.setVisible(true);


    }

    public static String[][] getCornerInfo(){
        String[][] data = {
                {"Corner A","1","2"},
                {"Corner B","3","4"},
                {"Corner C","5","6"},
                {"Corner D","7","8"}
        };
        return data;
    }
}
