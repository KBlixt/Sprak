package se.awesomeness;

import robocode.Robot;
import robocode.AdvancedRobot;
import robocode.RobotStatus;

public class Spark extends AdvancedRobot {
    RobotStatus status;

    public void run(){
        back(100);
        //noinspection InfiniteLoopStatement
        while (true){

            calculateMove();
            calculateRadar();
            calculateFire();
            execute();
        }

    }
    public void calculateMove(){

        setAhead(1000);
        setTurnLeft(40);
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
    public void onStatus(RobotStatus status){
        this.status = status;
    }
}
