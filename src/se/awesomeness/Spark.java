package se.awesomeness;

import robocode.*;

import java.util.List;
import java.util.Map;

public class Spark extends RateControlRobot {

    Point position = new Point(0,0);
    Vector2D velocityVector = new Vector2D(0,0);

    Map<String, EnemyRobot> enemyRobots;

    public void run(){

        //noinspection InfiniteLoopStatement
        while (true){
            execute();
        }

    }

    @Override
    public void onStatus(StatusEvent event) {
        position.setPoint(getX(), getY());
        velocityVector.setVector(getVelocity(), getHeading());
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        String robotName = event.getName();

        if (!enemyRobots.containsKey(robotName)){
            enemyRobots.put(robotName, new EnemyRobot(event, position, getHeading()));
        }else{
            enemyRobots.get(robotName).updateData(event, position, getHeading());
        }
    }

}
