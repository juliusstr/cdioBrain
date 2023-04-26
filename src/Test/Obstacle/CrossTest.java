package Test.Obstacle;

import misc.Cross;
import misc.Vector2Dv1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrossTest {
    @Test
    @DisplayName("cross point generator")
    void crossPointGeneratorTest(){
        Vector2Dv1 dirvec = new Vector2Dv1(0 , 1);
        Vector2Dv1 posvec = new Vector2Dv1(0 , 0);
        Cross cross = new Cross(posvec, dirvec);
        //Point 1
        assertEquals(2,cross.crossPoint.get(0).x);
        assertEquals(10,cross.crossPoint.get(0).y);

        //Point 2
        assertEquals(-2,cross.crossPoint.get(1).x);
        assertEquals(10,cross.crossPoint.get(1).y);

        //Point 3
        assertEquals(-2,cross.crossPoint.get(2).x);
        assertEquals(2,cross.crossPoint.get(2).y);

        //Point 4
        assertEquals(-10,cross.crossPoint.get(3).x);
        assertEquals(2,cross.crossPoint.get(3).y);

        //Point 5
        assertEquals(-10,cross.crossPoint.get(4).x);
        assertEquals(-2,cross.crossPoint.get(4).y);

        //Point 6
        assertEquals(-2,cross.crossPoint.get(5).x);
        assertEquals(-2,cross.crossPoint.get(5).y);

        //Point 7
        assertEquals(-2,cross.crossPoint.get(6).x);
        assertEquals(-10,cross.crossPoint.get(6).y);

        //Point 8
        assertEquals(2,cross.crossPoint.get(7).x);
        assertEquals(-10,cross.crossPoint.get(7).y);

        //Point 9
        assertEquals(2,cross.crossPoint.get(8).x);
        assertEquals(-2,cross.crossPoint.get(8).y);

        //Point 10
        assertEquals(10,cross.crossPoint.get(9).x);
        assertEquals(-2,cross.crossPoint.get(9).y);

        //Point 11
        assertEquals(10,cross.crossPoint.get(10).x);
        assertEquals(2,cross.crossPoint.get(10).y);

        //Point 12
        assertEquals(2,cross.crossPoint.get(11).x);
        assertEquals(2,cross.crossPoint.get(11).y);

    }
}
