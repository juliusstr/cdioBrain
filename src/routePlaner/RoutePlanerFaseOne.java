//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package routePlaner;

import misc.Ball;
import misc.Robot;
import misc.Vector2D;
import nav.NavAlgoFaseOne;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanerFaseOne {
    private NavAlgoFaseOne nav = new NavAlgoFaseOne();
    private List<Ball> balls = new ArrayList();
    private Robot robot = new Robot(0, 0, new Vector2D(1,1));

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
            return "exit";
        } else {
            Ball ball = this.getClosestBall();
            if (!this.nav.getNextBall().equals(ball)) {
                this.nav.setNextBall(ball);
            }

            command = this.nav.nextCommand();
            if (command.equals("stop -t;stop -d") || command.equals("stop -t -d")) {
                this.balls.remove(ball);
                command = "collect -g4 -s3";
                //command = this.nextCommand();
            }
            return command;
        }
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
        this.nav.setRobot(robot);
    }

    private Ball getClosestBall() {
        double bestDist = Double.MAX_VALUE;
        Ball closestBall = null;
        for (Ball ball : balls) {
            double dist = Math.sqrt(Math.pow((ball.getxPos() - this.robot.getxPos()), 2.0) + Math.pow((ball.getyPos() - this.robot.getyPos()), 2.0));
            if (bestDist > dist) {
                closestBall = ball;
                bestDist = dist;
            }
        }

        return closestBall;
    }

    public Robot getRobot(){
        return robot;
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
