package se.awesomeness;

import robocode.*;

public class Spark extends RateControlRobot {
    RobotStatus status;
    Point targetPoint;
    MoveGenerator mover;

    public void run(){
        mover = new MoveGenerator();
        targetPoint = new Point(
                getBattleFieldWidth() / 2,
                getBattleFieldHeight() / 2);

        //noinspection InfiniteLoopStatement
        while (true){

            calculateMove();
            calculateRadar();
            calculateFire();
            execute();
        }

    }

    public void calculateMove(){
        Point robotPoint = new Point(status.getX(), status.getY());
        mover.updateStatus(status);
        if (Algebra.getDistanceToPoint(robotPoint, targetPoint) < 10){
            targetPoint = MoveGenerator.getNewTargetPositionRandom(getBattleFieldWidth(),getBattleFieldHeight());
        }


        double[] desiredHeading = mover.moveTowardsPoint(targetPoint);

        System.out.println(desiredHeading[0]);
        setTurnRate(desiredHeading[0]);
        setVelocityRate(desiredHeading[1]);
    }

    public void calculateRadar(){
        setTurnRadarLeft(90);
    }

    public void calculateFire(){
        setFire(3);
        setTurnGunLeft(20);
        setAhead(50);
    }

    /** Uppdaterar statusen för roboten i början av varje runda */
    public void onStatus(StatusEvent e) {
        this.status = e.getStatus();
    }

}
