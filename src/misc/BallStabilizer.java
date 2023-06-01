package misc;

import exceptions.NoGoodCircleDataException;
import misc.ball.Ball;

import java.util.ArrayList;
import java.util.List;

public class BallStabilizer {

    private static final int BUFFERsIZE = 10;

    private ArrayList<Ball> blackCircle;
    private ArrayList<Ball> redCircle;

    private ArrayList<ArrayList<Ball>> balls2d;

    public BallStabilizer(){
        blackCircle = new ArrayList<>();
        redCircle = new ArrayList<>();
        balls2d = new ArrayList<>();
    }

    public void addBalls(List<Ball> balls){

        while(blackCircle.size() >= BUFFERsIZE){
            blackCircle.remove(0);
        }

        while(redCircle.size() >= BUFFERsIZE){
            redCircle.remove(0);
        }

        while(balls2d.size() >= BUFFERsIZE){
            balls2d.remove(0);
        }

        BallClassifier classifier = new BallClassifier(balls);

        if(classifier.getRobotCircle().size() != 2){
            blackCircle.add(null);
            redCircle.add(null);
        } else if(classifier.getRobotCircle().get(0).getColor().equals(classifier.getRobotCircle().get(1).getColor())){
            blackCircle.add(null);
            redCircle.add(null);
        } else if(classifier.getRobotCircle().get(0).getColor().equals(BallClassifier.RED)){
            redCircle.add(classifier.getRobotCircle().get(0));
            blackCircle.add(classifier.getRobotCircle().get(1));
        } else {
            redCircle.add(classifier.getRobotCircle().get(1));
            blackCircle.add(classifier.getRobotCircle().get(0));
        }

        if(classifier.getBalls().size() == 0){
            this.balls2d.add(null);
        } else {
            this.balls2d.add(classifier.getBalls());
        }
    }

    public ArrayList<Ball> getStableRobotCircels() throws NoGoodCircleDataException {
        ArrayList<Ball> circle = new ArrayList<>();
        //adds the newest black ball to the list if there is more than 2 black circles
        int count = 0;
        int i = -1;
        for (int j = 0; j < blackCircle.size(); j++) {
            if(blackCircle.get(j) != null){
                count++;
                i = j;
            }
        }
        if (count >= 2) {
            circle.add(blackCircle.get(i));
        } else {
            throw new NoGoodCircleDataException("No good black circles found");
        }

        count = 0;
        i = -1;
        for (int j = 0; j < redCircle.size(); j++) {
            if(redCircle.get(j) != null){
                count++;
                i = j;
            }
        }
        if(count >= 2) {
            circle.add(redCircle.get(i));
        } else {
            throw new NoGoodCircleDataException("No good red circles found");
        }
        return  circle;
    }


    public ArrayList<Ball> getStableBalls() throws NoGoodCircleDataException {
        ArrayList<Ball> balls = new ArrayList<>();
        if (balls2d.size() < 2){
            throw new NoGoodCircleDataException("Not enough data");
        }
        for (int i = 0; i < balls2d.size(); i++) {
            ArrayList<Ball> ball1d = balls2d.get(i);
            if (ball1d == null){
                continue;
            }
            for (Ball ball : ball1d) {
                if (ball == null){
                    continue;
                }
                if (balls.size() == 0){
                    balls.add(ball);
                    continue;
                }
                boolean updated = false;
                for (int j = 0; j < balls.size(); j++) {
                    if(!(balls.get(j).getPoint().distance(ball.getPoint()) > 5)){
                        balls.set(j, ball);
                        updated = true;
                        break;
                    }
                }
                if (!updated){
                    balls.add(ball);
                }
            }
        }

        return balls;
    }
}
