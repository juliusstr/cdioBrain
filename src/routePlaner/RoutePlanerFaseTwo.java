package routePlaner;

import Client.StandardSettings;
import Gui.ImageClick;
import Gui.RouteView;
import exceptions.BadDataException;
import exceptions.NoRouteException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.*;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import nav.CommandGenerator;
import nav.WaypointGenerator;
import org.opencv.core.Mat;

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

    public void setImage(Mat m){
        justInCase = m;
    }

    /**
     * Calculates the heats for the balls.
     * This method calculates the routes for three different heats based on the balls' positions.
     * The calculated heats are stored in separate lists.
     */
    public void getHeats(){
        int heat = 2;
        ballsHeat1 = new ArrayList<>();
        ballsHeat2 = new ArrayList<>();
        ballsHeat3 = new ArrayList<>();
        ArrayList<Ball> extraBalls = new ArrayList<>();
        ArrayList<Vector2Dv1> manualVec = new ArrayList<>();
        Route robotRoute = null;
        WaypointGenerator.WaypointRoute wrRobot = null;
        ArrayList<Ball> btaRobot = (ArrayList<Ball>) balls.clone();
        //heat 1
        System.out.println("\n-------------" + "\n Calculating Heat1...");
        for (Ball b : balls) {
            if(b.getPlacement() == Ball.Placement.FREE && !b.getColor().equals(BallClassifierPhaseTwo.ORANGE)){
                ballsHeat1.add(b);
                robotRoute = new Route(robot.getPosVector());
                robotRoute.setEnd(b);
                btaRobot.remove(b);
                try {
                    wrRobot = new WaypointGenerator(robot.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                } catch (NoRouteException e) {
                    btaRobot.add(b);
                    continue;
                } catch (TimeoutException e) {
                    btaRobot.add(b);
                    continue;
                }
                robotRoute.setScore(wrRobot.getCost());
                ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                robotRoute.setWaypoints(robotwaypoints);
                robot.addRoute(robotRoute);
                btaRobot.add(b);
            } else if (b.getColor().equals(BallClassifierPhaseTwo.ORANGE)) {
                ballsHeat1.add(b);
            }
        }
        if(ballsHeat1.size() < 4){
            for (Ball b: balls) {
                if(b.getPlacement() == Ball.Placement.PAIR && !b.getColor().equals(BallClassifierPhaseTwo.ORANGE)) {
                    ballsHeat1.add(b);
                    robotRoute = new Route(robot.getPosVector());
                    robotRoute.setEnd(b);
                    btaRobot.remove(b);
                    try {
                        wrRobot = new WaypointGenerator(robot.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                    } catch (NoRouteException e) {
                        btaRobot.add(b);
                        continue;
                    } catch (TimeoutException e) {
                        btaRobot.add(b);
                        continue;
                    }
                    robotRoute.setScore(wrRobot.getCost());
                    ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                    robotRoute.setWaypoints(robotwaypoints);
                    robot.addRoute(robotRoute);
                    btaRobot.add(b);
                }
            }
        }
        if(ballsHeat1.size() < 4){
            for (Ball b: balls) {
                if(b.getPlacement() != Ball.Placement.FREE && b.getPlacement() != Ball.Placement.PAIR && !b.getColor().equals(BallClassifierPhaseTwo.ORANGE)) {
                    ballsHeat1.add(b);
                    robotRoute = new Route(robot.getPosVector());
                    robotRoute.setEnd(b);
                    btaRobot.remove(b);
                    try {
                        wrRobot = new WaypointGenerator(robot.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                    } catch (NoRouteException e) {
                        btaRobot.add(b);
                        continue;
                    } catch (TimeoutException e) {
                        btaRobot.add(b);
                        continue;
                    }
                    robotRoute.setScore(wrRobot.getCost());
                    ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                    robotRoute.setWaypoints(robotwaypoints);
                    robot.addRoute(robotRoute);
                    btaRobot.add(b);
                }
            }
        }
        robot.endHeatRoutes();
        extraBalls.clear();
        extraBalls = (ArrayList<Ball>) ballsHeat1.clone();
        ballsHeat1 = heat1Generator(ballsHeat1);
        if(ballsHeat1.size() < 4){
            System.err.println("No route found!! \nChoose route by hand");
            ballsHeat1.clear();
            manualVec.clear();
            for (Ball b: balls) {
                manualVec.add(b.getPosVector());
            }
            ImageClick ic = new ImageClick(4, justInCase, "Choose a route", manualVec);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();
            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                double closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat1.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = b.getPosVector().distance(v);
                        continue;
                    }
                    double dis = b.getPosVector().distance(v);
                    if(dis < closestDis){
                        closest = b;
                        closestDis = dis;
                    }
                }
                ballsHeat1.add(closest);
            }
            ballsHeat1.get(3).setGoalRoute(new Route(ballsHeat1.get(3).getPosVector()));
            ballsHeat1.get(3).getGoalRoute().setScore(-1);
        }
        System.out.println("\nHeat 1 calculated: \n Possible stating balls: " + robot.getRoutes(1).size() + "\n Total score for Heat: " + ballsHeat1.get(3).getGoalRoute().getScore());
        for (Ball b: ballsHeat1) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor().equals(BallClassifierPhaseTwo.ORANGE) ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            balls.remove(b);
            btaRobot.remove(b);
        }
        if(heat < 2)
            return;
        //heat 2
        if(heat < 2)
            return;
        System.out.println("\n-------------" + "\n Calculating Heat 2...");
        wrRobot = null;
        for (Ball b : balls) {
            if(b.getPlacement() == Ball.Placement.FREE){
                ballsHeat2.add(b);
                robotRoute = new Route(robot.getPosVector());
                robotRoute.setEnd(b);
                btaRobot.remove(b);
                try {
                    wrRobot = new WaypointGenerator(robot.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                } catch (NoRouteException e) {
                    btaRobot.add(b);
                    continue;
                } catch (TimeoutException e) {
                    btaRobot.add(b);
                    continue;
                }
                robotRoute.setScore(wrRobot.getCost());
                ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                robotRoute.setWaypoints(robotwaypoints);
                robot.addRoute(robotRoute);
                btaRobot.add(b);
            }
        }
        if(ballsHeat2.size() < 4){
            for (Ball b: balls) {
                if(b.getPlacement() == Ball.Placement.PAIR) {
                    ballsHeat2.add(b);
                    robotRoute = new Route(goalFakeBall.getPosVector());
                    robotRoute.setEnd(b);
                    btaRobot.remove(b);
                    try {
                        wrRobot = new WaypointGenerator(goalFakeBall.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                    } catch (NoRouteException e) {
                        btaRobot.add(b);
                        continue;
                    } catch (TimeoutException e) {
                        btaRobot.add(b);
                        continue;
                    }
                    robotRoute.setScore(wrRobot.getCost());
                    ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                    robotRoute.setWaypoints(robotwaypoints);
                    robot.addRoute(robotRoute);
                    btaRobot.add(b);
                }
            }
        }
        if(ballsHeat2.size() < 4){
            for (Ball b: balls) {
                if(b.getPlacement() != Ball.Placement.FREE && b.getPlacement() != Ball.Placement.PAIR ) {
                    ballsHeat2.add(b);
                    robotRoute = new Route(goalFakeBall.getPosVector());
                    robotRoute.setEnd(b);
                    btaRobot.remove(b);
                    try {
                        wrRobot = new WaypointGenerator(goalFakeBall.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
                    } catch (NoRouteException e) {
                        btaRobot.add(b);
                        continue;
                    } catch (TimeoutException e) {
                        btaRobot.add(b);
                        continue;
                    }
                    robotRoute.setScore(wrRobot.getCost());
                    ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                    robotRoute.setWaypoints(robotwaypoints);
                    robot.addRoute(robotRoute);
                    btaRobot.add(b);
                }
            }
        }
        robot.endHeatRoutes();
        ballsHeat2 = heat2Generator(ballsHeat2);
        if(ballsHeat2.size() < 4){
            System.err.println("No route found!! \nChoose route by hand");
            ballsHeat2.clear();
            manualVec.clear();
            for (Ball b: balls) {
                manualVec.add(b.getPosVector());
            }
            ImageClick ic = new ImageClick(4, justInCase, "Choose a route", manualVec);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();

            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                double closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat2.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = b.getPosVector().distance(v);
                        continue;
                    }
                    double dis = b.getPosVector().distance(v);
                    if(dis < closestDis){
                        closest = b;
                        closestDis = dis;
                    }
                }
                ballsHeat2.add(closest);
            }
            ballsHeat2.get(3).setGoalRoute(new Route(ballsHeat2.get(3).getPosVector()));
            ballsHeat2.get(3).getGoalRoute().setScore(-1);
        }
        System.out.println("\nHeat 2 calculated: \n Possible stating balls: " + robot.getRoutes(2).size() + "\n Total score for Heat: " + ballsHeat2.get(3).getGoalRoute().getScore());
        for (Ball b: ballsHeat2) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor().equals(BallClassifierPhaseTwo.ORANGE) ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            balls.remove(b);
            btaRobot.remove(b);
        }
        if(heat < 3)
            return;
        //heat 3
        System.out.println("\n-------------" + "\n Calculating Heat 3...");
        wrRobot = null;
        for (Ball b : balls) {
            ballsHeat3.add(b);
            robotRoute = new Route(goalFakeBall.getPosVector());
            robotRoute.setEnd(b);
            btaRobot.remove(b);
            try {
                wrRobot = new WaypointGenerator(goalFakeBall.getPosVector(), b.getPickUpPoint(), cross, boundry, btaRobot).waypointRoute;
            } catch (NoRouteException e) {
                btaRobot.add(b);
                continue;
            } catch (TimeoutException e) {
                btaRobot.add(b);
                continue;
            }
            robotRoute.setScore(wrRobot.getCost());
            ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
            robotRoute.setWaypoints(robotwaypoints);
            robot.addRoute(robotRoute);
            btaRobot.add(b);
        }
        robot.endHeatRoutes();
        ballsHeat3 = heat3Generator(ballsHeat3);
        if(ballsHeat3.size() < 3){
            System.err.println("No route found!! \nChoose route by hand");
            ballsHeat3.clear();
            manualVec.clear();
            manualVec.add(balls.get(0).getPosVector());
            manualVec.add(balls.get(1).getPosVector());
            manualVec.add(balls.get(2).getPosVector());
            ImageClick ic = new ImageClick(3, justInCase, "Choose a route", manualVec);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();
            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                double closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat3.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = b.getPosVector().distance(v);
                        continue;
                    }
                    double dis = b.getPosVector().distance(v);
                    if(dis < closestDis){
                        closest = b;
                        closestDis = dis;
                    }
                }
                ballsHeat3.add(closest);
            }
            ballsHeat3.get(2).setGoalRoute(new Route(ballsHeat3.get(2).getPosVector()));
            ballsHeat3.get(2).getGoalRoute().setScore(-1);
        }
        System.out.println("\nHeat 3 calculated: \n Possible stating balls: " + robot.getRoutes(3).size() + "\n Total score for Heat: " + ballsHeat3.get(2).getGoalRoute().getScore());
        for (Ball b: ballsHeat3) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor().equals(BallClassifierPhaseTwo.ORANGE) ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            balls.remove(b);
            btaRobot.remove(b);
        }
    }

    /**
     * Generates a heat 1 configuration by finding the best combination of balls based on scores.
     *
     * @param ball_list The list of balls to generate the configuration from.
     * @return An ArrayList containing the best combination of balls for the heat 1 configuration.
     */
    public ArrayList<Ball> heat1Generator(ArrayList<Ball> ball_list) {

        //NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();
        ArrayList<Ball> best_heat = new ArrayList<>();

        double score1 = 0;
        double score2 = 0;
        double temp_score = 0;
        double best_score = -1;
        Ball orangeBall = null;
        boolean orange_flag = true;
        // find orange ball
        if(orange_flag){
            for (Ball b: ball_list) {
                if(b.getColor().equals(BallClassifierPhaseTwo.ORANGE)){
                    orangeBall = b;
                    break;
                }
            }
            ball_list.remove(orangeBall);
        }
        int iFree = 0;
        int iPair = 0;
        int iDiff = 0;
        for (Ball b: ball_list) {
            if(b.getPlacement() == Ball.Placement.FREE)
                iFree++;
        }
        if(iFree < 3){
            for (Ball b: ball_list) {
                if(b.getPlacement() == Ball.Placement.PAIR)
                    iPair++;
            }
        }
        iDiff = iFree+iPair;
        for (Ball b1: ball_list) {
            if(iFree > 0 && b1.getPlacement() != Ball.Placement.FREE)
                if(iDiff > 0 && b1.getPlacement() != Ball.Placement.PAIR)
                    continue;
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(1)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
            bta.remove(b1);
            if(best_score < score1 && best_score > 0)
                continue;
            for (Ball b2 : ball_list) {
                if(iFree > 0 && b2.getPlacement() != Ball.Placement.FREE)
                    if(iDiff > 0 && b2.getPlacement() != Ball.Placement.PAIR)
                        continue;
                if(b2 == b1 || b2 == orangeBall)
                    continue;
                bta.remove(b2);
                WaypointGenerator.WaypointRoute wrgoal = null;
                try {
                    wrgoal = new WaypointGenerator(b2.getPickUpPoint(), b1.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                } catch (NoRouteException e) {
                    continue;
                } catch (TimeoutException e) {
                    continue;
                }
                score2 = wrgoal.getCost();
                if(best_score < score1 + score2 && best_score > 0)
                    continue;
                for (Ball b3: ball_list) {
                    //Ball b3 = r3.getEnd();
                    if(iFree > 0 && b3.getPlacement() != Ball.Placement.FREE)
                        if(iDiff > 0 && b3.getPlacement() != Ball.Placement.PAIR)
                            continue;
                    if(b3 == b2 || b3 == b1 || b3 == orangeBall)
                        continue;
                    temp_score = score1 + score2;
                    bta.remove(b3);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(b3.getPickUpPoint(), b2.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    temp_score += wrgoal.getCost();

                    if(best_score < temp_score && best_score > 0)
                        continue;
                    bta.remove(orangeBall);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(orangeBall.getPickUpPoint(), b3.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    temp_score += wrgoal.getCost();

                    if(best_score < temp_score && best_score > 0)
                        continue;
                    Route goal = new Route(orangeBall.getPickUpPoint());
                    goal.setEnd(goalFakeBall);
                    ArrayList<Ball> btaGoal = (ArrayList<Ball>) balls.clone();
                    btaGoal.remove(b1);
                    btaGoal.remove(b2);
                    btaGoal.remove(b3);
                    btaGoal.remove(orangeBall);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(goalFakeBall.getPosVector(), orangeBall.getPickUpPoint(), cross, boundry, btaGoal).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    goal.setScore(wrgoal.getCost());
                    ArrayList<Vector2Dv1> goalwaypoints = wrgoal.getRoute();
                    goal.setWaypoints(goalwaypoints);
                    temp_score += goal.getScore();
                    // Set best_heat and best_score
                    if(best_score < 0 || best_score > temp_score){
                        goal.setScore(temp_score);
                        orangeBall.setGoalRoute(goal);
                        best_heat.clear();
                        best_heat.add(b1);
                        best_heat.add(b2);
                        best_heat.add(b3);
                        best_heat.add(orangeBall);
                        best_score = temp_score;
                    }
                    bta.add(b3);
                    bta.add(orangeBall);
                }
                bta.add(b2);
            }
        }
        return best_heat;
    }

    /**
     * Generates a heat 2 configuration by finding the best combination of balls based on scores.
     *
     * @param ball_list The list of balls to generate the configuration from.
     * @return An ArrayList containing the best combination of balls for the heat 2 configuration.
     */
    public ArrayList<Ball> heat2Generator(ArrayList<Ball> ball_list) {

        //NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();
        ArrayList<Ball> best_heat = new ArrayList<>();

        double score1 = 0;
        double score2 = 0;
        double score3 = 0;
        double score4 = 0;
        double temp_score = 0;
        double best_score = -1;
        int iFree = 0;
        int iPair = 0;
        int iDiff = 0;
        for (Ball b: ball_list) {
            if(b.getPlacement() == Ball.Placement.FREE)
                iFree++;
        }
        if(iFree < 3){
            for (Ball b: ball_list) {
                if(b.getPlacement() == Ball.Placement.PAIR)
                    iPair++;
            }
        }
        iDiff = iPair+iFree;
        for (Ball b1: ball_list) {
            if(iFree > 0 && b1.getPlacement() != Ball.Placement.FREE)
                if(iDiff > 0 && b1.getPlacement() != Ball.Placement.PAIR)
                    continue;
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(2)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
            bta.remove(b1);
            if(best_score < score1 && best_score > 0)
                continue;
            for (Ball b2 :ball_list) {
                if(iFree > 1 && b2.getPlacement() != Ball.Placement.FREE)
                    if(iDiff > 1 && b2.getPlacement() != Ball.Placement.PAIR)
                        continue;
                if(b2 == b1)
                    continue;
                bta.remove(b2);
                WaypointGenerator.WaypointRoute wrgoal = null;
                try {
                    wrgoal = new WaypointGenerator(b2.getPickUpPoint(), b1.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                } catch (NoRouteException e) {
                    continue;
                } catch (TimeoutException e) {
                    continue;
                }
                score2 = wrgoal.getCost();
                if(best_score < score1 + score2 && best_score > 0)
                    continue;
                for (Ball b3: ball_list) {
                    if(iFree > 2 && b3.getPlacement() != Ball.Placement.FREE)
                        if(iDiff > 2 && b3.getPlacement() != Ball.Placement.PAIR)
                            continue;
                    if(b3 == b2 || b3 == b1)
                        continue;
                    bta.remove(b3);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(b3.getPickUpPoint(), b2.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    score3 = wrgoal.getCost();
                    if(best_score < score1 + score2 + score3 && best_score > 0)
                        continue;
                    for (Ball b4: ball_list) {
                        if(iFree > 3 && b4.getPlacement() != Ball.Placement.FREE)
                            if(iDiff > 3 && b4.getPlacement() != Ball.Placement.PAIR)
                                continue;
                        if(b4 == b3 || b4 == b2 || b4 == b1)
                            continue;
                        bta.remove(b4);
                        wrgoal = null;
                        try {
                            wrgoal = new WaypointGenerator(b4.getPickUpPoint(), b3.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                        } catch (NoRouteException e) {
                            continue;
                        } catch (TimeoutException e) {
                            continue;
                        }
                        score4 = wrgoal.getCost();
                        if(best_score < score1 + score2 + score3 + score4 && best_score > 0)
                            continue;
                        Route goal = new Route(b4.getPickUpPoint());
                        goal.setEnd(goalFakeBall);
                        ArrayList<Ball> btaGoal = (ArrayList<Ball>) balls.clone();
                        btaGoal.remove(b1);
                        btaGoal.remove(b2);
                        btaGoal.remove(b3);
                        btaGoal.remove(b4);
                        wrgoal = null;
                        try {
                            wrgoal = new WaypointGenerator(goalFakeBall.getPosVector(), b4.getPickUpPoint(), cross, boundry, btaGoal).waypointRoute;
                        } catch (NoRouteException e) {
                            continue;
                        } catch (TimeoutException e) {
                            continue;
                        }
                        goal.setScore(wrgoal.getCost());
                        ArrayList<Vector2Dv1> goalwaypoints = wrgoal.getRoute();
                        goal.setWaypoints(goalwaypoints);
                        temp_score = score1 + score2 + score3 + score4 + goal.getScore();
                        // Set best_heat and best_score
                        if(best_score < 0 || best_score > temp_score){
                            goal.setScore(temp_score);
                            b4.setGoalRoute(goal);
                            best_heat.clear();
                            best_heat.add(b1);
                            best_heat.add(b2);
                            best_heat.add(b3);
                            best_heat.add(b4);
                            best_score = temp_score;
                        }
                        bta.add(b4);
                    }
                    bta.add(b3);
                }
                bta.add(b2);
            }
        }
        return best_heat;
    }

    /**
     * Generates a heat 3 configuration by finding the best combination of balls based on scores.
     *
     * @param ball_list The list of balls to generate the configuration from.
     * @return An ArrayList containing the best combination of balls for the heat 3 configuration.
     */
    public ArrayList<Ball> heat3Generator(ArrayList<Ball> ball_list) {

        //NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();
        ArrayList<Ball> best_heat = new ArrayList<>();

        double score1 = 0;
        double score2 = 0;
        double score3 = 0;
        double temp_score = 0;
        double best_score = -1;
        for (Ball b1: ball_list) {
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(3)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
            bta.remove(b1);
            if(best_score < score1 && best_score > 0)
                continue;
            for (Ball b2 : ball_list) {
                if(b2 == b1)
                    continue;
                bta.remove(b2);
                WaypointGenerator.WaypointRoute wrgoal = null;
                try {
                    wrgoal = new WaypointGenerator(b2.getPickUpPoint(), b1.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                } catch (NoRouteException e) {
                    continue;
                } catch (TimeoutException e) {
                    continue;
                }
                score2 = wrgoal.getCost();

                if(best_score < score1 + score2 && best_score > 0)
                    continue;
                for (Ball b3: ball_list) {
                    if(b3 == b2 || b3 == b1)
                        continue;
                    bta.remove(b3);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(b3.getPickUpPoint(), b2.getPickUpPoint(), cross, boundry, bta).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    score3 = wrgoal.getCost();

                    if(best_score < score3 +score1 +score2 && best_score > 0)
                        continue;
                    Route goal = new Route(b3.getPickUpPoint());
                    goal.setEnd(goalFakeBall);
                    ArrayList<Ball> btaGoal = (ArrayList<Ball>) balls.clone();
                    btaGoal.remove(b1);
                    btaGoal.remove(b2);
                    btaGoal.remove(b3);
                    wrgoal = null;
                    try {
                        wrgoal = new WaypointGenerator(goalFakeBall.getPosVector(), b3.getPickUpPoint(), cross, boundry, btaGoal).waypointRoute;
                    } catch (NoRouteException e) {
                        continue;
                    } catch (TimeoutException e) {
                        continue;
                    }
                    goal.setScore(wrgoal.getCost());
                    ArrayList<Vector2Dv1> goalwaypoints = wrgoal.getRoute();
                    goal.setWaypoints(goalwaypoints);
                    temp_score = score1 + score2 + score3 + goal.getScore();
                    // Set best_heat and best_score
                    if(best_score < 0 || best_score > temp_score){
                        goal.setScore(temp_score);
                        b3.setGoalRoute(goal);
                        best_heat.clear();
                        best_heat.add(b1);
                        best_heat.add(b2);
                        best_heat.add(b3);
                        best_score = temp_score;
                    }
                    bta.remove(b3);
                }
                bta.remove(b2);
            }
        }
        return best_heat;
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

        routExecuter = new RoutExecute(out, in, robot, cross, boundry);

        routExecuter.heatRunner(ballsHeat1, 1, imgRec, stabilizer, ballsToAvoid);
        routExecuter.heatRunner(ballsHeat2, 2, imgRec, stabilizer, ballsToAvoid);
        routExecuter.heatRunner(ballsHeat3, 3, imgRec, stabilizer, ballsToAvoid);

    }
}