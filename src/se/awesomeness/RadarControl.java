package se.awesomeness;

public class RadarControl {
    Sprak sprak;
    long nextAction;
    public RadarControl(Sprak sprak){
        this.sprak = sprak;
        nextAction = 1;
    }

    public void defaultMonitoring(EnemyRobot target){
        long timeToFire = sprak.turnsToFire;
        nextAction--;
        if (nextAction==0){
            if (timeToFire>11){
                sprak.setRadarRotationRate(45);
                nextAction = 8;
            }else{
                robotMonitoring(target);
                nextAction = 1;
            }
        }
    }

    public void robotMonitoring(EnemyRobot target){

    }
}
