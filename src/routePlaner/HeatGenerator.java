package routePlaner;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Gui.Image.GuiImage;
import Gui.ImageClick;
import Gui.RouteView;
import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import nav.WaypointGenerator;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.opencv.core.Mat;

public class HeatGenerator {

    private int MAXBALLSPERHEAT = 4;
    private  ArrayList<Ball> heat = new ArrayList<>();

    private  ArrayList<Ball> balls;
    private  ArrayList<Ball> ballsForHeat;

    private ArrayList<Ball> freeBalls = new ArrayList<>();
    private  ArrayList<Ball> pairBalls = new ArrayList<>();
    private  ArrayList<Ball> diffBalls =  new ArrayList<>();
    private  ArrayList<Ball> reqBalls =  new ArrayList<>();

    private  Ball orangeBall = null;

    private  Robotv1 robot;
    private  Cross cross;
    private  Boundry boundry;
    private  Ball goal;
    private  boolean orangeFirst;

    private  int amount;
    private  int heatNum;
    private  Vector2Dv1 robotPos;
    private GuiImage image;

    /**
     * Constructs a HeatGenerator object with the specified parameters.
     *
     * @param balls       The list of balls to generate heat from.
     * @param r           The Robotv1 object.
     * @param b           The Boundry object.
     * @param c           The Cross object.
     * @param g           The goal Ball object.
     * @param heatNum     The number of the heat.
     * @param m           The Mat object.
     * @param req         The list of required balls.
     * @param orangeFirst A boolean value indicating whether the orange ball should be placed last in the heat.
     */
    public HeatGenerator(ArrayList<Ball> balls, Robotv1 r, Vector2Dv1 robotPos, Boundry b, Cross c, Ball g, int heatNum, Mat m, ArrayList<Ball> req, boolean orangeFirst){
        image = new GuiImage(m);
        this.balls = balls;
        this.reqBalls = req;
        this.goal = g;
        this.boundry = b;
        this.cross = c;
        this.robot = r;
        this.orangeFirst = orangeFirst;
        this.heatNum = heatNum;
        this.robotPos = robotPos;
        this.amount = balls.size() > (MAXBALLSPERHEAT-1) ? MAXBALLSPERHEAT : balls.size();
        heat = new ArrayList<>();
        diffBalls = new ArrayList<>();
        freeBalls = new ArrayList<>();
        pairBalls = new ArrayList<>();
        reqBalls = new ArrayList<>();
        run();
    }

    /**
     * Constructs a HeatGenerator object with the specified parameters.
     *
     * @param balls   The list of balls to generate heat from.
     * @param r       The Robotv1 object.
     * @param b       The Boundry object.
     * @param c       The Cross object.
     * @param g       The goal Ball object.
     * @param heatNum The number of the heat.
     * @param m       The Mat object.
     */
    public HeatGenerator(ArrayList<Ball> balls, Robotv1 r, Vector2Dv1 robotPos, Boundry b, Cross c, Ball g, int heatNum, Mat m){
        image = new GuiImage(m);
        this.balls = balls;
        this.goal = g;
        this.boundry = b;
        this.cross = c;
        this.robot = r;
        this.robotPos = robotPos;
        this.orangeFirst = false;
        this.heatNum = heatNum;
        this.amount = balls.size() > (MAXBALLSPERHEAT-1) ? MAXBALLSPERHEAT : balls.size();
        heat = new ArrayList<>();
        diffBalls = new ArrayList<>();
        freeBalls = new ArrayList<>();
        pairBalls = new ArrayList<>();
        reqBalls = new ArrayList<>();
        run();
    }

    /**
     * Runs the heat generation
     */
    private void run(){
        System.out.println("-------------------\nStarting generation of heat " + heatNum + "\nHeat settings:\nHeat size: " + amount + "\nGet orange ball: " + (orangeFirst ? "Y" : "N"));
        sortBalls();
        if(orangeBall != null){
            System.out.println("Orange ball: (" + orangeBall.getxPos() + ", " + orangeBall.getyPos() + ")");
        }
        System.out.println("Required balls: " + reqBalls.size());
        for (Ball ball: reqBalls) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ")");
        }
        System.out.println("Free balls: " + freeBalls.size());
        for (Ball ball: freeBalls) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ")");
        }
        System.out.println("Pair balls: " + pairBalls.size());
        for (Ball ball: pairBalls) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ")");
        }
        System.out.println("Diff balls: " + diffBalls.size());
        for (Ball ball: diffBalls) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ")");
        }
        findPossibleBallsForHeat();
        System.out.println("Possible Balls For Heat: " + ballsForHeat.size());
        for (Ball ball: ballsForHeat) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ")");
        }
        addRouteToRobot();
        System.out.println("Robot routes: " + robot.getRoutes(heatNum).size());
        for (Route route: robot.getRoutes(heatNum)) {
            System.out.println("Ball: (" + route.getEnd().getxPos() + ", " + route.getEnd().getyPos() + ")");
        }
        new generate();
        if(heat.size() < amount && ballsForHeat.size() < balls.size()){
            ballsForHeat = (ArrayList<Ball>) balls.clone();
            orangeFirst = false;
            new generate();
        }
        if(heat.size() < amount)
            setRouteByHand(false);
        System.out.println("HEAT " + heatNum+": ");
        for (Ball ball: heat) {
            System.out.println("Ball: (" + ball.getxPos() + ", " + ball.getyPos() + ") Color: " + (ball.getColor().equals(BallClassifierPhaseTwo.ORANGE) ? "ORANGE" : "WHITE") + " TYPE: " + ball.getPlacement());
        }
        System.out.println("\nHEAT cost: " + heat.get(heat.size()-1).getRoutes().get(heat.get(heat.size()-1).getRoutes().size()-1).getScore());
        if(heat.get(heat.size()-1).getGoalRoute().getScore() < 0)
            return;
        System.out.println("heat size " + heat.size());
        ArrayList bta = (ArrayList) balls.clone();
        ArrayList<ArrayList<Vector2Dv1>> vv_list = new ArrayList<>();
        vv_list.add(new ArrayList<>());
        vv_list.get(0).add(robotPos);
        for (Route r: robot.getRoutes(heatNum)) {
            if(r.getEnd() == heat.get(0)){
                vv_list.add(r.getWaypoints());
                break;
            }
        }
        bta.remove(heat.get(0));
        int i = 1;
        for (Ball b: heat) {
            if(heat.get(heat.size()-1) == b)
                vv_list.add(b.getGoalRoute().getWaypoints());
            else {
                bta.remove(heat.get(i));
                Route r = getRoute(b.getPickUpPoint(), heat.get(i), bta);
                if (r == null){
                    ArrayList<Vector2Dv1> v_list = new ArrayList<>();
                    v_list.add(heat.get(i).getPickUpPoint());
                } else
                    vv_list.add(r.getWaypoints());
            }
            i++;
        }
        RouteView rw = new RouteView(vv_list, image.getMat());
    }

    /**
     * Sorts the balls into different categories based on their color and placement.
     * The sorted balls are stored in separate ArrayLists or Ball objects: orangeBall, freeBalls, pairBalls, and diffBalls.
     * The orange ball is assigned to the orangeBall instance variable.
     * The free balls are added to the freeBalls ArrayList.
     * The paired balls are added to the pairBalls ArrayList.
     * The remaining balls are added to the diffBalls ArrayList.
     */
    private void sortBalls(){
        for (Ball b: balls) {
            if(b.getColor().equals(BallClassifierPhaseTwo.ORANGE))
                orangeBall = b;
            else if(b.getPlacement() == Ball.Placement.FREE)
                freeBalls.add(b);
            else if(b.getPlacement() == Ball.Placement.PAIR)
                pairBalls.add(b);
            else
                diffBalls.add(b);
        }
    }

    /**
     * Adds routes to the robot based on the balls for the current heat.
     * The method creates a clone of the 'balls' list called 'bta' to use as balls to avoid.
     * It iterates over the ballsForHeat list, excluding the orange ball if 'orangeFirst' is set.
     * For each ball, it removes the ball from 'bta', calculates a route using the getRoute() method,
     * and if a route is found, it is added to the current heat routes in the robot.
     * After iterating through all the balls, if no routes were added, it resets the ballsForHeat list to include all balls,
     * and repeats the process. If no routes are found even with all balls, the 'setRouteByHand' method is called to get a start ball for the heat.
     * Finally, the cuurent heat routes for the robot are maked as completed.
     */
    private void addRouteToRobot(){
        ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
        int i = 0;
        for (Ball b: ballsForHeat) {
            if(orangeFirst && orangeBall == b)
                continue;
            bta.remove(b);
            Route r = getRoute(robotPos, b, bta);
            if(r != null){
                i++;
                robot.addRoute(r);
            }
            bta.add(b);
        }
        if(i == 0){
            ballsForHeat = (ArrayList<Ball>) balls.clone();
            for (Ball b: ballsForHeat) {
                bta.remove(b);
                Route r = getRoute(robotPos, b, bta);
                if(r != null){
                    i++;
                    robot.addRoute(r);
                }
                bta.add(b);
            }
            if(i == 0){
                setRouteByHand(true);
            }
        }
        robot.endHeatRoutes();
    }

    /**
     * Sets the route for the robot manually by allowing user input through image clicks.
     * The method clears the 'heat' list, creates an instance of 'ImageClick' with the 'image' parameter,
     * and draws the balls in the 'ballsForHeat' list using the 'drawBalls()' method of 'ImageClick'.
     * It initializes an empty list of 'v_list' to store the clicked positions.
     * If the 'robot' parameter is true, the method prompts the user to select the first ball by running 'ImageClick' with a message and limiting the clicks to 1.
     * If the 'robot' parameter is false, the method prompts the user to select the route by running 'ImageClick' with a message and limiting the clicks to 'amount'.
     * The method waits until the required number of clicks is obtained by continuously checking the size of 'v_list' with a do-while loop and a sleep interval.
     * After obtaining the clicks, the method iterates over the clicked positions in 'v_list' and finds the closest ball from the 'ballsForHeat' list.
     * It calculates the distance between the clicked position and each ball's position and selects the ball with the minimum distance as the closest.
     * The selected closest ball is added to the 'heat' list.
     * the cost of the heat or route is set to -1.
     */
    private void setRouteByHand(boolean robot){
        heat.clear();
        ImageClick ic = new ImageClick(image);
        ic.drawBalls(ballsForHeat);
        ArrayList<Vector2Dv1> v_list = new ArrayList<>();
        if(robot){
            ic.run("Select first ball",1, v_list,new ArrayList<Color>(),false);
            do{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }while (v_list.size() < 1);
        } else {
            ic.run("Select route",amount, v_list,new ArrayList<Color>(),false);
            do{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }while (v_list.size() < amount);
        }
        for (Vector2Dv1 v: v_list) {
            Ball clostest = null;
            Vector2Dv1 close = null;
            for (Ball b: ballsForHeat) {
                if(heat.contains(b))
                    continue;
                if(close == null){
                    clostest = b;
                    close = v.getSubtracted(b.getPosVector());
                } else if(close.x+close.y >v.getSubtracted(b.getPosVector()).x+v.getSubtracted(b.getPosVector()).y) {
                    clostest = b;
                    close = v.getSubtracted(b.getPosVector());
                }
            }
            heat.add(clostest);
        }
        Route route = new Route(heat.get(heat.size()-1).getPickUpPoint());
        route.setScore(-1);
        heat.get(heat.size()-1).addRoute(route);
    }

    /**
     * Calculates and returns a route from the start position to the end ball, considering the given list of balls to avoid.
     * The method initializes a 'WaypointGenerator.WaypointRoute' object 'wr' as null.
     * It creates a new 'Route' object with the start position and sets the end ball.
     * The method attempts to generate a waypoint route using the 'WaypointGenerator' class, passing the end ball's pick-up point, start position, cross, boundary, and the list of balls to avoid.
     * If a 'NoRouteException' or 'TimeoutException' occurs during waypoint route generation, the method returns null.
     * Otherwise, it sets the route's score to the cost of the generated waypoint route and sets the waypoints from the waypoint route to the route object.
     * Finally, it returns the route object.
     */
    private  Route getRoute(Vector2Dv1 start, Ball end, ArrayList<Ball> bta){
        WaypointGenerator.WaypointRoute wr = null;
        Route route = new Route(start);
        route.setEnd(end);
        try {
            wr = new WaypointGenerator(end.getPickUpPoint(), start, cross, boundry, bta).waypointRoute;
        } catch (NoRouteException e) {
            return null;
        } catch (TimeoutException e) {
            return null;
        }
        route.setScore(wr.getCost());
        route.setWaypoints(wr.getRoute());
        return route;
    }

    /**
     * Finds the possible balls that can be included in the heat.
     * Populates the 'ballsForHeat' ArrayList based on various conditions and ball placements.
     */
    private void findPossibleBallsForHeat(){
        ballsForHeat = new ArrayList<Ball>();
        int amountLeft = amount;
        if(orangeFirst && orangeBall != null) {
            ballsForHeat.add(orangeBall);
            amountLeft--;
        }
        ballsForHeat.addAll(reqBalls);
        if(reqBalls.size() >= amountLeft)
            return;
        amountLeft -= reqBalls.size();
        ballsForHeat.addAll(freeBalls);
        if(freeBalls.size() >= amountLeft)
            return;
        amountLeft -= freeBalls.size();
        ballsForHeat.addAll(pairBalls);
        if(pairBalls.size() >= amountLeft)
            return;
        ballsForHeat.addAll(diffBalls);
    }

    /**
     * The 'generate' class is a private class used internally by the parent class.
     * It is responsible for generating the best possible heat configuration.
     */
    private class generate {
        private int bestScore = -1;
        private ArrayList<Ball> best_heat = new ArrayList<>();
        private ArrayList<Ball> best_heat_orange = new ArrayList<>();
        private ArrayList<Ball> curBalls = new ArrayList<>();
        private ArrayList<Ball> curDiffBalls = new ArrayList<>();
        private ArrayList<Ball> curPairBalls = new ArrayList<>();
        private ArrayList<Ball> curReqBalls = new ArrayList<>();

        ArrayList<Ball> ball_list;

        private int curScore = 0;

        private int bestScoreO = -1;

        private int count = 0;
        private int gAmount = amount;

        /**
         * The constructor of the 'generate' class.
         * It initializes the class variables and prepares the ball list for generating the heat configuration.
         */
        private generate() {
            bestScore = -1;
            bestScoreO = -1;
            curBalls = new ArrayList<>();
            best_heat = new ArrayList<>();
            curPairBalls = new ArrayList<>();
            curReqBalls = new ArrayList<>();
            curDiffBalls = new ArrayList<>();
            best_heat_orange = new ArrayList<>();
            ball_list = (ArrayList<Ball>) ballsForHeat.clone();
            ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
            if (orangeFirst)
                ball_list.remove(orangeBall);
            if (heat.size() > 0) {
                for (int i = 0; i < heat.size(); i++) {
                    ball_list.remove(heat.get(i));
                    best_heat.add(heat.get(i));
                    bta.remove(heat.get(i));
                    gAmount--;
                }
                findNextBall(best_heat.get(best_heat.size() - 1), bta);
            } else {
                for (Route r : robot.getRoutes(heatNum)) {
                    bta.remove(r.getEnd());
                    curScore = (int) r.getScore();
                    findNextBall(r.getEnd(), bta);
                    bta.add(r.getEnd());
                }
            }
            if (orangeFirst && best_heat.size() < gAmount && best_heat_orange.size() == gAmount)
                heat = (ArrayList<Ball>) best_heat_orange.clone();
            else
                heat = (ArrayList<Ball>) best_heat.clone();
        }

        /**
         * Recursive method to find the next ball in the heat configuration.
         *
         * @param start The ball to start the search from.
         * @param bta   The list of balls available for selection.
         */
        private void findNextBall(Ball start, ArrayList<Ball> bta) {
            count++;
            int countInner = count;
            if(curBalls.size()+1 > gAmount)
                return;
            if (curScore >= bestScore && bestScore > 0)
                return;
            if (reqBalls.contains(start))
                curReqBalls.add(start);
            else if (pairBalls.contains(start))
                curPairBalls.add(start);
            else if (diffBalls.contains(start))
                curDiffBalls.add(start);
            if (curBalls.size()+1 + (reqBalls.size() - curReqBalls.size()) > gAmount) {
                if (curReqBalls.contains(start))
                    curReqBalls.remove(start);
                else if (curPairBalls.contains(start))
                    curPairBalls.remove(start);
                else if (curDiffBalls.contains(start))
                    curDiffBalls.remove(start);
            }
            curBalls.add(start);
            if (best_heat.size() < curBalls.size())
                best_heat = (ArrayList<Ball>) curBalls.clone();
            if (start.getPlacement() == Ball.Placement.PAIR)
                curScore += 100;
            else if (start.getPlacement() == Ball.Placement.EDGE)
                curScore += 200;
            else if (start.getPlacement() == Ball.Placement.CORNER)
                curScore += 200;
            if (curBalls.size() >= gAmount) {
                Route route = getRoute(start.getPickUpPoint(), goal, bta);
                if (route != null) {
                    if (curScore + (int) route.getScore() < bestScore || bestScore < 0) {
                        bestScore = curScore + (int) route.getScore();
                        route.setScore(bestScore);
                        start.addRoute(route);
                        start.setGoalRoute(route);
                        best_heat = (ArrayList<Ball>) curBalls.clone();
                    }
                }
            } else if (orangeFirst && curBalls.size() == gAmount - 1) {
                bta.remove(orangeBall);
                Route route = getRoute(start.getPickUpPoint(), orangeBall, bta);
                if (route == null) {
                    bta.add(orangeBall);
                    curBalls.remove(start);
                    if (start.getPlacement() == Ball.Placement.PAIR)
                        curScore -= 100;
                    else if (start.getPlacement() == Ball.Placement.EDGE)
                        curScore -= 200;
                    else if (start.getPlacement() == Ball.Placement.CORNER)
                        curScore -= 200;
                    return;
                }
                curScore += (int) route.getScore();
                findNextBall(orangeBall, bta);
                curScore -= (int) route.getScore();
                bta.add(orangeBall);
                for (Ball b : ball_list) {
                    if (curBalls.contains(b))
                        continue;
                    bta.remove(b);
                    Route route2 = getRoute(start.getPickUpPoint(), b, bta);
                    if (route2 == null) {
                        bta.add(b);
                        continue;
                    }
                    curScore += (int) route2.getScore();
                    curBalls.add(b);
                    Route route3 = getRoute(b.getPickUpPoint(), goal, bta);
                    if (route3 == null) {
                        curScore -= (int) route2.getScore();
                        bta.add(b);
                        curBalls.remove(b);
                        continue;
                    }
                    if (curScore + (int) route3.getScore() < bestScoreO || bestScoreO < 0) {
                        bestScoreO = curScore + (int) route.getScore();
                        route.setScore(bestScoreO);
                        b.addRoute(route);
                        b.setGoalRoute(route);
                        best_heat_orange = (ArrayList<Ball>) curBalls.clone();
                    }
                    curBalls.remove(b);
                    bta.add(b);
                    curScore -= (int) route2.getScore();
                }
                } else {
                    for (Ball b : ball_list) {
                        if (curBalls.contains(b))
                            continue;
                        bta.remove(b);
                        Route route = getRoute(start.getPickUpPoint(), b, bta);
                        if (route == null)
                            continue;
                        //System.out.println("Count: " + countInner + " score: " + (int)route.getScore());
                        //System.out.println("current score after: " + curScore);
                        curScore += (int) route.getScore();
                        //System.out.println("current score after: " + curScore);
                        findNextBall(b, bta);
                        //System.out.println("Count: " + countInner + " score: " + (int)route.getScore());
                        //System.out.println("current score after: " + curScore);
                        curScore -= (int) route.getScore();
                        //System.out.println("current score after: " + curScore);
                        bta.add(b);
                    }
                }
            if (curReqBalls.contains(start))
                curReqBalls.remove(start);
            else if (curPairBalls.contains(start))
                curPairBalls.remove(start);
            else if (curDiffBalls.contains(start))
                curDiffBalls.remove(start);
            if (start.getPlacement() == Ball.Placement.PAIR)
                curScore -= 100;
            else if (start.getPlacement() == Ball.Placement.EDGE)
                curScore -= 200;
            else if (start.getPlacement() == Ball.Placement.CORNER)
                curScore -= 200;
            curBalls.remove(start);
        }
    }

    /**
     * Retrieves the heat generated by the 'generate' class.
     *
     * @return The ArrayList of balls representing the heat.
     */
    public ArrayList<Ball> getHeat(){
        return heat;
    }
}
