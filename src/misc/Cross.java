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
     * @param robot
     * @param directionToTarget
     * @return  True if there is a hit
     *          False if there is no hit
     */
    public void hit(Robotv1 robot, Vector2Dv1 directionToTarget) throws LineReturnException, Vector2Dv1ReturnException, NoHitException {

        ArrayList<Line> lines = hits(robot, directionToTarget);//hits in line
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
                double localDist = robot.getPosVector().getSubtracted(hits.get(j)).getLength();
                if (localDist < dist)
                    index = j;
                dist = localDist;
            }
            Line closestLine = lines.get(index);
            throw new LineReturnException(closestLine);
        }

        for (Point point : crossPoint) { // hits in zones
            SafetyCircle circle = new SafetyCircle(new Vector2Dv1(point), SafetyCircle.SAFE_ROBOT_WITH);
            ArrayList<Vector2Dv1> intercepts = circle.willHitCircle(robot, directionToTarget);
            if(intercepts.size() != 0){
                int index = -1;
                double dist = Double.MAX_VALUE;
                for (int i = 0; i < intercepts.size(); i++) {
                    double temp = robot.getPosVector().distance(intercepts.get(i));
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

    public ArrayList<Line> hits(Robotv1 robot, Vector2Dv1 dir) {
        ArrayList<Line> lines = new ArrayList<>();
        for (Line line : crossLines) {
            if (line.hit(robot.getPosVector(), dir)) {
                lines.add(line);
            }
        }
        return lines;
    }


    //todo finde usages
    /*public double avoid(Robotv1 robot, Vector2Dv1 dir) throws NoHitException {
        Vector2Dv1 avoid_dir = new Vector2Dv1(dir);

        while(hitreg == Boolean.TRUE) {
            avoid_dir.getRotatedBy(rotate_angle * (-1));
            hit(robot, avoid_dir);
        }
        return rotate_angle;
    }*/
}
