package nav;

import Client.StandardSettings;
import exceptions.NoHitException;
import exceptions.NoRouteException;
import misc.*;
import misc.ball.Ball;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class WaypointGenerator {
    public static final double SEARCH_RAD_TO_TURN = (Math.PI/180)*1;
    public static final double TWO_PI = Math.PI*2;
    public static final int WATCHDOG_STEP_HALVS = 10;
    private Cross cross;
    private Boundry boundry;
    private ArrayList<Ball> ballsToAvoid;
    private static ThreadPoolExecutor threadPoolExecutor;

    private enum RotateDirection {clockwise, nothing, counterClockwise}
    private static int lowestWaypointCount = 0;
    public static int maxGroupeId = 2;

    private Vector2Dv1 start;
    private Vector2Dv1 target;

    private ArrayList<WaypointRoute> routes = null;

    public class WaypointRoute{
        protected ArrayList<Vector2Dv1> route = null;

        protected double cost = -1;

        WaypointRoute(ArrayList<Vector2Dv1> route, double cost){
            this.route = route;
            this.cost = cost;
        }
        WaypointRoute(){};

        public double getCost(){ return cost; }
        public ArrayList<Vector2Dv1> getRoute(){ return route; }
    }

    public WaypointRoute waypointRoute = null;


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

    private Zone hitOnAllFromPosToTarget(Vector2Dv1 pos){
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
    public WaypointGenerator(Vector2Dv1 target, Vector2Dv1 start, Cross c, Boundry b, ArrayList<Ball> bta) throws NoRouteException, TimeoutException {
        lowestWaypointCount = StandardSettings.NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT;
        routes = new ArrayList<>();
        ArrayList<Vector2Dv1> waypoints = new ArrayList<>();
        this.boundry = b;
        this.cross = c;
        this.ballsToAvoid = bta;
        this.target = target;
        this.start = start;
        Zone hitToTarget;
        updateZoneGroupIdOnBallsToAvoid();
        hitToTarget = hitOnAllFromPosToTarget(start);
        if(hitToTarget == null){
            waypoints.add(target);
            waypointRoute = new WaypointRoute();
            waypointRoute.route = (ArrayList<Vector2Dv1>) waypoints.clone();
            waypointRoute.cost = getRouteCost(waypointRoute.route);
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
        waypointRoute = getCheapestRoute();

    }

    private void wayPointGeneratorRecursive(ArrayList<Vector2Dv1> pastRoute, RotateDirection rotateDirection) throws TimeoutException {
        Vector2Dv1 localTargetVector = target;
        Vector2Dv1 pos;

        if (pastRoute.size() == 0){
            pos = start;
        } else {
            pos = pastRoute.get(pastRoute.size()-1);
        }
        Vector2Dv1 dir = localTargetVector.getSubtracted(pos);
        Zone hitToTarget;
        hitToTarget = hitOnAllFromPosToTarget(pos);
        if(hitToTarget == null){
            pastRoute.add(target);
            routes.add( new WaypointRoute((ArrayList<Vector2Dv1>) pastRoute.clone(), getRouteCost(pastRoute)));
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
                    text = "Did not finde a waypoint! - Watchdog triggered\nPos : " + pos.toString() + "\ndir: " + dir.toString() + "\ncOrCC: " + (rotateDirection.ordinal()-1) + "\n start pos: " + start;
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
    private WaypointRoute getCheapestRoute() {
        WaypointRoute returnRoute = null;
        double smallestCost = Double.MAX_VALUE;
            for (WaypointRoute route : this.routes) {
                if (route.cost < smallestCost)
                    smallestCost = route.cost;
                    returnRoute = route;
            }
                return returnRoute;
        }

    /**
     * Turn dir until out of Cross or and critical zone on cross.
     * @param dir vector for direction to change. dir vill be changed pass a clone if it may not change.
     * @param pos position vector. Should be robot pos og waypoint pos.
     * @param rotateDirection cc og c to rotate.
     * @return Vector2Dv1 next point after turn.
     * @exception TimeoutException Thrown if run local watchdog is triggered
     */
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
        // todo check if waypoint is outside of boundary
        // todo check if rout form pos to waypoint hits critical-zone closer to robot.
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
    public RotateDirection fliprorateDirection(RotateDirection rotateDirection){
        if(rotateDirection == RotateDirection.counterClockwise){
            return RotateDirection.clockwise;
        }
        return RotateDirection.counterClockwise;
    }

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

    /**
     * Gets the cost of a route.
     * @param waypoints the route we need the calculation of
     * @return the cost to take the route
     */
    //todo make it so this adds cost of turning
    public double getRouteCost(ArrayList<Vector2Dv1> waypoints){
        double cost;
        cost=this.start.getSubtracted(waypoints.get(0)).getLength();
        for (int cnt = 1; cnt < waypoints.size(); cnt++) {
            cost+=waypoints.get(cnt).getSubtracted(waypoints.get(cnt-1)).getLength();
        }
        return cost;
    }
}
