package Client;

import exceptions.NoGoodCircleData;
import imageRecognition.ImgRecFaseTwo;
import misc.Ball;
import misc.BallStabilizer;
import misc.Robot;
import misc.Vector2D;
import org.opencv.core.Core;
import routePlaner.RoutePlanerFaseOne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainClient {

    private static PrintWriter out;
    private static BufferedReader in;


    public static void main(String[] args) throws IOException, InterruptedException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.err.println("lib loaded");
        ImgRecFaseTwo imgRec = new ImgRecFaseTwo();

        List<Ball> balls = new ArrayList<>();
        RoutePlanerFaseOne routePlanerFaseOne = new RoutePlanerFaseOne();

        Ball initBall = new Ball(0,0,0,null,false);

        Robot robot = new Robot(0,0,new Vector2D(1,1));

        routePlanerFaseOne.setRobot(robot);

        Socket s = new Socket("192.168.1.102",6666);

        System.err.println("Wating on server");
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String greeting;
        String respons = "NA";

        balls = imgRec.captureBalls();
        BallStabilizer stabilizer = new BallStabilizer();
        routePlanerFaseOne.clearBalls();
        stabilizer.addBalls(balls);
        try{
            for (Ball ball :
                    stabilizer.getStableBalls()) {
                routePlanerFaseOne.addBallToList(ball);
            }
            ArrayList<Ball> roBall = stabilizer.getStableRobotCircels();
            robot.updatePos(roBall.get(0), roBall.get(1));
        } catch (NoGoodCircleData e){}
        routePlanerFaseOne.addBallToList(initBall);
        routePlanerFaseOne.init();

        do {
            greeting = in.readLine();
            if (greeting != null)
                System.out.println("in: " + greeting);
            if ("Got it".equals(greeting)) {

                balls = imgRec.captureBalls();
                stabilizer.addBalls(balls);
                routePlanerFaseOne.clearBalls();
                try {

                    for (Ball ball :
                            stabilizer.getStableBalls()) {
                        routePlanerFaseOne.addBallToList(ball);
                    }
                    ArrayList<Ball> roBall = stabilizer.getStableRobotCircels();
                    robot.updatePos(roBall.get(0), roBall.get(1));
                    respons = routePlanerFaseOne.nextCommand();
                } catch (NoGoodCircleData e){}

                out.println(respons);
                System.out.println("sendt : \"" + respons + "\" end.");
            } else {
                out.println("unrecognised greeting");
            }
        } while (!respons.equals("exit"));
    }
}

