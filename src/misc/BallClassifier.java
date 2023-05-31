package misc;

import misc.ball.Ball;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The BallClassifier class is used to classify balls based on their colors.
 * It separates the balls into two categories: regular balls and robot circles.
 * Regular balls are those that are classified as white, and robot circles are those that are classified as either black or red.
 * The classification is based on the color of the balls and a color correction algorithm.
 */
public class BallClassifier {
    private ArrayList<Ball> balls; // Stores regular balls (color classified as white)
    private ArrayList<Ball> robotCircle; // Stores robot circles (color classified as black or red)
    public static final Color BLACK = new Color(40, 40, 40);
    public static final Color RED = new Color(253, 97, 60);
    public static final Color WHITE = new Color(255, 255, 255);

    /**
     * Constructs a BallClassifier object with the given list of balls.
     * The balls are classified into regular balls and robot circles based on their colors.
     * The color classification is performed using a color correction algorithm.
     *
     * @param circles a list of Ball objects to be classified
     */
    public BallClassifier(List<Ball> circles) {
        balls = new ArrayList<>();
        robotCircle = new ArrayList<>();

        for (Ball ball : circles) {
            /*
            double distGreen = getColorDist(ball.getColor(), BALCK);
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
            System.out.println(ball.getColor() + ", radius: " + ball.getRadius());
            ball.setColor(colorCorrection(ball.getColor()));

            if (ball.getColor().equals(WHITE)) {
                balls.add(ball);
            } else {
                robotCircle.add(ball);
            }
        }
    }

    /**
     * Calculates the color distance between two colors.
     * The color distance is computed using the Euclidean distance formula.
     *
     * @param a the first color
     * @param b the second color
     * @return the color distance between the two colors
     */
    private double getColorDist(Color a, Color b) {
        double dist = 0;
        dist += Math.pow(a.getRed() - b.getRed(), 2);
        dist += Math.pow(a.getGreen() - b.getGreen(), 2);
        dist += Math.pow(a.getBlue() - b.getBlue(), 2);
        return Math.sqrt(dist);
    }

    /**
     * Performs color correction on the given color.
     * The color correction algorithm adjusts the color based on certain thresholds.
     * If the color is close to black, it is classified as black.
     * If the color is close to red, it is classified as red.
     * Otherwise, it is classified as white.
     *
     * @param color the color to be color-corrected
     * @return the color after color correction
     */
    private Color colorCorrection(Color color) {
        if (color.getRed() < 110 && color.getBlue() < 110 && color.getGreen() < 110) {
            return BLACK;
        }
        if (color.getRed() > 200 && color.getBlue() < 150 && color.getGreen() < 150) {
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