package routePlaner;

import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import nav.NavAlgoFaseOne;
import nav.NavAlgoPhaseTwo;
import nav.WaypointGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RoutePlanerFaseTwo {
    private NavAlgoPhaseTwo nav = null;
    private ArrayList<Ball> balls = null;
    private ArrayList<Ball> ballsHeat1 = null;
    private ArrayList<Ball> ballsHeat2 = null;
    private ArrayList<Ball> ballsHeat3 = null;
    private Robotv1 robot = null;
    private Ball goalFakeBall = null;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }

    public RoutePlanerFaseTwo(Robotv1 r, ArrayList<Ball> b) {
        balls = b;
        robot = r;
        nav = new NavAlgoPhaseTwo();
        cross = null;
        boundry = null;
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
            ballRoutes(false, 4, true, goalFakeBall.getPosVector());
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
        //heat 3
        try {
            ballRoutes(false, 4, true, goalFakeBall.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

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
        int temp_score = 0;
        int best_score = 0;
        int i, j ,k;

        for (i = 0; i < balls.size(); i++){

            for (j = 0; j < balls.get(i).getRoutes().size(); j++){
                score1 = balls.get(i).getRoutes().get(j).getScore();
                for (k = 0; k < balls.get(i).getRoutes().get(j).getEnd().getRoutes().size(); k++){
                    score2 = balls.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getScore();
                    temp_score = score1 + score2;
                    if(temp_score < best_score){
                        best_score = temp_score;
                        best_heat.add(balls.get(i));
                        best_heat.add(balls.get(i).getRoutes().get(j).getEnd());
                        best_heat.add(balls.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd());
                    }
                }
            }
        }
        return best_heat;
    }
}
