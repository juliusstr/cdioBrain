package Test.Robot;

import Client.StandardSettings;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotTest {


    @Test
    @DisplayName("simpel robot test")
    void simpelRobotTest(){
        Robotv1 robot = new Robotv1(0,0,new Vector2Dv1(0.0,1.0));
        assertEquals(0,robot.getyPos());
        assertEquals(0,robot.getxPos());
        assertEquals(0,robot.getDirection().x);
        assertEquals(1,robot.getDirection().y);
    }

    @Test
    @DisplayName("robot scale test")
    void robotScaleTest(){
        Robotv1 robot = new Robotv1(0,0,new Vector2Dv1(0.0,1.0));
        robot.setScale(new Vector2Dv1(500,500),new Vector2Dv1(505,505));
        Ball greenBall = new Ball(20,20, StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.GREEN,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.ROBOT_FRONT);
        Ball blackBall = new Ball(15,20, StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.BLACK,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.ROBOT_BACK);
        robot.updatePos(greenBall,blackBall);
        assertEquals(22,robot.getyPos());
        assertEquals(28,robot.getxPos());
        assertEquals(5,robot.getDirection().x);
        assertEquals(0,robot.getDirection().y);
    }
    @Test
    @DisplayName("robot scale test no click")
    void robotScaleTestNoClick(){
        Robotv1 robot = new Robotv1(0,0,new Vector2Dv1(0.0,1.0));
        robot.setScale(new Vector2Dv1(1,1),new Vector2Dv1(1,1));
        Ball greenBall = new Ball(20,20, StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.GREEN,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.ROBOT_FRONT);
        Ball blackBall = new Ball(15,20, StandardSettings.BALL_RADIUS_PX, BallClassifierPhaseTwo.BLACK,true, PrimitiveBall.Status.UNKNOWN,-1, Ball.Type.ROBOT_BACK);
        robot.updatePos(greenBall,blackBall);
        assertEquals(20,robot.getyPos());
        assertEquals(20,robot.getxPos());
        assertEquals(5,robot.getDirection().x);
        assertEquals(0,robot.getDirection().y);
    }
}
