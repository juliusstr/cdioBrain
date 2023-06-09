package Test.Nav;

import exceptions.NoRouteException;
import misc.*;
import misc.ball.Ball;
import nav.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        System.out.println(wr.getRoute());
        System.err.println(wr.getCost());
        assertTrue(true);
    }

    @Test
    @DisplayName("Waypoint generator test")
    void waypointGenTest() throws NoRouteException, TimeoutException {
        start.set(start.x,start.y+40);
        ballsToAvoid.add(new Ball(new Vector2Dv1(337, 330)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(200, 120)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(190, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(330, 256)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(440, 180)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(506, 118)));
        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(start, target, cross, boundry, ballsToAvoid).waypointRoute;
        System.out.println(wr.getRoute());
        System.err.println(wr.getCost());
        assertTrue(true);
    }

    @Test
    @DisplayName("Waypoint generator test out of bounds waypoint test")
    void waypointGenTestOutOfBoundsTest() throws TimeoutException {
        //start.set(start.x,start.y+40);
        ballsToAvoid.add(new Ball(new Vector2Dv1(228, 146)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(68, 115)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(154, 119)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(321, 274)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(322, 354)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(344, 486)));

        WaypointGenerator.WaypointRoute wr = null;
        try {
            wr = new WaypointGenerator(start, target, cross, boundry, ballsToAvoid).waypointRoute;
        } catch (NoRouteException e) {
            assertTrue(true);
            return;
        }
        System.out.println(wr.getRoute());
        System.err.println(wr.getCost());
        assertTrue(false);
    }




}
