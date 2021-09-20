package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import robocode.ScannedRobotEvent;

import java.util.ArrayList;
import java.util.List;

public class Spark extends Robot {

    RobotStatus status;

    // Variables to track distance & name of enemy robots.


    // List to keep track of robotNames.
    List<ScannedRobotEvent> robotNames = new ArrayList<>();

    // mover som flyttar på Spark.
    Mover mover;

    //Värde på hur många fiender det är på planen
    int opponentsLeft;

    public void run() {


        mover = new Mover(this);
        mover.moveToClosestWall(25);
        turnRight(180);
        turnRight(90);
        //noinspection InfiniteLoopStatement
        while (true) {
            calculateRadar();

            opponentsLeft = getOthers();
            String closestBotName = "";
            double closestDistance = 100_000_000;
            double angleToClosestBot = 0;
            for (int i = 0; i < robotNames.size(); i++) {
                if (robotNames.get(i).getDistance() < closestDistance) {
                    closestDistance = robotNames.get(i).getDistance();
                    closestBotName = robotNames.get(i).getName();
                    angleToClosestBot = robotNames.get(i).getBearing();
                }
            }
            calculateMovement();


            calculateFire(closestDistance, angleToClosestBot);


            /*
            if (closestDistance > 350) {
                turnRight(angleToClosestBot);
                ahead(closestDistance - 100);
            } else if (closestDistance > 200) {
                turnRight(angleToClosestBot);
                ahead(closestDistance - 50);
            } else {
                turnRight(angleToClosestBot);
            }

             */
        }
    }

    public void calculateMovement() {
        ahead(150);
        turnLeft(180);
        ahead(150);
        turnRight(180);

    }

    public void calculateRadar() {
        turnRadarRight(360);

    }

    public void calculateFire(double distanceToEnemy, double angleToTarget) {
        turnGunRight(angleToTarget +(getHeading()-getGunHeading()));
        if(distanceToEnemy > 350) {
            fireBullet(1);
        } else if (distanceToEnemy > 200){
            fireBullet(2);
        } else{
            fireBullet(3);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        status = e.getStatus();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        mover.updateEnemyPosition(e);


        for (int i = 0; i < robotNames.size(); i++) {
            // IF e's name = first name inside robotNames.
            if (e.getName().equals(robotNames.get(i).getName())) {

                // Removes old name from list and adds new.
                robotNames.remove(i);
                robotNames.add(e);
                return;
            }

        }
        robotNames.add(e);


        /*
        // Turns the gun towards our opponent.
        turnGunRight(getHeading() - getGunHeading() + event.getBearing());
        // Locks in on target, unless something closer gets inside the scanner sight.
        scan();
        resume();
        */
    }
}

