package routePlaner;

import Client.StandardSettings;
import Gui.Image.GuiImage;
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
    private Robotv1 robot = null;
    public Ball goalFakeBall = null;
    private Mat justInCase = null;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

    private Vector2Dv1 goalWaypoint0;//go firsts to this then 1,
    private Vector2Dv1 goalWaypoint1;

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
    public Vector2Dv1 getGoalWaypoint(int i){
        switch (i){
            case 0:
                return goalWaypoint0;
            case 1:
                return goalWaypoint1;
        }
        return null;
    }
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
        initGoalWaypoints();
    }

    public void setImage(GuiImage image){
        justInCase = image.getMat();
    }

    /**
     * Calculates the heats for the balls.
     * This method calculates the routes for three different heats based on the balls' positions.
     * The calculated heats are stored in separate lists.
     */
    public void getHeats(){
        int heat = 2;
        ImageClick ic = new ImageClick(new GuiImage(justInCase));
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
            ic.drawBalls(balls);
            ic.run("Choose a route", 4, manualVec, new ArrayList<Color>(), false);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();
            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                int closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat1.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
                        continue;
                    }
                    int dis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
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
            ic.drawBalls(balls);
            ic.run("Choose a route", 4, manualVec, new ArrayList<Color>(), false);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();

            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                int closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat2.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
                        continue;
                    }
                    int dis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
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
            ic.drawBalls(balls);
            ic.run("Choose a route", 4, manualVec, new ArrayList<Color>(), false);
            System.out.println("Press enter to end route!");
            Scanner inputWaitConfig = new Scanner(System.in);
            inputWaitConfig.nextLine();
            for (Vector2Dv1 v :manualVec) {
                Ball closest = null;
                int closestDis = 0;
                for (Ball b: balls) {
                    if(ballsHeat3.contains(b))
                        continue;
                    if(closest == null){
                        closest = b;
                        closestDis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
                        continue;
                    }
                    int dis = (b.getxPos() - (int)v.x) > 0 ? (b.getxPos() - (int)v.x) : ((int)v.x - b.getxPos()) + (b.getyPos() - (int)v.y) > 0 ? (b.getyPos() - (int)v.y) : ((int)v.y - b.getyPos());
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
     * Initializes the goal waypoints used for navigation.
     * Calculates the coordinates of two goal waypoints based on the boundary points.
     * Sets the goalWaypoint0, goalWaypoint1, and goalFakeBall variables.
     */
    public void initGoalWaypoints() {
        ArrayList<Vector2Dv1> corners = getCornersForGoal();
        Vector2Dv1 midVector = corners.get(0).getMidVector(corners.get(1));
        Vector2Dv1 dir = corners.get(0).getSubtracted(corners.get(1)).getNormalized().getRotatedBy((Math.PI / 2)*(-1));
        goalWaypoint1 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST));
        goalWaypoint0 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST + StandardSettings.ROUTE_PLANER_GOAL_CASTER_WEEL_LINE_UP));
        goalFakeBall = new Ball(goalWaypoint0);
    }

    /**
     * Returns the goal waypoint 0.
     *
     * @return The goal waypoint 0 as a Vector2Dv1 object.
     */
    public Vector2Dv1 getGoalWaypoint0() {
        return goalWaypoint0;
    }

    /**
     * Returns the goal waypoint 1.
     *
     * @return The goal waypoint 1 as a Vector2Dv1 object.
     */
    public Vector2Dv1 getGoalWaypoint1() {
        return goalWaypoint1;
    }

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

        heatRunner(ballsHeat1, 1, out, in, imgRec, stabilizer, ballsToAvoid);
        heatRunner(ballsHeat2, 2, out, in, imgRec, stabilizer, ballsToAvoid);
        heatRunner(ballsHeat3, 3, out, in, imgRec, stabilizer, ballsToAvoid);

    }

    void heatRunner(ArrayList<Ball> heat, int heatNr, PrintWriter out, BufferedReader in, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer, ArrayList<Ball> ballsToAvoid) {
        WaypointGenerator waypointGenerator;
        Ball lastBall = null;
        CommandGenerator commandGenerator;
        ArrayList<Vector2Dv1> routeToGoal;
        // checksize to have 11 balls ?
        int checkSize;
        if(heatNr == 3){
            checkSize = 3;
        } else {
            checkSize = 4;
        }

        for (int j = 0; j < heat.size(); j++) {
            //finde route from robot to ball
            ArrayList<Vector2Dv1> routToBall = new ArrayList<>();
            ballsToAvoid.remove(heat.get(j));
            if (heat.size() == checkSize) {
                for (int i = 0; i < robot.getRoutes(heatNr).size(); i++) {
                    if (heat.get(0) == robot.getRoutes(heatNr).get(i).getEnd()) {
                        //routToBall = robot.getRoutes(1).get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                                targetWaypoint = heat.get(j).getPosVector();
                            } else {
                                targetWaypoint = heat.get(j).getPickUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint, robot.getPosVector(), cross, boundry, ballsToAvoid);

                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        if (heat.get(j).getPlacement() != Ball.Placement.FREE) {
                            routToBall.add(heat.get(j).getLineUpPoint());
                        }
                        break;
                    }
                }
            } else {
                for (int i = 0; i < lastBall.getRoutes().size(); i++) {
                    if (lastBall.getRoutes().get(i).getEnd() == heat.get(j)) {
                        //routToBall = lastBall.getRoutes().get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                                targetWaypoint = heat.get(j).getPosVector();
                            } else {
                                targetWaypoint = heat.get(j).getPickUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint, robot.getPosVector(), cross, boundry, ballsToAvoid);
                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        if (heat.get(j).getPlacement() != Ball.Placement.FREE) {
                            routToBall.add(heat.get(j).getLineUpPoint());
                        }
                        break;
                    }
                }

            }
            //run to ball
            commandGenerator = new CommandGenerator(robot, routToBall);
            boolean isBallNotWaypoint;
            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                isBallNotWaypoint = true;
            } else {
                isBallNotWaypoint = false;
            }
            while (routToBall.size() != 0) {
                updateRobotFromImgRec(imgRec, robot, stabilizer);
                String command = commandGenerator.nextCommand(isBallNotWaypoint);
                if (command.contains("ball") || command.contains("waypoint")) {
                    out.println("stop -d -t");
                    wait(200);
                    routToBall.clear();
                } else {
                    out.println(command);
                }
            }
            //collect
            switch (heat.get(j).getPlacement()) {
                case FREE:
                    //check if we have the right angle to the target
                    turnBeforeHardcode(robot, imgRec, out,in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_COMMAND);
                    reverseIfCloseToBoundary(boundry.bound, robot, out, in);
                    reverseIfCloseToBoundary(cross.crossLines, robot, out, in);
                    break;
                case EDGE:
                    turnBeforeHardcode(robot, imgRec, out, in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_EDGE_COMMAND);
                    reverseIfCloseToBoundary(boundry.bound, robot, out, in);
                    reverseIfCloseToBoundary(cross.crossLines, robot, out, in);
                    break;
                case CORNER:
                    turnBeforeHardcode(robot, imgRec, out, in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_CORNER_COMMAND);
                    reverseIfCloseToBoundary(boundry.bound, robot, out, in);
                    reverseIfCloseToBoundary(cross.crossLines, robot, out, in);
                    break;
                default:
                    out.println("stop -t -d");
                    wait(500);
                    break;
            }
            lastBall = heat.get(j);

        }
        //go to goal and do a drop-off
        try {
            waypointGenerator = new WaypointGenerator(getGoalWaypoint0(), robot.getPosVector(), cross, boundry, ballsToAvoid);
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        routeToGoal = waypointGenerator.waypointRoute.getRoute();//lastBall.getGoalRoute().getWaypoints();
        routeToGoal.add(getGoalWaypoint1());
        commandGenerator = new CommandGenerator(robot, routeToGoal);
        while (routeToGoal.size() != 0) {
            updateRobotFromImgRec(imgRec, robot, stabilizer);
            String command = commandGenerator.nextCommand(false);
            if (command.contains("waypoint")) {
                routeToGoal.clear();
            }
            out.println(command);
        }
        turnBeforeHardcode(robot, imgRec, out, in, getGoalPos(), stabilizer);
        out.println(StandardSettings.DROP_OFF_COMMAND);
        reverseIfCloseToBoundary(boundry.bound, robot, out, in);
        reverseIfCloseToBoundary(cross.crossLines, robot, out, in);
    }

    /**
     * Pauses the execution for the specified number of milliseconds.
     *
     * @param millis The number of milliseconds to wait.
     */
    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks the angle between a robot and the target it is supposed to go to.
     * @param robot The robot
     * @param target The target
     * @return How wrong the angle of the robot is to target
     */
    public double angleBeforeHardcode(Robotv1 robot, Vector2Dv1 target) {
        return robot.getDirection().getAngleBetwen(target.getSubtracted(robot.getPosVector()));
    }

    /**
     * Checks if we have the correct angle to our target, within the constant ANGLE_ERROR
     * @param robot The robot
     * @param target The target
     * @param out The Printwriter to write to robot
     * @return True if we have the correct angle, false if we dont have the correct angle
     */
    public boolean correctAngleToTarget(Robotv1 robot, Vector2Dv1 target, PrintWriter out) {
        double angleToTarget = angleBeforeHardcode(robot, target);
        String command = "";
        if (Math.abs(angleToTarget) > ANGLE_ERROR) {
            command += "turn -";
            if (angleToTarget < 0) {
                command += "l";
            } else {
                command += "r";
            }
            double turnSpeed = Math.abs(angleToTarget / 5);
            if (turnSpeed > 0.2) {turnSpeed = 0.2;
            } else if (turnSpeed < 0.02) {
                turnSpeed = 0.02;
            }

            command += " -s" + String.format("%.2f", turnSpeed).replace(',', '.') + "";
            System.out.println("Send command: " + command);
            out.println(command);
            return false;
        } else{
            return true;
        }
    }

    /**
     * Updates the robots position from image rec
     * @param imgRec The imgRec used
     * @param robot The robot to update
     * @param stabilizer The stabilizer to use
     */
    public void updateRobotFromImgRec(ImgRecFaseTwo imgRec, Robotv1 robot, BallStabilizerPhaseTwo stabilizer){
        ArrayList<Ball> balls = imgRec.captureBalls();
        try {
            stabilizer.stabilizeBalls(balls);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }
        try {
            ArrayList<Ball> robotBalls = stabilizer.getStabelRobotCirce();
            robot.updatePos(robotBalls.get(0), robotBalls.get(1));
        } catch (BadDataException e) {
            //throw new RuntimeException(e);
        }
    }

    /**
     * Gets the corners that is on the small goal boundary
     * @return List of Vector2D with the coordinates to the corners
     */
    public ArrayList<Vector2Dv1> getCornersForGoal(){
        int index1 = -1, index2 = -1;;
        int minX = Integer.MAX_VALUE;
        ArrayList<Vector2Dv1> returnList = new ArrayList<>();
        for (int i = 0; i < boundry.points.size(); i++) {
            if (boundry.points.get(i).x < minX) {
                index1 = i;
                minX = boundry.points.get(i).x;
            }
        }
        returnList.add(new Vector2Dv1(boundry.points.get(index1)));

        minX = Integer.MAX_VALUE;

        for (int i = 0; i < boundry.points.size(); i++) {
            if (boundry.points.get(i).x < minX && i != index1) {
                index2 = i;
                minX = boundry.points.get(i).x;
            }
        }
        returnList.add(new Vector2Dv1(boundry.points.get(index2)));
        if(returnList.get(0).y < returnList.get(1).y){
            Vector2Dv1 temp = returnList.get(0);
            returnList.remove(temp);
            returnList.add(temp);
        }
        return  returnList;
    }

    /**
     * Gets the goal position as a vector from the corners with the smallest x coordinate
     * @return Vector2D with the pos of the goal
     */
    public Vector2Dv1 getGoalPos(){
        ArrayList<Vector2Dv1> corners = getCornersForGoal();
        Vector2Dv1 smallGoal = corners.get(0).getMidVector(corners.get(1));
        return smallGoal;
    }

    /**
     * To turn before starting a hardcoded command
     * @param robot         The robot to turn
     * @param imgRec        The imgRec to update robot pos
     * @param out           The Printwriter to send command to robot
     * @param target        The target to have minimal angle to
     * @param stabilizer    The stabilizer for the balls
     */
    public void turnBeforeHardcode(Robotv1 robot, ImgRecFaseTwo imgRec, PrintWriter out, BufferedReader in, Vector2Dv1 target, BallStabilizerPhaseTwo stabilizer){
        out.println("stop -d -t");
        try {
            while (in.readLine() != null) ;
        } catch (IOException e){
            throw new RuntimeException();
        }
        wait(100);
        //check if we have the right angle to the target
        while(!correctAngleToTarget(robot, target, out)){
            updateRobotFromImgRec(imgRec, robot, stabilizer);
            out.println("stop -d -t");
        }

        wait(100);
    }

    /**
     * Reverse if too close to a line after pickup
     * @param lines The ArrayList of lines to check for
     * @param robot The robot to check for
     * @param out the Printwriter to write to the robot
     */
    public void reverseIfCloseToBoundary(ArrayList<Line> lines, Robotv1 robot, PrintWriter out, BufferedReader in){
        try{
            while(in.readLine() != "hardcode done");
        } catch (IOException e){
            throw new RuntimeException();
        }
        for (Line line: lines) {
            if(line.findClosestPoint(robot.getPosVector()).getSubtracted(robot.getPosVector()).getLength() < StandardSettings.ROUTE_PLANER_DISTANCE_FROM_LINE_BEFORE_TURN){
                out.println("reverse -m500");
                wait(400);
                out.println("stop -d -t");
            }
        }
    }
}


