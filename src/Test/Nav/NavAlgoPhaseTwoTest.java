package Test.Nav;

import exceptions.NoHitException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import nav.NavAlgoPhaseTwo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavAlgoPhaseTwoTest {
    Robotv1 simulationRobot;
    Ball target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;
    String nextCommand;
    public static final double ANGLE_ERROR = 0.04;
    public static final double DISTANCE_ERROR = 20;
    double currentSpeed;
    double currentTurnSpeed;
    String currentTurnDirection;

    @BeforeEach
    void setUp(){
        simulationRobot = new Robotv1(100, 360/2, new Vector2Dv1(1, 1));
        cross = new Cross(new Vector2Dv1(640/2, 360/2), new Vector2Dv1(0.0));
        ArrayList<Vector2Dv1> boundryList = new ArrayList<>();
        boundryList.add(new Vector2Dv1(30,20));
        boundryList.add(new Vector2Dv1(30, 340));
        boundryList.add(new Vector2Dv1(610, 20));
        boundryList.add(new Vector2Dv1(610, 340));
        boundry = new Boundry(boundryList);
        target = new Ball(400, 360/2, 4, new Color(1,1,1), true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UKNOWN);
    }

    @Test
    @DisplayName("Hit on cross")
    void simpelHitOnCrossTest(){
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        try {
            navPlanner.nextCommand();
        } catch (NoHitException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @DisplayName("simulate one ball")
    void simpelCollectTest(){
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        double distanceToBall = Math.sqrt(Math.pow((target.getxPos()- simulationRobot.getxPos()), 2)+Math.pow((target.getyPos()- simulationRobot.getyPos()), 2));
        while(distanceToBall > DISTANCE_ERROR){
            try {
                nextCommand = navPlanner.nextCommand();
            } catch (NoHitException e) {
                throw new RuntimeException(e);
            }
            currentSpeed = extractSpeed(nextCommand);
            currentTurnSpeed = extractTurnSpeed(nextCommand);
            currentTurnDirection = extractTurnDirection(nextCommand);
            //simulationRobot.updatePos();
            // TODO @Ulleren lav noget matematik så du får opdateret position ud fra turnspeed speed osv..
        }
    }

    public static double extractSpeed(String command) {
        String[] parts = command.split(";");
        String driveCommand = "";

        for (String part : parts) {
            if (part.startsWith("drive")) {
                driveCommand = part;
                break;
            }
        }

        String[] driveTokens = driveCommand.split(" ");
        for (String token : driveTokens) {
            if (token.startsWith("-s")) {
                String speedString = token.substring(2);
                return Double.parseDouble(speedString);
            }
        }

        return 0.0; // Default speed if not found
    }

    public static double extractTurnSpeed(String command) {
        String[] parts = command.split(";");
        String turnCommand = "";

        for (String part : parts) {
            if (part.startsWith("turn")) {
                turnCommand = part;
                break;
            }
        }

        String[] turnTokens = turnCommand.split(" ");
        for (String token : turnTokens) {
            if (token.startsWith("-s")) {
                String turnSpeedString = token.substring(2);
                return Double.parseDouble(turnSpeedString);
            }
        }

        return 0.0; // Default turn speed if not found
    }
    public static String extractTurnDirection(String command) {
        String[] parts = command.split(";");
        String turnCommand = "";

        for (String part : parts) {
            if (part.startsWith("turn")) {
                turnCommand = part;
                break;
            }
        }

        String[] turnTokens = turnCommand.split(" ");
        for (String token : turnTokens) {
            if (token.equals("l")) {
                return "Left";
            } else if (token.equals("r")) {
                return "Right";
            }
        }

        return ""; // Default turn direction if not found
    }

}


