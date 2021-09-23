package se.awesomeness;

import java.util.ArrayList;
import java.util.List;

public class Shooter {

    Sprak sprak;
    EnemyRobot currentTarget;
    boolean hardTargetLock;
    boolean softTargetLock;
    double gunHeat;
    boolean shooting;

    public Shooter(Sprak sprak) {
        this.sprak = sprak;
        hardTargetLock = false;
        softTargetLock = false;
        shooting = false;
        gunHeat = sprak.getGunHeat();
    }

    public void prepareShot(){
        gunHeat = sprak.getGunHeat();
    }

    private List<EnemyRobot> targetSelection(){
        return new ArrayList<>();
    }

    private void aim(EnemyRobot enemyRobot){

    }

    public void fire(Vector moveVector){

    }

    public boolean canFire(){
        return gunHeat <= 0.1;
    }
}
