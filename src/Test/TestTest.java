package Test;

import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTest {
    Ball ball;

    @BeforeEach
    void setUp(){
        ball = new Ball(0, 0, 4, new Color(1,1,1), true, PrimitiveBall.Status.UNKNOWN, -1, Ball.Type.UNKNOWN);
    }

    @Test
    @DisplayName("simpel ball test")
    void simpelBallTest(){
        assertEquals(0, ball.getPoint().x);
    }
}
