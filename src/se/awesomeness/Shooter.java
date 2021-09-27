package se.awesomeness;

import java.util.Map;

public class Shooter {

    private final Vector gunHeading;
    private final Map<String, EnemyRobot> enemyRobots;

    private int turnsToFire;
    private Point nextPosition;
    private EnemyRobot target;

    private boolean setFire;
    private double adjustGunAngle;
    private double bulletPower;



    public Shooter(Vector gunHeading, Map<String, EnemyRobot> enemyRobots) {
        this.gunHeading = gunHeading;
        this.enemyRobots = enemyRobots;
    }

    public void prepareShot(Point nextPosition, int turnsToFire){
        this.turnsToFire = turnsToFire;
        this.nextPosition = nextPosition;

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
