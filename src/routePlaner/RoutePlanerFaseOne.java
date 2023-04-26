//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package routePlaner;

import java.util.ArrayList;
import java.util.List;
import misc.ball.Ball;
import misc.Robotv1;
import misc.Vector2Dv1;
import nav.NavAlgoFaseOne;

public class RoutePlanerFaseOne {
    private NavAlgoFaseOne nav = new NavAlgoFaseOne();
    private List<Ball> balls = new ArrayList();
    private Robotv1 robotv1 = new Robotv1(0, 0, new Vector2Dv1(1,1));

    public RoutePlanerFaseOne() {
    }

    public void addBallToList(Ball ball) {
        this.balls.add(ball);
    }

    public void init() {
        this.nav.setNextBall((Ball)this.balls.get(0));
    }

    public String nextCommand() {
        String command = "";
        if (this.balls.isEmpty()) {
            return "drop; exit" ;
        } else {
            Ball ball = this.getClosestBall();
            if (!this.nav.getNextBall().equals(ball)) {
                this.nav.setNextBall(ball);
            }

            command = this.nav.nextCommand();
            if (command.equals("stop -t;stop -d") || command.equals("stop -t -d")) {
                command = "collect -g18 -s4";
                this.balls.remove(ball);

                //command = this.nextCommand();
            }
            return command;
        }
    }

    public void setRobot(Robotv1 robotv1) {
        this.robotv1 = robotv1;
        this.nav.setRobot(robotv1);
    }

    private Ball getClosestBall() {
        double bestDist = Double.MAX_VALUE;
        Ball closestBall = null;
        for (Ball ball : balls) {
            double dist = Math.sqrt(Math.pow((ball.getxPos() - this.robotv1.getxPos()), 2.0) + Math.pow((ball.getyPos() - this.robotv1.getyPos()), 2.0));
            if (bestDist > dist) {
                closestBall = ball;
                bestDist = dist;
            }
        }

        return closestBall;
    }

    public Robotv1 getRobot(){
        return robotv1;
    }

    public Ball getNextBall(){
        return getClosestBall();
    }

    public boolean isDone(){
        return balls.isEmpty();
    }

    public void clearBalls(){
        balls.clear();
    }
}
