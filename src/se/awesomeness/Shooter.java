package se.awesomeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shooter {

    Sprak sprak;
    EnemyRobot currentTarget;
    boolean hardTargetLock;
    boolean softTargetLock;
    long turnsToFire;
    boolean shooting;

    public Shooter(Sprak sprak) {
        this.sprak = sprak;
        hardTargetLock = false;
        softTargetLock = false;
        shooting = false;
        turnsToFire = 10;
    }

    public void prepareShot(Vector moveVector){
        turnsToFire = Math.round(Math.ceil(sprak.getGunHeat()/0.1));

        String targetName = targetSelection();
        aim(targetName, moveVector);


    }

    private String targetSelection(){
        String targetRobotName = "";
        double shortestDistance = -1;

        for (Map.Entry<String, EnemyRobot> entry : sprak.enemyRobots.entrySet()) {
            Point position = entry.getValue().estimatedPosition(turnsToFire);
            double distance = sprak.position.distanceToPoint(position);
            if ( distance < shortestDistance || shortestDistance == -1){
                shortestDistance = distance;
                targetRobotName = entry.getKey();
            }
        }
        System.out.println("target: " + targetRobotName);
        return targetRobotName;
    }

    private void aim(String targetName, Vector moveVector){
        Point sprakPosition = sprak.position.addVector(moveVector.multiply(1));
        System.out.println("moveVector: " + moveVector);
        System.out.println("sparkAjustedPos: " + sprakPosition);
        if (targetName.equals("")){
            return;
        }
        EnemyRobot enemyRobot = sprak.enemyRobots.get(targetName);
        System.out.println(enemyRobot);
        System.out.println(enemyRobot.estimatedPosition(0));

        Point targetPoint = enemyRobot.getPosition();
        double distance = sprakPosition.distanceToPoint(targetPoint);
        System.out.println(distance);
        double addedTime = turnsToFire + distance/11;
        int iter = 6;
        while(true){
            targetPoint = enemyRobot.estimatedPosition(Math.round(addedTime));
            double newDistance = sprakPosition.distanceToPoint(targetPoint);
            double addTimeToTarget = (newDistance-distance)/19.7;
            addedTime += addTimeToTarget;
            System.out.println("addTimeToTarget: " + addTimeToTarget );
            distance = newDistance;
            iter--;
            if (iter<0){
                break;
            }
            System.out.println("iterTarget: " + targetPoint );
        }
        System.out.println("finalTargetPos " + targetPoint);
        Vector vectorToTarget = sprakPosition.vectorTo(targetPoint);
        double angleToTarget = sprak.gunHeading.angleToVector(vectorToTarget);
        sprak.setGunRotationRate(-angleToTarget);
    }

    public void fire(){
        sprak.fire(0.1);
    }

    public boolean canFire(){
        return turnsToFire <= 1;
    }
}
