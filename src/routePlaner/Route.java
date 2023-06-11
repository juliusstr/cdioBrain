package routePlaner;

import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private Ball end = null;
    private Vector2Dv1 startPoint = null;
    private ArrayList<Vector2Dv1> waypoints = null;
    private double score = 0;

    public Route(Vector2Dv1 v){ startPoint = v; }
    public void setScore(double s){ score = s; }
    public void setWaypoints(ArrayList<Vector2Dv1> r){ waypoints = r; }
    public void setEnd(Ball e){ end = e; }

    public double getScore(){
        return score;
    }

    public ArrayList<Vector2Dv1> getWaypoints(){
        return waypoints;
    }

    public Ball getEnd(){
        return end;
    }

}
