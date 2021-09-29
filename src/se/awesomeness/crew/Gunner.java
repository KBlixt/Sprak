package se.awesomeness.crew;

import se.awesomeness.EnemyRobot;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        double timeLimit = 20 + turnsToFire;

        Point targetPoint = target.getPosition();
        double distance = nextPosition.distanceTo(targetPoint);
        double addedTime = turnsToFire + distance/11;
        double bulletSpeed = 11;
        int iter = 10;
        while(iter>0){
            targetPoint = target.estimatedPosition((int)Math.round(addedTime));
            distance = nextPosition.distanceTo(targetPoint);
            addedTime = distance/bulletSpeed + turnsToFire;
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

    public Vector findFireSolutionAverageLocation(EnemyRobot target){
        ArrayList<Point> pastPositions = target.getPastPositions();
        int size = pastPositions.size();
        if (size<130){
            return new Vector();
        }
        Vector sumVectorPositions = new Vector();
        for (int i = size-110; i < size; i++){
            sumVectorPositions = sumVectorPositions.add(new Vector(pastPositions.get(i)));
        }
        Point avgPos = sumVectorPositions.divide(110).getPoint();

        sumVectorPositions = new Vector();
        for (int i = size-120; i < size-10; i++){
            sumVectorPositions = sumVectorPositions.add(new Vector(pastPositions.get(i)));
        }
        Point averagePosition2 = sumVectorPositions.divide(110).getPoint();
        Vector avgPosSpeed = averagePosition2.vectorTo(avgPos).divide(10);

        sumVectorPositions = new Vector();
        for (int i = size-130; i < size-20; i++){
            sumVectorPositions = sumVectorPositions.add(new Vector(pastPositions.get(i)));
        }
        Point averagePosition3 = sumVectorPositions.divide(110).getPoint();
        Vector avgPosSpeed2 = averagePosition3.vectorTo(averagePosition2).divide(10);
        Vector avgPosAcc = avgPosSpeed.subtract(avgPosSpeed2).divide(10);

        Vector vectorToTargetPoint = nextPosition.vectorTo(target.estimatedPosition(2));
        vectorToTargetPoint.setMagnitude(3);
        return vectorToTargetPoint;
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
