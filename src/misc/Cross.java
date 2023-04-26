package misc;

import misc.Lines;
import misc.Vector2Dv1;

import java.awt.*;
import java.util.ArrayList;

public class Cross {

    public static final int CROSS_LENGTH = 10;
    public static final int OFFSET_LENGTH = 2;
    public static final int SHORT_SIDE_LENGTH = 4;
    public static final double PI = 3.141592653589793;

    public ArrayList<Point> crossPoint;
    public ArrayList<Lines> crossLines;
    private Vector2Dv1 vec;
    private Vector2Dv1 pos;

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
            offsetvec.rotateBy(-PI/2);
            Vector2Dv1 point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            offsetvec.rotateBy(PI);
            point = Vector2Dv1.add(offsetvec, Vector2Dv1.add(pos, vec));
            crossPoint.add(point.getPoint());
            Vector2Dv1 cornervec = new Vector2Dv1(offsetvec);
            cornervec.multiply(SHORT_SIDE_LENGTH);
            cornervec.rotateBy(PI/2);
            point = Vector2Dv1.add(cornervec, point);
            crossPoint.add(point.getPoint());
            vec.rotateBy(PI/2);
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

}
