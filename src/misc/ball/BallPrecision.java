package misc.ball;

import misc.Robotv1;
import misc.Vector2Dv1;

import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;

public class BallPrecision {

    /*Diff heights*/
    public int ballH = 398; //height of ball in mm
    public int distCamFloor = 1850;
    public int distMidToObject = 1000;
    public int RobotH = 145;
    public int CrossH = 32;


    public void compensateBalls(ArrayList<Ball> listBalls){
        for (Ball ball : listBalls) {
            System.out.println("Ball position: " + ball.getPosVector());
            CompensateBall(ball);
            System.out.println("Updated ball position: " + ball.getPosVector() + "\n");
        }
        return;
    }
    public void CompensateBall(Ball BallVec)
    {
        //define mid of camera, point of ball and vector
        Vector2Dv1 Mid = new Vector2Dv1(640/2,360/2);
        //Point BallPoint = new Point(x,y);
        //Vector2Dv1 BallVec = new Vector2Dv1(BallPoint);

        //dist between MidCam and ball object
        Vector2Dv1 dist = Mid.subtract(Mid, BallVec.getPosVector());

        //calc hypo
        double c = Math.pow(distCamFloor,2)+Math.pow(dist.getLength(),2); //find hyp/distance from object to camera
        c = Math.sqrt(c);

        //check angle for ball to cam with certain camera height
        double BigB = Math.toDegrees(Math.asin(distCamFloor/c)); //ball look up to cam angle
        double BigC = 90-BigB; //cam angle to floor/ball

        double moveMidBallmm = (ballH/2)*Math.sin(Math.toRadians(BigC)); //(hypo)*(sin(C))


        //BallVec.setPos(BallVec.getPosVector().getAdded(dist.getNormalized().getMultiplied(moveMidBallmm/10)).getPoint());
        BallVec.setPos(BallVec.getPosVector().getSubtracted(dist.getNormalized().getMultiplied(moveMidBallmm/10)).getPoint());


        return;

    }
    public void CompensateRobot(Ball RobVec){
        //define mid of camera, point of ball and vector
        Vector2Dv1 Mid = new Vector2Dv1(640/2,360/2);
        //Point RobotPoint = new Point(x,y);
        //Vector2Dv1 BallVec = new Vector2Dv1(RobotPoint);

        //dist between MidCam and ball object
        Vector2Dv1 dist = Mid.subtract(Mid, RobVec.getPosVector());

        //calc hypo
        double c = Math.pow(distCamFloor,2)+Math.pow(dist.getLength(),2); //find hyp/distance from object to camera
        c = Math.sqrt(c);

        //check angle for ball to cam with certain camera height
        double BigB = Math.toDegrees(Math.asin(distCamFloor/c)); //ball look up to cam angle
        double BigC = 90-BigB; //cam angle to floor/ball

        double moveMidBallmm = (RobotH/2)*Math.sin(Math.toRadians(BigC)); //(hypo)*(sin(C))


        //RobVec.setPos(RobVec.getPosVector().getAdded(dist.getNormalized().getMultiplied(moveMidBallmm/10)).getPoint());
        RobVec.setPos(RobVec.getPosVector().getSubtracted(dist.getNormalized().getMultiplied(moveMidBallmm/10)).getPoint());

        return;
    }



}
