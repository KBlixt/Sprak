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
        ahead(Tools.distanceToClosestWall(
                status.getX(),
                status.getY(),
                getBattleFieldWidth(),
                getBattleFieldHeight())
        -50);

        //noinspection InfiniteLoopStatement
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
        // Distance to opponent > 350 pixels, fire: 1
        if(event.getDistance() > 350) {
            fire(1);
        }
        // Distance < 200 fire: 3
        else if (event.getDistance() < 200) {
            fire(3);
        }
        // 200 < Distance < 350 fire: 2
        else if (event.getDistance() < 350 && event.getDistance() > 200) {
                fire(2);
            }
        }
    }

