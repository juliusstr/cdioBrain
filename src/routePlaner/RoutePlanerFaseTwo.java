package routePlaner;

import Client.StandardSettings;
import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import nav.CommandGenerator;
import nav.WaypointGenerator;

import java.awt.*;
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

    public RoutePlanerFaseTwo(Robotv1 r, ArrayList<Ball> b, Boundry boundry) {
        balls = b;
        robot = r;
        cross = null;
        this.boundry = boundry;
        initGoalWaypoints();
    }

    private void generateheats(){
        //heat 1
        try {
            ballRoutes(false, 4, true, robot.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ballsHeat1 = balls;
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
        ballsHeat2 = balls;
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
        ballsHeat3 = balls;
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

    public String nextCommand(){
        return "";
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
        boolean orange_flag = true;

        for (i = 0; i < ball_list.size(); i++){
            if(ball_list.get(i).getColor() == Color.orange)
                i++;
            for (j = 0; j < ball_list.get(i).getRoutes().size(); j++){
                if (ball_list.get(i) == ball_list.get(i).getRoutes().get(j).getEnd())
                    continue;
                if(ball_list.get(i).getRoutes().get(j).getEnd().getColor() == Color.orange)
                    j++;
                score1 = ball_list.get(i).getRoutes().get(j).getScore();
                for (k = 0; k < ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().size(); k++){
                    if (ball_list.get(i) == ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd())
                        continue;
                    if (ball_list.get(i).getRoutes().get(j).getEnd() == ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd())
                        continue;
                    if(ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd().getColor() == Color.orange)
                        k++;
                    for (int l = 0; l < ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd().getRoutes().size(); l++){
                        if( ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd().getRoutes().get(l).getEnd().getColor() == Color.orange){
                            orange_ball_index = l;
                           orange_score = ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd().getRoutes().get(orange_ball_index).getScore();
                        }
                    }
                    score2 = ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getScore();

                    temp_score = score1 + score2 + orange_score;
                    if(temp_score < best_score || best_score == -1){
                        best_heat.clear();
                        best_score = temp_score;
                        best_heat.add(ball_list.get(i));
                        best_heat.add(ball_list.get(i).getRoutes().get(j).getEnd());
                        best_heat.add(ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd());
                        if(orange_flag){
                            best_heat.add(ball_list.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd().getRoutes().get(orange_ball_index).getEnd());
                        }
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
}
