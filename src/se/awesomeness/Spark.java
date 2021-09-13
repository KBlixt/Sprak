package se.awesomeness;

import robocode.Robot;
import robocode.AdvancedRobot;
public class Spark extends AdvancedRobot {

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
}
