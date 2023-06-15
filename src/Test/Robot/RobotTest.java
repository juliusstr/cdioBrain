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
}
