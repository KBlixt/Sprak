package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import robocode.ScannedRobotEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spark extends Robot {

    RobotStatus status;

    // List to keep track of robotNames.
    List<ScannedRobotEvent> robotNames = new ArrayList<>();
    Map<Double, Double> botDistanceAndBearing = new HashMap<>();

    // mover som flyttar på Spark.
    Mover mover;

    //Värde på hur många fiender det är på planen
    int opponentsLeft;

    public void run() {
        mover = new Mover(this);
        mover.moveToClosestWall(25);
        mover.moveToMidPointOfQuadrant();
        //  turnLeft(90);

        //noinspection InfiniteLoopStatement
        while (true) {
            calculateRadar();

            opponentsLeft = getOthers();
            double closestDistance = 100_000_000;
            double angleToClosestBot = 0;
            for (int i = 0; i < robotNames.size(); i++) {
                if (robotNames.get(i).getDistance() < closestDistance) {
                    closestDistance = robotNames.get(i).getDistance();
                    angleToClosestBot = robotNames.get(i).getBearing();

                }
            }


            calculateFire(closestDistance, angleToClosestBot);
            ahead(200);

            calculateRadar();

            opponentsLeft = getOthers();
            closestDistance = 100_000_000;
            angleToClosestBot = 0;
            for (int i = 0; i < robotNames.size(); i++) {
                if (robotNames.get(i).getDistance() < closestDistance) {
                    closestDistance = robotNames.get(i).getDistance();
                    angleToClosestBot = robotNames.get(i).getBearing();

                }
            }

            calculateFire(closestDistance, angleToClosestBot);
            back(200);



        }
    }


    public void calculateMovement() {
        ahead(100);
        back(100);
    }

    public void calculateRadar() {
        turnRadarRight(360);
    }

    public void calculateFire(double distanceToEnemy, double angleToTarget) {

        double adjustAngle = angleToTarget + (getHeading() - getGunHeading());

        // Följande kod reducerar vinkeln till den minsta ekvivalent vinkeln.
        adjustAngle %= 360;
        if (adjustAngle > 180) {
            adjustAngle -= 360;
        } else if (adjustAngle < -180) {
            adjustAngle += 360;
        }

        //vrider oss och skjuter
        turnGunRight(adjustAngle);
        if (distanceToEnemy < 200) {
            fireBullet(3);
        } else if (distanceToEnemy < 350 || opponentsLeft > 5) {
            fireBullet(2);
        } else {
            fireBullet(1);
        }
    }

    public void overrideFire(double angleToTarget) {
        turnGunRight(angleToTarget + (getHeading() - getGunHeading()));
        fireBullet(3);
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

            //Gets all bots Distance and bearing
            //botDistanceAndBearing.put(e.getDistance(), e.getBearing());
            //Double distAndBear = botDistanceAndBearing.get(e.getDistance() + e.getBearing());

        robotNames.add(e);
    }
}