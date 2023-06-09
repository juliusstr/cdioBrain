package Client;


import Gui.DataView;
import Gui.GUI_Menu;
import Gui.Image.GuiImage;
import Gui.LiveView;
import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.NoWaypointException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import imageRecognition.ImgRecV2;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import misc.ball.PrimitiveBall;
import nav.WaypointGenerator;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import routePlaner.HeatGenerator;
import routePlaner.RoutExecute;
import routePlaner.RoutePlanerFaseTwo;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainClient {

    private static PrintWriter out;
    private static BufferedReader in;


    public static void main(String[] args) throws IOException, TypeException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.err.println("lib loaded");
        ImgRecFaseTwo imgRec = new ImgRecFaseTwo();

        ArrayList<Ball> balls = new ArrayList<>();
        RoutePlanerFaseTwo routePlanerFaseTwo = null;

        Mat m = imgRec.frameGUI;

        // init balls for robot, to not have exception..

        balls = imgRec.captureBalls();
        BallStabilizerPhaseTwo stabilizer = new BallStabilizerPhaseTwo();
        stabilizer.stabilizeBalls(balls);
        ArrayList<Ball> realballs = new ArrayList<>();
        try {
            realballs = stabilizer.getStabelBalls();
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }


        //todo add balls if less then 11 / remove if needed
        while (realballs.size() < 11){
            realballs.add(new Ball(0,0,StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL));
        }
        boolean orangeInBalls = false;
        for (Ball ball :
                realballs) {
            if(ball.getColor().equals(BallClassifierPhaseTwo.ORANGE)){
                orangeInBalls = true;
                break;
            }
        }
        if(!orangeInBalls)
            realballs.get(0).setColor(BallClassifierPhaseTwo.ORANGE);


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
        //todo remove idf needed
        crossPosGUI.add(new Vector2Dv1(1,1));
        crossPosGUI.add(new Vector2Dv1(2,2));

        ArrayList<Vector2Dv1> ballsGUI = new ArrayList<>();

        //find orange ball
        int ballI = 0;
        for (; ballI < 11 ; ballI++) {
            Ball b = realballs.get(ballI);
            if(b.getColor().equals(BallClassifierPhaseTwo.ORANGE)){
                break;
            }
        }

        //orange first in list
        Ball ballO = realballs.get(ballI);
        Ball ball1 = realballs.get(0);
        if(ballO != ball1){
            realballs.set(0, ballO);
            realballs.set(ballI, ball1);
        }
        //add position to ballsGUI
        for (Ball b: realballs) {
            ballsGUI.add(b.getPosVector());
        }

        //choose predefined colors
        ArrayList<Color> robotColorsGUI = new ArrayList<>();
        robotColorsGUI.add(Color.BLACK);
        robotColorsGUI.add(Color.GREEN);

        //height of box
        ArrayList<Vector2Dv1> caliGUI = new ArrayList<>();
        caliGUI.add(new Vector2Dv1(1,1));
        caliGUI.add(new Vector2Dv1(1,1));

        Ball initBall = new Ball(0,0,0, BallClassifierPhaseTwo.BLACK,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.ROBOT_BACK);
        Ball initBall2 = new Ball(1,1,0,BallClassifierPhaseTwo.GREEN,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.ROBOT_FRONT);
        try {
            ArrayList<Ball> roball = stabilizer.getStabelRobotCirce();
            if(roball.get(0).getType() == Ball.Type.ROBOT_BACK){
                initBall = roball.get(0);
                initBall2 = roball.get(1);
            } else {
                initBall2 = roball.get(0);
                initBall = roball.get(1);
            }
        } catch (BadDataException e) {

        }
        Robotv1 robotv1 = new Robotv1(0,0,new Vector2Dv1(1,1));
        robotv1.updatePos(initBall,initBall2);

        ArrayList<Vector2Dv1> robotPos = new ArrayList<>();
        robotPos.add(initBall.getPosVector());
        robotPos.add(initBall2.getPosVector());

        ArrayList<Vector2Dv1> reqBalls = new ArrayList<>();
        reqBalls.add(new Vector2Dv1(0,0));
        reqBalls.add(new Vector2Dv1(0,0));
        reqBalls.add(new Vector2Dv1(0,0));
        reqBalls.add(new Vector2Dv1(0,0));

        //call interface
        new GUI_Menu(m, robotColorsGUI, boundryConorsGUI, crossPosGUI, ballsGUI, caliGUI, robotPos, reqBalls);
        System.out.println("Press enter to end config!");
        Scanner inputWaitConfig = new Scanner(System.in);
        inputWaitConfig.nextLine();


        //inserts clicked data to right places
        imgRec.imgRecObstacle.boundry = new Boundry((ArrayList<Vector2Dv1>) GUI_Menu.boundryPos.clone());
        imgRec.imgRecObstacle.cross = new Cross(GUI_Menu.crossPos);
        balls.clear();
        balls.add(new Ball(GUI_Menu.balls.get(0),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL));
        for (int i = 1 ; i < GUI_Menu.balls.size(); i++) {
            balls.add(new Ball(GUI_Menu.balls.get(i),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL));
        }
        //imgRec.imgRecObstacle.cross = new Cross()


        //stabilizer.stabilizeBalls(balls);
        try {
            BallClassifierPhaseTwo.ballSetPlacement(balls, imgRec.imgRecObstacle.boundry,imgRec.imgRecObstacle.cross);
        } catch (NoWaypointException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Ball> routeBalls = new ArrayList<>();
        ArrayList<Ball> balls1 = balls;
        System.out.println("balls1 = " + balls1);
        for (Ball ball : balls1) {
            System.out.println(ball.toString());
            routeBalls.add(ball);
        }
        //robotBalls = stabilizer.getStabelRobotCirce();


        Ball greenBall = new Ball((int) GUI_Menu.robotPos.get(1).x, (int) GUI_Menu.robotPos.get(1).y,0, BallClassifierPhaseTwo.GREEN,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.ROBOT_FRONT);
        Ball blackBall = new Ball((int) GUI_Menu.robotPos.get(0).x, (int) GUI_Menu.robotPos.get(0).y,0, BallClassifierPhaseTwo.BLACK,true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.ROBOT_BACK);


        //robotv1.setScale(GUI_Menu.caliPos.get(0), GUI_Menu.caliPos.get(1));
        robotv1.updatePos(greenBall, blackBall);


        System.out.println("\n-----------------------\nInfo to make sim test from");
        System.out.println("simulationRobot = new Robotv1(" + robotv1.getxPos() + ", " + robotv1.getyPos() +  ", new Vector2Dv1(" + robotv1.getPosVector().getAngle() + "));");
        System.out.println("cross = new Cross(new Vector2Dv1(" + crossPosGUI.get(0).x + "," + crossPosGUI.get(0).y + "), new Vector2Dv1(" + crossPosGUI.get(1).x + "," + crossPosGUI.get(1).y + "));");
        System.out.println("boundryList.add(new Vector2Dv1(" + GUI_Menu.boundryPos.get(0).x + "," + GUI_Menu.boundryPos.get(0).y + "));");
        System.out.println("boundryList.add(new Vector2Dv1(" + GUI_Menu.boundryPos.get(1).x + "," + GUI_Menu.boundryPos.get(1).y + "));");
        System.out.println("boundryList.add(new Vector2Dv1(" + GUI_Menu.boundryPos.get(2).x + "," + GUI_Menu.boundryPos.get(2).y + "));");
        System.out.println("boundryList.add(new Vector2Dv1(" + GUI_Menu.boundryPos.get(3).x + "," + boundryConorsGUI.get(3).y + "));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(0).x + "," + ballsGUI.get(0).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(1).x + "," + ballsGUI.get(1).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(2).x + "," + ballsGUI.get(2).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(3).x + "," + ballsGUI.get(3).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(4).x + "," + ballsGUI.get(4).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(5).x + "," + ballsGUI.get(5).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(6).x + "," + ballsGUI.get(6).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(7).x + "," + ballsGUI.get(7).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(8).x + "," + ballsGUI.get(8).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(9).x + "," + ballsGUI.get(9).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");
        System.out.println("ball_list.add(new Ball(new Vector2Dv1(" + ballsGUI.get(10).x + "," + ballsGUI.get(10).y + "),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));");

        System.out.println();


        ArrayList<Ball> req_balls = new ArrayList<>();
        for (Vector2Dv1 v: GUI_Menu.rBalls) {
            System.out.println("req ball: "+ v.x + ", " + v.y);
            if(imgRec.imgRecObstacle.boundry.vectorInsideBoundary(v)) {
                Ball clostest = null;
                double closeDis = 0;
                for (Ball b : routeBalls) {
                    if (req_balls.contains(b))
                        continue;
                    double curDis = sqrt((pow((v.x - b.getxPos()), 2) + pow((v.y - b.getyPos()), 2)));
                    if (curDis < 0)
                        curDis *= -1;
                    if (clostest == null) {
                        clostest = b;
                        closeDis = curDis;
                    } else if (closeDis > curDis) {
                        clostest = b;
                        closeDis = curDis;
                    }
                }
                req_balls.add(clostest);
                System.out.println("req ball: "+ clostest.getxPos() + ", " + clostest.getyPos());
            }
        }
        DataView lv = new DataView(imgRec, routeBalls, robotv1, req_balls, stabilizer);
        lv.start();

        routePlanerFaseTwo = new RoutePlanerFaseTwo(robotv1, routeBalls, imgRec.imgRecObstacle.boundry, imgRec.imgRecObstacle.cross);
        routePlanerFaseTwo.setImage(m);

        System.out.println(routeBalls);
        System.out.println("Mapping route...");
        routePlanerFaseTwo.getHeats(req_balls);
        System.out.println("Mapping route complete!");

        System.out.println();

        System.out.println("\nPress enter to Connect to server/robot:");
        Scanner inputWait = new Scanner(System.in);
        inputWait.nextLine();

        Socket s = new Socket("192.168.1.102",6666);
        System.err.println("\nWating on server...");

        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        System.out.println("Robot pos = \t" + robotv1.getPosVector().toString());
        System.out.println("\nPress enter to start!");
        inputWait.nextLine();

        System.out.println("Starting run...");
        routePlanerFaseTwo.run(out, in, imgRec, stabilizer, lv);

        //#############################  FINAL ROUND  ############################################
        while(true){
            try {
                ArrayList<Ball> heat_list = new ArrayList<>();
                balls.clear();
                balls = imgRec.captureBalls();
                try {
                    stabilizer.stabilizeBalls(balls);
                } catch (TypeException e) {
                    throw new RuntimeException(e);
                }
                try {
                    ArrayList<Ball> robotBalls = stabilizer.getStabelRobotCirce();
                    robotv1.updatePos(robotBalls.get(0), robotBalls.get(1));
                } catch (BadDataException e) {
                    System.err.println("no robot found");
                }
                routeBalls = new ArrayList<>();
                try {
                    balls1 = stabilizer.getStabelBalls();
                    System.out.println("balls1 = " + balls1);
                    for (Ball ball : balls1) {
                        BallClassifierPhaseTwo.ballSetPlacement(routeBalls, imgRec.imgRecObstacle.boundry, imgRec.imgRecObstacle.cross);
                        System.out.println(ball.toString());
                        routeBalls.add(ball);
                    }
                } catch (NoDataException e) {
                    throw new RuntimeException(e);
                } catch (NoWaypointException e) {
                    e.printStackTrace();
                }
                heat_list = new HeatGenerator(routeBalls, robotv1, robotv1.getPosVector(), imgRec.imgRecObstacle.boundry, imgRec.imgRecObstacle.cross, routePlanerFaseTwo.goalFakeBall, 4, new Mat()).getHeat();
                RoutExecute routExecuter = new RoutExecute(out, in, robotv1, imgRec.imgRecObstacle.cross, imgRec.imgRecObstacle.boundry);
                routExecuter.setLiveView(lv);
                routExecuter.heatRunner(heat_list, 4, imgRec, stabilizer, routeBalls);
            } catch (Exception j){

            }
        }
    }
}

