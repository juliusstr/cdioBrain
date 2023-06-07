package misc;

import exceptions.*;

import java.awt.*;
import java.util.ArrayList;

public class Cross {

    public static final int CROSS_LENGTH = 25;
    public static final int OFFSET_LENGTH = 4;
    public static final int SHORT_SIDE_LENGTH = CROSS_LENGTH - OFFSET_LENGTH;

    public ArrayList<Point> crossPoint;
    public ArrayList<Line> crossLines;
    public Vector2Dv1 vec;
    public Vector2Dv1 pos;

    public Boolean hitreg;
    public static final double rotate_angle = Math.PI / 180;

    public Cross(Vector2Dv1 pos, Vector2Dv1 vec) {
        this.vec = vec;
        this.pos = pos;
        crossPoint = new ArrayList<>();
        int i;

        for (i = 0; i < 4; i++) {
            vec.normalize();
            Vector2Dv1 offsetvec = new Vector2Dv1(vec);
            vec.multiply(CROSS_LENGTH);
            offsetvec.multiply(OFFSET_LENGTH);
            offsetvec.rotateBy(-Math.PI / 2);
            Vector2Dv1 point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            offsetvec.rotateBy(Math.PI);
            point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            Vector2Dv1 cornervec = new Vector2Dv1(offsetvec);
            cornervec.normalize();
            cornervec.multiply(SHORT_SIDE_LENGTH);
            cornervec.rotateBy(Math.PI / 2);
            point = Vector2Dv1.add(cornervec, point);
            crossPoint.add(point.getPoint());
            vec.rotateBy(Math.PI / 2);
        }

        crossLines = new ArrayList<>();
        for (i = 0; i < crossPoint.size(); i++) {
            if (i < crossPoint.size() - 1) {
                crossLines.add(new Line(crossPoint.get(i), crossPoint.get(i + 1)));
            } else {
                crossLines.add(new Line(crossPoint.get(i), crossPoint.get(0)));
            }
        }

    }

    /**
     * Checks if there is a hit against the cross, or any safety circles in the cross.
     * @param pos
     * @param directionToTarget
     * @return  True if there is a hit
     *          False if there is no hit
     */
    public void hit(Vector2Dv1 pos, Vector2Dv1 directionToTarget) throws LineReturnException, Vector2Dv1ReturnException, NoHitException {
        ArrayList<Line> lines = hitsLineOnCross(pos, directionToTarget);//hits in line
        if (lines.size() != 0){
            hitreg = Boolean.FALSE;
            ArrayList<Vector2Dv1> hits = new ArrayList<>();
            int size = lines.size();
            for (int i = 0; i < size; i++) {
                try {
                    hits.add(lines.get(i).getHitVector());
                } catch (NoDataException e) { // no line so do not add to hit list
                    lines.remove(i);
                    i--;
                    size--;
                }
            }

            int index = -1;
            double dist = Double.MAX_VALUE;
            for (int j = 0; j < lines.size(); j++) {
                double localDist = pos.getSubtracted(hits.get(j)).getLength();
                if (localDist < dist)
                    index = j;
                dist = localDist;
            }
            Line closestLine = lines.get(index);
            throw new LineReturnException(closestLine);
        }
        //todo fix to get closest hit or center for zone that was hit closest to pos
        for (Point point : crossPoint) { // hits in zones
            SafetyCircle circle = new SafetyCircle(new Vector2Dv1(point), SafetyCircle.SAFE_ROBOT_WITH);
            ArrayList<Vector2Dv1> intercepts = circle.willHitCircle(pos, directionToTarget);
            if(intercepts.size() != 0){
                int index = -1;
                double dist = Double.MAX_VALUE;
                for (int i = 0; i < intercepts.size(); i++) {
                    double temp = pos.distance(intercepts.get(i));
                    if(dist>temp){
                        index = i;
                        dist = temp;
                    }
                }
                throw new Vector2Dv1ReturnException(intercepts.get(index));
            }

        }
        throw new NoHitException();
    }

    public ArrayList<Line> hitsLineOnCross(Vector2Dv1 robotPos, Vector2Dv1 dir) {
        ArrayList<Line> lines = new ArrayList<>();
        for (Line line : crossLines) {
            if (line.hit(robotPos, dir)) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Gets the exit point of the intercept with the safeZone
     * @param pos Position vector. E.G. robot or waypoint.
     * @param dir Diriction to look for intercepts.
     * @return Vector for exit of safeZone
     * @throws NoHitException Thrown when no intercept.
     */
    public Vector2Dv1 safeZoneExit(Vector2Dv1 pos, Vector2Dv1 dir) throws NoHitException {
        //gets the closest intercept center from pos.
        ArrayList<Point> interceptZoneCenters = new ArrayList<>();
        for (Point point : crossPoint) { // hits in zones
            SafetyCircle circle = new SafetyCircle(new Vector2Dv1(point), SafetyCircle.SAFE_ZONE_RADIUS);
            ArrayList<Vector2Dv1> intercepts = circle.willHitCircle(pos, dir);
            if(intercepts.size() != 0){
                interceptZoneCenters.add(point);
            }

        }
        if (interceptZoneCenters.size() == 0)
            throw new NoHitException("No intercept with safeZones!");

        int index = -1;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < interceptZoneCenters.size(); i++) {
            double temp = pos.distance(new Vector2Dv1(interceptZoneCenters.get(i)));
            if(dist>temp){
                index = i;
                dist = temp;
            }
        }
        Point point = interceptZoneCenters.get(index);

        //getes the furthest intercept form pos
        SafetyCircle circle = new SafetyCircle(new Vector2Dv1(point), SafetyCircle.SAFE_ZONE_RADIUS);
        ArrayList<Vector2Dv1> intercepts = circle.willHitCircle(pos, dir);
        if(intercepts.size() != 0) {//todo remove me if i work. catch above.
            index = -1;
            dist = Double.MIN_VALUE;
            for (int i = 0; i < intercepts.size(); i++) {
                double temp = pos.distance(intercepts.get(i));
                if (dist < temp) {
                    index = i;
                    dist = temp;
                }
            }
            return intercepts.get(index);

        }
        throw new NoHitException("No intercept with safeZone!");
    }
}
