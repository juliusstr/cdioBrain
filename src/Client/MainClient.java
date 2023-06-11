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
import routePlaner.RoutePlanerFaseTwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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

        Socket s = new Socket("192.168.1.102",6666);

        System.err.println("Wating on server");
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String greeting;
        String respons = "NA";

        balls = imgRec.captureBalls();
        BallStabilizerPhaseTwo stabilizer = new BallStabilizerPhaseTwo();
        //clear the balls, to get the new list from the stabilizer
        stabilizer.stabilizeBalls(balls);

        ArrayList<Ball> robotBalls = new ArrayList<>();
        ArrayList<Ball> routeBalls = new ArrayList<>();
        try {
            ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
            for (Ball ball : balls1) {
                routeBalls.add(ball);
            }
            //robotBalls = stabilizer.getStabelRobotCirce();
        } catch (NoDataException e) {
            //throw new RuntimeException(e);

        }
        robotBalls.add(initBall);
        robotBalls.add(initBall2);

        robotv1.updatePos(robotBalls.get(0), robotBalls.get(1));
        routePlanerFaseTwo = new RoutePlanerFaseTwo(robotv1, routeBalls, imgRec.imgRecObstacle.boundry, imgRec.imgRecObstacle.cross);

        do {
            greeting = in.readLine();
            if (greeting != null)
                System.out.println("in: " + greeting);
            if ("Got it".equals(greeting)) { //todo lave så vi ikke venter på robot før vi beregner næste

                balls = imgRec.captureBalls();
                stabilizer.stabilizeBalls(balls);
                routeBalls.clear();

                try {
                    ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
                    for (Ball ball : balls1) {
                        routeBalls.add(ball);
                    }
                    robotBalls = stabilizer.getStabelRobotCirce();
                    robotv1.updatePos(robotBalls.get(0), robotBalls.get(1));
                    respons = routePlanerFaseTwo.nextCommand();
                } catch (NoDataException e) {
                    respons = "stop -d -t";
                } catch (IndexOutOfBoundsException e){
                    respons = "turn -r -s0.02";
                } catch (BadDataException e) {
                    respons = "stop -d -t";
                }


                out.println(respons);
                System.out.println("sendt : \"" + respons + "\" end.");
                if(respons.contains("collect")){
                    System.out.println("Sleeping...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Continue");
                }
                /*
                System.err.println("robot pos: " + robotv1.getPosVector());
                System.err.println("robot dir: " + robotv1.getPosVector());
                 */
            } else {
                out.println("unrecognised greeting");
            }
        } while (!respons.equals("exit"));
    }
}

