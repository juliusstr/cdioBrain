package routePlaner;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Gui.Image.GuiImage;
import Gui.ImageClick;
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
    private static ArrayList<Ball> heat = new ArrayList<>();

    private static ArrayList<Ball> balls;
    private static ArrayList<Ball> ballsForHeat;

    private ArrayList<Ball> freeBalls;
    private ArrayList<Ball> pairBalls;
    private ArrayList<Ball> diffBalls;
    private ArrayList<Ball> reqBalls;

    private static Ball orangeBall = null;

    private static Robotv1 robot;
    private static Cross cross;
    private static Boundry boundry;
    private static Ball goal;
    private static boolean orangeFirst;

    private static int amount;
    private static int heatNum;
    private GuiImage image;

    public HeatGenerator(ArrayList<Ball> balls, Robotv1 r, Boundry b, Cross c, Ball g, int heatNum, Mat m, ArrayList<Ball> req, boolean orangeFirst){
        image = new GuiImage(m);
        this.balls = balls;
        this.reqBalls = req;
        this.goal = g;
        this.boundry = b;
        this.cross = c;
        this.robot = r;
        this.orangeFirst = orangeFirst;
        this.heatNum = heatNum;
        this.amount = balls.size() > (MAXBALLSPERHEAT-1) ? MAXBALLSPERHEAT : balls.size();
        run();
    }
    public HeatGenerator(ArrayList<Ball> balls, Robotv1 r, Boundry b, Cross c, Ball g, int heatNum, Mat m){
        image = new GuiImage(m);
        this.balls = balls;
        this.goal = g;
        this.boundry = b;
        this.cross = c;
        this.robot = r;
        this.orangeFirst = false;
        this.heatNum = heatNum;
        this.amount = balls.size() > (MAXBALLSPERHEAT-1) ? MAXBALLSPERHEAT : balls.size();
        run();
    }

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
    }

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

    private void addRouteToRobot(){
        ArrayList<Ball> bta = (ArrayList<Ball>) balls.clone();
        int i = 0;
        for (Ball b: ballsForHeat) {
            bta.remove(b);
            Route r = getRoute(robot.getPosVector(), b, bta);
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
                Route r = getRoute(robot.getPosVector(), b, bta);
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

    private void setRouteByHand(boolean robot){
        ImageClick ic = new ImageClick(image);
        ic.drawBalls(ballsForHeat);
        ArrayList<Vector2Dv1> v_list = new ArrayList<>();
        if(robot){
            ic.run("Select first ball",1, v_list,new ArrayList<Color>(),false);
        } else {
            ic.run("Select route",amount, v_list,new ArrayList<Color>(),false);
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

    private static Route getRoute(Vector2Dv1 start, Ball end, ArrayList<Ball> bta){
        WaypointGenerator.WaypointRoute wr = null;
        Route route = new Route(robot.getPosVector());
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

    private void findPossibleBallsForHeat(){
        ballsForHeat = new ArrayList<Ball>();
        int amountLeft = amount;
        if(orangeFirst && orangeBall != null) {
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

    private static class generate{
        private int bestScore = -1;
        private ArrayList<Ball> best_heat = new ArrayList<>();
        private ArrayList<Ball> best_heat_orange = new ArrayList<>();
        private ArrayList<Ball> curBalls = new ArrayList<>();

        ArrayList<Ball> ball_list;

        private int curScore = 0;

        private int bestScoreO = -1;

        private generate(){
            ball_list = (ArrayList<Ball>) ballsForHeat.clone();
            ArrayList<Ball> bta = (ArrayList<Ball>) ballsForHeat.clone();
            if(orangeFirst)
                ball_list.remove(orangeBall);
            if(heat.size() > 0) {
                for (int i = 0; i < heat.size(); i++) {
                    ball_list.remove(heat.get(i));
                    best_heat.add(heat.get(i));
                    amount--;
                }
                findNextBall(best_heat.get(best_heat.size()-1), bta);
            } else {
                for (Route r: robot.getRoutes(heatNum)) {
                    findNextBall(r.getEnd(), bta);
                }
            }
            if(orangeFirst && best_heat.size() < amount && best_heat_orange.size() == amount)
                heat = (ArrayList<Ball>) best_heat_orange.clone();
            else
                heat = (ArrayList<Ball>) best_heat.clone();
        }

        private void findNextBall(Ball start, ArrayList<Ball> bta){
            curBalls.add(start);
            bta.remove(start);
            if(best_heat.size() < curBalls.size())
                best_heat = (ArrayList<Ball>) curBalls.clone();
            if(start.getPlacement() == Ball.Placement.PAIR)
                curScore += 100;
            else if(start.getPlacement() == Ball.Placement.EDGE)
                curScore += 200;
            else if(start.getPlacement() == Ball.Placement.CORNER)
                curScore += 200;
            if(curBalls.size() >= amount){
                Route route = getRoute(start.getPickUpPoint(), goal, bta);
                if(route == null) {
                    if(start.getPlacement() == Ball.Placement.PAIR)
                        curScore -= 100;
                    else if(start.getPlacement() == Ball.Placement.EDGE)
                        curScore -= 200;
                    else if(start.getPlacement() == Ball.Placement.CORNER)
                        curScore -= 200;
                    curBalls.remove(start);
                    bta.add(start);
                    return;
                }
                if(curScore+(int)route.getScore() < bestScore || bestScore < 0){
                    bestScore = curScore+(int)route.getScore();
                    route.setScore(bestScore);
                    start.addRoute(route);
                    best_heat = (ArrayList<Ball>) curBalls.clone();
                }
            } else if(orangeFirst && curBalls.size() == amount-1) {
                Route route = getRoute(start.getPickUpPoint(), orangeBall, bta);
                if(route == null) {
                    if(start.getPlacement() == Ball.Placement.PAIR)
                        curScore -= 100;
                    else if(start.getPlacement() == Ball.Placement.EDGE)
                        curScore -= 200;
                    else if(start.getPlacement() == Ball.Placement.CORNER)
                        curScore -= 200;
                    curBalls.remove(start);
                    bta.add(start);
                    return;
                }
                curScore += route.getScore();
                findNextBall(orangeBall, bta);
                curScore -= route.getScore();
                for (Ball b: ball_list) {
                    if(curBalls.contains(b))
                        continue;
                    Route route2 = getRoute(start.getPickUpPoint(), b, bta);
                    if(route2 == null) {
                        if(start.getPlacement() == Ball.Placement.PAIR)
                            curScore -= 100;
                        else if(start.getPlacement() == Ball.Placement.EDGE)
                            curScore -= 200;
                        else if(start.getPlacement() == Ball.Placement.CORNER)
                            curScore -= 200;
                        curBalls.remove(start);
                        bta.add(start);
                        return;
                    }
                    curScore += route2.getScore();
                    bta.remove(b);
                    curBalls.add(b);
                    Route route3 = getRoute(b.getPickUpPoint(), goal, bta);
                    if(route3 == null) {
                        if(start.getPlacement() == Ball.Placement.PAIR)
                            curScore -= 100;
                        else if(start.getPlacement() == Ball.Placement.EDGE)
                            curScore -= 200;
                        else if(start.getPlacement() == Ball.Placement.CORNER)
                            curScore -= 200;
                        curBalls.remove(start);
                        bta.add(start);
                        return;
                    }
                    if(curScore+(int)route3.getScore() < bestScoreO || bestScoreO < 0){
                        bestScoreO = curScore+(int)route.getScore();
                        route.setScore(bestScoreO);
                        start.addRoute(route);
                        best_heat_orange = (ArrayList<Ball>) curBalls.clone();
                    }
                    bta.add(b);
                    curScore -= route2.getScore();
                }
            } else {
                for (Ball b: ball_list) {
                    if(curBalls.contains(b))
                        continue;
                    Route route = getRoute(start.getPickUpPoint(), b, bta);
                    if(route == null) {
                        if(start.getPlacement() == Ball.Placement.PAIR)
                            curScore -= 100;
                        else if(start.getPlacement() == Ball.Placement.EDGE)
                            curScore -= 200;
                        else if(start.getPlacement() == Ball.Placement.CORNER)
                            curScore -= 200;
                        curBalls.remove(start);
                        bta.add(start);
                        return;
                    }
                    curScore += route.getScore();
                    findNextBall(b, bta);
                    curScore -= route.getScore();
                }
            }
            if(start.getPlacement() == Ball.Placement.PAIR)
                curScore -= 100;
            else if(start.getPlacement() == Ball.Placement.EDGE)
                curScore -= 200;
            else if(start.getPlacement() == Ball.Placement.CORNER)
                curScore -= 200;
            curBalls.remove(start);
            bta.add(start);
        }

    }

    public ArrayList<Ball> getHeat(){
        return heat;
    }
}
