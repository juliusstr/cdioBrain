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
import routePlaner.RoutePlanerFaseOne;
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
        Ball ball1 = new Ball(new Vector2Dv1(30+25,20+25),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1,boundry,cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 97);
        assertEquals(((int) ball1.getPickUpPoint().y) , 87);

        ball1 = new Ball(new Vector2Dv1(610-25,340-25),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        BallClassifierPhaseTwo.ballSetPlacement(ball1,boundry,cross);
        System.out.println("ball pickUp point: " + ball1.getPickUpPoint());
        assertEquals(ball1.getPlacement() == Ball.Placement.CORNER, true);
        assertEquals(((int) ball1.getPickUpPoint().x), 97);
        assertEquals(((int) ball1.getPickUpPoint().y) , 87);
    }

    @Test
    @DisplayName("Ball classifier test")
    void heatGenTest(){

        RoutePlanerFaseTwo hg = new RoutePlanerFaseTwo();
        ArrayList<Ball> best_route = new ArrayList<>();

        double score = 0;

        Ball ball1 = new Ball(new Vector2Dv1(30,20),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball2 = new Ball(new Vector2Dv1(210,140),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball3 = new Ball(new Vector2Dv1(180,340),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball4 = new Ball(new Vector2Dv1(94,256),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball5 = new Ball(new Vector2Dv1(217,36),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball ball6 = new Ball(new Vector2Dv1(57,345),StandardSettings.BALL_RADIUS_PX,BallClassifierPhaseTwo.WHITE,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
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
                ball_list.get(i).getRoutes().get(k).setRoute(null);
                ball_list.get(i).getRoutes().get(k).setEnd(ball_list.get(j));
                ball_list.get(i).getRoutes().get(k).setScore((int) ball_list.get(i).getPosVector().distance(ball_list.get(i).getRoutes().get(k).getEnd().getPosVector()));
                k++;
                }
            }
        }

        best_route = hg.heatGenerator(ball_list);
       // System.out.println(ball_list);
        for(int l = 0; l < best_route.size(); l++){
            System.out.println("\n x:"+best_route.get(l).getxPos()+" y:"+best_route.get(l).getyPos());
        }


    }

}
