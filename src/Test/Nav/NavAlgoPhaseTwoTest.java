package Test.Nav;

import exceptions.NoHitException;
import exceptions.NoRouteException;
import misc.*;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import misc.simulation.simulator;
import nav.NavAlgoPhaseTwo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.naming.SizeLimitExceededException;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavAlgoPhaseTwoTest {
    Robotv1 simulationRobot;
    Ball target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;
    String nextCommand;
    public static final double DISTANCE_ERROR = NavAlgoPhaseTwo.TARGET_DISTANCE_ERROR;

    /**
     * setUp for all the tests. Makes boundarys, a robot and a ball.
     */
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
        assertTrue(navPlanner.hitOnCrossToTarget());
    }
    @Test
    @DisplayName("hit on cross")
    void hitOnCrossTest(){
        simulationRobot.setPos(100,250);
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        assertTrue(navPlanner.hitOnCrossToTarget());
    }

    @Test
    @DisplayName("No hit on cross")
    void nohitOnCrossTest(){
        simulationRobot.setPos(470,340);
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        assertTrue(!navPlanner.hitOnCrossToTarget());


    }

    @Test
    @DisplayName("Hit on critical circle")
    void simpelHitOnCritCircleTest(){

        Vector2Dv1 Corner = new Vector2Dv1(200,360/2);
        Vector2Dv1 Path = new Vector2Dv1(target.getxPos()-simulationRobot.getxPos(),target.getyPos()-simulationRobot.getyPos());
        SafetyCircle circle = new SafetyCircle(Corner, 20);
        //simulationRobot.setDirection(Path);
        assertTrue(circle.willHitCircle(simulationRobot.getPosVector(), Path).size() != 0);
    }

    /**
     * Test to check if nav can navigate with simulating a ball and a robot.
     * @throws NoHitException
     */
    @Test
    @DisplayName("simulate one ball")
    void simpelCollectTest() throws NoRouteException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.wayPointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
    }

    @Test
    @DisplayName("Test next command")
    void nextCommandTest(){
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.getWaypoints().add(new Vector2Dv1(200, 180));
        int iterationCount = 1000;
        while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, navPlanner.nextCommand(), target.getPosVector()) && iterationCount-- > 0);
        if(navPlanner.getWaypoints().size() == 0) navPlanner.getWaypoints().add(new Vector2Dv1(200, 180));
        assertEquals(false, simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, navPlanner.nextCommand(), target.getPosVector()));
    }

    /**
     * test for straightline route.
     */
    @Test
    @DisplayName("Waypoint generator simple test")
    void simpleWaypointGenTest(){
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        target.setYPos(target.getyPos()+1);
        target.setXPos(20);
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        try {
            navPlanner.wayPointGenerator();
        } catch (NoRouteException e){
            throw new RuntimeException(e);
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Waypoint generator test")
    void WaypointGenTest(){
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        try {
            navPlanner.wayPointGenerator();
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        }
        System.out.println(navPlanner.waypoints);
        System.err.println(navPlanner.routes);
        assertTrue(true);
    }

}
