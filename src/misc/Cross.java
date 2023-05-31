package misc;

import exceptions.NoDataException;
import exceptions.NoHitException;
import misc.Lines;
import misc.Vector2Dv1;

import javax.sound.sampled.Line;
import java.awt.*;
import java.util.ArrayList;

public class Cross {

    public static final int CROSS_LENGTH = 25;
    public static final int OFFSET_LENGTH = 4;
    public static final int SHORT_SIDE_LENGTH = CROSS_LENGTH - OFFSET_LENGTH;

    public ArrayList<Point> crossPoint;
    public ArrayList<Lines> crossLines;
    public Vector2Dv1 vec;
    public Vector2Dv1 pos;

    public Cross(Vector2Dv1 pos, Vector2Dv1 vec){
        this.vec = vec;
        this.pos = pos;
        crossPoint = new ArrayList<>();
        int i;

        for (i = 0; i < 4 ; i++){
            vec.normalize();
            Vector2Dv1 offsetvec = new Vector2Dv1(vec);
            vec.multiply(CROSS_LENGTH);
            offsetvec.multiply(OFFSET_LENGTH);
            offsetvec.rotateBy(- Math.PI/2);
            Vector2Dv1 point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            offsetvec.rotateBy(Math.PI);
            point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            Vector2Dv1 cornervec = new Vector2Dv1(offsetvec);
            cornervec.normalize();
            cornervec.multiply(SHORT_SIDE_LENGTH);
            cornervec.rotateBy(Math.PI/2);
            point = Vector2Dv1.add(cornervec, point);
            crossPoint.add(point.getPoint());
            vec.rotateBy(Math.PI/2);
        }

        crossLines = new ArrayList<>();
        for (i = 0; i < crossPoint.size(); i++){
            if(i < crossPoint.size()-1) {
                crossLines.add(new Lines(crossPoint.get(i), crossPoint.get(i + 1)));
            }
            else{
                crossLines.add(new Lines(crossPoint.get(i), crossPoint.get(0)));
            }
        }

    }
    public Lines hit(Robotv1 robot, Vector2Dv1 dir) throws NoHitException {
        ArrayList<Lines> lines = hits(robot, dir);
        if(lines.size() == 0)
            throw new NoHitException("No lines in the cross was hit!");
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
        for (int j = 0; j < lines.size(); j++){
            double localDist = robot.getPosVector().getSubtracted(hits.get(j)).getLength();
            if (localDist < dist)
                index = j;
                dist = localDist;
        }

        return lines.get(index);
    }
    public ArrayList<Lines> hits(Robotv1 robot, Vector2Dv1 dir){
        ArrayList<Lines> lines = new ArrayList<>();
        for (Lines line : crossLines) {
            if(line.hit(robot.getPosVector(), dir)){
                lines.add(line);
            }
        }
        return lines;
    }

}
