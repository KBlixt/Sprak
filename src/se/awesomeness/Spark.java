package se.awesomeness;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Spark extends Robot {


    public void run() {


        while (true) {
            calulateRadar();

        }
    }

    public void calulateRadar() {

        // Turns the radar 180 degrees to the right.
        turnRadarRight(180);

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {


        // Turns the gun towards our opponent.
        turnGunRight(getHeading() - getGunHeading() + event.getBearing());
        fire(3);


    }
}


