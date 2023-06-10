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


    public ArrayList<Ball> heatGenerator(ArrayList<Ball> ball_list) {

        //NavAlgoPhaseTwo nav = new NavAlgoPhaseTwo();
        ArrayList<Ball> best_heat = new ArrayList<>();

        int score1 = 0;
        int score2 = 0;
        int temp_score = 0;
        int best_score = 0;
        int i, j ,k;

        for (i = 0; i < balls.size(); i++){

            for (j = 0; j < balls.get(i).getRoutes().size(); j++){
                score1 = balls.get(i).getRoutes().get(j).getScore();
                for (k = 0; k < balls.get(i).getRoutes().get(j).getEnd().getRoutes().size(); k++){
                    score2 = balls.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getScore();
                    temp_score = score1 + score2;
                    if(temp_score < best_score){
                        best_score = temp_score;
                        best_heat.add(balls.get(i));
                        best_heat.add(balls.get(i).getRoutes().get(j).getEnd());
                        best_heat.add(balls.get(i).getRoutes().get(j).getEnd().getRoutes().get(k).getEnd());
                    }
                }
            }
        }
        return best_heat;
    }
}
