package misc;
import exceptions.NoDataException;

import java.awt.*;

public class Lines {

    private Vector2Dv1 p1;
    private Vector2Dv1 p2;

    private Point hitPoint;


    public boolean hit(Vector2Dv1 pos, Vector2Dv1 dir) {

        dir.normalize();

        // Calculate the slope and y-intercept of the line
        double slope = (p2.y - p1.y) / (p2.x - p1.x);
        double intercept = p1.x - slope * p1.x;

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        // Step 3: Calculate the cross product of the direction vector and the vector perpendicular to the line
        double cross = dx * dir.y - dy * dir.x;

        // Step 4: Find the intersection point between the line and the vector of the robot
        double t = (intercept - pos.y + slope * pos.x) / (slope * dir.y - dir.x);
        double x_intersect = pos.x + t * dir.y;
        double y_intersect = pos.y + t * dir.y;

        // Check if the robot hits the line
        if (cross == 0.0) {
            System.err.println("The robot's direction is parallel to the line and will not hit it.");
            return false;
        } else if (t >= 0.0 && t <= 1.0) {
            System.err.println("The robot will hit the line at (" + x_intersect + ", " + y_intersect + ").");
            hitPoint = new Point((int) x_intersect, (int) y_intersect);
            return true;
        } else {
            System.err.println("The robot will not hit the line.");
            return false;
        }

        /*
        // Calculate the x-coordinate of the point where the vector intersects the line
        double xIntersection = (yIntercept - pos.y) / dir.y * dir.x + pos.x;

        // Determine if the x-coordinate of the intersection point is between the x-coordinates of the two points
        if ((p1.x <= xIntersection && xIntersection <= p2.x) || (p2.x <= xIntersection && xIntersection <= p1.x)) {
            double yIntersection = slope * xIntersection + yIntercept;
            hitPoint = new Point((int) xIntersection, (int) yIntersection);
            return true;
        } else {
            return false;
        }*/

    }

    @Override
    public String toString() {
        return "Lines{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", hitPoint=" + hitPoint +
                '}';
    }

    public Lines(Point p1, Point p2){
        this.p1 = new Vector2Dv1(p1);
        this.p2 = new Vector2Dv1(p2);
        hitPoint = null;
    }

    public Point getHitPoint() throws NoDataException {
        if (hitPoint == null){
            throw new NoDataException("No hit on line!");
        }
        return hitPoint;
    }
    public Vector2Dv1 getHitVector() throws NoDataException{
        if (hitPoint == null){
            throw new NoDataException("No hit on line!");
        }
        return new Vector2Dv1(hitPoint);
    }

}

