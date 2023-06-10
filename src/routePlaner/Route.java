package routePlaner;

import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private Ball end = null;
    private Vector2Dv1 startPoint = null;
    private List<Vector2Dv1> waypoints = null;
    private int score = 0;

    public Route(Vector2Dv1 v){ startPoint = v; }
    public void setScore(int s){ score = s; }
    public void setWaypoints(List<Vector2Dv1> r){ waypoints = r; }
    public void setEnd(Ball e){ end = e; }

    public int getScore(){
        return score;
    }

    public List<Vector2Dv1> getWaypoints(){
        return waypoints;
    }

    public Ball getEnd(){
        return end;
    }

}
