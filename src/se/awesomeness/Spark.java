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



    }


