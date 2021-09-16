package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import robocode.ScannedRobotEvent;

public class Spark extends Robot {

    RobotStatus status;

    public void run() {
        turnRight(
                MovementTools.angleToWall(
                        status.getX(),
                        status.getY(),
                        status.getHeading(),
                        getBattleFieldWidth(),
                        getBattleFieldHeight()));
        ahead(-50 +
                MovementTools.distanceToClosestWall(
                        status.getX(),
                        status.getY(),
                        getBattleFieldWidth(),
                        getBattleFieldHeight())
        );
        turnRight(180);

        //noinspection InfiniteLoopStatement
        while (true) {
            calulateRadar();
        }
    }


    public void calulateRadar() {

        // Turns the radar 180 degrees to the right.
        turnRadarRight(180);

    }

    public void calculateFire(double distanceToEnemy) {
        // Vår robot ska skjuta på den opponent som är närmst.
        // Vår robot ska skjuta olika stora kulor beroende på avståndet till opponent.
        // Definiera en metod som kan anropas och löser problemet.
        // Distance to opponent > 350 pixels, fire: 1

        fire(Math.ceil(300 / distanceToEnemy));
    }


    @Override
    public void onStatus(StatusEvent e) {
        status = e.getStatus();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {


        // Turns the gun towards our opponent.
        turnGunRight(getHeading() - getGunHeading() + event.getBearing());
        calculateFire(event.getDistance());
        // Locks in on target, unless something closer gets inside the scanner sight.
        scan();
        resume();
    }
}

