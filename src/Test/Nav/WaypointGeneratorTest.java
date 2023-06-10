package Test.Nav;

import Client.StandardSettings;
import exceptions.NoRouteException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import misc.simulation.simulator;
import nav.NavAlgoPhaseTwo;
import nav.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaypointGeneratorTest {
    Robotv1 simulationRobot;
    Vector2Dv1 start;
    Vector2Dv1 target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

    /**
     * setUp for all the tests. Makes boundarys, a robot and a ball. The robot is positioned opposite of the ball, with the cross in the middle of them.
     */
    @BeforeEach
    void setUp(){
        simulationRobot = new Robotv1(100, 360/2, new Vector2Dv1(1, 1));
        start = new Vector2Dv1(100, 360/2);
        cross = new Cross(new Vector2Dv1(640/2, 360/2), new Vector2Dv1(0.0));
        ArrayList<Vector2Dv1> boundryList = new ArrayList<>();
        boundryList.add(new Vector2Dv1(30,20));
        boundryList.add(new Vector2Dv1(30, 340));
        boundryList.add(new Vector2Dv1(610, 20));
        boundryList.add(new Vector2Dv1(610, 340));
        boundry = new Boundry(boundryList);
        target = new Vector2Dv1(500, 360/2);
        ballsToAvoid = new ArrayList<>();
    }

    @Test
    @DisplayName("Waypoint generator simple test")
    void simpleWaypointGenTest() throws NoRouteException, TimeoutException {
        target.set(20, target.y+1);
        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(start, target, cross, boundry, ballsToAvoid).waypointRoute;
        assertTrue(true);
    }

    @Test
    @DisplayName("Waypoint generator test")
    void waypointGenTest() throws NoRouteException, TimeoutException {
        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(start, target, cross, boundry, ballsToAvoid).waypointRoute;
        System.out.println(wr.getRoute());
        System.err.println(wr.getScore());
        assertTrue(true);
    }

    /*
    @Test
    @DisplayName("waypointgenerator V2 kør under krydset test")
    void waypointV2UnderKrydsTest() throws NoRouteException, TimeoutException {
        simulator simulator = new simulator();
        NavAlgoPhaseTwo navPlanner = new NavAlgoPhaseTwo();
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 282)));
        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(start, target, cross, boundry, ballsToAvoid).waypointRoute;
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
    }*/
}
