package nav;


import exceptions.*;
import misc.*;
import misc.ball.Ball;

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
    private enum RotateDirection {clockwise, nothing, counterClockwise}

    public ArrayList<ArrayList<Vector2Dv1>> routes;

    public static final double ANGLE_ERROR = Math.PI/180*1;
    public static final double TARGET_DISTANCE_ERROR = 45;
    public static final double WAYPOINT_DISTANCE_ERROR = 5;
    public static final double MAX_SEARCH_TREE_DEPTH_WAYPOINT = 10;


    public NavAlgoPhaseTwo(){}

    public void updateNav(Robotv1 robot, Ball target, Cross cross, Boundry boundry, ArrayList<Ball> ballsToAvoid){
        this.robot = robot;
        this.target = target;
        target.setZoneGroupId(-1);
        this.cross = cross;
        this.boundry = boundry;
        this.ballsToAvoid = ballsToAvoid;
        waypoints = new ArrayList<>();
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

    public boolean hitOnCrossToTargetFromPosAndDir(Vector2Dv1 pos,Vector2Dv1 dir){
        try {
            cross.hit(pos, dir);
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

    public boolean hitOnCrossToTargetVectorFromPos(Vector2Dv1 pos,Vector2Dv1 target){
        Vector2Dv1 dir = target.getSubtracted(pos);
        try {
            cross.hit(pos, dir);
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

    /**
     * Populates ArrayList<Vector2Dv1> waypoints with waypoints to target.
     * Index 0 in list will be next waypoint for straight line nav on the way to target.
     * @implNote Run only once before generate commands.
     * nextCommand() will remove waypoints ass needed.
     */
    public void wayPointGenerator() throws NoRouteException {
        routes = new ArrayList<>();

        Vector2Dv1 localTargetVector = target.getPosVector();
        boolean hitToTarget = hitOnCrossToTargetVectorFromPos(robot.getPosVector(),localTargetVector);
        if(!hitToTarget){
            waypoints.add(target.getPosVector());
            return;
        }
        ArrayList<Vector2Dv1> route = new ArrayList<>();
        wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) route.clone(), RotateDirection.counterClockwise);
        wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) route.clone(), RotateDirection.clockwise);

        //todo add check to se if a waypoint is out of bound

        waypoints = shortestRoute(routes);

    }

    private void wayPointGeneratorRecursive(ArrayList<Vector2Dv1> pastRoute, RotateDirection rotateDirection) {
        Vector2Dv1 localTargetVector = target.getPosVector();
        Vector2Dv1 pos;
        boolean hitToTarget;
        if (pastRoute.size() == 0){
            hitToTarget = hitOnCrossToTargetVectorFromPos(robot.getPosVector(),localTargetVector);
            pos = robot.getPosVector();
        } else {
            hitToTarget = hitOnCrossToTargetVectorFromPos(pastRoute.get(pastRoute.size()-1),localTargetVector);
            pos = pastRoute.get(pastRoute.size()-1);
        }
        if(!hitToTarget){
            pastRoute.add(target.getPosVector());
            routes.add(pastRoute);
            return;
        }
        try {
            pastRoute.add(rotateVector(pos,localTargetVector, rotateDirection));
        } catch (TimeoutException e) {
            return;
        }

        if(pastRoute.size() < MAX_SEARCH_TREE_DEPTH_WAYPOINT) {
            wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.counterClockwise);
            wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.clockwise);
        }
    }

    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
    }

    /**
     * Turn rTVector CC until out of Cross or and critical zone on cross.
     * @param rTVector vector for pos or waypoint to target. rTVector will be changes in this function. Pass a clone.
     * @param pos position vector. Should be robot pos og waypoint pos.
     * @param rotateDirection cc og c to rotate.
     * @return Vector2Dv1 next point after turn.
     * @exception TimeoutException Thrown if run local watchdog is triggered
     */
    private Vector2Dv1 rotateVector(Vector2Dv1 pos, Vector2Dv1 rTVector, RotateDirection rotateDirection) throws TimeoutException {
        Vector2Dv1 dir = rTVector.getSubtracted(pos);
        while (hitOnCrossToTargetFromPosAndDir(pos, dir)) {
            dir.rotateBy(SEARCH_RAD_TO_TURN * (rotateDirection.ordinal()-1));
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
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\nrTVector: " + rTVector.toString() + "\ncOrCC: " + (rotateDirection.ordinal()-1) + "\n robot pos: " + robot.getPosVector() + "\nrobot dir: " + robot.getDirection();
                    throw new TimeoutException(text);
                }
                waypoint = null;
                step = step/2;
                dir.rotateBy(SEARCH_RAD_TO_TURN * step * (rotateDirection.ordinal()-1));
            }
        } while (waypoint == null);
        return waypoint;
    }

    private ArrayList<Vector2Dv1> shortestRoute(ArrayList<ArrayList<Vector2Dv1>> routes) throws NoRouteException {
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
        try {
            return routes.get(index);
        } catch (IndexOutOfBoundsException e){
            throw new NoRouteException("No rout was found!");
        }
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

    private void updateZoneGroupIdOnBallsToAvoid(){
        ArrayList<Zone> crossCriticalZones = cross.getCriticalZones();
        //todo take in to account the line on the cross not only the zone
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
    };
}
