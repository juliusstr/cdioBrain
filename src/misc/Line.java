package misc;
import exceptions.NoDataException;

import java.awt.*;

public class Line {

    public Vector2Dv1 p1;
    public Vector2Dv1 p2;

    private Vector2Dv1 hitPoint;


    public boolean hit(Vector2Dv1 pos, Vector2Dv1 dir1) {

        Vector2Dv1 dir = new Vector2Dv1(dir1);
        dir.normalize();

        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;

        // Define the position and direction vectors of the robot
        double x_robot = pos.x;
        double y_robot = pos.y;
        double direction_x = dir.x;
        double direction_y = dir.y;

        // Calculate the intersection point between the line segment and the infinite line
        double t;
        double x_intersect, y_intersect;

        if (x1 == x2) {
            // Handle vertical line segment
            t = (x1 - x_robot) / direction_x;
            y_intersect = y_robot + t * direction_y;
            x_intersect = x1;
        } else {
            // Calculate the intersection point
            t = ((x1 - x_robot) * (y1 - y2) - (y1 - y_robot) * (x1 - x2)) /
                    (direction_x * (y1 - y2) - direction_y * (x1 - x2));
            x_intersect = x_robot + t * direction_x;
            y_intersect = y_robot + t * direction_y;
        }

        // Check if the intersection point lies within the line segment
        boolean intersectsLineSegment = isBetween(x_intersect, x1, x2) && isBetween(y_intersect, y1, y2);

        if (intersectsLineSegment) {
            //System.out.println("The robot will hit the line segment at (" + x_intersect + ", " + y_intersect + ").");
            hitPoint = new Vector2Dv1( x_intersect, y_intersect);
            return true;
        } else {
            //System.out.println("The robot will not hit the line segment.");
            return false;
        }
    }

    // Helper method to check if a value is between two other values
    private static boolean isBetween(double value, double min, double max) {
        return value >= Math.min(min, max) && value <= Math.max(min, max);
    }

    @Override
    public String toString() {
        return "Lines{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", hitPoint=" + hitPoint +
                '}';
    }

    public Line(Point p1, Point p2){
        this.p1 = new Vector2Dv1(p1);
        this.p2 = new Vector2Dv1(p2);
        hitPoint = null;
    }

    public Vector2Dv1 getHitVector() throws NoDataException{
        if (hitPoint == null){
            throw new NoDataException("No hit on line!");
        }
        return new Vector2Dv1(hitPoint);
    }

}

