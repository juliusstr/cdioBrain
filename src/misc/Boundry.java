package misc;

import Client.StandardSettings;
import org.opencv.core.Mat;

import java.awt.*;
import java.util.ArrayList;

import static Client.StandardSettings.BOUNDERY_WAYPOINT_DISTANCE_FROM_BOUNDERY;

public class Boundry {

    public ArrayList<Line> bound;
    public ArrayList<Point> points;
    public double scale = 1;

    public int zoneGroupID;

    public Vector2Dv1 goalWaypoint0;//go firsts to this then 1,
    public Vector2Dv1 goalWaypoint1;

    public Boundry(ArrayList<Vector2Dv1> vectors) {
        Point a ,b, c, d;
        zoneGroupID = 1;
        //finding a
        int index = -1;
        double score = Double.MAX_VALUE;
        for (int i = 0; i < vectors.size(); i++) {
            double length = vectors.get(i).getLength();
            if (score > length){
                index = i;
                score = length;
            }
        }
        a = vectors.get(index).getPoint();
        vectors.remove(index);

        //finding c
        index = -1;
        score = Double.MIN_VALUE;
        for (int i = 0; i < vectors.size(); i++) {
            double length = vectors.get(i).getLength();
            if (score < length){
                index = i;
                score = length;
            }
        }
        c = vectors.get(index).getPoint();
        vectors.remove(index);

        //finding b and d
        if(vectors.get(0).y<vectors.get(1).y){
            b = vectors.get(0).getPoint();
            d = vectors.get(1).getPoint();
        } else {
            b = vectors.get(1).getPoint();
            d = vectors.get(0).getPoint();
        }
        //todo add scale avg to C-D and y axis
        //scale = 1800/a.distance(b);

        /*a.x *= scale;
        a.y *= scale;
        b.x *= scale;
        b.y *= scale;
        c.x *= scale;
        c.y *= scale;
        d.x *= scale;
        d.y *= scale;
         */

        points = new ArrayList<>();

        points.add(a);
        points.add(b);
        points.add(c);
        points.add(d);


        Line ab = new Line(a, b,zoneGroupID);
        Line bc = new Line(b, c,zoneGroupID);
        Line cd = new Line(c, d,zoneGroupID);
        Line da = new Line(d, a,zoneGroupID);
        bound = new ArrayList<>();
        bound.add(ab);
        bound.add(bc);
        bound.add(cd);
        bound.add(da);
    }
    /**
     * Initializes the goal waypoints used for navigation.
     * Calculates the coordinates of two goal waypoints based on the boundary points.
     * Sets the goalWaypoint0, goalWaypoint1, and goalFakeBall variables.
     */
    public void initGoalWaypoints() {
        ArrayList<Vector2Dv1> corners = getCornersForGoal();
        Vector2Dv1 midVector = corners.get(0).getMidVector(corners.get(1));
        Vector2Dv1 dir = corners.get(0).getSubtracted(corners.get(1)).getNormalized().getRotatedBy((Math.PI / 2)*(-1));
        goalWaypoint1 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST));
        goalWaypoint0 = midVector.getAdded(dir.getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_RUN_UP_DIST + StandardSettings.ROUTE_PLANER_GOAL_CASTER_WEEL_LINE_UP));

    }
    /**
     * Gets the corners that is on the small goal boundary
     * @return List of Vector2D with the coordinates to the corners
     */
    public ArrayList<Vector2Dv1> getCornersForGoal(){
        int index1 = -1, index2 = -1;;
        int minX = Integer.MAX_VALUE;
        ArrayList<Vector2Dv1> returnList = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < minX) {
                index1 = i;
                minX = points.get(i).x;
            }
        }
        returnList.add(new Vector2Dv1(points.get(index1)));

        minX = Integer.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < minX && i != index1) {
                index2 = i;
                minX = points.get(i).x;
            }
        }
        returnList.add(new Vector2Dv1(points.get(index2)));
        if(returnList.get(0).y < returnList.get(1).y){
            Vector2Dv1 temp = returnList.get(0);
            returnList.remove(temp);
            returnList.add(temp);
        }
        return  returnList;
    }
    /**
     * Gets the goal position as a vector from the corners with the smallest x coordinate
     * @return Vector2D with the pos of the goal
     */
    public Vector2Dv1 getGoalPos(){
        ArrayList<Vector2Dv1> corners = getCornersForGoal();
        Vector2Dv1 smallGoal = corners.get(0).getMidVector(corners.get(1));
        return smallGoal;
    }

    public Vector2Dv1 getGoalWaypoint(int i){
        switch (i){
            case 0:
                return goalWaypoint0;
            case 1:
                return goalWaypoint1;
        }
        return null;
    }

    public boolean vectorInsideBoundary(Vector2Dv1 pos) {
        Vector2Dv1 edge1 = (new Vector2Dv1(points.get(0))).getSubtracted((new Vector2Dv1(points.get(3))));
        Vector2Dv1 edge2 = (new Vector2Dv1(points.get(2))).getSubtracted((new Vector2Dv1(points.get(3))));

        Vector2Dv1 pointVector1 = pos.getSubtracted((new Vector2Dv1(points.get(3))));

        double dotProduct1 = pointVector1.dot(edge1);
        double dotProduct2 = pointVector1.dot(edge2);

        if (dotProduct1 < 0 || dotProduct1 > edge1.dot(edge1)) {
            return false;
        }

        if (dotProduct2 < 0 || dotProduct2 > edge2.dot(edge2)) {
            return false;
        }

        return true;
    }

    public boolean waypointSafeDistFromBoundary(Vector2Dv1 waypoint){
        Vector2Dv1 a = new Vector2Dv1(points.get(0));
        Vector2Dv1 b = new Vector2Dv1(points.get(1));
        Vector2Dv1 c = new Vector2Dv1(points.get(2));
        Vector2Dv1 d = new Vector2Dv1(points.get(3));
        Vector2Dv1 ac = c.getSubtracted(a).getNormalized().getMultiplied(BOUNDERY_WAYPOINT_DISTANCE_FROM_BOUNDERY);
        Vector2Dv1 bd = d.getSubtracted(b).getNormalized().getMultiplied(BOUNDERY_WAYPOINT_DISTANCE_FROM_BOUNDERY);
        a.add(ac);
        ac.rotateBy(Math.PI);
        c.add(ac);
        b.add(bd);
        bd.rotateBy(Math.PI);
        d.add(bd);
        ArrayList<Vector2Dv1> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        Boundry tempBoundery = new Boundry(list);
        return tempBoundery.vectorInsideBoundary(waypoint);
    }

}
