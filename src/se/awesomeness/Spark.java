package se.awesomeness;

import robocode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spark extends Robot {

    RobotStatus status;

    // List to keep track of robotNames.
    List<ScannedRobotEvent> robotNames = new ArrayList<>();
    Map<String, Double> botDistance = new HashMap<>();
    Map<String, Double> botBearing = new HashMap<>();

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


            calculateFire();
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

            calculateFire();
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

    public void calculateFire() {

        double[] target = pickTarget();
        double distanceToTarget = target[0];
        double angleToTarget = target[1];

        double adjustAngle = angleToTarget + (getHeading() - getGunHeading());
        adjustAngle = reduceAngle(adjustAngle);
        turnGunRight(adjustAngle);

        double bulletPower = pickBulletPower(distanceToTarget);
        if (distanceToTarget > 0){
            fire(bulletPower);
        }
    }

    public double reduceAngle(double angle){
        // Följande kod reducerar vinkeln till den minsta ekvivalenta vinkeln.
        angle %= 360;
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    public double[] pickTarget(){
        double distanceToClosestBotWeighted = -1;
        double angleToClosestBotWeighted = 0;
        double heading = getHeading();
        double prioritizedAngle = 25;

        for (Map.Entry<Double, Double> entry : botDistanceAndBearing.entrySet()) {
            double distance = entry.getKey();
            double bearing = entry.getValue();
            double absoluteAngle = reduceAngle(heading + bearing);
            boolean insideZoneRight = 90+prioritizedAngle > absoluteAngle && absoluteAngle > 90-prioritizedAngle;
            boolean insideZoneLeft =  270+prioritizedAngle > absoluteAngle && absoluteAngle > 270-prioritizedAngle;
            if (insideZoneLeft || insideZoneRight){
                distance *= 0.25;
            }
            if (distance < distanceToClosestBotWeighted || distanceToClosestBotWeighted == -1){
                angleToClosestBotWeighted = bearing;
                distanceToClosestBotWeighted = distance;
            }
        }
        return new double[]{distanceToClosestBotWeighted,angleToClosestBotWeighted};
    }

    public double pickBulletPower(double distanceToEnemy){
        double bulletPower;
        if (distanceToEnemy < 200) {
            bulletPower = 3;
        } else if (distanceToEnemy < 350 || opponentsLeft > 5) {
            bulletPower = 2;
        } else {
            bulletPower = 1;
        }
        return bulletPower;
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
        //Removes robot from list to add new value
        //Get robots Name(Key) and Distance(Value in a double),
        botDistance.remove(e.getName());
        botDistance.put(e.getName(), e.getDistance());

        //Removes robot from list to add new value
        //Get robots Name(Key) and Bearing(Value in a double),
        botBearing.remove(e.getName());
        botBearing.put(e.getName(), e.getBearing());


        robotNames.add(e);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);
        if (botDistance.containsKey(event.getName()));
        botDistance.remove(event.getName()); //On enemy robot death, removes them from Map list
    }
}