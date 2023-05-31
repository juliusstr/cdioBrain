package Test.Nav;

import exceptions.NoHitException;
import misc.*;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import misc.simulation.simulator;
import nav.NavAlgoPhaseTwo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavAlgoPhaseTwoTest {
    Robotv1 simulationRobot;
    Ball target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;
    String nextCommand;
    public static final double ANGLE_ERROR = 0.04;
    public static final double DISTANCE_ERROR = 20;


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
        target = new Ball(400, 360/2, 4, new Color(1,1,1), true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UKNOWN);
    }

    @Test
    @DisplayName("Simpel hit on cross")
    void simpelHitOnCrossTest(){
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        Lines line = navPlanner.hitOnCrossToTarget();
        assertTrue(line.toString().equals("Lines{p1=Vector2d[295.0, 184.0], p2=Vector2d[295.0, 176.0], hitPoint=Vector2d[295.0, 180.0]}"));
    }
    @Test
    @DisplayName("hit on cross")
    void hitOnCrossTest(){
        simulationRobot.setPos(100,250);
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        Lines line = navPlanner.hitOnCrossToTarget();
        assertTrue(line.toString().equals("Lines{p1=Vector2d[316.0, 205.0], p2=Vector2d[316.0, 184.0], hitPoint=Vector2d[316.0, 199.6]}"));
    }

    @Test
    @DisplayName("No hit on cross")
    void nohitOnCrossTest(){
        simulationRobot.setPos(130,300);
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        Lines line = navPlanner.hitOnCrossToTarget();
        assertTrue(line == null);


    }

    @Test
    @DisplayName("Hit on critical circle")
    void simpelHitOnCritCircleTest(){

        Vector2Dv1 Corner = new Vector2Dv1(200,360/2);
        Vector2Dv1 Path = new Vector2Dv1(target.getxPos()-simulationRobot.getxPos(),target.getyPos()-simulationRobot.getyPos());
        SafetyCircle circle = new SafetyCircle(Corner, 20);
        simulationRobot.setDirection(Path);
        boolean crithit = circle.willHitCircle(simulationRobot);
        assertTrue(crithit);
    }

    @Test
    @DisplayName("simulate one ball")
    void simpelCollectTest() throws NoHitException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        int iterationCount = 10000;
        while(simulator.updatePos(this.target, simulationRobot, navPlanner.nextCommand()) && iterationCount-- > 0);
        assertEquals(simulator.updatePos(this.target, simulationRobot, navPlanner.nextCommand()), false);
    }
}
