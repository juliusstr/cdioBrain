package misc;

public class SafetyCircle {

    public static final int SAFE_ROBOT_WITH = 30;//todo fine tune meeeeeee

    public Vector2Dv1 pos;
    public double radius;

    public SafetyCircle(Vector2Dv1 pos, double radius){
        this.pos = pos;
        this.radius = radius;
    }

    public ArrayList<Vector2Dv1> willHitCircle(Robotv1 robot, Vector2Dv1 directionToTarget) {
        ArrayList<Vector2Dv1> returnList = new ArrayList<>();
        double xr = robot.getxPos(); // x-coordinate of robot position
        double yr = robot.getyPos(); // y-coordinate of robot position
        double dx = directionToTarget.x; // x-component of robot direction
        double dy = directionToTarget.y; // y-component of robot direction
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
