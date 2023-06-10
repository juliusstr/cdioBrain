package routePlaner;

import exceptions.NoRouteException;
import misc.Robotv1;
import misc.Vector2Dv1;
import misc.ball.Ball;
import nav.NavAlgoFaseOne;
import nav.NavAlgoPhaseTwo;
import nav.WaypointGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RoutePlanerFaseTwo {
    private NavAlgoPhaseTwo nav = null;
    private List<Ball> balls = null;
    private List<Ball> ballsHeat1 = null;
    private List<Ball> ballsHeat2 = null;
    private List<Ball> ballsHeat3 = null;
    private Robotv1 robot = null;
    private Ball goalFakeBall = null;

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

    private void ballRoutes(Boolean difficultBalls, int minAmount) throws NoRouteException, TimeoutException {
        List<Ball> usedBalls = new ArrayList<>();
        for (Ball b : balls) {
            usedBalls.add(b);
            if((!difficultBalls || b.getPlacement() == Ball.Placement.FREE)){
                for (Ball b2: balls) {
                    if(!usedBalls.contains(b2) && (!difficultBalls || b2.getPlacement() == Ball.Placement.FREE)){
                        Route r1 = new Route(b.getPosVector());
                        r1.setEnd(b2);
                        WaypointGenerator.WaypointRoute wr = new WaypointGenerator(b.getPosVector(), b2.getPosVector()).waypointRoute;
                        r1.setScore(wr.getScore());
                        List<Vector2Dv1> waypoints = wr.getRoute();
                        r1.setRoute(waypoints);
                        b.addRoute(r1);
                        Route r2 = new Route(b2.getPosVector());
                        r2.setEnd(b);
                        List<Vector2Dv1> r2Waypoints = new ArrayList<>();
                        for (int i = waypoints.size()-1; i > 0; i++) {
                            r2Waypoints.add(waypoints.get(i));
                        }
                        r2Waypoints.add(b.getPosVector());
                        r2.setScore(r1.getScore());
                        b.addRoute(r2);
                    }
                }
            }
        }
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
