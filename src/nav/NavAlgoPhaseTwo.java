package nav;


import exceptions.LineReturnException;
import exceptions.NoHitException;
import exceptions.Vector2Dv1ReturnException;
import misc.*;
import misc.ball.Ball;

import javax.naming.SizeLimitExceededException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class NavAlgoPhaseTwo {
    /**
     * SEARCH_RAD_TO_TURN is the step size used to search for waypoints.
     */
    public static final double SEARCH_RAD_TO_TURN = (Math.PI/180)*1;
    public static final int WATCHDOG_STEP_HALVS = 10;
    public static final int WATCHDOG_MAX_TURNS_IN_ROUTE = 20;
    private Robotv1 robot;
    private Ball target;
    private Cross cross;
    private Boundry boundry;
    private ArrayList<Ball> ballsToAvoid;
    public ArrayList<Vector2Dv1> waypoints;

    public static final double ANGLE_ERROR = Math.PI/180;
    public static final double TARGET_DISTANCE_ERROR = 45;
    public static final double WAYPOINT_DISTANCE_ERROR = 5;

    public NavAlgoPhaseTwo(){}

    public void updateNav(Robotv1 robot, Ball target, Cross cross, Boundry boundry, ArrayList<Ball> ballsToAvoid){
        this.robot = robot;
        this.target = target;
        this.cross = cross;
        this.boundry = boundry;
        this.ballsToAvoid = ballsToAvoid;
        waypoints = new ArrayList<>();
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
        if(distDelta < TARGET_DISTANCE_ERROR && waypoints.size() == 1){
            if (waypoints.size() > 1){
                waypoints.remove(0);
                return nextCommand();
            }
            System.out.printf("On ball\n");
            return "stop -t -d";
        }
        if(distDelta < WAYPOINT_DISTANCE_ERROR && waypoints.size() != 1){
            System.err.println("On waypoint");
            return "stop -t";
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
        if(Math.abs(angleDelta) > ANGLE_ERROR*4){
            System.out.printf("command = %s\n", command);
            return command + ";stop -d";
        }
        if(distDelta > WAYPOINT_DISTANCE_ERROR){
            double speed = distDelta/2;
            if (speed > 5)
                speed = 5;
            command += ";drive -s" + String.format("%.2f", speed).replace(',','.');
        } else {
            command += ";stop -d;stop -t";
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
            cross.hit(robot.getPosVector(), dir);
        } catch (LineReturnException e) {
            //System.out.println(e.line.toString());;
            return true;
        } catch (Vector2Dv1ReturnException e) {
            //System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            //System.out.println("No hit");
            return false;
        }
        return false;
    }
    public boolean hitOnCrossToTargetFromPosAndDir(Vector2Dv1 pos,Vector2Dv1 dir){
        try {
            cross.hit(pos, dir);
        } catch (LineReturnException e) {
            //System.out.println(e.line.toString());;
            return true;
        } catch (Vector2Dv1ReturnException e) {
            //System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            //System.out.println("No hit");
            return false;
        }
        return false;
    }

    public boolean hitOnCrossToTargetVectorFromPos(Vector2Dv1 pos,Vector2Dv1 target){
        Vector2Dv1 dir = target.getSubtracted(pos);
        try {
            cross.hit(robot.getPosVector(), dir);
        } catch (LineReturnException e) {
            //System.out.println(e.line.toString());;
            return true;
        } catch (Vector2Dv1ReturnException e) {
            //System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            //System.out.println("No hit");
            return false;
        }
        return false;
    }

    /**
     * Populates ArrayList<Vector2Dv1> waypoints with waypoints to target.
     * Index 0 in list will be next waypoint for straight line nav on the way to target.
     */
    public void wayPointGenerator() throws SizeLimitExceededException, TimeoutException {
        ArrayList<ArrayList<Vector2Dv1>> routes = new ArrayList<>();


        Vector2Dv1 localTargetVector = target.getPosVector();
        boolean hitToTarget = hitOnCrossToTargetVectorFromPos(robot.getPosVector(),localTargetVector);
        if(!hitToTarget){
            waypoints.add(target.getPosVector());
            return;
        }

        //first cc around obstruction. first turn
        ArrayList<Vector2Dv1> route = new ArrayList<>();
        route.add(rotateVector(robot.getPosVector(),localTargetVector, -1));
        //other turns
        localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        int watchdog = 0;
        while (hitOnCrossToTargetVectorFromPos(route.get(route.size()-1),localTargetVector) && watchdog++ < WATCHDOG_MAX_TURNS_IN_ROUTE ){
            route.add(rotateVector(route.get(route.size()-1),localTargetVector, 1));
            localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        }
        if(watchdog > WATCHDOG_MAX_TURNS_IN_ROUTE)
            throw new SizeLimitExceededException("Too many turns in cc route");
        route.add(target.getPosVector());
        routes.add(route);

        //first c around obstruction. first turn
        localTargetVector = target.getPosVector();
        route = new ArrayList<>();
        route.add(rotateVector(robot.getPosVector(),localTargetVector, 1));
        //other turns
        localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        watchdog = 0;
        while (hitOnCrossToTargetVectorFromPos(route.get(route.size()-1),localTargetVector) && watchdog++ < WATCHDOG_MAX_TURNS_IN_ROUTE ){
            route.add(rotateVector(route.get(route.size()-1),localTargetVector, -1));
            localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        }
        if(watchdog > WATCHDOG_MAX_TURNS_IN_ROUTE)
            throw new SizeLimitExceededException("Too many turns in cc route");
        route.add(target.getPosVector());
        routes.add(route);
        waypoints = shortestRoute(routes);

    }

    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
    }

    /**
     * Turn rTVector CC until out of Cross or and critical zone on cross.
     * @param rTVector vector for pos or waypoint to target. rTVector will be changes in this function. Pass a clone.
     * @param pos position vector. Should be robot pos og waypoint pos.
     * @param cOrCC 1 = CC, -1 = C;
     * @return Vector2Dv1 next point after turn.
     * @exception TimeoutException Thrown if run local watchdog is triggered
     */
    private Vector2Dv1 rotateVector(Vector2Dv1 pos, Vector2Dv1 rTVector, int cOrCC) throws TimeoutException {
        Vector2Dv1 dir = rTVector.getSubtracted(pos);
        while (hitOnCrossToTargetFromPosAndDir(pos, dir)) {
            dir.rotateBy(SEARCH_RAD_TO_TURN * cOrCC);
        }
        Vector2Dv1 waypoint;
        double step = -1;
        int i = 0;//watchdog
        String text = "";
        do {
            try {
                waypoint = cross.safeZoneExit(pos, dir);
            } catch (NoHitException e) {
                if(i++ < WATCHDOG_STEP_HALVS) {
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\nrTVector: " + rTVector.toString() + "\ncOrCC: " + cOrCC + "\n robot pos: " + robot.getPosVector() + "\nrobot dir: " + robot.getDirection();
                    throw new TimeoutException(text);
                }
                waypoint = null;
                step = step/2;
                dir.rotateBy(SEARCH_RAD_TO_TURN * step * cOrCC);
            }
        } while (waypoint == null);
        return waypoint;
    }

    public ArrayList<Vector2Dv1> shortestRoute(ArrayList<ArrayList<Vector2Dv1>> routes){
        int index = -1;
        double smallest_length = Double.MAX_VALUE;


        for(int i = 0; i < routes.size(); i++){
            double length = robot.getPosVector().distance(routes.get(i).get(0));
            for(int j = 1; j < routes.get(i).size(); j++){
                length+=routes.get(i).get(j-1).distance(routes.get(i).get(j));
            }
            if(length<smallest_length){
                smallest_length = length;
                index = i;
            }
        }

       return routes.get(index);
    }

    public Robotv1 getRobot() {
        return robot;
    }

    public void setRobot(Robotv1 robot) {
        this.robot = robot;
    }

    public Ball getTarget() {
        return target;
    }

    public void setTarget(Ball target) {
        this.target = target;
    }

    public void setCross(Cross cross) {
        this.cross = cross;
    }
}
