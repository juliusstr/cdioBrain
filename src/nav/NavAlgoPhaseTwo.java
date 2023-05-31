package nav;


import exceptions.LineReturnException;
import exceptions.NoHitException;
import exceptions.Vector2Dv1ReturnException;
import misc.*;
import misc.ball.Ball;

import java.util.ArrayList;

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

    public String nextCommand() {
        Vector2Dv1 dir = target.getPosVector().getSubtracted(robot.getPosVector());
        try {
            cross.hit(robot, dir);
        } catch (LineReturnException e) {
            System.out.println(e.line.toString());;
        } catch (Vector2Dv1ReturnException e) {
            System.out.println(e.vector2D.toString());
        } catch (NoHitException e) {
            System.out.println("No hit");
        }
        return "";
    };

    /**
     * Checks if there is a hit on the cross
     * @return  True if there is a hit
     *          False if there is no hit
     */
    /*TODO @Ulleren OPTIMERING, lave det så der bliver returneret en line i stedet for en bool om der er hit.
        Det er så denne line og dens safety circles der skal testes fremover..
        return evt også en cirkel. Forskellige returerings typer kan laves ved exceptions.
     */
    public boolean hitOnCrossToTarget(){
        Vector2Dv1 dir = target.getPosVector().getSubtracted(robot.getPosVector());
        try {
            cross.hit(robot, dir);
        } catch (LineReturnException e) {
            System.out.println(e.line.toString());;
            return true;
        } catch (Vector2Dv1ReturnException e) {
            System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            System.out.println("No hit");
            return false;
        }
        return false;
    }


}
