package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import robocode.ScannedRobotEvent;

public class Spark extends Robot {

    RobotStatus status;

    public void run(){
        turnRight(Tools.angleToWall(
                status.getX(),
                status.getY(),
                status.getHeading(),
                getBattleFieldWidth(),
                getBattleFieldHeight()));

        while (true) {
            calulateRadar();

        }
    }

    public void calulateRadar() {

        // Turns the radar 180 degrees to the right.
        turnRadarRight(180);

    }

    @Override
    public void onStatus(StatusEvent e) {
        status = e.getStatus();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {


        // Turns the gun towards our opponent.
        turnGunRight(getHeading() - getGunHeading() + event.getBearing());
        fire(3);


    }
}

