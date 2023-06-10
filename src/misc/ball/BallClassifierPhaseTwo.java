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
    public static final Color[] COLOR_LIST = {BLACK, GREEN, WHITE};




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
    
    public static void ballSetPlacement(Ball ball, Boundry boundry, Cross cross){

        ArrayList<Zone> zones = cross.getCriticalZones();
        for (Zone zone: zones) {
            if(zone.posInsideZone(ball.getPosVector())){
                ball.setPlacement(Ball.Placement.CORNER);
                break;
            }
        }
        if(ball.getPlacement() != Ball.Placement.CORNER){
            int edgeCloseenughCount = 0;
            ArrayList<LineDist> lineDists = new ArrayList<>();
            for (Line line : boundry.bound) {
                lineDists.add(new LineDist(line, line.findDistanceToPoint(ball.getPosVector())));
                if(lineDists.get(lineDists.size()-1).distToline < StandardSettings.BALL_RADIUS_PX + Zone.CRITICAL_ZONE_RADIUS) {
                    edgeCloseenughCount++;
                }
            }
        }

        
        //todo finde out if its in a corner or a edge
    }
    

}
