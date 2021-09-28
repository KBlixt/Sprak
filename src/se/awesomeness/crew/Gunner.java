package se.awesomeness.crew;

import se.awesomeness.EnemyRobot;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Vector;

import java.util.Map;

public class Gunner {

    private final Vector gunHeading;

    private int turnsToFire;
    private Point nextPosition;

    private double adjustGunAngle;
    private double bulletPower;



    public Gunner(Vector gunHeading, Map<String, EnemyRobot> enemyRobots) {
        this.gunHeading = gunHeading;
    }

    public void takeAim(Vector fireSolution){
        double angleToTarget = gunHeading.angleToVector(fireSolution);
        adjustGunAngle = -angleToTarget;
        bulletPower = fireSolution.getMagnitude();
    }

    public void updateInfo(Point nextPosition, int turnsToFire){
        this.turnsToFire = turnsToFire;
        this.nextPosition = nextPosition;
    }

    public Vector findFireSolution(EnemyRobot target){
        double timeToTargetLimit = 30;

        Point targetPoint = target.getPosition();
        double distance = nextPosition.distanceTo(targetPoint);
        double addedTime = turnsToFire + distance/11;

        double bulletSpeed = 11;
        int iter = 10;
        while(iter>0){
            targetPoint = target.estimatedPosition(Math.round(addedTime));
            double newDistance = nextPosition.distanceTo(targetPoint);
            double addTimeToTarget = (newDistance-distance)/bulletSpeed;
            addedTime += addTimeToTarget;
            if (addedTime > timeToTargetLimit && bulletSpeed < 17){
                bulletSpeed = addedTime/timeToTargetLimit * bulletSpeed;
                addedTime = timeToTargetLimit;
                if (bulletSpeed > 17){
                    addedTime = bulletSpeed/17 * addedTime;
                    bulletSpeed = 17;
                }
                iter--;
                continue;
            }
            if (addedTime < timeToTargetLimit && bulletSpeed > 11){
                bulletSpeed = addedTime/timeToTargetLimit * bulletSpeed;
                if (bulletSpeed < 11){
                    addedTime = bulletSpeed/11 * addedTime;
                    bulletSpeed = 11;
                }
                iter--;
                continue;
            }
            distance = newDistance;
            iter--;
        }
        Vector vectorToTarget = nextPosition.vectorTo(targetPoint);
        return new Vector(-(bulletSpeed-20)/3,vectorToTarget.getDirection());
    }

    public boolean readyToFire(){
        return turnsToFire < 1;
    }
    public double getAdjustGunAngle(){
        return adjustGunAngle;
    }
    public double getBulletPower(){
        return bulletPower;
    }
}
