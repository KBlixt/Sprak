package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import robocode.ScannedRobotEvent;

public class Spark extends Robot {

    RobotStatus status;

    // Variables to track distance & name of enemy robots.
    double distanceToClosestBot = 100_000_000;
    String closetBotName = "";

    // mover som flyttar på Spark.
    Mover mover;
    public void run() {
        mover = new Mover(this);

        mover.moveToClosestWall(50);
        turnRight(180);

        //noinspection InfiniteLoopStatement
        while (true) {
            distanceToClosestBot=100_000_000;
            calculateRadar();
            System.out.println("Robot: [" + closetBotName + "]" + " Distance: [" + distanceToClosestBot + "]");
            System.out.println();

            if (distanceToClosestBot > 350){
                mover.moveToClosestRobot(100);
            } else if (distanceToClosestBot > 200){
                mover.moveToClosestRobot(50);
            } else {
                mover.doNotMove();
            }

        }
    }

    public void calculateRadar() {
        turnRadarRight(360);

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
    public void onScannedRobot(ScannedRobotEvent e) {
            mover.updateEnemyPosition(e);

        // Gets the closest bots name and distance.
        if (e.getDistance() < distanceToClosestBot) {
            distanceToClosestBot = e.getDistance();
            closetBotName = e.getName();
        }


        /*
        // Turns the gun towards our opponent.
        turnGunRight(getHeading() - getGunHeading() + event.getBearing());
        calculateFire(event.getDistance());
        // Locks in on target, unless something closer gets inside the scanner sight.
        scan();
        resume();
        */
    }
}

