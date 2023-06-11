package Test.RoutePlaner;

import Client.StandardSettings;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import routePlaner.Route;
import routePlaner.RoutePlanerFaseTwo;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePlanerfaseTwoTest {

    Robotv1 simulationRobot;
    Ball target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;
    String nextCommand;

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
        target = new Ball(500, 360/2, StandardSettings.BALL_RADIUS_PX, new Color(1,1,1), true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);
        ballsToAvoid = new ArrayList<>();
    }

    @Test
    @DisplayName("Ball classifier test")
    void ballClassifierTest(){
        //bondery corner
        Ball ball1 = new Ball(new Vector2Dv1(30 + 25, 20 + 25), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 97);
        assertEquals(((int) ball1.getPickUpPoint().y), 87);

        ball1 = new Ball(new Vector2Dv1(610 - 25, 340 - 25), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 542);
        assertEquals(((int) ball1.getPickUpPoint().y) , 272);

        ball1 = new Ball(new Vector2Dv1(610 - 25, 20 + 25), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 542);
        assertEquals(((int) ball1.getPickUpPoint().y) , 87);

        ball1 = new Ball(new Vector2Dv1(30 + 25, 340 - 25), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 97);
        assertEquals(((int) ball1.getPickUpPoint().y) , 272);

        //bondery edge
        ball1 = new Ball(new Vector2Dv1(30 + 270, 20 + 8), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.EDGE, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 300);
        assertEquals(((int) ball1.getPickUpPoint().y) , 88);

        ball1 = new Ball(new Vector2Dv1(30 + 270, 340 - 8), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.EDGE, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 300);
        assertEquals(((int) ball1.getPickUpPoint().y), 272);

        ball1 = new Ball(new Vector2Dv1(30 + 8, 20 + (340 - 20) / 2), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.EDGE, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 98);
        assertEquals(((int) ball1.getPickUpPoint().y), 180);

        ball1 = new Ball(new Vector2Dv1(610 - 8, 20 + (340 - 20) / 2), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.EDGE, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 542);
        assertEquals(((int) ball1.getPickUpPoint().y), 180);

        //cross
        ball1 = new Ball(new Vector2Dv1(337, 161), StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE, true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1, boundry, cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 377);
        assertEquals(((int) ball1.getPickUpPoint().y), 116);
    }
    @Test
    @DisplayName("Goal init test")
    void goalInitTest(){
        RoutePlanerFaseTwo routePlaner = new RoutePlanerFaseTwo(simulationRobot,ballsToAvoid, boundry,cross);
        System.out.println(routePlaner.getGoalWaypoint(0));
        System.out.println(routePlaner.getGoalWaypoint(1));
    }

    @Test
    @DisplayName("Ball classifier test")
    void heatGenTest(){

        RoutePlanerFaseTwo hg = new RoutePlanerFaseTwo(new Robotv1(1,1,new Vector2Dv1(1,1)),new ArrayList<>(), boundry, cross);
        ArrayList<Ball> best_route = new ArrayList<>();

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,Color.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball7 = new Ball(new Vector2Dv1(479,240),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball8 = new Ball(new Vector2Dv1(610,40),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball9 = new Ball(new Vector2Dv1(556,443),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball10 = new Ball(new Vector2Dv1(610,114),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball11 = new Ball(new Vector2Dv1(290,478),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);

        ArrayList<Ball> ball_list = new ArrayList<>();

        ball_list.add(ball1);
        ball_list.add(ball2);
        ball_list.add(ball3);
        ball_list.add(ball4);
        ball_list.add(ball5);
        ball_list.add(ball6);
        ball_list.add(ball7);
        ball_list.add(ball8);
        ball_list.add(ball9);
        ball_list.add(ball10);
        ball_list.add(ball11);


        for(int i = 0; i < ball_list.size(); i++){
            for(int j = 0, k = 0;j < 10; j++){
                if(i!=j){
                ball_list.get(i).addRoute(new Route(ball_list.get(i).getPosVector()));
                ball_list.get(i).getRoutes().get(k).setWaypoints(null);
                ball_list.get(i).getRoutes().get(k).setEnd(ball_list.get(j));
                ball_list.get(i).getRoutes().get(k).setScore((int) ball_list.get(i).getPosVector().distance(ball_list.get(i).getRoutes().get(k).getEnd().getPosVector()));
                k++;
                }
            }
        }

        best_route = hg.heat1Generator(ball_list);
       // System.out.println(ball_list);
        for(int l = 0; l < best_route.size(); l++){
            System.out.println("\n x:"+best_route.get(l).getxPos()+" y:"+best_route.get(l).getyPos());
        }


    }

    @Test
    @DisplayName("Test getHeats")
    void getHeatTest(){

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,Color.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball7 = new Ball(new Vector2Dv1(479,240),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball8 = new Ball(new Vector2Dv1(610,40),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball9 = new Ball(new Vector2Dv1(556,443),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball10 = new Ball(new Vector2Dv1(610,114),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball11 = new Ball(new Vector2Dv1(290,478),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);

        ball1.setPlacement(Ball.Placement.FREE);
        ball2.setPlacement(Ball.Placement.FREE);
        ball3.setPlacement(Ball.Placement.FREE);
        ball4.setPlacement(Ball.Placement.FREE);
        ball5.setPlacement(Ball.Placement.FREE);
        ball6.setPlacement(Ball.Placement.FREE);
        ball7.setPlacement(Ball.Placement.FREE);
        ball8.setPlacement(Ball.Placement.FREE);
        ball9.setPlacement(Ball.Placement.FREE);
        ball10.setPlacement(Ball.Placement.FREE);
        ball11.setPlacement(Ball.Placement.FREE);

        ArrayList<Ball> ball_list = new ArrayList<>();
        ball_list.add(ball1);
        ball_list.add(ball2);
        ball_list.add(ball3);
        ball_list.add(ball4);
        ball_list.add(ball5);
        ball_list.add(ball6);
        ball_list.add(ball7);
        ball_list.add(ball8);
        ball_list.add(ball9);
        ball_list.add(ball10);
        ball_list.add(ball11);

        RoutePlanerFaseTwo hg = new RoutePlanerFaseTwo(simulationRobot,ball_list, boundry, cross);
        ArrayList<Ball> best_route = new ArrayList<>();

        hg.getHeats();
        best_route = hg.ballsHeat1;
        System.out.println(simulationRobot.getRoutes(1).size());
        for (Ball b: best_route) {
            System.out.println("\n x:"+b.getxPos()+" y:"+b.getyPos());
        }


    }

}
