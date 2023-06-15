package Gui;

import Gui.Image.GuiImage;
import misc.Boundry;
import misc.Cross;
import misc.Vector2Dv1;
import misc.ball.BallClassifierPhaseTwo;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class GUI_Menu {
    private static int WIDTH = 1000;
    private static int HEIGHT = 550;

    public static ArrayList<Vector2Dv1> boundryPos = null;
    public static ArrayList<Vector2Dv1> crossPos = null;
    public static ArrayList<Vector2Dv1> balls = null;
    public static ArrayList<Vector2Dv1> caliPos = null;
    public static ArrayList<Color> robotColor = null;
    public static ArrayList<Vector2Dv1> robotPos = null;

    private static ImageClick clicker = null;

    private static GuiImage image;

    public GUI_Menu(Mat m, ArrayList<Color> rc, ArrayList<Vector2Dv1> bp, ArrayList<Vector2Dv1> cp, ArrayList<Vector2Dv1> balls, ArrayList<Vector2Dv1> calip, ArrayList<Vector2Dv1> rPos){
        image = new GuiImage(m);
        clicker = new ImageClick(image);
        robotColor = rc;
        boundryPos = bp;
        crossPos = cp;
        this.balls = balls;
        caliPos = calip;
        robotPos = rPos;
        setUpMenu();
    }

    public static void setUpMenu(){
        JFrame jFrame = new JFrame("Calibrate menu");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Panel
        JPanel jPanel = new JPanel();
        jPanel.setSize(WIDTH,HEIGHT);
        jPanel.setLayout(new GridLayout(6,1));


        // Boundary corners
        JLabel labelCorner = new JLabel("Corner set to", SwingConstants.LEFT);
        JTable tableConer = new JTable(getCornerInfo() , new String[] {"Corner","X position","Y position"});
        JScrollPane spConrner = new JScrollPane(tableConer);
        JButton buttonCorner = new JButton("Set corners");
        // TODO add height and width
        jPanel.add(labelCorner);
        jPanel.add(spConrner);
        jPanel.add(buttonCorner);

        // Cross
        JLabel labelCross = new JLabel("Cross set to", SwingConstants.LEFT);
        JTable tableCross = new JTable(getCrossInfo() , new String[] {"Point","X position","Y position"});
        JScrollPane spCross = new JScrollPane(tableCross);
        JButton buttonCross = new JButton("Set Cross");
        // TODO add height and width
        jPanel.add(labelCross);
        jPanel.add(spCross);
        jPanel.add(buttonCross);

        // Balls
        JLabel labelBalls = new JLabel("Balls set to", SwingConstants.LEFT);
        JTable tableBalls = new JTable(getBallsInfo() , new String[] {"Ball","X position","Y position"});
        JScrollPane spBalls = new JScrollPane(tableBalls);
        JButton buttonBalls = new JButton("Set Balls");
        jPanel.add(labelBalls);
        jPanel.add(spBalls);
        jPanel.add(buttonBalls);

        // Robot colors
        JLabel labelRobot = new JLabel("Robot set to", SwingConstants.LEFT);
        JTable tableRobot = new JTable(getRobotInfo() , new String[] {"Ball vector","X position","Y position"});
        JScrollPane spRobot = new JScrollPane(tableRobot);
        JButton buttonRobot = new JButton("Set Robot");
        jPanel.add(labelRobot);
        jPanel.add(spRobot);
        jPanel.add(buttonRobot);
        // Calibrate colors
        JLabel labelCali = new JLabel("Calibrate to", SwingConstants.LEFT);
        JTable tableCali = new JTable(getCaliInfo() , new String[] {"Height","X","Y"});
        JScrollPane spCali = new JScrollPane(tableCali);
        JButton buttonCali = new JButton("Calibrate");
        jPanel.add(labelCali);
        jPanel.add(spCali);
        jPanel.add(buttonCali);

        // Completed setup
        JButton buttonCompleted = new JButton("Setup completed");
        jPanel.add(buttonCompleted);


        // button ActionListener
        buttonCorner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                boundryPos.clear();
                clicker.run("Set boundry corners", 4, boundryPos, c, tableConer, false);
            }
        });
        buttonCross.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                crossPos.clear();
                clicker.run("Set Cross", 4, crossPos, c, tableCross, false);
            }
        });
        buttonBalls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                balls.clear();
                clicker.run("Set balls(orange first)", 11, balls, c, tableBalls, false);
            }
        });
        buttonRobot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                robotPos.clear();
                clicker.run("Choose robot", 2, robotPos, c, tableRobot, false);
            }
        });
        buttonCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        buttonCali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                caliPos.clear();
                clicker.run( "Choose cali blok posses", 2, caliPos, c, tableCali, false);
            }
        });


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

    public static String[][] getCrossInfo(){
        String[][] data = {
                {"Cross middle",String.valueOf(crossPos.get(0).x),String.valueOf(crossPos.get(0).x)},
                {"Cross top",String.valueOf(crossPos.get(1).x),String.valueOf(crossPos.get(1).x)},
                {"Cross corner middle",String.valueOf(crossPos.get(2).x),String.valueOf(crossPos.get(2).x)},
                {"Cross corner top",String.valueOf(crossPos.get(3).x),String.valueOf(crossPos.get(3).x)}
        };
        return data;
    }

    public static String[][] getBallsInfo(){
        String[][] data = {
                {"Ball 1(Orange)",String.valueOf(balls.get(0).x),String.valueOf(balls.get(0).x)},
                {"Ball 2",String.valueOf(balls.get(1).x),String.valueOf(balls.get(1).x)},
                {"Ball 3",String.valueOf(balls.get(2).x),String.valueOf(balls.get(2).x)},
                {"Ball 4",String.valueOf(balls.get(3).x),String.valueOf(balls.get(3).x)},
                {"Ball 5",String.valueOf(balls.get(4).x),String.valueOf(balls.get(4).x)},
                {"Ball 6",String.valueOf(balls.get(5).x),String.valueOf(balls.get(5).x)},
                {"Ball 7",String.valueOf(balls.get(6).x),String.valueOf(balls.get(6).x)},
                {"Ball 8",String.valueOf(balls.get(7).x),String.valueOf(balls.get(7).x)},
                {"Ball 9",String.valueOf(balls.get(8).x),String.valueOf(balls.get(8).x)},
                {"Ball 10",String.valueOf(balls.get(9).x),String.valueOf(balls.get(9).x)},
                {"Ball 11",String.valueOf(balls.get(10).x),String.valueOf(balls.get(10).x)}
        };
        return data;
    }

    public static String[][] getRobotInfo(){
        String[][] data = {
                {"Black",String.valueOf(robotPos.get(0).x),String.valueOf(robotPos.get(0).y)},
                {"Green",String.valueOf(robotPos.get(1).x),String.valueOf(robotPos.get(1).y)}
        };
        return data;
    }
    public static String[][] getCaliInfo(){
        String[][] data = {
                {"Low",String.valueOf(caliPos.get(0).x),String.valueOf(caliPos.get(0).y)},
                {"High",String.valueOf(caliPos.get(1).x),String.valueOf(caliPos.get(1).y)}
        };
        return data;
    }
}
