package se.awesomeness.crew;

import se.awesomeness.EnemyRobot;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Vector;

public class Gunner {

    private final Vector gunHeading;

    private int turnsToFire;
    private Point nextPosition;

    private double adjustGunAngle;
    private double bulletPower;



    public Gunner(Vector gunHeading) {
        this.gunHeading = gunHeading;
    }

    public void takeAim(Vector fireSolution){
        adjustGunAngle = gunHeading.angleToVector(fireSolution);
        bulletPower = fireSolution.getMagnitude();
    }

    public void updateInfo(Point nextPosition, int turnsToFire) {
        this.turnsToFire = turnsToFire;
        this.nextPosition = nextPosition;
    }

    public Vector findFireSolution(EnemyRobot target){
        System.out.println("debug: " + target.isStandingStill());
        int offset = 0;
        if (target.isStandingStill()) {
            offset = target.getClosestMatchingState();
        }
        System.out.println("offset: " + offset);

        Point targetPoint = target.estimatedPosition(offset);
        Vector enemyVelocity = target.estimatedVelocity(turnsToFire+ offset);
        if (enemyVelocity.getMagnitude() < 0){
            enemyVelocity = enemyVelocity.negative();
        }
        double timeLimitFactor = Math.sin(Math.toRadians(nextPosition.vectorTo(targetPoint).angleToVector(enemyVelocity)));
        double timeLimit = 15 + 10*timeLimitFactor + turnsToFire;

        double distance = nextPosition.distanceTo(targetPoint);
        double addedTime = turnsToFire + distance/11;
        double bulletSpeed = 11;
        int iter = 10;
        while(iter>0){
            targetPoint = target.estimatedPosition((int)Math.round(addedTime + offset));
            distance = nextPosition.distanceTo(targetPoint);
            addedTime = distance/bulletSpeed + turnsToFire;
            System.out.println("addedTime: " + addedTime);

            if (addedTime > timeLimit && bulletSpeed < 17){
                bulletSpeed *= addedTime/timeLimit;
                addedTime = timeLimit;
                if (bulletSpeed > 17){
                    addedTime *= bulletSpeed/17;
                    bulletSpeed = 17;
                }
            }else if (addedTime < timeLimit && bulletSpeed > 11){
                bulletSpeed *= addedTime/timeLimit;
                addedTime = timeLimit;
                if (bulletSpeed < 11){
                    addedTime *= bulletSpeed/11;
                    bulletSpeed = 11;
                }
            }
            iter--;
        }
        if(addedTime > 35){
            bulletSpeed = 20;
        }
        Vector vectorToTarget = nextPosition.vectorTo(targetPoint);
        return new Vector(-(bulletSpeed-20)/3,vectorToTarget.getDirection());
    }

    public boolean isAligned(Vector fireSolution){
        return gunHeading.angleToVector(fireSolution) <= 0.00001 && gunHeading.angleToVector(fireSolution) >= -0.00001;
    }

    public double getAdjustGunAngle(){
        return -adjustGunAngle;
    }
    public double getBulletPower(){
        return bulletPower;
    }
}
