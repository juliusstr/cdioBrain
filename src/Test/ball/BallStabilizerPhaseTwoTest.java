package Test.ball;

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
            throw new RuntimeException(e);
        }
        balls.clear();
        balls.add(ball1);
        try {
            stabilizer.stabilizeBalls(balls);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(stabilizer.getStabelBalls().contains(ball));
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(stabilizer.getStabelBalls().contains(ball1));
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }
        balls.clear();
        for (int i = 0; i < BallStabilizerPhaseTwo.TIME_TO_LIVE-1; i++) {
            try {
                stabilizer.stabilizeBalls(balls);
            } catch (TypeException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            boolean temp = stabilizer.getStabelBalls().contains(ball);
            assertTrue(!temp);
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }
        try {
            boolean temp = stabilizer.getStabelBalls().contains(ball1);
            assertTrue(temp);
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }

    }



}
