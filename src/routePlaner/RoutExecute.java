package routePlaner;

import Client.StandardSettings;
import Gui.LiveView;
import exceptions.BadDataException;
import exceptions.NoRouteException;
import exceptions.NoWaypointException;
import exceptions.TypeException;
import imageRecognition.ImgRecFaseTwo;
import misc.*;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import misc.ball.BallStabilizerPhaseTwo;
import nav.CommandGenerator;
import nav.WaypointGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static Client.StandardSettings.ANGLE_ERROR;

public class RoutExecute {
    private Boundry boundry = null;
    private Cross cross = null;
    private Robotv1 robot = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private LiveView liveView = null;
    private String lastCommand;

    public RoutExecute(PrintWriter out, BufferedReader in, Robotv1 robot, Cross cross, Boundry boundry){
        this.in = in;
        this.out = out;
        this.robot = robot;
        this.cross = cross;
        this.boundry = boundry;
        lastCommand = "stop -t -d";
    }

    public void setLiveView(LiveView lv){
        liveView = lv;
    }

    public void heatRunner(ArrayList<Ball> heat, int heatNr, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer, ArrayList<Ball> ballsToAvoid) {
        WaypointGenerator waypointGenerator;
        Ball lastBall = null;
        CommandGenerator commandGenerator;
        ArrayList<Vector2Dv1> routeToGoal;
        // checksize to have 11 balls ?
        int checkSize;
        if(heatNr == 3){
            checkSize = 3;
        } else {
            checkSize = 4;
        }
        if(heatNr == 4){
            checkSize = heat.size();
        }

        for (int j = 0; j < heat.size(); j++) {
            //finde route from robot to ball
            ArrayList<Vector2Dv1> routToBall = new ArrayList<>();
            try {
                BallClassifierPhaseTwo.ballSetPlacement(ballsToAvoid,boundry,cross);
            } catch (NoWaypointException e) {
                throw new RuntimeException(e);
            }
            if(heat.get(j).getPlacement() == Ball.Placement.FREE){
                ballsToAvoid.remove(heat.get(j));
            }
            if (heat.size() == checkSize) {
                for (int i = 0; i < robot.getRoutes(heatNr).size(); i++) {
                    if (heat.get(0) == robot.getRoutes(heatNr).get(i).getEnd()) {
                        //routToBall = robot.getRoutes(1).get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                                targetWaypoint = heat.get(j).getPosVector();
                            } else {
                                targetWaypoint = heat.get(j).getPickUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint, robot.getPosVector(), cross, boundry, ballsToAvoid);
                            if(heat.get(j).getPlacement() != Ball.Placement.FREE){
                                ballsToAvoid.remove(heat.get(j));
                            }

                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        /*
                        if (heat.get(j).getPlacement() != Ball.Placement.FREE) {
                            routToBall.add(heat.get(j).getLineUpPoint());
                        }
                         */
                        break;
                    }
                }
            } else {
                for (int i = 0; i < lastBall.getRoutes().size(); i++) {
                    if (lastBall.getRoutes().get(i).getEnd() == heat.get(j)) {
                        //routToBall = lastBall.getRoutes().get(i).getWaypoints();
                        try {
                            Vector2Dv1 targetWaypoint;
                            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                                targetWaypoint = heat.get(j).getPosVector();
                            } else {
                                targetWaypoint = heat.get(j).getPickUpPoint();
                            }
                            waypointGenerator = new WaypointGenerator(targetWaypoint, robot.getPosVector(), cross, boundry, ballsToAvoid);
                        } catch (NoRouteException e) {
                            throw new RuntimeException(e);
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        routToBall = waypointGenerator.waypointRoute.getRoute();
                        /*
                        if (heat.get(j).getPlacement() != Ball.Placement.FREE) {
                            routToBall.add(heat.get(j).getLineUpPoint());
                        }
                         */
                        break;
                    }
                }

            }
            //run to ball
            commandGenerator = new CommandGenerator(robot, routToBall);
            if(liveView != null)
                liveView.setRout(routToBall);
            boolean isBallNotWaypoint;
            if (heat.get(j).getPlacement() == Ball.Placement.FREE) {
                isBallNotWaypoint = true;
            } else {
                isBallNotWaypoint = false;
            }
            while (routToBall.size() != 0) {
                updateRobotFromImgRec(imgRec, robot, stabilizer);
                String command = commandGenerator.nextCommand(isBallNotWaypoint);
                if (command.contains("ball") || command.contains("waypoint")) {
                    out.println("stop -d -t");
                    wait(200);
                    routToBall.clear();
                } else {
                    if(!lastCommand.equals(command))
                        out.println(command);
                    lastCommand = command;
                }
            }
            //collect
            switch (heat.get(j).getPlacement()) {
                case FREE:
                    //check if we have the right angle to the target
                    turnBeforeHardcode(robot, imgRec, out,in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_COMMAND);
                    checkForHardcodeDone(in, StandardSettings.COLLECT_COMMAND);
                    //reverseIfCloseToBoundary(boundry.bound, cross.crossLines, robot, imgRec, stabilizer, out, in);
                    break;
                case EDGE:
                    turnBeforeHardcode(robot, imgRec, out, in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_EDGE_COMMAND);
                    checkForHardcodeDone(in, StandardSettings.COLLECT_EDGE_COMMAND);
                    //reverseIfCloseToBoundary(boundry.bound, cross.crossLines, robot, imgRec, stabilizer, out, in);
                    break;
                case CORNER:
                    turnBeforeHardcode(robot, imgRec, out, in, heat.get(j).getPosVector(), stabilizer);
                    out.println(StandardSettings.COLLECT_CORNER_COMMAND);
                    checkForHardcodeDone(in, StandardSettings.COLLECT_CORNER_COMMAND);
                    //reverseIfCloseToBoundary(boundry.bound, cross.crossLines, robot, imgRec, stabilizer, out, in);
                    break;
                case PAIR:
                    turnBeforeHardcode(robot,imgRec,out,in,heat.get(j).getPosVector(),stabilizer);
                    out.println(StandardSettings.COLLECT_PAIR_COMMAND);
                    checkForHardcodeDone(in, StandardSettings.COLLECT_PAIR_COMMAND);
                default:
                    out.println("stop -t -d");

                    break;
            }
            wait(500);
            lastBall = heat.get(j);

        }
        //go to goal and do a drop-off
        updateRobotFromImgRec(imgRec,robot,stabilizer);
        try {
            waypointGenerator = new WaypointGenerator(boundry.goalWaypoint1, robot.getPosVector(), cross, boundry, ballsToAvoid);
        } catch (NoRouteException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        routeToGoal = waypointGenerator.waypointRoute.getRoute();//lastBall.getGoalRoute().getWaypoints();
        //routeToGoal.add(boundry.goalWaypoint1);
        commandGenerator = new CommandGenerator(robot, routeToGoal);
        if(liveView != null)
            liveView.setRout(routeToGoal);
        while (routeToGoal.size() != 0) {
            updateRobotFromImgRec(imgRec, robot, stabilizer);
            String command = commandGenerator.nextCommand(false);
            if (command.contains("waypoint")) {
                routeToGoal.clear();
            }
            if(!lastCommand.equals(command))
                out.println(command);
            lastCommand = command;
        }
        turnBeforeHardcode(robot, imgRec, out, in, boundry.getGoalPos(), stabilizer);
        out.println(StandardSettings.DROP_OFF_COMMAND);
        checkForHardcodeDone(in, StandardSettings.DROP_OFF_COMMAND);
        //reverseIfCloseToBoundary(boundry.bound, cross.crossLines, robot, imgRec, stabilizer, out, in);
    }

    /**
     * Updates the robots position from image rec
     * @param imgRec The imgRec used
     * @param robot The robot to update
     * @param stabilizer The stabilizer to use
     */
    public void updateRobotFromImgRec(ImgRecFaseTwo imgRec, Robotv1 robot, BallStabilizerPhaseTwo stabilizer){
        ArrayList<Ball> balls = imgRec.captureBalls();
        try {
            stabilizer.stabilizeBalls(balls);
        } catch (TypeException e) {
            throw new RuntimeException(e);
        }
        try {
            ArrayList<Ball> robotBalls = stabilizer.getStabelRobotCirce();
            robot.updatePos(robotBalls.get(0), robotBalls.get(1));
        } catch (BadDataException e) {
            //throw new RuntimeException(e);
        }
        if(liveView != null)
            liveView.setMat(imgRec.getFrame());
    }

    /**
     * To turn before starting a hardcoded command
     * @param robot         The robot to turn
     * @param imgRec        The imgRec to update robot pos
     * @param out           The Printwriter to send command to robot
     * @param target        The target to have minimal angle to
     * @param stabilizer    The stabilizer for the balls
     */
    public void turnBeforeHardcode(Robotv1 robot, ImgRecFaseTwo imgRec, PrintWriter out, BufferedReader in, Vector2Dv1 target, BallStabilizerPhaseTwo stabilizer){
        out.println("stop -d -t");
        wait(100);
        //check if we have the right angle to the target
        while(!correctAngleToTarget(robot, target, out)){
            updateRobotFromImgRec(imgRec, robot, stabilizer);
        }
        out.println("stop -t -d");
        lastCommand = "stop -d -t";
        wait(500);
    }
    /**
     * Reverse if too close to a line after pickup
     * @param lines The ArrayList of lines to check for
     * @param robot The robot to check for
     * @param out the Printwriter to write to the robot
     */
    public void reverseIfCloseToBoundary(ArrayList<Line> lines,ArrayList<Line> lines2, Robotv1 robot, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer, PrintWriter out, BufferedReader in) {
        String message;
        try{
            message = in.readLine();
            while(!message.equals("hardcode done")){
                message = in.readLine();
                System.out.println(message);
            }
        } catch (IOException e){
            throw new RuntimeException();
        }
        for (Line line: lines) {
            if(reverseWhileCloseToLine(line, robot, imgRec, stabilizer, out)){
                return;
            }
        }
        for (Line line: lines2) {
            if(reverseWhileCloseToLine(line, robot, imgRec, stabilizer, out)){
                return;
            }
        }
    }

    /**
     * reverses until the robot is long enough away from a line to turn
     * @param line the line to back away from
     * @param robot
     * @param imgRec
     * @param stabilizer
     * @param out
     * @return
     */
    public boolean reverseWhileCloseToLine(Line line, Robotv1 robot, ImgRecFaseTwo imgRec, BallStabilizerPhaseTwo stabilizer, PrintWriter out){
        if(line.findClosestPoint(robot.getPosVector()).getSubtracted(robot.getPosVector()).getLength() < StandardSettings.ROUTE_PLANER_DISTANCE_FROM_LINE_BEFORE_TURN){
            out.println("reverse -s5");
            while(line.findClosestPoint(robot.getPosVector()).getSubtracted(robot.getPosVector()).getLength() < StandardSettings.ROUTE_PLANER_DISTANCE_FROM_LINE_BEFORE_TURN){
                updateRobotFromImgRec(imgRec, robot, stabilizer);
            }
            out.println("stop -d -t");
            return true;
        }
        return false;
    }

    /**
     * Pauses the execution for the specified number of milliseconds.
     *
     * @param millis The number of milliseconds to wait.
     */
    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if we have the correct angle to our target, within the constant ANGLE_ERROR
     * @param robot The robot
     * @param target The target
     * @param out The Printwriter to write to robot
     * @return True if we have the correct angle, false if we dont have the correct angle
     */
    public boolean correctAngleToTarget(Robotv1 robot, Vector2Dv1 target, PrintWriter out) {
        double angleToTarget = angleBeforeHardcode(robot, target);
        String command = "";
        if (Math.abs(angleToTarget) > ANGLE_ERROR) {
            command += "turn -";
            if (angleToTarget < 0) {
                command += "l";
            } else {
                command += "r";
            }
            double turnSpeed = Math.abs(angleToTarget / 3);
            if (turnSpeed > 0.5) {
                turnSpeed = 0.5;
            } else if (turnSpeed < StandardSettings.MIN_TURN_SPEED) {
                turnSpeed = StandardSettings.MIN_TURN_SPEED;
            }

            command += " -s" + String.format("%.2f", turnSpeed).replace(',', '.') + "";
            System.out.println("Send command: " + command);
            if(!lastCommand.equals(command))
                out.println(command);
            lastCommand = command;
            return false;
        } else{
            return true;
        }
    }

    /**
     * Checks the angle between a robot and the target it is supposed to go to.
     * @param robot The robot
     * @param target The target
     * @return How wrong the angle of the robot is to target
     */
    public double angleBeforeHardcode(Robotv1 robot, Vector2Dv1 target) {
        return robot.getDirection().getAngleBetwen(target.getSubtracted(robot.getPosVector()));
    }

    public void checkForHardcodeDone(BufferedReader in, String commandSend){
        try {
            String input = in.readLine();
            while(!input.contains(commandSend)){
                input = in.readLine();
            }
        }
        catch(IOException e){
            throw new RuntimeException();
            }
    }

}
