package se.awesomeness.crew;

import se.awesomeness.EnemyRobot;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Vector;

public class RadarOperator {

    private final Vector radarHeading;
    private final Point position;

    private long nextAction;
    private double nextRadarTurn;

    public RadarOperator(Point position, Vector radarHeading){
        nextAction = 1;
        this.position = position;
        this.radarHeading = radarHeading;
    }

    public void monitor(EnemyRobot target, int turnsToFire){
        nextAction--;
        if (nextAction==0){
            if (turnsToFire>10){
                nextRadarTurn = 75;
                nextAction = 8;
            }else{
                robotMonitoring(target);
                nextAction = 1;
            }
        }
    }

    public void robotMonitoring(EnemyRobot target){
        Point targetPosition = target.estimatedPosition(1);
        double radarBearingToTarget = radarHeading.angleToVector(position.vectorTo(targetPosition));
        System.out.println("radarBearingToTarget: " + radarBearingToTarget);
        if (radarBearingToTarget >= 0){
            if (radarBearingToTarget + 22.5 < 75){
                nextRadarTurn = radarBearingToTarget + 22.5;
            }else{
                nextRadarTurn = 75;
            }
        }else {
            if (radarBearingToTarget - 22.5 > -75) {
                nextRadarTurn = radarBearingToTarget - 22.5;
            } else {
                nextRadarTurn = -75;
            }
        }
    }

    public double getNextRadarTurn() {
        return -nextRadarTurn;
    }
}
