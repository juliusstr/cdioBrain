package misc;

import misc.ball.Ball;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BallClassifier {
    private ArrayList<Ball> balls;
    private ArrayList<Ball> robotCircle;
    public static final Color BALCK = new Color(40, 40, 40);
    public static final Color RED = new Color(253, 97, 60);
    public static final Color WHITE = new Color(255, 255, 255);
    public BallClassifier(List<Ball> circles){
        balls = new ArrayList<>();
        robotCircle = new ArrayList<>();
        for (Ball ball :
                circles) {
            /*double distGreen = getColorDist(ball.getColor(), BALCK);
            double distOrange = getColorDist(ball.getColor(), RED);
            double distWhite = getColorDist(ball.getColor(), WHITE);
            System.out.println(ball.getColor());
            if(distWhite > distGreen && distWhite > distOrange){
                if(distGreen > distOrange){
                    ball.setColor(RED);
                } else {
                    ball.setColor(BALCK);
                }
                robotCircle.add(ball);
            } else {
                ball.setColor(WHITE);
                balls.add(ball);
            }*/
            System.out.println(ball.getColor() + ",  radius: " + ball.getRadius());
            ball.setColor(colorCorection(ball.getColor()));
            if (ball.getColor().equals(WHITE)){
                balls.add(ball);
            } else {
                robotCircle.add(ball);
            }
        }
    }

    private double getColorDist(Color a, Color b){
        double dist = 0;
        dist += Math.pow(a.getRed()-b.getRed(),2);
        dist += Math.pow(a.getGreen()-b.getGreen(),2);
        dist += Math.pow(a.getBlue()-b.getBlue(),2);
        return  Math.sqrt(dist);

    }

    private Color colorCorection(Color color){
        if (color.getRed() < 110 && color.getBlue() < 110 && color.getGreen() < 110){
            return BALCK;
        }
        if(color.getRed() > 200 && color.getBlue() < 150 && color.getGreen() < 150){
            return RED;
        }
        return WHITE;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public ArrayList<Ball> getRobotCircle() {
        return robotCircle;
    }
}
