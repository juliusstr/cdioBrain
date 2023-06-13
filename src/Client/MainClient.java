package Client;


import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import misc.ball.PrimitiveBall;
import org.opencv.core.Core;
import routePlaner.Route;
import routePlaner.RoutePlanerFaseTwo;

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

        // init balls for robot, to not have exception..
        Ball initBall = new Ball(0,0,0, BallClassifierPhaseTwo.BLACK,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);
        Ball initBall2 = new Ball(1,1,0,BallClassifierPhaseTwo.GREEN,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);

        Robotv1 robotv1 = new Robotv1(0,0,new Vector2Dv1(1,1));
        balls = imgRec.captureBalls();
        BallStabilizerPhaseTwo stabilizer = new BallStabilizerPhaseTwo();
        stabilizer.stabilizeBalls(balls);
        ArrayList<Ball> robotBalls = new ArrayList<>();
        ArrayList<Ball> routeBalls = new ArrayList<>();
        try {
            ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
            System.out.println("balls1 = " + balls1);
            for (Ball ball : balls1) {
                BallClassifierPhaseTwo.ballSetPlacement(ball, imgRec.imgRecObstacle.boundry,imgRec.imgRecObstacle.cross);
                System.out.println(ball.toString());
                routeBalls.add(ball);
            }
            //robotBalls = stabilizer.getStabelRobotCirce();
        } catch (NoDataException e) {
            throw new RuntimeException(e);

        }
        try {
            robotBalls = stabilizer.getStabelRobotCirce();
        } catch (BadDataException e) {
            robotBalls.add(initBall);
            robotBalls.add(initBall2);
        }


        robotv1.updatePos(robotBalls.get(0), robotBalls.get(1));
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

