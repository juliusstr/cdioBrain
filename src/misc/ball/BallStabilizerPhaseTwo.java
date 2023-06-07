package misc.ball;

import exceptions.BadDataException;
import exceptions.NoDataException;
import exceptions.TypeException;


import java.awt.*;
import java.util.ArrayList;

public class BallStabilizerPhaseTwo {


    public static final double MAX_DISTANCE_FOR_RELATION = 12;
    public static final int TIME_TO_LIVE = 30;
    public static final int AGE_TO_GONE = 1;


    private ArrayList<Ball> balls;
    private int nextId;



    public BallStabilizerPhaseTwo(){
        balls = new ArrayList<>();
        nextId = 1;
    }

    public void stabilizeBalls(ArrayList<Ball> balls) throws TypeException {
        for (Ball ball :
                balls) {
            stabilizeBall(ball);
        }
        for (Ball ball :
                this.balls) {
            ball.incrementLastSeenAlive();
            if (ball.getLastSeenAlive() <= AGE_TO_GONE){
                if(ball.getStatus() != PrimitiveBall.Status.ROBOT){
                    ball.setStatus(PrimitiveBall.Status.IN_PLAY);
                } else {
                    if (ball.getColor().equals(BallClassifierPhaseTwo.GREEN) || ball.getColor().equals(BallClassifierPhaseTwo.BLACK)){
                        ball.setStatus(PrimitiveBall.Status.ROBOT);
                    } else {
                        ball.setStatus(PrimitiveBall.Status.IN_PLAY);
                    }
                }

            } else {
                ball.setStatus(PrimitiveBall.Status.GONE);
            }
        }
    }

    private void stabilizeBall(Ball ball) throws TypeException {
        if(ball.getType() == Ball.Type.UNKNOWN){
            BallClassifierPhaseTwo.classify(ball);
        }
        if(ball.getType() == Ball.Type.UNKNOWN){
            throw new TypeException("Ball have no type.");
        }
        Ball relation = null;
        try {
            relation = relates(ball);
        } catch (NoDataException e) {
            addBallToBalls(ball);
            return;
        } catch (BadDataException e) {
            addBallToBalls(ball);
            return;
        }
        ArrayList<Point> ballPosHis = relation.getBallPosHis();
        while (ballPosHis.size() > Ball.BALL_POS_HIS_MAX_SIZE){
            ballPosHis.remove(ballPosHis.size()-1);
        }
        ballPosHis.add(0,relation.getPoint());
        relation.setPos(ball.getPoint());
        relation.zeroLastSeenAlive();
    }

    // returns the ball that the ball relates to the input ball. throws exception if it fails.
    public Ball relates(Ball ball) throws NoDataException, BadDataException {
        if (balls.isEmpty())
            throw new NoDataException("No balls to relate to");
        int bestRelationIndex = -1;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < balls.size(); i++) {
            double dist = balls.get(i).getPoint().distance(ball.getPoint());
            if(bestDist > dist && dist < MAX_DISTANCE_FOR_RELATION){
                bestRelationIndex = i;
                bestDist = dist;
            }
        }
        if (bestRelationIndex == -1){
            throw new BadDataException("No relations");
        }
        return balls.get(bestRelationIndex);
    }

    private void addBallToBalls(Ball ball){
        ball.setId(nextId++);
        ball.zeroLastSeenAlive();
        balls.add(ball);
    }

    //gets only stabel balls
    public ArrayList<Ball> getStabelBalls() throws NoDataException {
        ArrayList<Ball> ballsToReturn = new ArrayList<>();

        for (int i = 0; i < balls.size(); i++) {
            if(balls.get(i).getType() != Ball.Type.BALL)
                continue;

            if(balls.get(i).getLastSeenAlive() > TIME_TO_LIVE) {
                balls.remove(i--);
                continue;
            }
            //todo maybe run through the posHis to determine if the ball moves. For now i will just say they are stabel when they are in the list.

            ballsToReturn.add(balls.get(i));
        }

        if (ballsToReturn.isEmpty())
            throw new NoDataException("No stabel balls");

        return ballsToReturn;
    }

    //gets only robot circles
    public ArrayList<Ball> getStabelRobotCirce() throws BadDataException {
        ArrayList<Ball> ballsToReturn = new ArrayList<>();

        for (int i = 0; i < balls.size(); i++) {
            if(balls.get(i).getStatus() != PrimitiveBall.Status.ROBOT)
                continue;

            if(balls.get(i).getLastSeenAlive() > TIME_TO_LIVE) {
                balls.remove(i--);
                continue;
            }
            //todo maybe run through the posHis to determine if the ball moves. For now i will just say they are stabel when they are in the list.

            ballsToReturn.add(balls.get(i));
        }

        if (ballsToReturn.size() != 2)
            throw new BadDataException("No stable robot balls. Balls to return size: " + ballsToReturn.size() + "   Number of balls in list: " + balls.size());

        return ballsToReturn;
    }
}
