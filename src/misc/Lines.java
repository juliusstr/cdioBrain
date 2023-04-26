package misc;
import java.awt.*;

public class Lines {

    private Point p1;
    private Point p2;

    public boolean hit(Vector2Dv1 pos, Vector2Dv1 dir) {

        // Calculate the slope and y-intercept of the line
        double slope = (p2.y - p2.y) / (p2.x - p1.x);
        double yIntercept = p1.y - slope * p1.x;

        // Calculate the x-coordinate of the point where the vector intersects the line
        double xIntersection = (yIntercept - pos.y) / dir.y * dir.x + pos.x;

        // Determine if the x-coordinate of the intersection point is between the x-coordinates of the two points
        if ((p1.x <= xIntersection && xIntersection <= p2.x) || (p2.x <= xIntersection && xIntersection <= p1.x)) {
            return true;
        } else {
           return false;
        }
    }

        public Lines(Point p1, Point p2){
            this.p1=p1;
            this.p2=p2;
        }

}

