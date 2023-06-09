package Test.Nav;

import Client.StandardSettings;
import exceptions.NoHitException;
import exceptions.NoRouteException;
import misc.*;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.PrimitiveBall;
import misc.simulation.simulator;
import nav.NavAlgoPhaseTwo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
     * setUp for all the tests. Makes boundarys, a robot and a ball. The robot is positioned opposite of the ball, with the cross in the middle of them.
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
        target = new Ball(500, 360/2, StandardSettings.BALL_RADIUS_PX, new Color(1,1,1), true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UKNOWN);
        ballsToAvoid = new ArrayList<>();
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
        Zone circle = new Zone(Corner, 20);
        //simulationRobot.setDirection(Path);
        assertTrue(circle.willHitZone(simulationRobot.getPosVector(), Path).size() != 0);
    }

    /**
     * Test to check if nav can navigate with simulating a ball and a robot.
     * @throws NoHitException
     */
    @Test
    @DisplayName("simulate one ball")
    void simpelCollectTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
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
        Vector2Dv1 waypoint = new Vector2Dv1(200, 180);
        navPlanner.getWaypoints().add(waypoint);
        int iterationCount = 1000;
        while(simulator.updatePosSimple(waypoint, simulationRobot, navPlanner.nextCommand(), waypoint) && iterationCount-- > 0);
        assertEquals(false, simulator.updatePosSimple(waypoint, simulationRobot, navPlanner.nextCommand(), waypoint));
    }

    /**
     * test for straightline route.
     */
    @Test
    @DisplayName("Waypoint generator simple test")
    void simpleWaypointGenTest() throws NoRouteException, TimeoutException {
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        target.setYPos(target.getyPos()+1);
        target.setXPos(20);
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        assertTrue(true);
    }

    @Test
    @DisplayName("Waypoint generator test")
    void waypointGenTest() throws NoRouteException, TimeoutException {
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        System.out.println(navPlanner.waypoints);
        System.err.println(navPlanner.routes);
        assertTrue(true);
    }

    @Test
    @DisplayName("waypointgenerator V2 kør under krydset test")
    void waypointV2UnderKrydsTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 282)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println(navPlanner.routes);
    }

    @Test
    @DisplayName("waypointgenerator V2 kør over krydset test")
    void waypointV2OverKrydsTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(303, 78)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println(navPlanner.routes);
    }
    @Test
    @DisplayName("waypointgenerator V2 bold foran krydset test")
    void waypointV2ForanKrydsTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(218, 197)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("Number of routes: " + navPlanner.routes.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 bold bag krydset test")
    void waypointV2BagKrydsTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(422, 163)));

        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("Number of routes: " + navPlanner.routes.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 4 bolde test")
    void waypointV2_4BoldeTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(422, 163)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(218, 197)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(303, 78)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 282)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("Number of routes: " + navPlanner.routes.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 bold væk fra kryds oppe test")
    void waypointV2BoldOverTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
    }

    @Test
    @DisplayName("waypointgenerator V2 bold væk fra kryds oppe og nede test")
    void waypointV2BoldOverOgUnderTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 3 flyvende bolde test")
    void waypointV2Bold3FlyvendeBolde() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 2 flyvende bolde test med stor gruppe 2")
    void waypointV2Bold2FlyvendeBoldeStorgruppe2() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(330, 256)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 3 flyvende bolde test med stor gruppe 2")
    void waypointV2Bold3FlyvendeBoldeStorgruppe2() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(330, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(440, 180)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 3 flyvende bolde test med stor gruppe 2 og gruppe ved target")
    void waypointV2Bold3FlyvendeBoldeStorgruppe2medgruppevedtarget() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(330, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(440, 180)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(506, 118)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("waypointgenerator V2 4 flyvende bolde test med stor gruppe 2 og gruppe ved target")
    void waypointV2Bold4FlyvendeBoldeStorgruppe2medgruppevedtarget() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(330, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(440, 180)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(506, 118)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(441, 285)));
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);
        navPlanner.waypointGenerator();
        ArrayList<Vector2Dv1> routeCopy = (ArrayList<Vector2Dv1>) navPlanner.getWaypoints().clone();
        ArrayList<ArrayList<Vector2Dv1>> routsCopy = (ArrayList<ArrayList<Vector2Dv1>>)  navPlanner.routes.clone();
        int iterationCount = 1000;
        String command = "";
        do {
            command = navPlanner.nextCommand();
        } while(simulator.updatePosSimple(navPlanner.getWaypoints().get(0), simulationRobot, command,target.getPosVector()) && iterationCount-- > 0);
        assertEquals(simulator.updatePosSimple(target.getPosVector(), simulationRobot, command,target.getPosVector()), false);
        System.out.println("The route: " + routeCopy);
        System.out.println("number of waypoints: " + routeCopy.size());
        System.out.println("total calculated routs: " + routsCopy.size());
    }
    @Test
    @DisplayName("ZoneGroupeId balls next to cross & away from cross")
    void zoneGroupeIdTest(){
        Vector2Dv1 crossCorner = new Vector2Dv1(cross.crossPoint.get(0));
        System.out.println("corner pos: " + crossCorner);
        Vector2Dv1 crossCenter = cross.pos;
        Vector2Dv1 dir = crossCorner.getSubtracted(crossCenter).getNormalized();
        System.out.println("cross pos: " + crossCenter);
;
        final int ballZoneRadius = Zone.CRITICAL_ZONE_RADIUS + StandardSettings.BALL_RADIUS_PX;
        final int crossZoneRadius = Zone.CRITICAL_ZONE_RADIUS;

        Ball hitZoneBall = new Ball(crossCorner.getAdded(dir.getMultiplied(ballZoneRadius+crossZoneRadius-10)),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball edgeZoneBall = new Ball(crossCorner.getAdded(dir.getMultiplied(ballZoneRadius*2+crossZoneRadius)),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball noHitZoneBall = new Ball(crossCorner.getAdded(dir.getMultiplied(ballZoneRadius*5+crossZoneRadius+10)),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);
        Ball noHitZoneBall2 = new Ball(crossCorner.getAdded(dir.getMultiplied(ballZoneRadius*6+crossZoneRadius+10)),StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.BALL);

        ballsToAvoid.add(edgeZoneBall);
        ballsToAvoid.add(hitZoneBall);
        ballsToAvoid.add(noHitZoneBall2);
        ballsToAvoid.add(noHitZoneBall);

        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        navPlanner.updateNav(simulationRobot, target, cross, boundry, ballsToAvoid);

        navPlanner.updateZoneGroupIdOnBallsToAvoid();
        System.out.println("hit" + hitZoneBall);
        System.out.println("edge" + edgeZoneBall);
        System.out.println("no hit" + noHitZoneBall);
        System.out.println("no hit2" + noHitZoneBall2);
        assertTrue(hitZoneBall.getZoneGroupId() == 2);
        assertTrue(edgeZoneBall.getZoneGroupId() == 2);
        assertTrue(noHitZoneBall2.getZoneGroupId() == 3);
        assertTrue(noHitZoneBall.getZoneGroupId() == 3);
    }

}
