package se.awesomeness.crew;

import se.awesomeness.EnemyRobot;

public class RadarOperator {
    private long nextAction;
    private double nextRadarTurn;

    public RadarOperator(){
        nextAction = 1;
    }

    public void monitor(int turnsToFire){
        nextAction--;
        if (nextAction==0){
            if (turnsToFire>11){
                nextRadarTurn = 45;
                nextAction = 8;
            }else{
                //robotMonitoring(target);
                nextAction = 1;
            }
        }
    }

    public void robotMonitoring(EnemyRobot target){
        nextRadarTurn = 45;
    }

    public double getNextRadarTurn() {
        return nextRadarTurn;
    }
}
