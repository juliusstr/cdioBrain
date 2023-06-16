package Test.ball;

import exceptions.NoWaypointException;
import misc.Boundry;
import misc.Cross;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;

public class BallClassifierPhaseTwoTest {

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
    @DisplayName("Pair ball test")
    void pairBallTest() throws NoWaypointException {
        /**
         * test made on ball radius 8, crit zone 40.
         */
        ballsToAvoid.add(new Ball(new Vector2Dv1(185, 260)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(186, 292)));
        ballsToAvoid.add(new Ball(new Vector2Dv1(100, 300)));
        BallClassifierPhaseTwo.ballSetPlacement(ballsToAvoid,boundry,cross);

        for (Ball ball:
             ballsToAvoid) {
            System.out.println(ball.getPlacement());
            System.out.println(ball.getPickUpPoint());
            System.out.println(ball.getLineUpPoint());
        }

        assertEquals(ballsToAvoid.get(0).getPlacement(), Ball.Placement.PAIR);
        assertEquals(ballsToAvoid.get(1).getPlacement(), Ball.Placement.PAIR);
        assertEquals(ballsToAvoid.get(2).getPlacement(), Ball.Placement.EDGE);

        //actual values calculated in geogebra
        //dont mide the expected and actual values are fliped.
        assertEquals((int) ballsToAvoid.get(0).getPickUpPoint().x, 182);
        assertEquals((int) ballsToAvoid.get(1).getPickUpPoint().x, 255);
        assertEquals((int) ballsToAvoid.get(2).getPickUpPoint().x, 100);

        assertEquals((int) ballsToAvoid.get(0).getPickUpPoint().y, 190);
        assertEquals((int) ballsToAvoid.get(1).getPickUpPoint().y, 289);
        assertEquals((int) ballsToAvoid.get(2).getPickUpPoint().y, 230);

    }
}
