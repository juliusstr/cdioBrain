package misc;
import exceptions.NoDataException;

import java.awt.*;

public class Line {

    public Vector2Dv1 p1;
    public Vector2Dv1 p2;

    private Vector2Dv1 hitPoint;
    public int zoneGroupID;


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

        // Calculate the direction vector of the line segment
        double lineDirection_x = x2 - x1;
        double lineDirection_y = y2 - y1;

        // Calculate the vector from the robot's position to a point on the line
        double vectorToLine_x = x1 - x_robot;
        double vectorToLine_y = y1 - y_robot;

        // Calculate the cross product of the line direction vector and the vector to the line
        double crossProduct = lineDirection_x * vectorToLine_y - lineDirection_y * vectorToLine_x;

        // Check if the robot will hit the line segment
        if (crossProduct == 0) {
            // The robot is collinear with the line segment, now check if it lies within the segment bounds
            double dotProduct = lineDirection_x * direction_x + lineDirection_y * direction_y;
            if (dotProduct > 0 && dotProduct <= Math.sqrt(lineDirection_x * lineDirection_x + lineDirection_y * lineDirection_y)) {
                //System.out.println("The robot will hit the line segment.");
                double t = vectorToLine_x / direction_x;
                double x_intersect = x_robot + t * direction_x;
                double y_intersect = y_robot + t * direction_y;
                hitPoint = new Vector2Dv1(x_intersect, y_intersect);
                return true;
            } else {
                //System.out.println("The robot will not hit the line segment.");
                return false;
            }
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
        zoneGroupID = -1;
    }
    public Line(Point p1, Point p2, int zoneGroupID){
        this.p1 = new Vector2Dv1(p1);
        this.p2 = new Vector2Dv1(p2);
        hitPoint = null;
        this.zoneGroupID = zoneGroupID;
    }

    public Vector2Dv1 getHitVector() throws NoDataException{
        if (hitPoint == null){
            throw new NoDataException("No hit on line!");
        }
        return new Vector2Dv1(hitPoint);
    }

    public Vector2Dv1 findClosestPoint(Vector2Dv1 point) {
        // Calculate direction vector of the line segment
        double dx = p1.x - p2.x;
        double dy = p1.x - p2.x;

        // Calculate vector from p2 to the given point
        double px = point.x - p2.x;
        double py = point.x - p2.x;

        // Calculate the parameter t
        double dotProduct = px * dx + py * dy;
        double lineSegmentLengthSquared = dx * dx + dy * dy;
        double t = dotProduct / lineSegmentLengthSquared;

        // Check if the closest point lies within the line segment
        if (t < 0) {
            return p2; // Closest point is p2
        } else if (t > 1) {
            return p1; // Closest point is p1
        } else {
            // Calculate the coordinates of the closest point
            double closestX = p2.x + t * dx;
            double closestY = p2.x + t * dy;
            return new Vector2Dv1(closestX, closestY);
        }
    }

    public double findDistanceToPoint(Vector2Dv1 point) {
        Vector2Dv1 intercept = this.findClosestPoint(point);
        return point.distance(intercept);
    }

}

