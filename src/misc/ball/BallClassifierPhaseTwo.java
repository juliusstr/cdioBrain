package misc.ball;
import Client.StandardSettings;
import exceptions.NoWaypointException;
import misc.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BallClassifierPhaseTwo {

    public static Color BLACK = new Color(81, 83, 82);
    public static Color GREEN = new Color(61, 143, 100);
    public static final Color WHITE = new Color(240, 237, 213);
    public static final Color ORANGE = new Color(246, 162, 93);
    public static final Color[] COLOR_LIST = {BLACK, GREEN, WHITE, ORANGE};

    public static void UpdateColor(ArrayList<Color> colors){
        //BLACK = new Color(colors.getRGB());
        //GREEN = colors.get(1);

        //BLACK = new Color(colors.get(0).getRGB());
        BLACK = new Color(colors.get(0).getRGB());
        GREEN = new Color(colors.get(1).getRGB());

        System.out.println("2. sort " + BallClassifierPhaseTwo.BLACK + "\n");
        System.out.println("2. green" + BallClassifierPhaseTwo.GREEN + "\n");

    }


    public static void classify(List<Ball> balls){
        for (Ball ball : balls) {
            classify(ball);
        }
    }

    public static void classify(Ball ball){
        ball.setColor(colorCorection(ball.getColor()));
        if(ball.getColor().equals(WHITE) || ball.getColor().equals(ORANGE)){
            ball.setType(Ball.Type.BALL);
        } else if (ball.getColor().equals(GREEN)) {
            ball.setType(Ball.Type.ROBOT_FRONT);
            ball.setStatus(PrimitiveBall.Status.ROBOT);
        } else if (ball.getColor().equals(BLACK)) {
            ball.setType(Ball.Type.ROBOT_BACK);
            ball.setStatus(PrimitiveBall.Status.ROBOT);
        } else {
            ball.setType(Ball.Type.UNKNOWN);
        }
    }

    private static Color colorCorection(Color color){
        /*if (color.getRed() < 100 && color.getBlue() < 100 && color.getGreen() < 100){
            return BLACK;
        }

         */
        double dist = Double.MAX_VALUE;
        Color closestColer = null;
        for (int i = 0; i < COLOR_LIST.length; i++){
            double r = Math.pow(COLOR_LIST[i].getRed()-color.getRed(),2);
            double g = Math.pow(COLOR_LIST[i].getGreen()-color.getGreen(),2);
            double b = Math.pow(COLOR_LIST[i].getBlue()-color.getBlue(),2);
            double temp = Math.sqrt(r+g+b);
            if(dist > temp){
                dist = temp;
                closestColer = COLOR_LIST[i];
            }
        }
        return closestColer;
    }

    /**
     Sets the placement and pick-up waypoint for each ball based on the provided boundaries and cross.
     @param balls the list of balls to set the placement for
     @param boundry the boundry object representing the boundaries of the playing area
     @param cross the cross object representing the cross in the playing area
     */
    public static void ballSetPlacement(ArrayList<Ball> balls, Boundry boundry, Cross cross) throws NoWaypointException {
        //cross
        for (Ball ball : balls) {
            ArrayList<Zone> zones = cross.getCriticalZones();
            for (Zone zone : zones) {
                if (zone.posInsideZone(ball.getPosVector())) {
                    ball.setPlacement(Ball.Placement.CORNER);
                    Vector2Dv1 dir = ball.getPosVector().getSubtracted(cross.pos);
                    dir.normalize();
                    ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
                    break;
                }
            }
        }
        //bonderyas
        for (Ball ball : balls) {

            if(ball.getPlacement() != null)
                continue;

            int edgeCloseenughCount = 0;
            ArrayList<LineDist> lineDists = new ArrayList<>();
            LineDist lineDist = new LineDist(null, Double.MAX_VALUE);
            for (Line line : boundry.bound) {
                lineDists.add(new LineDist(line, line.findDistanceToPoint(ball.getPosVector())));
                if (lineDists.get(lineDists.size() - 1).distToline < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS) {
                    edgeCloseenughCount++;
                    if (lineDist.distToline > lineDists.get(lineDists.size() - 1).distToline) {
                        lineDist = lineDists.get(lineDists.size() - 1);
                    }
                }
            }
            if (edgeCloseenughCount == 2) {
                boolean breakBool = false;
                ball.setPlacement(Ball.Placement.CORNER);
                for (int i = 0; i < boundry.bound.size(); i++) {
                    for (int j = 0; j < boundry.bound.size(); j++) {
                        if (i == j)
                            continue;
                        Vector2Dv1 endPoint1 = boundry.bound.get(i).getClosestLineEndPointToPos(ball.getPosVector());
                        Vector2Dv1 endPoint2 = boundry.bound.get(j).getClosestLineEndPointToPos(ball.getPosVector());
                        if (endPoint1.samePos(endPoint2) && endPoint1.distance(ball.getPosVector()) < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS) {
                            Vector2Dv1 dir1 = boundry.bound.get(i).furthestLineEndPointToPos.getSubtracted(boundry.bound.get(i).closestLineEndPointToPos).getNormalized();
                            Vector2Dv1 dir2 = boundry.bound.get(j).furthestLineEndPointToPos.getSubtracted(boundry.bound.get(j).closestLineEndPointToPos).getNormalized();
                            Vector2Dv1 dir = dir1.getMidVector(dir2);
                            dir.normalize();
                            ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
                            breakBool = true;
                            break;
                        }
                    }
                    if (breakBool)
                        break;
                }
            }
            if (edgeCloseenughCount == 1) {
                ball.setPlacement(Ball.Placement.EDGE);
                Vector2Dv1 closestPointOnLine = lineDist.line.findClosestPoint(ball.getPosVector());
                Vector2Dv1 dir = ball.getPosVector().getSubtracted(closestPointOnLine);
                dir.normalize();
                ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
            }
        }
        for (Ball ball : balls) {
            if(ball.getPlacement() != null)
                continue;

            Zone ballZone = ball.getCriticalZone();
            for (int i = 0; i < balls.size(); i++) {
                if(ball == balls.get(i))
                    continue;
                if(ballZone.posInsideZone(balls.get(i).getPosVector())) {
                    Vector2Dv1 dir = balls.get(i).getPosVector().getSubtracted(ball.getPosVector());
                    dir.normalize();
                    dir.multiply(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL);
                    Vector2Dv1 waypoint = ball.getPosVector().getAdded(dir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(dir);
                        break;
                    }
                    dir.rotateBy(Math.PI/2);
                    waypoint = ball.getPosVector().getAdded(dir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(dir);
                        break;
                    }
                    dir.rotateBy(Math.PI);
                    waypoint = ball.getPosVector().getAdded(dir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(dir);
                        break;
                    }
                    throw new NoWaypointException("No waypoint was found on pair ball");
                }
            }
        }
    }
    private static boolean waypointInCriticalZone(Vector2Dv1 waypoint, ArrayList<Ball> balls, Cross cross, Boundry boundry){
        for (Ball ball: balls) {
            if (ball.getCriticalZone().posInsideZone(waypoint))
                return true;
        }
        ArrayList<Zone> crossZone = cross.getCriticalZones();
        for (Zone zone :
                crossZone) {
            if(zone.posInsideZone(waypoint))
                return true;
        }
        if(!boundry.vectorInsideBoundary(waypoint))
            return true;

        return false;
    }
}
