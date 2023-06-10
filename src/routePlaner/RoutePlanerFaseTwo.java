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

    public List<Ball> getBalls() {
        return balls;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public RoutePlanerFaseTwo(Robotv1 r, List<Ball> b) {
        balls = b;
        robot = r;
        nav = new NavAlgoPhaseTwo();
    }

    public String nextCommand(){
        return "";
    }


    public void heatGenerator() {

        List<Route> best_route = new ArrayList<>();
        NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();

        int best_score = 0;
        int i = 0;
        int j = 0;
        int route_index = 0;

        for (i = 0; i < balls.size(); i++){
            for (j = 0; j < balls.get(i).getRoutes().size(); j++)
            if(balls.get(i).getRoutes().get(j).getScore() < best_score){
                best_route.add(balls.get(i).getRoutes().get(j));
                route_index++;

            }

        }




    }

}
