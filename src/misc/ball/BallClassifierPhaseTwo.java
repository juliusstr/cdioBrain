package misc.ball;
import Client.StandardSettings;
import exceptions.NoWaypointException;
import imageRecognition.RGBtoHSVConverter;
import misc.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BallClassifierPhaseTwo {

    public static Color BLACK = new Color(72, 63, 49);
    public static Color GREEN = new Color(32, 113, 76);
    public static final Color WHITE = new Color(234, 219, 191);
    public static final Color ORANGE = new Color(242, 114, 46);
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


    public static void classify(ArrayList<Ball> balls){
        colorCorection(balls);
        for (Ball ball : balls) {
            classify(ball);
        }
    }

    public static void classify(Ball ball){
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

    private static void colorCorection(ArrayList<Ball> balls) {
        if(balls.size() == 0)
            return;
        Ball black = balls.get(0), green = balls.get(0);
        for (Ball ball : balls) {
            if(RGBtoHSVConverter.convertRGBtoHSV(ball.getColor())[0] > RGBtoHSVConverter.convertRGBtoHSV(green.getColor())[0])
                green = ball;
            if(RGBtoHSVConverter.convertRGBtoHSV(ball.getColor())[2] < RGBtoHSVConverter.convertRGBtoHSV(black.getColor())[2])
                black = ball;
        }
        for (Ball ball : balls) {
            if(ball.getColor().equals(BallClassifierPhaseTwo.ORANGE))
                continue;
            if(ball == black) {
                ball.setColor(BallClassifierPhaseTwo.BLACK);
            } else if(ball == green){
                ball.setColor(BallClassifierPhaseTwo.GREEN);
            } else {
                ball.setColor(BallClassifierPhaseTwo.WHITE);
            }
        }
    }

    /**
     Sets the placement and pick-up waypoint for each ball based on the provided boundaries and cross.
     @param balls the list of balls to set the placement for
     @param boundry the boundry object representing the boundaries of the playing area
     @param cross the cross object representing the cross in the playing area
     */
    public static void ballSetPlacement(ArrayList<Ball> balls, Boundry boundry, Cross cross) throws NoWaypointException {
        for (Ball b: balls) {
            b.setPlacement(null);
        }
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
            Line countOneLine = null, countTwoLine = null;
            for (Line line : boundry.bound) {
                lineDists.add(new LineDist(line, line.findDistanceToPoint(ball.getPosVector())));
                if (lineDists.get(lineDists.size() - 1).distToline < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS) {
                    edgeCloseenughCount++;
                    if (lineDist.distToline > lineDists.get(lineDists.size() - 1).distToline) {
                        lineDist = lineDists.get(lineDists.size() - 1);
                    }
                    countOneLine = line;
                    break;
                }
            }
            if(edgeCloseenughCount == 1){
                for (Line line : boundry.bound) {
                    if(line == countOneLine)
                        continue;
                    lineDists.add(new LineDist(line, line.findDistanceToPoint(ball.getPosVector())));
                    if (lineDists.get(lineDists.size() - 1).distToline < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS*2) {
                        edgeCloseenughCount++;
                        if (lineDist.distToline > lineDists.get(lineDists.size() - 1).distToline) {
                            lineDist = lineDists.get(lineDists.size() - 1);
                        }
                        countTwoLine = line;
                        break;
                    }
                }
            }

            if (edgeCloseenughCount == 2) {
                ball.setPlacement(Ball.Placement.CORNER);
                Vector2Dv1 endPoint1 = countOneLine.getClosestLineEndPointToPos(ball.getPosVector());
                Vector2Dv1 endPoint2 = countTwoLine.getClosestLineEndPointToPos(ball.getPosVector());
                if (endPoint1.samePos(endPoint2) && endPoint1.distance(ball.getPosVector()) <= StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS*2) {
                    Vector2Dv1 dir1 = countOneLine.furthestLineEndPointToPos.getSubtracted(countOneLine.closestLineEndPointToPos).getNormalized();
                    Vector2Dv1 dir2 = countTwoLine.furthestLineEndPointToPos.getSubtracted(countTwoLine.closestLineEndPointToPos).getNormalized();
                    Vector2Dv1 dir = dir1.getMidVector(dir2);
                    dir.normalize();
                    ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
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
                    ball.setPlacement(Ball.Placement.PAIR);
                    Vector2Dv1 dir = balls.get(i).getPosVector().getSubtracted(ball.getPosVector());
                    dir.normalize();
                    dir.rotateBy(Math.PI);
                    dir.multiply(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL);
                    Vector2Dv1 waypoint = ball.getPosVector().getAdded(dir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(dir);
                        break;
                    }
                    Vector2Dv1 tempDir = dir.getRotatedBy(Math.PI/2);
                    waypoint = ball.getPosVector().getAdded(tempDir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(tempDir);
                        break;
                    }
                    tempDir = dir.getRotatedBy(Math.PI/2*(-1));
                    waypoint = ball.getPosVector().getAdded(tempDir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(tempDir);
                        break;
                    }
                    tempDir = dir.getRotatedBy(Math.PI/4);
                    waypoint = ball.getPosVector().getAdded(tempDir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(tempDir);
                        break;
                    }
                    tempDir = dir.getRotatedBy(Math.PI/8);
                    waypoint = ball.getPosVector().getAdded(tempDir);
                    if(!waypointInCriticalZone(waypoint,balls,cross,boundry)){
                        ball.setPickUpWaypoint(tempDir);
                        break;
                    }
                    throw new NoWaypointException("No waypoint was found on pair ball");
                }
            }
            if(ball.getPlacement() == null)
                ball.setPlacement(Ball.Placement.FREE);
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
        if(!boundry.waypointSafeDistFromBoundary(waypoint))
            return true;

        return false;
    }
}
