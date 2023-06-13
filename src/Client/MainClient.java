package Client;


import Gui.GUI_Menu;
import Gui.GuiData;
import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.Boundry;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import misc.ball.PrimitiveBall;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import routePlaner.Route;
import routePlaner.RoutePlanerFaseTwo;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MainClient {

    private static PrintWriter out;
    private static BufferedReader in;


    public static void main(String[] args) throws IOException, TypeException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.err.println("lib loaded");
        ImgRecFaseTwo imgRec = new ImgRecFaseTwo();

        ArrayList<Ball> balls = new ArrayList<>();
        RoutePlanerFaseTwo routePlanerFaseTwo = null;


        Mat m = imgRec.getFrame();

        // init balls for robot, to not have exception..
        Ball initBall = new Ball(0,0,0, BallClassifierPhaseTwo.BLACK,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);
        Ball initBall2 = new Ball(1,1,0,BallClassifierPhaseTwo.GREEN,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);
        Robotv1 robotv1 = new Robotv1(0,0,new Vector2Dv1(1,1));
        balls = imgRec.captureBalls();
        BallStabilizerPhaseTwo stabilizer = new BallStabilizerPhaseTwo();
        ArrayList<Ball> robotBalls = new ArrayList<>();
        try {
            robotBalls = stabilizer.getStabelRobotCirce();
        } catch (BadDataException e) {
            robotBalls.add(initBall);
            robotBalls.add(initBall2);
        }
        robotv1.updatePos(robotBalls.get(0), robotBalls.get(1));


        //vars for GUI
        //getting info from pic reg and
        ArrayList<Vector2Dv1> boundryConorsGUI = new ArrayList<>();
        boundryConorsGUI.add(new Vector2Dv1(imgRec.imgRecObstacle.boundry.points.get(0).x, imgRec.imgRecObstacle.boundry.points.get(0).y));
        boundryConorsGUI.add(new Vector2Dv1(imgRec.imgRecObstacle.boundry.points.get(1).x, imgRec.imgRecObstacle.boundry.points.get(1).y));
        boundryConorsGUI.add(new Vector2Dv1(imgRec.imgRecObstacle.boundry.points.get(2).x, imgRec.imgRecObstacle.boundry.points.get(2).y));
        boundryConorsGUI.add(new Vector2Dv1(imgRec.imgRecObstacle.boundry.points.get(3).x, imgRec.imgRecObstacle.boundry.points.get(3).y));


        ArrayList<Vector2Dv1> crossPosGUI = new ArrayList<>();
        crossPosGUI.add(new Vector2Dv1(imgRec.imgRecObstacle.cross.pos.x,imgRec.imgRecObstacle.cross.pos.y));
        crossPosGUI.add(imgRec.imgRecObstacle.cross.vec);

        ArrayList<Vector2Dv1> ballsGUI = new ArrayList<>();

        //find orange ball
        int ballI = 0;
        for (; ballI < 11 ; ballI++) {
            Ball b = balls.get(ballI);
            if(b.getColor() == BallClassifierPhaseTwo.ORANGE){
                break;
            }
        }

        //orange first in list
        Ball ballO = balls.get(ballI);
        Ball ball1 = balls.get(0);
        if(ballO != ball1){
            balls.set(0, ballO);
            balls.set(ballI, ball1);
        }
        //add position to ballsGUI
        for (Ball b: balls) {
            ballsGUI.add(b.getPosVector());
        }

        //choose predefined colors
        ArrayList<Color> robotColorsGUI = new ArrayList<>();
        robotColorsGUI.add(Color.BLACK);
        robotColorsGUI.add(Color.GREEN);

        //??
        GuiData gd = new GuiData();
        gd.boundryHeight = 122;
        gd.boundryLength = 161;
        gd.crossLength = 1;
        gd.robotLength = 1;

        //height of box
        ArrayList<Vector2Dv1> caliGUI = new ArrayList<>();
        caliGUI.add(new Vector2Dv1(1,1));
        caliGUI.add(new Vector2Dv1(1,1));

        //call interface
        new GUI_Menu(m, robotColorsGUI, boundryConorsGUI, crossPosGUI, ballsGUI, gd, caliGUI);

        System.out.println("Press enter to end config!");
        Scanner inputWaitConfig = new Scanner(System.in);
        inputWaitConfig.nextLine();


        //boundery corner

        //cross mid and direction
        //cross length of side





        stabilizer.stabilizeBalls(balls);
        ArrayList<Ball> routeBalls = new ArrayList<>();

        try {
            ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
            System.out.println("balls1 = " + balls1);
            for (Ball ball : balls1) {
                BallClassifierPhaseTwo.ballSetPlacement(ball, imgRec.imgRecObstacle.boundry,imgRec.imgRecObstacle.cross);
                //BallClassifierPhaseTwo.ballSetPlacement(ball, boundryConorsGUI,imgRec.imgRecObstacle.cross);
                System.out.println(ball.toString());
                routeBalls.add(ball);
            }
            //robotBalls = stabilizer.getStabelRobotCirce();
        } catch (NoDataException e) {
            throw new RuntimeException(e);

        }

        routePlanerFaseTwo = new RoutePlanerFaseTwo(robotv1, routeBalls, imgRec.imgRecObstacle.boundry, imgRec.imgRecObstacle.cross);
        System.out.println(routeBalls);
        System.out.println("Mapping route...");
        routePlanerFaseTwo.getHeats();
        System.out.println("Mapping route complete!");

        Socket s = new Socket("192.168.1.102",6666);
        System.err.println("Wating on server...");

        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        System.out.println("Robot pos = \t" + robotv1.getPosVector().toString());


        System.out.println();

        System.out.println("Press enter to start!");


        Scanner inputWait = new Scanner(System.in);
        inputWait.nextLine();

        routePlanerFaseTwo.run(out, in, imgRec, stabilizer);
    }
}

