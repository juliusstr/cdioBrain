package misc;

import java.awt.*;
import java.util.ArrayList;

public class Boundry {

    public ArrayList<Line> bound;
    public ArrayList<Point> points;
    public double scale;

    public Boundry(ArrayList<Vector2Dv1> vectors) {
        Point a ,b, c, d;

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

        scale = 1800/a.distance(b);

        a.x *= scale;
        a.y *= scale;
        b.x *= scale;
        b.y *= scale;
        c.x *= scale;
        c.y *= scale;
        d.x *= scale;
        d.y *= scale;

        points = new ArrayList<>();

        points.add(a);
        points.add(b);
        points.add(c);
        points.add(d);


        Line ab = new Line(a, b);
        Line bc = new Line(b, c);
        Line cd = new Line(c, d);
        Line da = new Line(d, a);
        bound = new ArrayList<>();
        bound.add(ab);
        bound.add(bc);
        bound.add(cd);
        bound.add(da);
    }

}
