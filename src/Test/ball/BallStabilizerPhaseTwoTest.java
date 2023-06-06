package Test.ball;

import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.TypeException;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BallStabilizerPhaseTwoTest {

    BallStabilizerPhaseTwo stabilizer;

    @BeforeEach
    void setUp(){
        stabilizer = new BallStabilizerPhaseTwo();
    }

    @Test
    @DisplayName("init ball stabilizer phase two")
    void initTest(){
        assertNotNull(stabilizer);
    }

    @Test
    @DisplayName("init ball stabilizer phase two with static ball data")
    void initTestWithData(){
        Ball ball = new Ball(0,0,3, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        Ball ball1 = new Ball(10,10,3, BallClassifierPhaseTwo.WHITE,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        ArrayList<Ball> balls = new ArrayList<>();
        balls.add(ball);
        try {
            stabilizer.stabilizeBalls(balls);
        } catch (TypeException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        balls.clear();
        balls.add(ball1);
        try {
            stabilizer.stabilizeBalls(balls);
        } catch (TypeException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        try {
            assertTrue(stabilizer.getStabelBalls().contains(ball));
        } catch (NoDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        try {
            assertTrue(stabilizer.getStabelBalls().contains(ball1));
        } catch (NoDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        balls.clear();
        for (int i = 0; i < BallStabilizerPhaseTwo.TIME_TO_LIVE-1; i++) {
            try {
                stabilizer.stabilizeBalls(balls);
            } catch (TypeException e) {
                assertTrue(false);
                throw new RuntimeException(e);
            }
        }
        try {
            boolean temp = stabilizer.getStabelBalls().contains(ball);
            assertTrue(!temp);
        } catch (NoDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        try {
            boolean temp = stabilizer.getStabelBalls().contains(ball1);
            assertTrue(temp);
        } catch (NoDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        balls.clear();
        for (int i = 0; i < 5; i++) {
            try {
                stabilizer.stabilizeBalls(balls);
            } catch (TypeException e) {
                assertTrue(false);
                throw new RuntimeException(e);
            }
        }
        try {
            boolean temp = stabilizer.getStabelBalls().contains(ball);
            assertTrue(false);
        } catch (NoDataException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("init ball stabilizer phase two with static robot data")
    void initTestWithRobotData(){
        Ball ball1 = new Ball(0,0,3, BallClassifierPhaseTwo.GREEN,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        Ball ball2 = new Ball(5,5,3, BallClassifierPhaseTwo.BLACK,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        Ball ball11 = new Ball(20,20,3, BallClassifierPhaseTwo.GREEN,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        Ball ball12 = new Ball(25,25,3, BallClassifierPhaseTwo.BLACK,false, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.UKNOWN);
        ArrayList<Ball> balls = new ArrayList<>();
        balls.add(ball1);
        balls.add(ball2);
        try {
            for (int i = 0; i < 20; i++) {
                Point point = ball1.getPoint();
                point.x--;
                ball1.setPos(point);
                point = ball2.getPoint();
                point.x--;
                ball2.setPos(point);
                stabilizer.stabilizeBalls(balls);
                ArrayList<Ball> robotCirce = null;
                try {
                    robotCirce = stabilizer.getStabelRobotCirce();
                } catch (BadDataException e) {
                    assertTrue(false);
                    throw new RuntimeException(e);
                }
                assertTrue(robotCirce.get(0).getId() == 1);
                assertTrue(robotCirce.get(1).getId() == 2);
            }
            stabilizer.stabilizeBalls(balls);
            ArrayList<Ball> robotCirce = null;
            try {
                robotCirce = stabilizer.getStabelRobotCirce();
            } catch (BadDataException e) {
                assertTrue(false);
                throw new RuntimeException(e);
            }
            assertTrue(robotCirce.get(0).getId() == 1);
            assertTrue(robotCirce.get(1).getId() == 2);
        } catch (TypeException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        try {
            ArrayList<Ball> robotCirce =  stabilizer.getStabelRobotCirce();
            assertTrue(robotCirce.get(0).getId() == 1);
            assertTrue(robotCirce.get(1).getId() == 2);
        } catch (BadDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        balls.clear();
        balls.add(ball11);
        balls.add(ball12);
        try {
            for (int i = 0; i < 20; i++) {
                Point point = ball1.getPoint();
                point.x--;
                ball1.setPos(point);
                point = ball2.getPoint();
                point.x--;
                ball2.setPos(point);
                stabilizer.stabilizeBalls(balls);
                ArrayList<Ball> robotCirce = null;
                try {
                    robotCirce = stabilizer.getStabelRobotCirce();
                } catch (BadDataException e) {
                    assertTrue(false);
                    throw new RuntimeException(e);
                }
                assertTrue(robotCirce.get(0).getId() == 3);
                assertTrue(robotCirce.get(1).getId() == 4);
            }
            stabilizer.stabilizeBalls(balls);
            ArrayList<Ball> robotCirce = null;
            try {
                robotCirce = stabilizer.getStabelRobotCirce();
            } catch (BadDataException e) {
                assertTrue(false);
                throw new RuntimeException(e);
            }
            assertTrue(robotCirce.get(0).getId() == 3);
            assertTrue(robotCirce.get(1).getId() == 4);
        } catch (TypeException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
        try {
            ArrayList<Ball> robotCirce =  stabilizer.getStabelRobotCirce();
            assertTrue(robotCirce.get(0).getId() == 3);
            assertTrue(robotCirce.get(1).getId() == 4);
        } catch (BadDataException e) {
            assertTrue(false);
            throw new RuntimeException(e);
        }
    }

}
