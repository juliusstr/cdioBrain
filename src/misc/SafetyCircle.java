package misc;

public class SafetyCircle {

    Vector2Dv1 pos;
    double radius;

    public SafetyCircle(Vector2Dv1 pos, double radius){
        this.pos = pos;
        this.radius = radius;
    }


    public boolean willHitCircle(double[] robotPos, double[] robotDir) {
        double xr = robotPos[0]; // x-coordinate of robot position
        double yr = robotPos[1]; // y-coordinate of robot position
        double dx = robotDir[0]; // x-component of robot direction
        double dy = robotDir[1]; // y-component of robot direction
        double xc = pos.x; // x-coordinate of circle center
        double yc = pos.y; // y-coordinate of circle center

        double a = dx*dx + dy*dy;
        double b = 2*(dx*(xr-xc) + dy*(yr-yc));
        double c = (xr-xc)*(xr-xc) + (yr-yc)*(yr-yc) - radius*radius;

        double discriminant = b*b - 4*a*c;

        if (discriminant < 0) {
            // no real solutions, line does not intersect circle
            return false;
        } else if (discriminant == 0) {
            // one real solution, line is tangent to circle
            double t = -b / (2*a);
            if (t < 0) {
                // tangent point is behind robot, circle is not hit
                return false;
            } else {
                // tangent point is in front of robot, circle is grazed
                return true;
            }
        } else {
            // two real solutions, line intersects circle
            double t1 = (-b + Math.sqrt(discriminant)) / (2*a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2*a);
            if (t1 < 0 && t2 < 0) {
                // both intersection points are behind robot, circle is not hit
                return false;
            } else if (t1 < 0) {
                // one intersection point is behind robot, check the other
                if (t2 > 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (t2 < 0) {
                // one intersection point is behind robot, check the other
                if (t1 > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                // both intersection points are in front of robot, circle is hit
                return true;
            }
        }
    }

    public boolean willHitCircle(Robotv1 robot) {
        double xr = robot.getxPos(); // x-coordinate of robot position
        double yr = robot.getyPos(); // y-coordinate of robot position
        double dx = robot.getDirection().x; // x-component of robot direction
        double dy = robot.getDirection().y; // y-component of robot direction
        double xc = pos.x; // x-coordinate of circle center
        double yc = pos.y; // y-coordinate of circle center

        double a = dx*dx + dy*dy;
        double b = 2*(dx*(xr-xc) + dy*(yr-yc));
        double c = (xr-xc)*(xr-xc) + (yr-yc)*(yr-yc) - radius*radius;

        double discriminant = b*b - 4*a*c;

        if (discriminant < 0) {
            // no real solutions, line does not intersect circle
            return false;
        } else if (discriminant == 0) {
            // one real solution, line is tangent to circle
            double t = -b / (2*a);
            if (t < 0) {
                // tangent point is behind robot, circle is not hit
                return false;
            } else {
                // tangent point is in front of robot, circle is grazed
                return true;
            }
        } else {
            // two real solutions, line intersects circle
            double t1 = (-b + Math.sqrt(discriminant)) / (2*a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2*a);
            if (t1 < 0 && t2 < 0) {
                // both intersection points are behind robot, circle is not hit
                return false;
            } else if (t1 < 0) {
                // one intersection point is behind robot, check the other
                if (t2 > 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (t2 < 0) {
                // one intersection point is behind robot, check the other
                if (t1 > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                // both intersection points are in front of robot, circle is hit
                return true;
            }
        }
    }


}