package routePlaner;

import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private Vektor2Dv1 end = null;
    private List<Vektor2Dv1> route = null;
    private int score = null;


    public void setScore(int s){ score = s; }
    public void setRoute(List<Vektor2Dv1> r){ route = r; }
    public void setEnd(Vektor2Dv1 e){ end = e; }

    public int getScore(){
        return score;
    }

    public List<Vektor2Dv1> getRoute(){
        return route;
    }

    public Vektor2Dv1 getEnd(){
        return end;
    }

}
