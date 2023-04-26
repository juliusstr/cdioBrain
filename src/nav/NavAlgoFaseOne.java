package nav;

import misc.Ball;
import misc.Robot;
import misc.Vector2D;

public class NavAlgoFaseOne {
    public static final double ANGLE_ERROR = 0.04;
    public static final double DISTANCE_ERROR = 3;
    public static final boolean SIMULATE = false;



    private Ball nextBall;
    private Robot robot;
    //---------------------------
    //constructor
    //---------------------------
    public NavAlgoFaseOne() {
    }

    public NavAlgoFaseOne(Ball nextBall, Robot robot) {
        this.nextBall = nextBall;
        this.robot = robot;
    }

    //---------------------------
    //nav algo
    //---------------------------

    public String nextCommand(){
        String command = "";

        //double angleDelta = robot.getDirection() - Math.atan((nextBall.getyPos()-robot.getyPos())/(nextBall.getxPos()-robot.getxPos()));//old


        //*** cal dist and angle ***
        double distDelta = Math.sqrt(Math.pow((nextBall.getxPos()- robot.getxPos()), 2)+Math.pow((nextBall.getyPos()- robot.getyPos()), 2));

        Vector2D rp = new Vector2D(nextBall.getxPos() - robot.getxPos(), nextBall.getyPos() - robot.getyPos());
        double dot = rp.dot(robot.getDirection());
        double cross = rp.cross(robot.getDirection());
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
                robot.setDirection( Vector2D.toCartesian(1,robot.getDirection().getAngle()- angleDelta/2));
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
            robot.setDirection( Vector2D.toCartesian(1,robot.getDirection().getAngle() - angleDelta/2));
            robot.setxPos(robot.getxPos()+Math.cos(robot.getDirection().getAngle())*distDelta/2);
            robot.setyPos(robot.getyPos()+Math.sin(robot.getDirection().getAngle())*distDelta/2);
        }
        return command;
    }

    //---------------------------
    //getter setter
    //---------------------------
    public Robot getRobot() {
        return robot;
    }
    public void setRobot(Robot robot) {
        this.robot = robot;
    }
    public Ball getNextBall() {
        return nextBall;
    }
    public void setNextBall(Ball nextBall) {
        this.nextBall = nextBall;
    }



}
