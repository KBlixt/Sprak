package se.awesomeness;

import java.util.Map;

public class Shooter {

    Vector gunHeading;
    Point nextPosition;
    Map<String, EnemyRobot> enemyRobots;
    EnemyRobot target;

    int turnsToFire;

    boolean setFire;
    double adjustGunAngle;
    double bulletPower;



    public Shooter(Vector gunHeading, Point nextPosition, Map<String, EnemyRobot> enemyRobots, EnemyRobot target) {
        this.gunHeading = gunHeading;
        this.nextPosition = nextPosition;
        this.enemyRobots = enemyRobots;
        this.target = target;
    }

    public void prepareShot(int turnsToFire){
        this.turnsToFire = turnsToFire;
        target = enemyRobots.get(targetSelection());
        aim();
        setFire = turnsToFire < 1 && bulletPower >= 1 && target.getInfoAge() < 2;
    }

    private String targetSelection(){
        String targetRobotName = "";
        double shortestDistance = -1;

        for (Map.Entry<String, EnemyRobot> entry : enemyRobots.entrySet()) {
            Point position = entry.getValue().estimatedPosition(turnsToFire);
            double distance = nextPosition.distanceTo(position);
            if ( distance < shortestDistance || shortestDistance == -1){
                shortestDistance = distance;
                targetRobotName = entry.getKey();
            }
        }
        return targetRobotName;
    }

    private void aim(){
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
            if (addedTime > timeToTargetLimit){
                bulletSpeed = addedTime/timeToTargetLimit * bulletSpeed;
                addedTime = timeToTargetLimit;
                iter--;
                continue;
            }
            distance = newDistance;
            iter--;
        }
        Vector vectorToTarget = nextPosition.vectorTo(targetPoint);
        double angleToTarget = gunHeading.angleToVector(vectorToTarget);
        adjustGunAngle = -angleToTarget;
        bulletPower = -(bulletSpeed-20)/3;
    }

    public boolean getSetFire(){
        return setFire;
    }
    public double getAdjustGunAngle(){
        return adjustGunAngle;
    }
    public double getBulletPower(){
        return bulletPower;
    }
}
