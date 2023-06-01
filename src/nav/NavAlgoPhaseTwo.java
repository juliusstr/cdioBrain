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
    private ArrayList<Vector2Dv1> waypoints;

    public static final double ANGLE_ERROR = 0.04;
    public static final double DISTANCE_ERROR = 19;

    public NavAlgoPhaseTwo(){}

    public void updateNav(Robotv1 robot, Ball target, Cross cross, Boundry boundry, ArrayList<Ball> ballsToAvoid){
        this.robot = robot;
        this.target = target;
        this.cross = cross;
        this.boundry = boundry;
        this.ballsToAvoid = ballsToAvoid;
        this.waypoints = new ArrayList<>();
    }

    public String nextCommand() {

        String command = "";

        Vector2Dv1 dir = waypoints.get(0).getSubtracted(robot.getPosVector());

        //*** cal dist and angle ***
        double distDelta = Math.sqrt(Math.pow((waypoints.get(0).x- robot.getxPos()), 2)+Math.pow((waypoints.get(0).y- robot.getyPos()), 2));

        double dot = dir.dot(robot.getDirection());
        double cross = dir.cross(robot.getDirection());
        double angleDelta;

        //*** Close enough ***
        if(distDelta < DISTANCE_ERROR){
            System.out.printf("On ball\n");
            return "stop -t -d";
        }

        //***turn***
        angleDelta = Math.atan2(cross, dot);

        //System.out.println("delta angle: " + angleDelta);
        //angleDelta = Math.acos(dot/dist);
        if (Math.abs(angleDelta) > ANGLE_ERROR) {
            command += "turn -";
            if (angleDelta > 0) {
                command += "l";
            } else {
                command += "r";
            }
            double turnSpeed = Math.abs(angleDelta / 2);
            if (turnSpeed > 0.2)
                turnSpeed = 0.2;
            command += " -s" + String.format("%.2f", turnSpeed).replace(',','.') + "";
        } else {
            command += "stop -t";
        }

        //***drive***
        if(Math.abs(angleDelta) > ANGLE_ERROR*2){
            System.out.printf("command = %s\n", command);
            return command;// + ";stop -d -t";
        }
        if(distDelta > DISTANCE_ERROR){
            double speed = distDelta/2;
            if (speed > 5)
                speed = 5;
            command += ";drive -s" + String.format("%.2f", speed).replace(',','.');
        } else {
            command += ";stop -d -t";
        }
        System.out.printf("command = %s\n", command);
        return command;
    }

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

    public void wayPointGenerator(){

    }

    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
    }
}
