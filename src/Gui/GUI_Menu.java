package Gui;

import Gui.Image.GuiImage;
import misc.Boundry;
import misc.Cross;
import misc.Vector2Dv1;
import misc.ball.BallClassifierPhaseTwo;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class GUI_Menu {
    private static int WIDTH = 500;
    private static int HEIGHT = 750;

    public static ArrayList<Vector2Dv1> boundryPos = null;
    public static ArrayList<Vector2Dv1> rBalls = null;
    public static ArrayList<Vector2Dv1> crossPos = null;
    public static ArrayList<Vector2Dv1> balls = null;
    public static ArrayList<Vector2Dv1> caliPos = null;
    public static ArrayList<Color> robotColor = null;
    public static ArrayList<Vector2Dv1> robotPos = null;

    private static ImageClick clicker = null;

    private static GuiImage image;

    public GUI_Menu(Mat m, ArrayList<Color> rc, ArrayList<Vector2Dv1> bp, ArrayList<Vector2Dv1> cp, ArrayList<Vector2Dv1> balls, ArrayList<Vector2Dv1> calip, ArrayList<Vector2Dv1> rPos, ArrayList<Vector2Dv1> rballs){
        image = new GuiImage(m);
        clicker = new ImageClick(image);
        robotColor = rc;
        boundryPos = bp;
        crossPos = cp;
        this.balls = balls;
        caliPos = calip;
        robotPos = rPos;
        this.rBalls = rballs;
        setUpMenu();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("test_img/WIN_20230315_10_32_53_Pro.jpg");
        image = new GuiImage(tImage);
        clicker = new ImageClick(image);
        robotColor = new ArrayList<>();
        robotColor.add(Color.BLACK);
        robotColor.add(Color.GREEN);
        boundryPos = new ArrayList<>();
        boundryPos.add(new Vector2Dv1(0,0));
        boundryPos.add(new Vector2Dv1(0,0));
        boundryPos.add(new Vector2Dv1(0,0));
        boundryPos.add(new Vector2Dv1(0,0));
        crossPos = new ArrayList<>();
        crossPos.add(new Vector2Dv1(0,0));
        crossPos.add(new Vector2Dv1(0,0));
        crossPos.add(new Vector2Dv1(0,0));
        crossPos.add(new Vector2Dv1(0,0));
        balls = new ArrayList<>();
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        balls.add(new Vector2Dv1(0,0));
        caliPos = new ArrayList<>();
        caliPos.add(new Vector2Dv1(0,0));
        caliPos.add(new Vector2Dv1(0,0));
        robotPos = new ArrayList<>();
        robotPos.add(new Vector2Dv1(0,0));
        robotPos.add(new Vector2Dv1(0,0));
        rBalls = new ArrayList<>();
        rBalls.add(new Vector2Dv1(0,0));
        rBalls.add(new Vector2Dv1(0,0));
        rBalls.add(new Vector2Dv1(0,0));
        rBalls.add(new Vector2Dv1(0,0));

        setUpMenu();
    }

    public static void setUpMenu(){
        JFrame jFrame = new JFrame("Calibrate menu");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(WIDTH,HEIGHT);
        mainPanel.setLayout(new GridLayout(6,1));


        // Boundary corners
        JPanel cornerPanel = new JPanel();
        cornerPanel.setSize(WIDTH,HEIGHT/6);
        cornerPanel.setLayout(new BorderLayout());

        JLabel labelCorner = new JLabel("Calibrate boundary corners:", SwingConstants.LEFT);
        JTable tableConer = new JTable(getCornerInfo() , new String[] {"Corner","X position","Y position"});
        JScrollPane spConrner = new JScrollPane(tableConer);
        JButton buttonCorner = new JButton("Calibrate corners");
        // TODO add height and width
        cornerPanel.add(labelCorner, BorderLayout.PAGE_START);
        cornerPanel.add(spConrner, BorderLayout.CENTER);
        cornerPanel.add(buttonCorner, BorderLayout.LINE_END);
        mainPanel.add(cornerPanel);

        // Cross
        JPanel crossPanel = new JPanel();
        crossPanel.setSize(WIDTH,HEIGHT/6);
        crossPanel.setLayout(new BorderLayout());

        JLabel labelCross = new JLabel("Calibrate Cross: ", SwingConstants.LEFT);
        JTable tableCross = new JTable(getCrossInfo() , new String[] {"Point","X position","Y position"});
        JScrollPane spCross = new JScrollPane(tableCross);
        JButton buttonCross = new JButton("Calibrate Cross");
        // TODO add height and width
        crossPanel.add(labelCross, BorderLayout.PAGE_START);
        crossPanel.add(spCross, BorderLayout.CENTER);
        crossPanel.add(buttonCross, BorderLayout.LINE_END);
        mainPanel.add(crossPanel);

        // Balls
        JPanel ballsPanel = new JPanel();
        ballsPanel.setSize(WIDTH,HEIGHT/6);
        ballsPanel.setLayout(new BorderLayout());

        JLabel labelBalls = new JLabel("Calibrate Balls: ", SwingConstants.LEFT);
        JTable tableBalls = new JTable(getBallsInfo() , new String[] {"Ball","X position","Y position"});
        JScrollPane spBalls = new JScrollPane(tableBalls);
        JButton buttonBalls = new JButton("Calibrate Balls");
        ballsPanel.add(labelBalls, BorderLayout.PAGE_START);
        ballsPanel.add(spBalls, BorderLayout.CENTER);
        ballsPanel.add(buttonBalls, BorderLayout.LINE_END);
        mainPanel.add(ballsPanel);

        // Robot pos
        JPanel robotPanel = new JPanel();
        robotPanel.setSize(WIDTH,HEIGHT/6);
        robotPanel.setLayout(new BorderLayout());

        JLabel labelRobot = new JLabel("Calibrate Robot:", SwingConstants.LEFT);
        JTable tableRobot = new JTable(getRobotInfo() , new String[] {"Ball vector","X position","Y position"});
        JScrollPane spRobot = new JScrollPane(tableRobot);
        JButton buttonRobot = new JButton("Calibrate Robot");
        robotPanel.add(labelRobot, BorderLayout.PAGE_START);
        robotPanel.add(spRobot, BorderLayout.CENTER);
        robotPanel.add(buttonRobot, BorderLayout.LINE_END);
        mainPanel.add(robotPanel);

        // Set req balls
        JPanel rballsPanel = new JPanel();
        rballsPanel.setSize(WIDTH,HEIGHT/6);
        rballsPanel.setLayout(new BorderLayout());
        JPanel rballsBtnPanel = new JPanel();
        rballsBtnPanel.setSize(WIDTH,(HEIGHT/12));
        rballsBtnPanel.setLayout(new GridLayout(2,2));

        JLabel labelRBalls = new JLabel("Set required balls", SwingConstants.LEFT);
        JButton buttonRBalls1 = new JButton("Set required 1 ball");
        JButton buttonRBalls2 = new JButton("Set required 2 ball");
        JButton buttonRBalls3 = new JButton("Set required 3 ball");
        JButton buttonRBalls4 = new JButton("Set required 4 ball");
        rballsPanel.add(labelRBalls, BorderLayout.PAGE_START);
        rballsBtnPanel.add(buttonRBalls1);
        rballsBtnPanel.add(buttonRBalls2);
        rballsBtnPanel.add(buttonRBalls3);
        rballsBtnPanel.add(buttonRBalls4);
        rballsPanel.add(rballsBtnPanel, BorderLayout.CENTER);
        mainPanel.add(rballsPanel);

        // Completed setup
        JPanel endPanel = new JPanel();
        endPanel.setSize(WIDTH,HEIGHT/6);
        endPanel.setLayout(new BorderLayout());

        JButton buttonCompleted = new JButton("Setup completed");
        endPanel.add(buttonCompleted);
        mainPanel.add(endPanel);

        // button ActionListener
        buttonCorner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                boundryPos.clear();
                clicker.run("Calibrate boundary corners", 4, boundryPos, c, tableConer, false);
            }
        });
        buttonCross.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                crossPos.clear();
                clicker.run("Calibrate Cross", 4, crossPos, c, tableCross, false);
            }
        });
        buttonBalls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                balls.clear();
                clicker.run("Calibrate balls(orange first)", 11, balls, c, tableBalls, false, balls);
            }
        });
        buttonRobot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                robotPos.clear();
                clicker.run("Calibrate robot", 2, robotPos, c, tableRobot, false);
            }
        });
        buttonCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        buttonRBalls1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                rBalls.clear();
                rBalls.add(new Vector2Dv1(0,0));
                rBalls.add(new Vector2Dv1(0,0));
                rBalls.add(new Vector2Dv1(0,0));
                clicker.run( "Set 1 required balls", 1, rBalls, c, false, balls);
            }
        });
        buttonRBalls2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                rBalls.clear();
                rBalls.add(new Vector2Dv1(0,0));
                rBalls.add(new Vector2Dv1(0,0));
                clicker.run( "Set 2 required balls", 2, rBalls, c, false, balls);
            }
        });
        buttonRBalls3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                rBalls.clear();
                rBalls.add(new Vector2Dv1(0,0));
                clicker.run( "Set 3 required balls", 3, rBalls, c, false, balls);
            }
        });
        buttonRBalls4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Color> c = new ArrayList<>();
                rBalls.clear();
                clicker.run( "Set 4 required balls", 4, rBalls, c, false, balls);
            }
        });


        //Show jFrame
        jFrame.add(mainPanel);

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
                {"Ball 1(Orange)",String.valueOf(balls.get(0).x),String.valueOf(balls.get(0).y)},
                {"Ball 2",String.valueOf(balls.get(1).x),String.valueOf(balls.get(1).y)},
                {"Ball 3",String.valueOf(balls.get(2).x),String.valueOf(balls.get(2).y)},
                {"Ball 4",String.valueOf(balls.get(3).x),String.valueOf(balls.get(3).y)},
                {"Ball 5",String.valueOf(balls.get(4).x),String.valueOf(balls.get(4).y)},
                {"Ball 6",String.valueOf(balls.get(5).x),String.valueOf(balls.get(5).y)},
                {"Ball 7",String.valueOf(balls.get(6).x),String.valueOf(balls.get(6).y)},
                {"Ball 8",String.valueOf(balls.get(7).x),String.valueOf(balls.get(7).y)},
                {"Ball 9",String.valueOf(balls.get(8).x),String.valueOf(balls.get(8).y)},
                {"Ball 10",String.valueOf(balls.get(9).x),String.valueOf(balls.get(9).y)},
                {"Ball 11",String.valueOf(balls.get(10).x),String.valueOf(balls.get(10).y)}
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
