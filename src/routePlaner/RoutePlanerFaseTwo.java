package routePlaner;

import Client.StandardSettings;
import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.NoRouteException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
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
    public ArrayList<Ball> ballsHeat1 = null;
    public ArrayList<Ball> ballsHeat2 = null;
    public ArrayList<Ball> ballsHeat3 = null;
    private Robotv1 robot = null;
    private Ball goalFakeBall = null;
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

    /**
     * Calculates the heats for the balls.
     * This method calculates the routes for three different heats based on the balls' positions.
     * The calculated heats are stored in separate lists.
     */
    public void getHeats(){
        int heat = 2;
        //heat 1
        try {
            ballRoutes(false, 4, true, robot.getPosVector());
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ballsHeat1 = (ArrayList<Ball>) balls.clone();
        ArrayList<Ball> removeballs = new ArrayList<>();
        for (Ball b: ballsHeat1) {
            if(b.getRoutes().size() == 0){
                removeballs.add(b);
            }
        }
        for (Ball b: removeballs) {
            ballsHeat1.remove(b);
        }
        ballsHeat1 = heat1Generator(ballsHeat1);
        for (Ball b: ballsHeat1) {
            balls.remove(b);
        }
        if(heat < 2)
            return;
        for (Ball b: balls) {
            b.setRoutes(new ArrayList<>());
            b.setGoalRoute(null);
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
        removeballs.clear();
        for (Ball b: ballsHeat2) {
            if(b.getRoutes().size() == 0){
                removeballs.add(b);
            }
        }
        for (Ball b: removeballs) {
            ballsHeat2.remove(b);
        }
        ballsHeat2 = heat2Generator(ballsHeat2);
        for (Ball b: ballsHeat2) {
            balls.remove(b);
        }
        if(heat < 3)
            return;
        for (Ball b: balls) {
            b.setRoutes(new ArrayList<>());
            b.setGoalRoute(null);
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
        removeballs.clear();
        for (Ball b: ballsHeat3) {
            if(b.getRoutes().size() == 0){
                removeballs.add(b);
            }
        }
        for (Ball b: removeballs) {
            ballsHeat3.remove(b);
        }
        ballsHeat3 = heat3Generator(ballsHeat3);
        for (Ball b: ballsHeat3) {
            balls.remove(b);
        }
    }

    /**
     * Calculates the routes for the balls based on the given parameters.
     *
     * @param difficultBalls Indicates whether to consider difficult balls or not.
     * @param minAmount      The minimum amount of free balls required.
     * @param orange         Indicates whether to consider orange balls or not.
     * @param robotPos       The position of the robot.
     * @throws NoRouteException   If no route is found.
     * @throws TimeoutException   If the calculation exceeds the time limit.
     */
    private void ballRoutes(Boolean difficultBalls, int minAmount, boolean orange, Vector2Dv1 robotPos) throws NoRouteException, TimeoutException {
        ArrayList<Ball> usedBalls = new ArrayList<>();

        ArrayList<Ball> outerBalls = (ArrayList<Ball>) balls.clone();
        ArrayList<Ball> innerBalls = (ArrayList<Ball>) balls.clone();
        int free = 0;
        for (Ball b : outerBalls) {
            if(b.getPlacement() == Ball.Placement.FREE){
                free++;
            }
        }
        if(free < minAmount)
            difficultBalls = true;
        for (Ball b : outerBalls) {
            usedBalls.add(b);
            if((difficultBalls || b.getPlacement() == Ball.Placement.FREE || (orange && b.getColor().equals(BallClassifierPhaseTwo.ORANGE)))){
                //ball to goal
                if(b.getGoalRoute() == null){
                    Route goal = new Route(b.getPosVector());
                    goal.setEnd(goalFakeBall);
                    ArrayList<Ball> btaGoal = (ArrayList<Ball>) balls.clone();
                    btaGoal.remove(b);

                    WaypointGenerator.WaypointRoute wrgoal = new WaypointGenerator(goalFakeBall.getPosVector(), b.getPosVector(), cross, boundry, btaGoal).waypointRoute;
                    goal.setScore(wrgoal.getCost());
                    ArrayList<Vector2Dv1> goalwaypoints = wrgoal.getRoute();
                    goal.setWaypoints(goalwaypoints);
                    b.setGoalRoute(goal);
                }
                //ball to robot
                Route robotRoute = new Route(robotPos);
                robotRoute.setEnd(b);
                ArrayList<Ball> btaRobot = (ArrayList<Ball>) balls.clone();
                btaRobot.remove(b);
                WaypointGenerator.WaypointRoute wrRobot = new WaypointGenerator(b.getPosVector(), robotPos, cross, boundry, btaRobot).waypointRoute;
                robotRoute.setScore(wrRobot.getCost());
                ArrayList<Vector2Dv1> robotwaypoints = wrRobot.getRoute();
                robotRoute.setWaypoints(robotwaypoints);
                robot.addRoute(robotRoute);
                for (Ball b2: innerBalls) {
                    if(!usedBalls.contains(b2) && (difficultBalls || b2.getPlacement() == Ball.Placement.FREE)){
                        Route r1 = new Route(b.getPosVector());
                        r1.setEnd(b2);
                        ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
                        bta.remove(b);
                        bta.remove(b2);
                        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(b2.getPosVector(), b.getPosVector(), cross, boundry, bta).waypointRoute;
                        r1.setScore(wr.getCost());
                        ArrayList<Vector2Dv1> waypoints = wr.getRoute();
                        r1.setWaypoints(waypoints);
                        b.addRoute(r1);
                        Route r2 = new Route(b2.getPosVector());
                        r2.setEnd(b);
                        ArrayList<Vector2Dv1> r2Waypoints = new ArrayList<>();
                        for (int i = waypoints.size()-1; i > 0; i--) {
                            r2Waypoints.add(waypoints.get(i));
                        }
                        r2Waypoints.add(b.getPosVector());
                        r2.setScore(r1.getScore());
                        r2.setWaypoints(r2Waypoints);
                        b2.addRoute(r2);
                    }
                }
            }
        }
        robot.endHeatRoutes();
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
        Boolean difficult = false;
        int iDiff = 0;
        for (Ball b: ball_list) {
            if(b.getPlacement() == Ball.Placement.FREE)
                iDiff++;
        }
        if(iDiff < 3)
            difficult = true;
        for (Ball b1: ball_list) {
            if((!difficult || iDiff > 0) && b1.getPlacement() != Ball.Placement.FREE)
                continue;
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(1)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            for (Route r2 :b1.getRoutes()) {
                Ball b2 = r2.getEnd();
                if((!difficult || iDiff > 1) && b2.getPlacement() != Ball.Placement.FREE)
                    continue;
                if(b2 == b1 || b2 == orangeBall)
                    continue;
                score2 = r2.getScore();
                for (Route r3: b2.getRoutes()) {
                    Ball b3 = r3.getEnd();
                    if((!difficult || iDiff > 2) && b3.getPlacement() != Ball.Placement.FREE)
                        continue;
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
        double temp_score = 0;
        double best_score = -1;
        Boolean difficult = false;
        int iDiff = 0;
        for (Ball b: ball_list) {
            if(b.getPlacement() == Ball.Placement.FREE)
                iDiff++;
        }
        if(iDiff < 4)
            difficult = true;
        for (Ball b1: ball_list) {
            if((!difficult || iDiff > 0) && b1.getPlacement() != Ball.Placement.FREE)
                continue;
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(2)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            for (Route r2 :b1.getRoutes()) {
                Ball b2 = r2.getEnd();
                if((!difficult || iDiff > 1) && b2.getPlacement() != Ball.Placement.FREE)
                    continue;
                if(b2 == b1)
                    continue;
                score2 = r2.getScore();
                for (Route r3: b2.getRoutes()) {
                    Ball b3 = r3.getEnd();
                    if((!difficult || iDiff > 2) && b3.getPlacement() != Ball.Placement.FREE)
                        continue;
                    if(b3 == b2 || b3 == b1)
                        continue;
                    score3 = r3.getScore();
                    for (Route r4: b3.getRoutes()) {
                        Ball b4 = r4.getEnd();
                        if((!difficult || iDiff > 3) && b4.getPlacement() != Ball.Placement.FREE)
                            continue;
                        if(b4 == b3 || b4 == b2 || b4 == b1)
                            continue;
                        temp_score = score1 + score2 + score3 + r4.getScore() + b4.getGoalRoute().getScore();
                        // Set best_heat and best_score
                        if(best_score < 0 || best_score > temp_score){
                            best_heat.clear();
                            best_heat.add(b1);
                            best_heat.add(b2);
                            best_heat.add(b3);
                            best_heat.add(b4);
                            best_score = temp_score;
                        }
                    }
                }
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
        double temp_score = 0;
        double best_score = -1;
        Boolean difficult = false;
        int iDiff = 0;
        for (Ball b: ball_list) {
            if(b.getPlacement() == Ball.Placement.FREE)
                iDiff++;
        }
        if(iDiff < 4)
            difficult = true;
        for (Ball b1: ball_list) {
            if((!difficult || iDiff > 0) && b1.getPlacement() != Ball.Placement.FREE)
                continue;
            // Add score from robot to b1 to temp_score
            for (Route rRobot: robot.getRoutes(3)) {
                if(rRobot.getEnd() == b1){
                    score1 = rRobot.getScore();
                    break;
                }
            }
            for (Route r2 :b1.getRoutes()) {
                Ball b2 = r2.getEnd();
                if((!difficult || iDiff > 1) && b2.getPlacement() != Ball.Placement.FREE)
                    continue;
                if(b2 == b1)
                    continue;
                score2 = r2.getScore();
                for (Route r3: b2.getRoutes()) {
                    Ball b3 = r3.getEnd();
                    if((!difficult || iDiff > 2) && b3.getPlacement() != Ball.Placement.FREE)
                        continue;
                    if(b3 == b2 || b3 == b1)
                        continue;
                    temp_score = score1 + score2 + r3.getScore() + b3.getGoalRoute().getScore();
                    // Set best_heat and best_score
                    if(best_score < 0 || best_score > temp_score){
                        best_heat.clear();
                        best_heat.add(b1);
                        best_heat.add(b2);
                        best_heat.add(b3);
                        best_score = temp_score;
                    }
                }
            }
        }
        return best_heat;
    }

    /**
     * Initializes the goal waypoints used for navigation.
     * Calculates the coordinates of two goal waypoints based on the boundary points.
     * Sets the goalWaypoint0, goalWaypoint1, and goalFakeBall variables.
     */
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
        Vector2Dv1 dir = corner1.getSubtracted(corner2).getNormalized().getRotatedBy((Math.PI/2)*(-1));
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
     *    a. Finds the route from the robot to the ball.
     *    b. Runs to the ball using waypoint navigation and captures ball images.
     *    c. Collects the ball if it is in a free placement.
     *    d. Updates the lastBall variable.
     * 4. Navigates to the goal and performs a drop-off.
     * 5. Iterates over the heat2 balls and performs the same sub-steps as in step 3.
     * 6. Navigates to the goal again and performs a drop-off.
     * 7. Iterates over the heat3 balls and performs the same sub-steps as in step 3.
     * 8. Navigates to the goal again and performs a drop-off.
     *
     * @param out         PrintWriter object for sending commands.
     * @param in          BufferedReader object for receiving responses.
     * @param imgRec      ImgRecFaseTwo object for capturing ball images.
     * @param stabilizer  BallStabilizerPhaseTwo object for stabilizing balls.
     */
    public void run(PrintWriter out, BufferedReader in, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer){
        System.out.println("heats : " + ballsHeat1);
        ArrayList<Ball> ballsToAvoid = new ArrayList<>();
        ballsToAvoid.addAll(ballsHeat1);
        ballsToAvoid.addAll(ballsHeat2);
        //ballsToAvoid.addAll(ballsHeat3);
        WaypointGenerator waypointGenerator;
        Ball lastBall = null;
        /**
         * -------------
         * heat 1
         * -------------
         */
        for (int j = 0; j < ballsHeat1.size(); j++){
            //finde route from robot to ball
            ArrayList<Vector2Dv1> routToBall = new ArrayList<>();
            ballsToAvoid.remove(ballsHeat1.get(j));
            if(ballsHeat1.size() == 4){
                for (int i = 0; i < robot.getRoutes(1).size(); i++) {
                    if(ballsHeat1.get(0) == robot.getRoutes(1).get(i).getEnd()){
                        //routToBall = robot.getRoutes(1).get(i).getWaypoints();
                        try {
                            waypointGenerator = new WaypointGenerator(ballsHeat1.get(j).getPosVector(),robot.getPosVector(),cross, boundry, ballsToAvoid);
                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        break;
                    }
                }
            } else {
                for (int i = 0; i < lastBall.getRoutes().size(); i++) {
                    if(lastBall.getRoutes().get(i).getEnd() == ballsHeat1.get(0)){
                        //routToBall = lastBall.getRoutes().get(i).getWaypoints();
                        try {
                            waypointGenerator = new WaypointGenerator(ballsHeat1.get(j).getPosVector(),lastBall.getPosVector(),cross, boundry, ballsToAvoid);
                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        break;
                    }
                }

            }
            //run to ball
            CommandGenerator commandGenerator = new CommandGenerator(robot,routToBall);
            while (routToBall.size() != 0){
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
                String command = commandGenerator.nextCommand(true);
                if(command.contains("ball")){
                    out.println("stop -d -t");
                    routToBall.clear();
                }
                out.println(command);
            }
            //collect
            switch (ballsHeat1.get(0).getPlacement()){
                case FREE:
                    out.println(StandardSettings.COLLECT_COMMAND);
                    wait(500);
                    break;
                default:
                    out.println("stop -t -d");
            }
            lastBall = ballsHeat1.get(0);

        }
        //go to goal and do a drop-off
        try {
            waypointGenerator = new WaypointGenerator(getGoalWaypoint0(),robot.getPosVector(),cross, boundry, ballsToAvoid);
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Vector2Dv1> routeToGoal = waypointGenerator.waypointRoute.getRoute();//lastBall.getGoalRoute().getWaypoints();
        routeToGoal.add(getGoalWaypoint1());
        CommandGenerator commandGenerator = new CommandGenerator(robot,routeToGoal);
        while (routeToGoal.size() != 0){
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
            String command = commandGenerator.nextCommand(false);
            if(command.contains("waypoint")){
                routeToGoal.clear();
            }
            out.println(command);
        }
        out.println(StandardSettings.DROP_OFF_COMMAND);
        wait(500);
        /**
         * -------------
         * heat 2
         * -------------
         */
        for (int j = 0; j < ballsHeat2.size(); j++){
            //finde route from robot to ball
            ArrayList<Vector2Dv1> routToBall = new ArrayList<>();
            ballsToAvoid.remove(ballsHeat2.get(j));
            if(ballsHeat2.size() == 4){
                for (int i = 0; i < robot.getRoutes(1).size(); i++) {
                    if(ballsHeat2.get(0) == robot.getRoutes(1).get(i).getEnd()){
                        //routToBall = robot.getRoutes(1).get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if(ballsHeat2.get(j).getPlacement() == Ball.Placement.FREE){
                                targetWaypoint = ballsHeat2.get(j).getPosVector();
                            } else {
                                targetWaypoint = ballsHeat2.get(j).getLineUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint,robot.getPosVector(),cross, boundry, ballsToAvoid);

                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        if(ballsHeat2.get(j).getPlacement() != Ball.Placement.FREE){
                            routToBall.add(ballsHeat2.get(j).getPickUpPoint());
                        }
                        break;
                    }
                }
            } else {
                for (int i = 0; i < lastBall.getRoutes().size(); i++) {
                    if(lastBall.getRoutes().get(i).getEnd() == ballsHeat2.get(0)){
                        //routToBall = lastBall.getRoutes().get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if(ballsHeat2.get(j).getPlacement() == Ball.Placement.FREE){
                                targetWaypoint = ballsHeat2.get(j).getPosVector();
                            } else {
                                targetWaypoint = ballsHeat2.get(j).getLineUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint,robot.getPosVector(),cross, boundry, ballsToAvoid);
                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        if(ballsHeat2.get(j).getPlacement() != Ball.Placement.FREE){
                            routToBall.add(ballsHeat2.get(j).getPickUpPoint());
                        }
                        break;
                    }
                }

            }
            //run to ball
            commandGenerator = new CommandGenerator(robot,routToBall);
            while (routToBall.size() != 0){
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
                String command = commandGenerator.nextCommand(true);
                if(command.contains("ball")){
                    out.println("stop -d -t");
                    routToBall.clear();
                }
                out.println(command);
            }
            //collect
            switch (ballsHeat2.get(0).getPlacement()){
                case FREE:
                    out.println(StandardSettings.COLLECT_COMMAND);
                    wait(500);
                    break;
                case EDGE:
                    out.println(StandardSettings.COLLECT_EDGE_COMMAND);
                    wait(500);
                    break;
                case CORNER:
                    out.println(StandardSettings.COLLECT_CORNER_COMMAND);
                    wait(500);
                    break;
                default:
                    out.println("stop -t -d");
                    wait(500);
                    break;
            }
            lastBall = ballsHeat2.get(0);

        }
        //go to goal and do a drop-off
        try {
            waypointGenerator = new WaypointGenerator(getGoalWaypoint0(),robot.getPosVector(),cross, boundry, ballsToAvoid);
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        routeToGoal = waypointGenerator.waypointRoute.getRoute();//lastBall.getGoalRoute().getWaypoints();
        routeToGoal.add(getGoalWaypoint1());
        commandGenerator = new CommandGenerator(robot,routeToGoal);
        while (routeToGoal.size() != 0){
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
            String command = commandGenerator.nextCommand(false);
            if(command.contains("waypoint")){
                routeToGoal.clear();
            }
            out.println(command);
        }
        out.println(StandardSettings.DROP_OFF_COMMAND);
        wait(500);
    }

    /**
     * Pauses the execution for the specified number of milliseconds.
     *
     * @param millis  The number of milliseconds to wait.
     */
    private void wait(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
