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

    /**
     * @param pos for witch to check
     * @param dir to check;
     * @param zoneId to return as the hit zone
     * @return boolean - true if cross critical zone hit else false
     */
    public boolean hitOnCrossToTargetFromPosAndDir(Vector2Dv1 pos,Vector2Dv1 dir, Integer zoneId){
        try {
            cross.hit(pos, dir);
        } catch (LineReturnException e) {
            zoneId = e.line.zoneGroupID;
            //System.out.println(e.line.toString());;
            return true;
        } catch (ZoneReturnException e) {
            zoneId = e.zone.zoneGroupID;
            //System.out.println(e.vector2D.toString());
            return true;
        } catch (NoHitException e) {
            zoneId = -1;
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

    public Zone hitOnAllFromPosInDir(Vector2Dv1 pos, Vector2Dv1 dir){
        ArrayList<Zone> allZones = new ArrayList<>();
        allZones.addAll(cross.getCriticalZones());
        for (Ball ball :
                ballsToAvoid) {
            allZones.add(ball.getCriticalZone());
        }
        if(allZones.size() == 0){
            return null;
        }
        int index = -1;
        double maxDist = Double.MAX_VALUE;
        for (int i = 0; i < allZones.size(); i++) {
            allZones.get(i).willHitZone(pos,dir);
            try {
                double dist = allZones.get(i).getClosestIntercept().distance(pos);
                if(dist<maxDist){
                    index = i;
                    maxDist = dist;
                }
            } catch (NoHitException e) {
                allZones.remove(i--);
            }
        }
        if(index == -1) return null;
        return allZones.get(index);
    }

    public Zone hitOnAllFromPosToTarget(Vector2Dv1 pos, Vector2Dv1 target){
        Vector2Dv1 dir = target.getSubtracted(pos);
        double distFromPosToTarget = pos.distance(target);
        ArrayList<Zone> allZones = new ArrayList<>();
        allZones.addAll(cross.getCriticalZones());
        for (Ball ball :
                ballsToAvoid) {
            allZones.add(ball.getCriticalZone());
        }
        if(allZones.size() == 0){
            return null;
        }
        int index = -1;
        double maxDist = Double.MAX_VALUE;
        for (int i = 0; i < allZones.size(); i++) {
            allZones.get(i).willHitZone(pos,dir);
            try {
                double dist = allZones.get(i).getClosestIntercept().distance(pos);
                if(dist<maxDist && dist < distFromPosToTarget){
                    index = i;
                    maxDist = dist;
                }
            } catch (NoHitException e) {
                allZones.remove(i--);
            }
        }
        if(index == -1) return null;
        return allZones.get(index);
    }

    public Zone hitOnAllInGroupFromPosInDir(Vector2Dv1 pos, Vector2Dv1 dir, int zoneGroupId){
        ArrayList<Zone> allZones = new ArrayList<>();
        if(zoneGroupId == 2)
            allZones.addAll(cross.getCriticalZones());
        for (Ball ball :
                ballsToAvoid) {
            if(ball.getZoneGroupId() == zoneGroupId)
                allZones.add(ball.getCriticalZone());
        }
        if(allZones.size() == 0){
            return null;
        }
        int index = -1;
        double maxDist = Double.MAX_VALUE;
        for (int i = 0; i < allZones.size(); i++) {
            allZones.get(i).willHitZone(pos,dir);
            try {
                double dist = allZones.get(i).getClosestIntercept().distance(pos);
                if(dist<maxDist){
                    index = i;
                    maxDist = dist;
                }
            } catch (NoHitException e) {
                allZones.remove(i--);
            }
        }
        if(index == -1) return null;
        return allZones.get(index);
    }

    /**
     * Populates ArrayList<Vector2Dv1> waypoints with waypoints to target.
     * Index 0 in list will be next waypoint for straight line nav on the way to target.
     * @implNote Run only once before generate commands.
     * nextCommand() will remove waypoints ass needed.
     * See options to modify algorithm in StandardSettings.
     */

    public void waypointGenerator() throws NoRouteException, TimeoutException {
        lowestWaypointCount = StandardSettings.NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT;
        routes = new ArrayList<>();

        Vector2Dv1 localTargetVector = target.getPosVector();
        Zone hitToTarget;
        hitToTarget = hitOnAllFromPosToTarget(robot.getPosVector(), localTargetVector);
        if(hitToTarget == null){
            waypoints.add(target.getPosVector());
            return;
        }

        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(StandardSettings.NAV_WAYPOINT_GENERATOR_NUMBER_OF_THREADS);


        ArrayList<Vector2Dv1> route = new ArrayList<>();
        threadPoolExecutor.submit(() -> {
            try {
                wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) route.clone(), RotateDirection.counterClockwise);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
        threadPoolExecutor.submit(() -> {
            try {
                wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) route.clone(), RotateDirection.clockwise);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
        int i = 0;
        while (threadPoolExecutor.getActiveCount()>0){
            System.err.println("sleep: " + (++i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //todo add check to se if a waypoint is out of bound

        waypoints = shortestRoute(routes);
    }

    private void wayPointGeneratorRecursive(ArrayList<Vector2Dv1> pastRoute, RotateDirection rotateDirection) throws TimeoutException {
        Vector2Dv1 localTargetVector = target.getPosVector();
        Vector2Dv1 pos;

        if (pastRoute.size() == 0){
            pos = robot.getPosVector();
        } else {
            pos = pastRoute.get(pastRoute.size()-1);
        }
        Vector2Dv1 dir = localTargetVector.getSubtracted(pos);
        Zone hitToTarget;
        hitToTarget = hitOnAllFromPosToTarget(pos,localTargetVector);
        if(hitToTarget == null){
            pastRoute.add(target.getPosVector());
            routes.add(pastRoute);
            if(StandardSettings.NAV_WAYPOINT_GENERATOR_SPEED_SEARCH)
                lowestWaypointCount = pastRoute.size();
            return;
        }
        if(pastRoute.size()+1 >= lowestWaypointCount)//todo set to lowestWaypointCount
            return;



        Zone hitZone = null;
        Zone previusHitZone = null;
        previusHitZone = hitOnAllFromPosInDir(pos, dir);
        double angleTurendeRad = 0;

        while ((hitZone = hitOnAllFromPosInDir(pos, dir)) != null) {
            if(!previusHitZone.pos.equals(hitZone.pos)) {
                if (previusHitZone.zoneGroupID != hitZone.zoneGroupID) {
                    caseClosestZoneChange((ArrayList<Vector2Dv1>) pastRoute.clone(), rotateDirection, previusHitZone, hitZone, dir.clone(), pos);
                }
            }
            previusHitZone = hitZone;
            dir.rotateBy(SEARCH_RAD_TO_TURN * (rotateDirection.ordinal()-1));
            angleTurendeRad += SEARCH_RAD_TO_TURN;
            if(angleTurendeRad > TWO_PI)
                return;
        }

        Vector2Dv1 waypoint;
        double step = -1;
        int i = 0;//watchdog
        String text = "";
        angleTurendeRad = 0;
        Zone safeZone = previusHitZone.getNewSafetyZoneFromCriticalZone();
        do {
            try {

                safeZone.willHitZone(pos, dir);
                waypoint = safeZone.getFurthestIntercept();

            } catch (NoHitException e) {
                if(i++ > WATCHDOG_STEP_HALVS) {
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\ndir: " + dir.toString() + "\ncOrCC: " + (rotateDirection.ordinal()-1) + "\n robot pos: " + robot.getPosVector() + "\nrobot dir: " + robot.getDirection();
                    throw new TimeoutException(text);
                }
                waypoint = null;
                step = step/2;
                dir.rotateBy(SEARCH_RAD_TO_TURN * step * (rotateDirection.ordinal()-1));
                angleTurendeRad += Math.abs(SEARCH_RAD_TO_TURN * step);
                if(angleTurendeRad > TWO_PI)
                    return;
            }
        } while (waypoint == null);
        //todo check if rout form pos to waypoint hits critical-zone closer to robot.

        pastRoute.add(waypoint);

        if(pastRoute.size() < lowestWaypointCount) {
            threadPoolExecutor.submit(() -> {
                try {
                    wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.counterClockwise);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
            threadPoolExecutor.submit(() -> {
                try {
                    wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.clockwise);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void caseClosestZoneChange(ArrayList<Vector2Dv1> pastRoute, RotateDirection rotateDirection, Zone previusHitZone, Zone hitZone, Vector2Dv1 dir, Vector2Dv1 pos) throws TimeoutException {

        Zone zone;

        try {//sker ikke
            if(previusHitZone.getClosestIntercept().distance(pos) > hitZone.getClosestIntercept().distance(pos)){
                rotateDirection = fliprorateDirection(rotateDirection);
                zone = hitZone;
            } else {
                zone = previusHitZone;
            }
        } catch (NoHitException e) {
            throw new RuntimeException(e);
        }

        Vector2Dv1 waypoint;
        double step = -1;
        int i = 0;//watchdog
        String text = "";
        Zone safeZone = zone.getNewSafetyZoneFromCriticalZone();
        Zone critZoneHit = null;
        waypoint = null;
        double angleTurendeRad = 0;
        do {
            try {

                critZoneHit = hitOnAllInGroupFromPosInDir(pos,dir,zone.zoneGroupID);
                if(critZoneHit != null){
                    dir.rotateBy(SEARCH_RAD_TO_TURN * (rotateDirection.ordinal()-1));
                    angleTurendeRad += SEARCH_RAD_TO_TURN;
                    safeZone = critZoneHit.getNewSafetyZoneFromCriticalZone();
                    step = -1;
                    i = 0;
                    if(angleTurendeRad > TWO_PI)
                        return;
                } else {
                    //tjek om der er et hit på crit hvis der er roter dir ud og nulstil watchdog og step.
                    safeZone.willHitZone(pos, dir);
                    waypoint = safeZone.getFurthestIntercept();
                }
            } catch (NoHitException e) {
                if(i++ > WATCHDOG_STEP_HALVS) {
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\ndir: " + dir.toString() + "\ncOrCC: " + (rotateDirection.ordinal()-1) + "\nzone: " + zone;
                    throw new TimeoutException(text);
                }
                waypoint = null;
                step = step/2;
                dir.rotateBy(SEARCH_RAD_TO_TURN * step * (rotateDirection.ordinal()-1));
            }
        } while (waypoint == null);
        //todo check if rout form pos to waypoint hits critical-zone closer to robot.
        pastRoute.add(waypoint);

        if(pastRoute.size() < lowestWaypointCount) {
            threadPoolExecutor.submit(() -> {
                try {
                    wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.counterClockwise);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
            threadPoolExecutor.submit(() -> {
                try {
                    wayPointGeneratorRecursive((ArrayList<Vector2Dv1>) pastRoute.clone(), RotateDirection.clockwise);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public ArrayList<Vector2Dv1> getWaypoints() {
        return waypoints;
    }

    /**
     * Turn dir until out of Cross or and critical zone on cross.
     * @param dir vector for direction to change. dir vill be changed pass a clone if it may not change.
     * @param pos position vector. Should be robot pos og waypoint pos.
     * @param rotateDirection cc og c to rotate.
     * @return Vector2Dv1 next point after turn.
     * @exception TimeoutException Thrown if run local watchdog is triggered
     */
    private Vector2Dv1 rotateVector(Vector2Dv1 pos, Vector2Dv1 dir, RotateDirection rotateDirection) throws TimeoutException {
        Integer hitZoneId = -1;
        Integer previusHitZoneId = -1;
        hitOnCrossToTargetFromPosAndDir(pos, dir, hitZoneId);
        previusHitZoneId = hitZoneId;
        while (hitOnCrossToTargetFromPosAndDir(pos, dir, hitZoneId)) {
            dir.rotateBy(SEARCH_RAD_TO_TURN * (rotateDirection.ordinal()-1));
            if(previusHitZoneId.intValue() != hitZoneId.intValue()){
                //throw new HitException("New zone hit!");
                //idear for new. will not implement in this function. all new will be made with inspiration from this.
            }
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
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\ndir: " + dir.toString() + "\ncOrCC: " + (rotateDirection.ordinal()-1) + "\n robot pos: " + robot.getPosVector() + "\nrobot dir: " + robot.getDirection();
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

    public RotateDirection fliprorateDirection(RotateDirection rotateDirection){
        if(rotateDirection == RotateDirection.counterClockwise){
            return RotateDirection.clockwise;
        }
        return RotateDirection.counterClockwise;
    }
}
