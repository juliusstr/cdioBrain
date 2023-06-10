package nav;


import Client.StandardSettings;
import exceptions.*;
import misc.*;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.concurrent.*;

public class NavAlgoPhaseTwo {
    /**
     * SEARCH_RAD_TO_TURN is the step size used to search for waypoints.
     */
    public static final double SEARCH_RAD_TO_TURN = (Math.PI/180)*1;
    public static final double TWO_PI = Math.PI*2;
    public static final int WATCHDOG_STEP_HALVS = 10;
    private Robotv1 robot;
    private Ball target;
    private Cross cross;
    private Boundry boundry;
    private ArrayList<Ball> ballsToAvoid;
    public ArrayList<Vector2Dv1> waypoints;
    private enum RotateDirection {clockwise, nothing, counterClockwise}

    public ArrayList<ArrayList<Vector2Dv1>> routes;

    public static final double ANGLE_ERROR = Math.PI/180*1;
    public static final double TARGET_DISTANCE_ERROR = 45;
    public static final double WAYPOINT_DISTANCE_ERROR = 5;

    public static int maxGroupeId = 2;

    public static int lowestWaypointCount;

    private static ThreadPoolExecutor threadPoolExecutor;


    public NavAlgoPhaseTwo(){}

    public void updateNav(Robotv1 robot, Ball target, Cross cross, Boundry boundry, ArrayList<Ball> ballsToAvoid){
        this.robot = robot;
        this.target = target;
        target.setZoneGroupId(-1);
        this.cross = cross;
        this.boundry = boundry;
        this.ballsToAvoid = ballsToAvoid;
        waypoints = new ArrayList<>();
        this.updateZoneGroupIdOnBallsToAvoid();
    }

    public String nextCommand() {

        String command = "";
        if(waypoints.size() == 0){
            return "stop -d -t";
        }
        Vector2Dv1 dir = waypoints.get(0).getSubtracted(robot.getPosVector());

        //*** cal dist and angle ***
        double distDelta = Math.sqrt(Math.pow((waypoints.get(0).x- robot.getxPos()), 2)+Math.pow((waypoints.get(0).y- robot.getyPos()), 2));

        double dot = dir.dot(robot.getDirection());
        double cross = dir.cross(robot.getDirection());
        double angleDelta;

        //*** Close enough ***
        if(distDelta < WAYPOINT_DISTANCE_ERROR && waypoints.size() != 1){
            System.err.println("On waypoint");
            waypoints.remove(0);
            return "stop -t -d";
        }
        if(distDelta < TARGET_DISTANCE_ERROR && waypoints.size() == 1){
            waypoints.remove(0);
            System.err.printf("On ball\n");
            return "stop -d -t";
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
        } catch (ZoneReturnException e) {
            //System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            //System.out.println("No hit");
            return false;
        }
        return false;
    }


    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
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
        target.setZoneGroupId(-1);
    }

    public void setCross(Cross cross) {
        this.cross = cross;
    }

    /**
     * updates the zoneGroupId on all balls in ballsToAvoid but not target
     */
    public void updateZoneGroupIdOnBallsToAvoid(){
        ArrayList<Zone> crossCriticalZones = cross.getCriticalZones();
        //todo take in to account the line on the cross not only the zone
        for (Ball ball: ballsToAvoid) {
            ball.setZoneGroupId(-1);
        }
        for (Ball ball : ballsToAvoid) {
            for (Zone crossZone : crossCriticalZones) {
                double distMax = ball.getCriticalZone().radius+crossZone.radius;
                double dist = ball.getPosVector().distance(crossZone.pos);
                if(dist<= distMax){
                    ball.setZoneGroupId(crossZone.zoneGroupID);
                    ball.setZoneGroupIdToAdjacentBalls(ballsToAvoid);
                }
            }
        }
        int currentMaxId = 2;
        for (int i = 0; i < ballsToAvoid.size(); i++) {
            if (ballsToAvoid.get(i).getZoneGroupId() == -1){
                ballsToAvoid.get(i).setZoneGroupId(++currentMaxId);
                ballsToAvoid.get(i).setZoneGroupIdToAdjacentBalls(ballsToAvoid);
            }
        }
        maxGroupeId = currentMaxId;
    };

}
