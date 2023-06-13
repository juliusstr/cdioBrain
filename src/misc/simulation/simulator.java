package misc.simulation;

import Client.StandardSettings;
import misc.Robotv1;
import misc.Vector2Dv1;
import nav.CommandGenerator;

import static Test.Nav.NavAlgoPhaseTwoTest.DISTANCE_ERROR;

public class simulator {
    commandExtractor commandExtractor = new commandExtractor();
    double currentSpeed;
    double currentTurnSpeed;
    String currentTurnDirection;
    double turnSpeedMultiplier = 2;

    /**
     * Updates the position of the simulated robot. Works with a waypoint Ball. If the ball has been reached returns false.
     * @param waypoint The waypoint ball
     * @param simulationRobot The robot object to simulate a command on
     * @param nextCommand The command that was to be send to the robot
     * @return  true if a command has been run.
     *          false if the robot is on the ball.
     */
    public Boolean updatePosSimple(Vector2Dv1 waypoint, Robotv1 simulationRobot, String nextCommand, Vector2Dv1 target) {
        double distanceToBall = Math.sqrt(Math.pow((waypoint.x - simulationRobot.getxPos()), 2) + Math.pow((waypoint.y - simulationRobot.getyPos()), 2));
        currentSpeed = this.commandExtractor.extractSpeed(nextCommand);
        currentTurnSpeed = this.commandExtractor.extractTurnSpeed(nextCommand);
        currentTurnDirection = this.commandExtractor.extractTurnDirection(nextCommand);
        double deltaAngle;

        if(currentTurnDirection == "l"){
            deltaAngle = currentTurnSpeed * turnSpeedMultiplier;
        } else if (currentTurnDirection == "r") {
            deltaAngle = -currentTurnSpeed * turnSpeedMultiplier;
        } else {
            deltaAngle = 0;
        }
        simulationRobot.setDirection(Vector2Dv1.toCartesian(1, simulationRobot.getDirection().getAngle() - deltaAngle/2));
        simulationRobot.setxPos(simulationRobot.getxPos() + (Math.cos(simulationRobot.getDirection().getAngle())*currentSpeed));
        simulationRobot.setyPos(simulationRobot.getyPos() + (Math.sin(simulationRobot.getDirection().getAngle())*currentSpeed));
        System.out.printf("Robot moved to: x=%d, y=%d \n", ((int) simulationRobot.getxPos()), (int)simulationRobot.getyPos());

        distanceToBall = Math.sqrt(Math.pow((target.x - simulationRobot.getxPos()), 2) + Math.pow((target.y - simulationRobot.getyPos()), 2));

        if (distanceToBall < StandardSettings.TARGET_DISTANCE_ERROR){
            return false;
        } else {
            return true;
        }
    }
    public Boolean updateWaypointPos(Vector2Dv1 waypoint, Robotv1 simulationRobot, String nextCommand) {
        double distanceToBall = Math.sqrt(Math.pow((waypoint.x - simulationRobot.getxPos()), 2) + Math.pow((waypoint.y - simulationRobot.getyPos()), 2));
        if(distanceToBall >= DISTANCE_ERROR) {
            currentSpeed = this.commandExtractor.extractSpeed(nextCommand);
            currentTurnSpeed = this.commandExtractor.extractTurnSpeed(nextCommand);
            currentTurnDirection = this.commandExtractor.extractTurnDirection(nextCommand);
            double deltaAngle;

            if(currentTurnDirection == "l"){
                deltaAngle = currentTurnSpeed * turnSpeedMultiplier;
            } else if (currentTurnDirection == "r") {
                deltaAngle = -currentTurnSpeed * turnSpeedMultiplier;
            } else {
                deltaAngle = 0;
            }
            simulationRobot.setDirection(Vector2Dv1.toCartesian(1, simulationRobot.getDirection().getAngle() - deltaAngle/2));
            simulationRobot.setxPos(simulationRobot.getxPos() + (Math.cos(simulationRobot.getDirection().getAngle())*currentSpeed));
            simulationRobot.setyPos(simulationRobot.getyPos() + (Math.sin(simulationRobot.getDirection().getAngle())*currentSpeed));
            System.out.printf("Robot moved to: x=%d, y=%d \n", ((int) simulationRobot.getxPos()), (int)simulationRobot.getyPos());

            return true;
        } else {
            return false;
        }
    }
}

