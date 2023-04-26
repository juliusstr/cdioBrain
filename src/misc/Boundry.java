package misc;

import java.awt.*;
import java.util.ArrayList;

public class Boundry {

    public ArrayList<Lines> bound;

    public Boundry(Point a, Point b, Point c, Point d) {
        Lines ab = new Lines (a, b);
        Lines bc = new Lines (b, c);
        Lines cd = new Lines (c, d);
        Lines da = new Lines (d, a);
        bound = new ArrayList<>();
        bound.add(ab);
        bound.add(bc);
        bound.add(cd);
        bound.add(da);
    }

}
