package nav;

import misc.*;
import misc.ball.Ball;
import misc.ball.PrimitiveBall;

import java.util.ArrayList;

public class NavAlgoFaseTwo {
    public static final double ANGLE_ERROR = 0.04;
    public static final double DISTANCE_ERROR = 50;
    public static final boolean SIMULATE = false;

    private Ball nextBall;

    private Robotv1 Robot;

    public ArrayList<SafetyCircle> critZones;
    //---------------------------
    //constructor
    //---------------------------
    public NavAlgoFaseTwo() {
    }

    public NavAlgoFaseTwo(Ball nextBall, Robotv1 Robot) {
        this.nextBall = nextBall;
        this.Robot = Robot;


    }

    //---------------------------
    //nav algo
    //---------------------------


    public String Navv(){
        String ret="beginner string";
        /*Robotv1 Robot = new Robotv1(5,1,new Vector2Dv1(1,0));*/

        PrimitiveBall Ball1 = new PrimitiveBall(5,5, PrimitiveBall.Status.IN_PLAY);
        PrimitiveBall Ball2 = new PrimitiveBall(1,1, PrimitiveBall.Status.IN_PLAY);

        SafetyCircle safeCircle = new SafetyCircle(nextBall.getPosVector(), 1);

        Vector2Dv1 dirvec = new Vector2Dv1(0 , 1);
        Vector2Dv1 posvec = new Vector2Dv1(0 , 0);
        Cross cross = new Cross(posvec, dirvec);

       critZones = new ArrayList<>();
        for (int i=0; i<cross.crossPoint.size(); i++){
            critZones.add(i,new SafetyCircle(cross.crossPoint,1));
        }

        
        /*Vector between robot and ball*/
        Vector2Dv1 VecRobotBall = new Vector2Dv1(Robot.getxPos()-Ball1.getxPos(), Robot.getyPos()-Ball1.getyPos());

       
        
        return ret;
    }
    /*
    public String nextCommand(){
        String command = "";

        //double angleDelta = robot.getDirection() - Math.atan((nextBall.getyPos()-robot.getyPos())/(nextBall.getxPos()-robot.getxPos()));//old


        //*** cal dist and angle ***
        double distDelta = Math.sqrt(Math.pow((nextBall.getxPos()- robotv1.getxPos()), 2)+Math.pow((nextBall.getyPos()- robotv1.getyPos()), 2));

        Vector2Dv1 rp = new Vector2Dv1(nextBall.getxPos() - robotv1.getxPos(), nextBall.getyPos() - robotv1.getyPos());
        double dot = rp.dot(robotv1.getDirection());
        double cross = rp.cross(robotv1.getDirection());
        double angleDelta;

        //*** Close enough ***
        if(distDelta < DISTANCE_ERROR){
            return "stop -t -d";
        }

        //***turn***
        angleDelta = Math.atan2(cross, dot);

        //System.out.println("delta angle: " + angleDelta);
        //angleDelta = Math.acos(dot/dist);
        if (Math.abs(angleDelta) > ANGLE_ERROR) {
            command += "turn -";
            if (angleDelta > 0) {
                command += "l";
            } else {
                command += "r";
            }
            double turnSpeed = Math.abs(angleDelta / 2);
            if (turnSpeed > 0.2)
                turnSpeed = 0.2;
            command += " -s" + String.format("%.2f", turnSpeed).replace(',','.') + "";
        } else {
            command += "stop -t";
        }

        //***drive***
        if(Math.abs(angleDelta) > ANGLE_ERROR*2){
            if(SIMULATE){
                robotv1.setDirection( Vector2Dv1.toCartesian(1, robotv1.getDirection().getAngle()- angleDelta/2));
            }
            return command + ";stop -d";
        }
        if(distDelta > DISTANCE_ERROR){
            double speed = distDelta/2;
            if (speed > 5)
                speed = 5;
            command += ";drive -s" + String.format("%.2f", speed).replace(',','.');
        } else {
            command += ";stop -d";
        }
        if(SIMULATE){
            robotv1.setDirection( Vector2Dv1.toCartesian(1, robotv1.getDirection().getAngle() - angleDelta/2));
            robotv1.setxPos(robotv1.getxPos()+Math.cos(robotv1.getDirection().getAngle())*distDelta/2);
            robotv1.setyPos(robotv1.getyPos()+Math.sin(robotv1.getDirection().getAngle())*distDelta/2);
        }
        return command;
    }

     */

    //---------------------------
    //getter setter
    //---------------------------

    /*
    public Robotv1 getRobot() {
        return robotv1;
    }
    public void setRobot(Robotv1 robotv1) {
        this.robotv1 = robotv1;
    }
    public Ball getNextBall() {
        return nextBall;
    }
    public void setNextBall(Ball nextBall) {
        this.nextBall = nextBall;
    }

     */



}
