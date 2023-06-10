package routePlaner;

import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private Ball end = null;
    private List<Vector2Dv1> route = null;
    private int score = 0;

    public Route(){}
    public void setScore(int s){ score = s; }
    public void setRoute(List<Vector2Dv1> r){ route = r; }
    public void setEnd(Ball e){ end = e; }

    public int getScore(){
        return score;
    }

    public List<Vector2Dv1> getRoute(){
        return route;
    }

    public Ball getEnd(){
        return end;
    }

}
