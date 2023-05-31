package nav;


import exceptions.NoHitException;
import misc.*;
import misc.ball.Ball;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Vector;

public class NavAlgoPhaseTwo {
    private Robotv1 robot;
    private Ball target;
    private Cross cross;
    private Boundry boundry;
    private ArrayList<Ball> ballsToAvoid;

    public NavAlgoPhaseTwo(){}

    public void updateNav(Robotv1 robot, Ball target, Cross cross, Boundry boundry, ArrayList<Ball> ballsToAvoid){
        this.robot = robot;
        this.target = target;
        this.cross = cross;
        this.boundry = boundry;
        this.ballsToAvoid = ballsToAvoid;
    }

    public String nextCommand() throws NoHitException {
        Vector2Dv1 dir = target.getPosVector().getSubtracted(robot.getPosVector());
        System.out.println(cross.hit(robot, dir));
        return "";
    };

    public Lines hitOnCrossToTarget(){
        Vector2Dv1 dir = target.getPosVector().getSubtracted(robot.getPosVector());
        try {
            return cross.hit(robot, dir);
        } catch (NoHitException e) {
            return null;
        }
    }


}
