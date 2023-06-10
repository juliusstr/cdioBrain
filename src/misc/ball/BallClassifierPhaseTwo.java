package misc.ball;
import Client.StandardSettings;
import misc.*;
import nav.NavAlgoPhaseTwo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BallClassifierPhaseTwo {

    public static final Color BLACK = new Color(81, 83, 82);
    public static final Color GREEN = new Color(61, 143, 100);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color ORANGE = new Color(253, 97, 60);
    public static final Color[] COLOR_LIST = {BLACK, GREEN, WHITE, ORANGE};




    public static void classify(List<Ball> balls){
        for (Ball ball : balls) {
            classify(ball);
        }
    }

    public static void classify(Ball ball){
        ball.setColor(colorCorection(ball.getColor()));
        if(ball.getColor().equals(WHITE)){
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
     *Sets placment and generates the picup waypoint
     * @param ball
     * @param boundry
     * @param cross
     */
    public static void ballSetPlacement(Ball ball, Boundry boundry, Cross cross){
        //cross
        ArrayList<Zone> zones = cross.getCriticalZones();
        for (Zone zone: zones) {
            if(zone.posInsideZone(ball.getPosVector())){
                ball.setPlacement(Ball.Placement.CORNER);
                Vector2Dv1 dir = ball.getPosVector().getSubtracted(cross.pos);
                dir.normalize();
                ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
                return;
            }
        }
        //bonderyas
        int edgeCloseenughCount = 0;
        ArrayList<LineDist> lineDists = new ArrayList<>();
        LineDist lineDist = new LineDist(null,Double.MAX_VALUE);
        for (Line line : boundry.bound) {
            lineDists.add(new LineDist(line, line.findDistanceToPoint(ball.getPosVector())));
            if(lineDists.get(lineDists.size()-1).distToline < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS) {
                edgeCloseenughCount++;
                if(lineDist.distToline>lineDists.get(lineDists.size()-1).distToline){
                    lineDist = lineDists.get(lineDists.size()-1);
                }
            }
        }
        if(edgeCloseenughCount == 2){
            ball.setPlacement(Ball.Placement.CORNER);
            for (int i = 0; i < boundry.bound.size(); i++) {
                for (int j = 0; j < boundry.bound.size(); j++) {
                    if(i == j)
                        continue;
                    Vector2Dv1 endPoint1 = boundry.bound.get(i).getClosestLineEndPointToPos(ball.getPosVector());
                    Vector2Dv1 endPoint2 = boundry.bound.get(j).getClosestLineEndPointToPos(ball.getPosVector());
                    if(endPoint1.samePos(endPoint2) && endPoint1.distance(ball.getPosVector()) < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS){
                        Vector2Dv1 dir1 = boundry.bound.get(i).furthestLineEndPointToPos.getSubtracted(boundry.bound.get(i).closestLineEndPointToPos).getNormalized();
                        Vector2Dv1 dir2 = boundry.bound.get(j).furthestLineEndPointToPos.getSubtracted(boundry.bound.get(j).closestLineEndPointToPos).getNormalized();
                        Vector2Dv1 dir = dir1.getMidVector(dir2);
                        dir.normalize();
                        ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
                        return;
                    }
                }
            }

        }
        if(edgeCloseenughCount == 1){
            ball.setPlacement(Ball.Placement.EDGE);
            Vector2Dv1 closestPointOnLine = lineDist.line.findClosestPoint(ball.getPosVector());
            Vector2Dv1 dir = ball.getPosVector().getSubtracted(closestPointOnLine);
            dir.normalize();
            ball.setPickUpWaypoint(dir.getMultiplied(StandardSettings.CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL));
            return;
        }
        ball.setPlacement((Ball.Placement.FREE));
    }
    

}
