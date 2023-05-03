package Test.NAVAlgoPhaseTwo;

import exceptions.BadDataException;
import misc.Lines;
import misc.Robotv1;
import misc.SafetyCircle;
import misc.Vector2Dv1;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class NAVPhaseTwo {
    @Test
    @DisplayName("NAV test phase 2")

    void NAVTest2(){

    }
    /*
    void NAVTest(){
        Robotv1 Robot = new Robotv1(5,1,new Vector2Dv1(1,0));
        PrimitiveBall Ball = new PrimitiveBall(5,5, PrimitiveBall.Status.IN_PLAY);
        PrimitiveBall Ball2 = new PrimitiveBall(1,1, PrimitiveBall.Status.IN_PLAY);


        System.out.printf(Ball.toString() + "\n");
        System.out.printf("Robot info "+String.valueOf(Robot.getxPos()) +" "+ String.valueOf(Robot.getyPos()) +" "+ String.valueOf(Robot.getDirection()));

        System.out.printf("Robot info 2 point " + Robot.getDirection().getPoint());

        Vector2Dv1 VecRobotBall = new Vector2Dv1(Robot.getxPos()-Ball.getxPos(), Robot.getyPos()-Ball.getyPos());
        Vector2Dv1 VecRobotBall2 = new Vector2Dv1(Robot.getxPos(), Robot.getyPos());
        try {
            System.out.printf("\n vector between ball and robot" + VecRobotBall.getPoint());

            System.out.printf("\n vector between ball and robot2 " + VecRobotBall2.distance(Ball.getPosVector()));

            //Lines line = new Lines(Robot.getDirection().getPoint(), Ball.getPoint());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try {

            SafetyCircle BallSafe = new SafetyCircle(Ball.getPosVector(), 1);
            SafetyCircle BallSafe2 = new SafetyCircle(Ball2.getPosVector(), 1);
            if(BallSafe.willHitCircle(Robot)){
                System.out.printf(" \n true for BallSafe");
            }
            if(BallSafe2.willHitCircle(Robot)){
                System.out.printf(" \n true for BallSafe2");
            }
            //Lines liine = new Lines(Robot.getDirection().getPoint(), Ball.getPoint());






        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
    */

}
