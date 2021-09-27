package se.awesomeness;

import java.util.Map;

public class Shooter {

    Sprak sprak;
    long turnsToFire;
    EnemyRobot targetRobot;

    public Shooter(Sprak sprak) {
        this.sprak = sprak;
    }

    public void prepareShot(Vector moveVector){
        turnsToFire = Math.round(Math.ceil(sprak.getGunHeat()/0.1));

        targetRobot = sprak.enemyRobots.get(targetSelection());
        double bulletPower = aim(moveVector);

        if(canFire() && bulletPower >= 1 && targetRobot.getInfoAge() < 2){
            sprak.setFire(bulletPower);
        }
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
        return targetRobotName;
    }

    private double aim(Vector moveVector){
        Point sprakPosition = sprak.position.addVector(moveVector);
        double timeToTargetLimit = 30;

        Point targetPoint = targetRobot.getPosition();
        double distance = sprakPosition.distanceToPoint(targetPoint);
        double addedTime = turnsToFire + distance/11;
        double bulletSpeed = 11;
        int iter = 10;
        while(iter>0){
            targetPoint = targetRobot.estimatedPosition(Math.round(addedTime));
            double newDistance = sprakPosition.distanceToPoint(targetPoint);
            double addTimeToTarget = (newDistance-distance)/bulletSpeed;
            addedTime += addTimeToTarget;
            if (addedTime > timeToTargetLimit){
                bulletSpeed = addedTime/timeToTargetLimit * bulletSpeed;
                addedTime = timeToTargetLimit;
                iter--;
                continue;
            }
            distance = newDistance;
            iter--;
        }
        Vector vectorToTarget = sprakPosition.vectorTo(targetPoint);
        double angleToTarget = sprak.gunHeading.angleToVector(vectorToTarget);
        sprak.setGunRotationRate(-angleToTarget);
        return -(bulletSpeed-20)/3;
    }
    public boolean canFire(){
        return turnsToFire <= 1;
    }

    public EnemyRobot getTargetRobot() {
        return targetRobot;
    }

    public long getTurnsToFire() {
        return turnsToFire;
    }
}
