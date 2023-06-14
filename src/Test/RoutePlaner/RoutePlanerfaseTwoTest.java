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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import routePlaner.Route;
import routePlaner.RoutePlanerFaseTwo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static Test.Gui.ImageClickTest.imageIconToMat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
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
    void getHeat1Test(){

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
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

    @Test
    @DisplayName("Test getHeats")
    void getHeat2Test(){

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
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
        best_route = hg.ballsHeat2;
        System.out.println(simulationRobot.getRoutes(2).size());
        for (Ball b: best_route) {
            System.out.println("\n x:"+b.getxPos()+" y:"+b.getyPos());
        }


    }

    @Test
    @DisplayName("Test getHeats")
    void getHeat3Test(){

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball7 = new Ball(new Vector2Dv1(479,240),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball8 = new Ball(new Vector2Dv1(610,40),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball9 = new Ball(new Vector2Dv1(556,443),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball10 = new Ball(new Vector2Dv1(610,114),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball11 = new Ball(new Vector2Dv1(290,478),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);

        ball1.setPlacement(Ball.Placement.CORNER);
        ball2.setPlacement(Ball.Placement.CORNER);
        ball3.setPlacement(Ball.Placement.CORNER);
        ball4.setPlacement(Ball.Placement.CORNER);
        ball5.setPlacement(Ball.Placement.FREE);
        ball6.setPlacement(Ball.Placement.FREE);
        ball7.setPlacement(Ball.Placement.FREE);
        ball8.setPlacement(Ball.Placement.FREE);
        ball9.setPlacement(Ball.Placement.FREE);
        ball10.setPlacement(Ball.Placement.FREE);
        ball11.setPlacement(Ball.Placement.FREE);

        ball1.setId(1);
        ball2.setId(2);
        ball3.setId(3);
        ball4.setId(4);
        ball5.setId(5);
        ball6.setId(6);
        ball7.setId(7);
        ball8.setId(8);
        ball9.setId(9);
        ball10.setId(10);
        ball11.setId(11);

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
        ball_list.clear();
        ball_list.add(new Ball(new Vector2Dv1(432.0,82.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.ORANGE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(446.0,287.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(335.0,216.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(295.0,134.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(260.0,278.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(171.0,194.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(163.0,41.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(113.0,31.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(163.0,42.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(278.0,147.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));
        ball_list.add(new Ball(new Vector2Dv1(408.0,124.0),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL));

        RoutePlanerFaseTwo hg = new RoutePlanerFaseTwo(simulationRobot,ball_list, boundry, cross);
        ArrayList<Ball> best_route = new ArrayList<>();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("test_img/WIN_20230315_10_32_53_Pro.jpg");
        Mat mat = imageIconToMat(tImage);
        hg.setImage(mat);
        hg.getHeats();
        best_route = hg.ballsHeat1;/*
        System.out.println("-----------------" + "\n Heat 1 \n" + "\n robotRoutes i.e total balls left: " + simulationRobot.getRoutes(1).size());
        double scoreEnd = 0;
        int i = 1;
        for (Ball b: best_route) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor() == BallClassifierPhaseTwo.ORANGE ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            if(i < best_route.size()){
                for (Route r: b.getRoutes()) {
                    if(r.getEnd() == best_route.get(i)){
                        scoreEnd += r.getScore();
                        break;
                    }
                }
            } else {
                scoreEnd += b.getGoalRoute().getScore();
            }
            i++;
        }
        System.out.println("\n Score: " + scoreEnd);
        scoreEnd = 0;
        i = 1;
        best_route = hg.ballsHeat2;
        System.out.println("\n -----------------" + "\n Heat 2 \n" + "\n robotRoutes i.e total balls left: " + simulationRobot.getRoutes(2).size());
        for (Ball b: best_route) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor() == BallClassifierPhaseTwo.ORANGE ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            if(i < best_route.size()){
                for (Route r: b.getRoutes()) {
                    if(r.getEnd() == best_route.get(i)){
                        scoreEnd += r.getScore();
                        break;
                    }
                }
            } else {
                scoreEnd += b.getGoalRoute().getScore();
            }
            i++;
        }
        System.out.println("\n Score: " + scoreEnd);
        scoreEnd = 0;
        i = 1;
        scoreEnd = 0;
        best_route = hg.ballsHeat3;
        System.out.println("-----------------" + "\n Heat 3 \n" + "\n robotRoutes i.e total balls left: " + simulationRobot.getRoutes(3).size());
        for (Ball b: best_route) {
            System.out.println("\n Ball: " + b.getId() +  " Pos: (x:"+b.getxPos()+" y:"+b.getyPos() + ") Color: " + (b.getColor() == BallClassifierPhaseTwo.ORANGE ? "ORANGE" : "WHITE") + " TYPE: " + b.getPlacement());
            if(i < best_route.size()){
                for (Route r: b.getRoutes()) {
                    if(r.getEnd() == best_route.get(i)){
                        scoreEnd += r.getScore();
                        break;
                    }
                }
            } else {
                scoreEnd += b.getGoalRoute().getScore();
            }
            i++;
        }
        System.out.println("\n Score: " + scoreEnd);
        scoreEnd = 0;
        i = 1;*/


    }

    @Test
    @DisplayName("Test Angle from pos")
    void angleFromPosTest(){
        RoutePlanerFaseTwo routePlan = new RoutePlanerFaseTwo(simulationRobot,new ArrayList<>(), boundry, cross);
        Vector2Dv1 vec1 = new Vector2Dv1(1,1);
        Vector2Dv1 vec2 = new Vector2Dv1(2, 2);
        double angle = routePlan.angleFromPosVectorToPosVector(vec1, vec2);
        assertTrue(angle > 0.77 || angle < 0.79);

        vec1 = new Vector2Dv1(0, 1);
        vec2 = new Vector2Dv1(1, 0);
        angle = routePlan.angleFromPosVectorToPosVector(vec1, vec2);
        assertTrue(angle > 0.77 || angle < 0.79);
    }
    @Test
    @DisplayName("Test correct Angle Before Hardcode")
    void correctAngleBeforeHardcodeTest(){
        RoutePlanerFaseTwo routePlan = new RoutePlanerFaseTwo(simulationRobot,new ArrayList<>(), boundry, cross);
        simulationRobot.setDirection(new Vector2Dv1(1, 1));
        simulationRobot.setPos(1, 1);

        //tjek for om den regner rigtigt når det passer
        Vector2Dv1 vec2 = new Vector2Dv1(2, 2);
        double angle = routePlan.angleBeforeHardcode(simulationRobot, vec2);
        assertTrue(Math.abs(angle) < 0.01);

        // Tjek for at den regner rigtigt når det ikke passer
        vec2 = new Vector2Dv1(1, 0);
        angle = routePlan.angleBeforeHardcode(simulationRobot, vec2);
        assertTrue(angle > 0.77 || angle < 0.79);
    }
}
