package nav;


import exceptions.*;
import misc.*;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.concurrent.*;

import static Client.StandardSettings.*;

public class CommandGenerator {
    /**
     * SEARCH_RAD_TO_TURN is the step size used to search for waypoints.
     */
    public static final double SEARCH_RAD_TO_TURN = (Math.PI/180)*1;
    public static final double TWO_PI = Math.PI*2;
    public static final int WATCHDOG_STEP_HALVS = 10;
    private Robotv1 robot;
    public ArrayList<Vector2Dv1> waypoints;

    public static int lowestWaypointCount;

    private static ThreadPoolExecutor threadPoolExecutor;

    public CommandGenerator(Robotv1 robot, ArrayList<Vector2Dv1> waypoints){
        this.robot = robot;
        this.waypoints = (ArrayList<Vector2Dv1>) waypoints.clone();
    }

    public String nextCommand(boolean isTargetBall) {

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
        if(distDelta < WAYPOINT_DISTANCE_ERROR && waypoints.size() > 1){
            System.err.println("On waypoint");
            waypoints.remove(0);
            return "stop -t -d";
        }
        if(isTargetBall && distDelta < TARGET_DISTANCE_ERROR && waypoints.size() == 1){
            waypoints.remove(0);
            System.err.printf("On ball\n");
            return "On ball\n";
        }
        if(!isTargetBall && distDelta < WAYPOINT_DISTANCE_ERROR && waypoints.size() == 1){
            waypoints.remove(0);
            System.err.printf("On waypoint\n");
            return "On waypoint\n";
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
            if (turnSpeed > 1)
                turnSpeed = 1;
            command += " -s" + String.format("%.2f", turnSpeed).replace(',','.') + "";
        } else {
            command += "stop -t";
        }

        //***drive***
        if(Math.abs(angleDelta) > ANGLE_ERROR*6){
            System.out.printf("command = %s\n", command);
            return command + ";stop -d";
        }
        if(distDelta > WAYPOINT_DISTANCE_ERROR){
            double speed = distDelta/12;
            if (speed > 15) {
                speed = 15;
            } else if(speed < 1){
                speed = 1;
            }
            command += ";drive -s" + String.format("%.2f", speed).replace(',','.');
        } else {
            command += ";stop -d;stop -t";
        }
        System.out.printf("command = %s\n", command);
        return command;
    }


    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
    }
    public void setWaypoints(ArrayList<Vector2Dv1> waypoints){
        this.waypoints = (ArrayList<Vector2Dv1>) waypoints.clone();
    }

    public Robotv1 getRobot() {
        return robot;
    }
    public void setRobot(Robotv1 robot) {
        this.robot = robot;
    }

    /**
     * updates the zoneGroupId on all balls in ballsToAvoid but not target
     */


}
