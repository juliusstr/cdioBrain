package routePlaner;

import Client.StandardSettings;
import Gui.Image.GuiImage;
import Gui.ImageClick;
import Gui.LiveView;
import Gui.RouteView;
import exceptions.BadDataException;
import exceptions.NoRouteException;
import exceptions.NoWaypointException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.*;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import nav.CommandGenerator;
import nav.WaypointGenerator;
import org.opencv.core.Mat;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static Client.StandardSettings.ANGLE_ERROR;

public class RoutePlanerFaseTwo {
    private ArrayList<Ball> balls;
    public ArrayList<Ball> ballsHeat1 = null;
    public ArrayList<Ball> ballsHeat2 = null;
    public ArrayList<Ball> ballsHeat3 = null;
    public static ArrayList<Ball> ballsAllRun = null;
    private Robotv1 robot = null;
    public Ball goalFakeBall = null;
    private Mat justInCase = null;
    Cross cross;
    public Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

    public RoutExecute routExecuter;

    /**
     * Sets the boundary for the route planner.
     *
     * @param b The boundary to set.
     */
    public void setBoundry(Boundry b){
        this.boundry = b;
    }
    /**
     * Sets the cross for the route planner.
     *
     * @param c The cross to set.
     */
    public void setCross(Cross c){
        this.cross = c;
    }
    /**
     * Gets the goal waypoint for the specified index.
     *
     * @param i The index of the goal waypoint.
     * @return The goal waypoint vector.
     */

    /**
     * Gets the list of balls.
     *
     * @return The list of balls.
     */
    public ArrayList<Ball> getBalls() {
        return balls;
    }
    /**
     * Sets the list of balls.
     *
     * @param balls The list of balls to set.
     */
    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }
    /**
     * Initializes a new instance of the RoutePlanerFaseTwo class.
     *
     * @param r       The robot for the route planner.
     * @param b       The list of balls for the route planner.
     * @param boundry The boundary for the route planner.
     * @param c       The cross for the route planner.
     */
    public RoutePlanerFaseTwo(Robotv1 r, ArrayList<Ball> b, Boundry boundry, Cross c) {
        balls = (ArrayList<Ball>) b.clone();
        robot = r;
        cross = c;
        this.boundry = boundry;
        boundry.initGoalWaypoints();
        goalFakeBall = new Ball(boundry.goalWaypoint0);
    }

    public void setImage(Mat mat){
        justInCase = mat;
    }

    /**
     * Calculates the heats for the balls.
     * This method calculates the routes for three different heats based on the balls' positions.
     * The calculated heats are stored in separate lists.
     */
    public void getHeats(ArrayList<Ball> req){
        ballsHeat1 = new HeatGenerator(balls, robot, robot.getPosVector(), boundry, cross, goalFakeBall, 1, justInCase, req, true).getHeat();
        for (Ball b: ballsHeat1) {
            balls.remove(b);
        }
        try {
            BallClassifierPhaseTwo.ballSetPlacement(balls, boundry, cross);
        } catch (NoWaypointException e) {
            throw new RuntimeException(e);
        }
        ballsHeat2 = new HeatGenerator(balls, robot, goalFakeBall.getPosVector(), boundry, cross, goalFakeBall, 2, justInCase).getHeat();
        for (Ball b: ballsHeat2) {
            balls.remove(b);
        }
        try {
            BallClassifierPhaseTwo.ballSetPlacement(balls, boundry, cross);
        } catch (NoWaypointException e) {
            throw new RuntimeException(e);
        }
        ballsHeat3 = new HeatGenerator(balls, robot, goalFakeBall.getPosVector(), boundry, cross, goalFakeBall, 3, justInCase).getHeat();
        for (Ball b: ballsHeat3) {
            balls.remove(b);
        }
        ballsAllRun = new ArrayList<>();
        ballsAllRun.addAll(ballsHeat1);
        ballsAllRun.addAll(ballsHeat2);
        ballsAllRun.addAll(ballsHeat3);
        try {
            BallClassifierPhaseTwo.ballSetPlacement(ballsAllRun, boundry, cross);
        } catch (NoWaypointException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the goal waypoint 0.
     *
     * @return The goal waypoint 0 as a Vector2Dv1 object.
     */


    /**
     * Executes the main run logic for the robot.
     * Performs the following steps:
     * 1. Prints the heats information.
     * 2. Prepares a list of balls to avoid during navigation.
     * 3. Iterates over the heat1 balls and performs the following sub-steps:
     * a. Finds the route from the robot to the ball.
     * b. Runs to the ball using waypoint navigation and captures ball images.
     * c. Collects the ball if it is in a free placement.
     * d. Updates the lastBall variable.
     * 4. Navigates to the goal and performs a drop-off.
     * 5. Iterates over the heat2 balls and performs the same sub-steps as in step 3.
     * 6. Navigates to the goal again and performs a drop-off.
     * 7. Iterates over the heat3 balls and performs the same sub-steps as in step 3.
     * 8. Navigates to the goal again and performs a drop-off.
     *
     * @param out        PrintWriter object for sending commands.
     * @param in         BufferedReader object for receiving responses.
     * @param imgRec     ImgRecFaseTwo object for capturing ball images.
     * @param stabilizer BallStabilizerPhaseTwo object for stabilizing balls.
     */
    public void run(PrintWriter out, BufferedReader in, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer) {
        System.out.println("heats : " + ballsHeat1);
        ArrayList<Ball> ballsToAvoid = new ArrayList<>();
        ballsToAvoid.addAll(ballsHeat1);
        ballsToAvoid.addAll(ballsHeat2);
        ballsToAvoid.addAll(ballsHeat3);
        WaypointGenerator waypointGenerator;
        Ball lastBall = null;
        LiveView lv = new LiveView(imgRec.getFrame(), robot);
        routExecuter = new RoutExecute(out, in, robot, cross, boundry);

        routExecuter.heatRunner(ballsHeat1, 1, imgRec, stabilizer, ballsToAvoid);
        routExecuter.heatRunner(ballsHeat2, 2, imgRec, stabilizer, ballsToAvoid);
        routExecuter.heatRunner(ballsHeat3, 3, imgRec, stabilizer, ballsToAvoid);

    }
}