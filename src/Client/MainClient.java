package Client;


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
import routePlaner.RoutePlanerFaseOne;

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
        RoutePlanerFaseOne routePlanerFaseOne = new RoutePlanerFaseOne();

        Ball initBall = new Ball(0,0,0, BallClassifierPhaseTwo.BLACK,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UKNOWN);
        Ball initBall2 = new Ball(1,1,0,BallClassifierPhaseTwo.RED,false, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UKNOWN);

        Robotv1 robotv1 = new Robotv1(0,0,new Vector2Dv1(1,1));

        routePlanerFaseOne.setRobot(robotv1);

        Socket s = new Socket("192.168.1.102",6666);

        System.err.println("Wating on server");
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String greeting;
        String respons = "NA";

        balls = imgRec.captureBalls();
        BallStabilizerPhaseTwo stabilizer = new BallStabilizerPhaseTwo();
        routePlanerFaseOne.clearBalls();
        stabilizer.stabilizeBalls(balls);

        ArrayList<Ball> roBall = new ArrayList<>();
        try {
            ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
            for (Ball ball : balls1) {
                routePlanerFaseOne.addBallToList(ball);
            }
            //roBall = stabilizer.getStabelRobotCirce();
        } catch (NoDataException e) {
            //throw new RuntimeException(e);

        }
        roBall.add(initBall);
        roBall.add(initBall2);

        robotv1.updatePos(roBall.get(0), roBall.get(1));

        routePlanerFaseOne.addBallToList(initBall);
        routePlanerFaseOne.init();

        do {
            greeting = in.readLine();
            if (greeting != null)
                System.out.println("in: " + greeting);
            if ("Got it".equals(greeting)) {

                balls = imgRec.captureBalls();
                stabilizer.stabilizeBalls(balls);
                routePlanerFaseOne.clearBalls();

                try {
                    ArrayList<Ball> balls1 = stabilizer.getStabelBalls();
                    for (Ball ball : balls1) {
                        routePlanerFaseOne.addBallToList(ball);
                    }
                    roBall = stabilizer.getStabelRobotCirce();
                    robotv1.updatePos(roBall.get(0), roBall.get(1));
                    respons = routePlanerFaseOne.nextCommand();
                } catch (NoDataException e) {
                    respons = "stop -d -t";
                } catch (IndexOutOfBoundsException e){
                    respons = "turn -r -s0.02";
                }



                out.println(respons);
                System.out.println("sendt : \"" + respons + "\" end.");
            } else {
                out.println("unrecognised greeting");
            }
        } while (!respons.equals("exit"));
    }
}

