package Test.RoutePlaner;

import Client.StandardSettings;
import Gui.DataView;
import Gui.Image.GuiImage;
import Gui.RouteView;
import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.PrimitiveBall;
import nav.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import routePlaner.Route;
import routePlaner.RoutePlanerFaseTwo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
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
        cross = new Cross(new Vector2Dv1(301.0,205.0), new Vector2Dv1(306.0,180.0));
        ArrayList<Vector2Dv1> boundryList = new ArrayList<>();
        boundryList.add(new Vector2Dv1(30,20));
        boundryList.add(new Vector2Dv1(30, 340));
        boundryList.add(new Vector2Dv1(610, 20));
        boundryList.add(new Vector2Dv1(610, 340));
        boundryList.clear();
        boundryList.add(new Vector2Dv1(739.0,1.0));
        boundryList.add(new Vector2Dv1(739.0,319.0));
        boundryList.add(new Vector2Dv1(1.0,1.0));
        boundryList.add(new Vector2Dv1(1.0,319.0));

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
        System.out.println(boundry.getGoalWaypoint(0));
        System.out.println(boundry.getGoalWaypoint(1));
    }

    @Test
    @DisplayName("Test getHeats")
    void getHeatTest(){
        ArrayList<Ball> ball_list = new ArrayList<>();
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

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImageIcon tImage = new ImageIcon("test_img/WIN_20230315_10_32_53_Pro.jpg");
        GuiImage image = new GuiImage(tImage);
        Mat m = image.getMat();
        Imgproc.resize(m,m, new Size(1280, 720), 0, 0, INTER_CUBIC);
        hg.setImage(m);
        hg.getHeats();
    }

    @Test
    @DisplayName("Test correct Angle Before Hardcode")
    void correctAngleBeforeHardcodeTest() {
        RoutePlanerFaseTwo routePlan = new RoutePlanerFaseTwo(simulationRobot, new ArrayList<>(), boundry, cross);
        simulationRobot.setDirection(new Vector2Dv1(1, 1));
        simulationRobot.setPos(1, 1);

        //tjek for om den regner rigtigt når det passer
        Vector2Dv1 vec2 = new Vector2Dv1(2, 2);
        double angle = routePlan.routExecuter.angleBeforeHardcode(simulationRobot, vec2);
        assertTrue(Math.abs(angle) < 0.01);

        // Tjek for at den regner rigtigt når det ikke passer
        vec2 = new Vector2Dv1(1, 0);
        angle = routePlan.routExecuter.angleBeforeHardcode(simulationRobot, vec2);
        assertTrue(angle > 0.77 || angle < 0.79);
    }
}
