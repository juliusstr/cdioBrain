package misc;

import exceptions.NoHitException;

import java.util.ArrayList;

public class Zone {

    public static final int CRITICAL_ZONE_RADIUS = 40;//todo fine tune meeeeeee
    public static final int SAFE_ZONE_RADIUS = 50;//todo fine tune meeeeeee

    public Vector2Dv1 pos;
    public double radius;
    public int zoneGroupID;

    private ArrayList<Vector2Dv1> intercepts;
    private Vector2Dv1 closestIntercept;

    public Zone(Vector2Dv1 pos, double radius){
        this.pos = pos;
        this.radius = radius;
        zoneGroupID = -1;
    }
    public Zone(Vector2Dv1 pos, double radius, int zoneGroupID){
        this.pos = pos;
        this.radius = radius;
        this.zoneGroupID = zoneGroupID;
    }

    public ArrayList<Vector2Dv1> willHitZone(Vector2Dv1 pos, Vector2Dv1 dir) {
        closestIntercept = null;
        intercepts = new ArrayList<>();
        ArrayList<Vector2Dv1> returnList = new ArrayList<>();
        double xr = pos.x; // x-coordinate of robot position
        double yr = pos.y; // y-coordinate of robot position
        double dx = dir.x; // x-component of robot direction
        double dy = dir.y; // y-component of robot direction
        double xc = this.pos.x; // x-coordinate of circle center
        double yc = this.pos.y; // y-coordinate of circle center

        double a = dx*dx + dy*dy;
        double b = 2*(dx*(xr-xc) + dy*(yr-yc));
        double c = (xr-xc)*(xr-xc) + (yr-yc)*(yr-yc) - radius*radius;

        double discriminant = b*b - 4*a*c;

        if (discriminant < 0) {
            // no real solutions, line does not intersect circle
            return returnList;
        } else if (discriminant == 0) {
            // one real solution, line is tangent to circle
            double t = -b / (2*a);
            if (t < 0) {
                // tangent point is behind robot, circle is not hit
                return returnList;
            } else {
                // tangent point is in front of robot, circle is grazed
                Vector2Dv1 vector2D = pos.getAdded(dir.getMultiplied(t));
                returnList.add(vector2D);
                closestIntercept = vector2D;
                intercepts.add(vector2D);
                return returnList;
            }
        } else {
            // two real solutions, line intersects circle
            double t1 = (-b + Math.sqrt(discriminant)) / (2*a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2*a);
            if (t1 < 0 && t2 < 0) {
                // both intersection points are behind robot, circle is not hit
                return returnList;
            } else if (t1 < 0) {
                // one intersection point is behind robot, check the other
                if (t2 > 0) {
                    Vector2Dv1 vector2D = pos.getAdded(dir.getMultiplied(t2));
                    returnList.add(vector2D);
                    closestIntercept = vector2D;
                    intercepts.add(vector2D);
                    return returnList;
                } else {
                    return returnList;
                }
            } else if (t2 < 0) {
                // one intersection point is behind robot, check the other
                if (t1 > 0) {
                    Vector2Dv1 vector2D = pos.getAdded(dir.getMultiplied(t1));
                    returnList.add(vector2D);
                    closestIntercept = vector2D;
                    intercepts.add(vector2D);
                    return returnList;
                } else {
                    return returnList;
                }
            } else {
                // both intersection points are in front of robot, circle is hit
                Vector2Dv1 vector2D = pos.getAdded(dir.getMultiplied(t1));
                returnList.add(vector2D);
                intercepts.add(vector2D);
                vector2D = pos.getAdded(dir.getMultiplied(t2));
                returnList.add(vector2D);
                intercepts.add(vector2D);
                if(intercepts.get(0).distance(pos) > intercepts.get(1).distance(pos)){
                    closestIntercept = intercepts.get(1);
                } else {
                    closestIntercept = intercepts.get(0);
                }
                return returnList;
            }
        }
    }


    private double calY(Vector2Dv1 pos, Vector2Dv1 dir , double t){
        if (dir.x != 0){
            double step = (t- pos.x)/dir.x;
            return pos.y + step * dir.y;
        } else{
            double x = pos.x;
            double y1 = this.pos.y + Math.sqrt(Math.pow(radius, 2) - Math.pow(x - this.pos.x, 2));
            double y2 = this.pos.y - Math.sqrt(Math.pow(radius, 2) - Math.pow(x - this.pos.x, 2));
            double distY1 = pos.y - y1;
            double distY2 = pos.y - y2;
            if(distY1>distY2){
                return y2;
            } else {
                return y1;
            }

        }
    }

    public ArrayList<Vector2Dv1> getIntercepts() throws NoHitException {
        if(intercepts == null){
            throw new NoHitException("No intercept on zone!");
        }
        return intercepts;
    }

    public Vector2Dv1 getClosestIntercept() throws NoHitException {
        if(closestIntercept == null){
            throw new NoHitException("No intercept on zone!");
        }
        return closestIntercept;
    }
}
