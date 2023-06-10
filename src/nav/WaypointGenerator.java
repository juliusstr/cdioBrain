package nav;

import Client.StandardSettings;
import exceptions.NoHitException;
import exceptions.NoRouteException;
import misc.*;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;
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

    private static Vector2Dv1 start;
    private static Vector2Dv1 target;

    private static ArrayList<ArrayList<Vector2Dv1>> routes = null;

    public class WaypointRoute{
        protected ArrayList<Vector2Dv1> route = null;

        protected int score = -1;

        public int getScore(){ return score; }
        public List<Vector2Dv1> getRoute(){ return route; }
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

    public WaypointGenerator(Vector2Dv1 target, Vector2Dv1 start) throws NoRouteException, TimeoutException {
        lowestWaypointCount = StandardSettings.NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT;
        routes = new ArrayList<>();
        List<Vector2Dv1> waypoints = new ArrayList<>();
        this.target = target;
        this.start = start;
        Zone hitToTarget;
        hitToTarget = hitOnAllFromPosToTarget(start);
        if(hitToTarget == null){
            waypoints.add(target);
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
        waypointRoute = new WaypointRoute();

        waypointRoute.route = shortestRoute();

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
    private ArrayList<Vector2Dv1> shortestRoute() throws NoRouteException {
        int index = -1;
        double smallest_length = Double.MAX_VALUE;


        for(int i = 0; i < routes.size(); i++){
            double length = start.distance(routes.get(i).get(0));
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
    public RotateDirection fliprorateDirection(RotateDirection rotateDirection){
        if(rotateDirection == RotateDirection.counterClockwise){
            return RotateDirection.clockwise;
        }
        return RotateDirection.counterClockwise;
    }
}