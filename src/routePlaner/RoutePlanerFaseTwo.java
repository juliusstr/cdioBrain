package routePlaner;

import Client.StandardSettings;
import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import nav.CommandGenerator;
import nav.WaypointGenerator;

import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RoutePlanerFaseTwo {
    private ArrayList<Ball> balls = null;
    private ArrayList<Ball> ballsHeat1 = null;
    private ArrayList<Ball> ballsHeat2 = null;
    private ArrayList<Ball> ballsHeat3 = null;
    private Robotv1 robot = null;
    private Ball goalFakeBall = null;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

    private Vector2Dv1 goalWaypoint0;//go firsts to this then 1,
    private Vector2Dv1 goalWaypoint1;


    public void setBoundry(Boundry b){
        this.boundry = b;
    }
    public void setCross(Cross c){
        this.cross = c;
    }

    public Vector2Dv1 getGoalWaypoint(int i){
        switch (i){
            case 0:
                return goalWaypoint0;
            case 1:
                return goalWaypoint1;
        }
        return null;
    }
    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }

    public RoutePlanerFaseTwo(Robotv1 r, ArrayList<Ball> b, Boundry boundry, Cross c) {
        balls = (ArrayList<Ball>) b.clone();
        robot = r;
        cross = c;
        this.boundry = boundry;
        initGoalWaypoints();
    }

    /**
     *
     */
    private void generateheats(){
        //heat 1
        try {
            ballRoutes(false, 4, true, robot.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ballsHeat1 = (ArrayList<Ball>) balls.clone();
        for (Ball b: ballsHeat1) {
            if(b.getRoutes().size() == 0){
                ballsHeat1.remove(b);
            }
        }
        ballsHeat1 = heatGenerator(ballsHeat1);
        for (Ball b: balls) {
            if(ballsHeat1.contains(b)){
                balls.remove(b);
            }
        }

        //heat 2
        try {
            ballRoutes(false, 4, false, goalFakeBall.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ballsHeat2 = (ArrayList<Ball>)balls.clone();
        for (Ball b: ballsHeat1) {
            if(b.getRoutes().size() == 0){
                ballsHeat2.remove(b);
            }
        }
        ballsHeat2 = heatGenerator(ballsHeat2);
        for (Ball b: balls) {
            if(ballsHeat2.contains(b)){
                balls.remove(b);
            }
        }
        //heat 3
        try {
            ballRoutes(true, 3, false, goalFakeBall.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ballsHeat3 = (ArrayList<Ball>)balls.clone();
        ballsHeat3 = heatGenerator(ballsHeat3);
    }

    private void ballRoutes(Boolean difficultBalls, int minAmount, boolean orange, Vector2Dv1 robotPos) throws NoRouteException, TimeoutException {
        ArrayList<Ball> usedBalls = new ArrayList<>();
        for (Ball b : balls) {
            usedBalls.add(b);
            if((difficultBalls || b.getPlacement() == Ball.Placement.FREE || (orange && b.getColor() == Color.orange))){
                //ball to goal
                if(b.getGoalRoute() == null){
                    Route goal = new Route(b.getPosVector());
                    goal.setEnd(goalFakeBall);
                    ArrayList<Ball> btaGoal = balls;
                    btaGoal.remove(b);
                    WaypointGenerator.WaypointRoute wrgoal = new WaypointGenerator(b.getPosVector(), goalFakeBall.getPosVector(), cross, boundry, btaGoal).waypointRoute;
                    goal.setScore(wrgoal.getScore());
                    ArrayList<Vector2Dv1> goalwaypoints = wrgoal.getRoute();
                    goal.setWaypoints(goalwaypoints);
                    b.setGoalRoute(goal);
                }
                //ball to robot
                Route robotRoute = new Route(robotPos);
                robotRoute.setEnd(b);
                ArrayList<Ball> btaGoal = balls;
                btaGoal.remove(b);
                WaypointGenerator.WaypointRoute wrRobot = new WaypointGenerator(robotPos, b.getPosVector(), cross, boundry, btaGoal).waypointRoute;
                robotRoute.setScore(wrRobot.getScore());
                ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                robotRoute.setWaypoints(robotwaypoints);
                robot.addRoute(robotRoute);

                for (Ball b2: balls) {
                    if(!usedBalls.contains(b2) && (difficultBalls || b2.getPlacement() == Ball.Placement.FREE)){
                        Route r1 = new Route(b.getPosVector());
                        r1.setEnd(b2);
                        ArrayList<Ball> bta = balls;
                        bta.remove(b);
                        bta.remove(b2);
                        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(b.getPosVector(), b2.getPosVector(), cross, boundry, bta).waypointRoute;
                        r1.setScore(wr.getScore());
                        ArrayList<Vector2Dv1> waypoints = wr.getRoute();
                        r1.setWaypoints(waypoints);
                        b.addRoute(r1);
                        Route r2 = new Route(b2.getPosVector());
                        r2.setEnd(b);
                        ArrayList<Vector2Dv1> r2Waypoints = new ArrayList<>();
                        for (int i = waypoints.size()-1; i > 0; i++) {
                            r2Waypoints.add(waypoints.get(i));
                        }
                        r2Waypoints.add(b.getPosVector());
                        r2.setScore(r1.getScore());
                        r2.setWaypoints(r2Waypoints);
                        b.addRoute(r2);
                    }
                }
                robot.endHeatRoutes();
            }
        }
    }


    public ArrayList<Ball> heatGenerator(ArrayList<Ball> ball_list) {

        //NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();
        ArrayList<Ball> best_heat = new ArrayList<>();

        int score1 = 0;
        int score2 = 0;
        int orange_score = 0;
        int temp_score = 0;
        int best_score = -1;
        int i, j ,k;
        int orange_ball_index = 0;
        Ball orangeBall = null;
        boolean orange_flag = true;
        // find orange ball
        if(orange_flag){
            for (Ball b: ball_list) {
                if(b.getColor() == Color.ORANGE){
                    orangeBall = b;
                    break;
                }
            }
            ball_list.remove(orangeBall);
        }
        for (Ball b1: ball_list) {
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(1)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            for (Route r2 :b1.getRoutes()) {
                Ball b2 = r2.getEnd();
                if(b2 == b1 || b2 == orangeBall)
                    continue;
                score2 = r2.getScore();
                for (Route r3: b2.getRoutes()) {
                    Ball b3 = r3.getEnd();
                    if(b3 == b2 || b3 == b1 || b3 == orangeBall)
                        continue;
                    temp_score = score1 + score2;
                    temp_score += r3.getScore();
                    // find route to orangeBall
                    for (Route r4: b3.getRoutes()) {
                        if(r4.getEnd() == orangeBall){
                            temp_score += r4.getScore();
                            break;
                        }
                    }
                    // Set best_heat and best_score
                    if(best_score < 0 || best_score > temp_score){
                        best_heat.clear();
                        best_heat.add(b1);
                        best_heat.add(b2);
                        best_heat.add(b3);
                        best_heat.add(orangeBall);
                        best_score = temp_score;
                    }
                }
            }
        }
        return best_heat;
    }

    public void initGoalWaypoints(){
        int index1 = -1, index2 = -1;
        int minX = Integer.MAX_VALUE;
        for (int i = 0; i < boundry.points.size(); i++) {
            if(boundry.points.get(i).x < minX){
                index1 = i;
                minX = boundry.points.get(i).x;
            }
        }
        minX = Integer.MAX_VALUE;
        for (int i = 0; i < boundry.points.size(); i++) {
            if(boundry.points.get(i).x < minX && i != index1){
                index2 = i;
                minX = boundry.points.get(i).x;
            }
        }
        Vector2Dv1 corner1 = new Vector2Dv1(boundry.points.get(index1));
        Vector2Dv1 corner2 = new Vector2Dv1(boundry.points.get(index2));
        Vector2Dv1 midVector = corner1.getMidVector(corner2);
        Vector2Dv1 dir = corner1.getSubtracted(corner2).getNormalized().getRotatedBy(Math.PI/2);
        goalWaypoint1 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST));
        goalWaypoint0 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST + StandardSettings.ROUTE_PLANER_GOAL_CASTER_WEEL_LINE_UP));
    }

    public Vector2Dv1 getGoalWaypoint0() {
        return goalWaypoint0;
    }

    public Vector2Dv1 getGoalWaypoint1() {
        return goalWaypoint1;
    }

    public void run(PrintWriter out, BufferedReader in){
        while (ballsHeat1.size() != 0){

        }
    }
}
