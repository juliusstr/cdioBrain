package routePlaner;

import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import nav.NavAlgoFaseOne;
import nav.NavAlgoPhaseTwo;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanerFaseTwo {
    private NavAlgoPhaseTwo nav = null;
    private List<Ball> balls = null;
    private List<Ball> ballsHeat1 = null;
    private List<Ball> ballsHeat2 = null;
    private List<Ball> ballsHeat3 = null;
    private Robotv1 robot = null;
    private Ball goalFakeBall = null;

    public RoutePlanerFaseTwo(Robotv1 r, List<Ball> b) {
        balls = b;
        robot = r;
        nav = new NavAlgoPhaseTwo();
    }

    public void ballRoutes(Boolean dificultBalls, int minAmount){

    }

    public String nextCommand(){
        return "";
    }



}
