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
    private ArrayList<Vector2Dv1> waypoints;

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
            cross.hit(robot.getPosVector(), dir);
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
            cross.hit(robot.getPosVector(), dir);
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
    public boolean hitOnCrossToTargetVectorFromPos(Vector2Dv1 pos,Vector2Dv1 target){
        Vector2Dv1 dir = target.getSubtracted(pos);
        try {
            cross.hit(robot.getPosVector(), dir);
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

    /**
     * Populates ArrayList<Vector2Dv1> waypoints with waypoints to target.
     * Index 0 in list will be next waypoint for straight line nav on the way to target.
     */
    public void wayPointGenerator() throws SizeLimitExceededException, TimeoutException {
        /*
        /hit to target?
            yes: rotate dir both + and -
                Stille hit?
                    no: set waypoint to exit of saftyzone
                    yes: continue to rotate in minus and plus.
            /no : go with strait line så set target ass waypoint


            note:
                THe function should return list of lists containing waypoints.
                Then an evaluator will choose the actual rout.
         */
        ArrayList<ArrayList<Vector2Dv1>> routes = new ArrayList<>();


        Vector2Dv1 localTargetVector = target.getPosVector();
        boolean hitToTarget = hitOnCrossToTargetVectorFromPos(robot.getPosVector(),localTargetVector);
        if(hitToTarget){
            waypoints.add(target.getPosVector());
        }

        //first cc around obstruction. first turn
        ArrayList<Vector2Dv1> route = new ArrayList<>();
        route.add(rotateVector(robot.getPosVector(),localTargetVector.clone(), -1));
        //other turns
        localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        int watchdog = 0;
        while (hitOnCrossToTargetVectorFromPos(route.get(route.size()-1),localTargetVector) && watchdog++ < WATCHDOG_MAX_TURNS_IN_ROUTE ){
            route.add(rotateVector(route.get(route.size()-1),localTargetVector, 1));
            localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        }
        if(watchdog < WATCHDOG_MAX_TURNS_IN_ROUTE)
            throw new SizeLimitExceededException("Too many turns in cc route");
        routes.add(route);

        //first c around obstruction. first turn
        route = new ArrayList<>();
        route.add(rotateVector(robot.getPosVector(),localTargetVector.clone(), 1));
        //other turns
        localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        watchdog = 0;
        while (hitOnCrossToTargetVectorFromPos(route.get(route.size()-1),localTargetVector) && watchdog++ < WATCHDOG_MAX_TURNS_IN_ROUTE ){
            route.add(rotateVector(route.get(route.size()-1),localTargetVector, -1));
            localTargetVector = target.getPosVector().getSubtracted(route.get(route.size()-1));
        }
        if(watchdog < WATCHDOG_MAX_TURNS_IN_ROUTE)
            throw new SizeLimitExceededException("Too many turns in cc route");
        routes.add(route);
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
        rTVector.normalize();
        while (hitOnCrossToTargetVectorFromPos(pos, rTVector)) {
            rTVector.rotateBy(SEARCH_RAD_TO_TURN * cOrCC);
        }
        Vector2Dv1 waypoint;
        double step = -1;
        int i = 0;//watchdog
        do {
            try {
                waypoint = cross.safeZoneExit(pos, rTVector);
            } catch (NoHitException e) {
                if(i++ < WATCHDOG_STEP_HALVS)
                    throw new TimeoutException("Did not finde a waypoint! - Watchdog triggered");
                waypoint = null;
                step = step/2;
                rTVector.rotateBy(SEARCH_RAD_TO_TURN * step * cOrCC);
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



}
