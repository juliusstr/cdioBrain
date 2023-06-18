package Test.Obstacle;

import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoundryTest {

    Robotv1 simulationRobot;
    Vector2Dv1 start;
    Vector2Dv1 target;
    Cross cross;
    Boundry boundry;
    ArrayList<Ball> ballsToAvoid;

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
    @DisplayName("waypoint safe distance from border")
    void waypointDistanceSafeTest(){
        assertTrue(!boundry.waypointSafeDistFromBoundary(new Vector2Dv1(35,25)));
        assertTrue(boundry.waypointSafeDistFromBoundary(new Vector2Dv1(66,41)));
    }
}
