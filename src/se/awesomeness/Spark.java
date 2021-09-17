package se.awesomeness;

import robocode.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spark extends RateControlRobot {

    Point position = new Point();
    Vector2D velocityVector = new Vector2D();

    Map<String, EnemyRobot> enemyRobots;
    List<String> deadRobots = new ArrayList<>();

    public void run(){
        enemyRobots = new HashMap<>();
        ThreatBasedMovement mover = new ThreatBasedMovement(this);
        setRadarRotationRate(45);

        //noinspection InfiniteLoopStatement
        while (true){
            mover.UpdateThreats(getTime());
            mover.moveAway();
            execute();
        }

    }

    @Override
    public void onStatus(StatusEvent event) {
        position.setPoint(getX(), getY());
        velocityVector.setVector(getVelocity(), getHeading());
        super.onStatus(event);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        String robotName = event.getName();

        if (!deadRobots.contains(robotName)){
            if (!enemyRobots.containsKey(robotName)){
                enemyRobots.put(robotName, new EnemyRobot(event, position, getHeading()));
            }else{
                enemyRobots.get(robotName).updateData(event, position, getHeading());
            }
        }

        super.onScannedRobot(event);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        String deadRobotName = event.getName();
        deadRobots.add(deadRobotName);
        enemyRobots.remove(deadRobotName);
        super.onRobotDeath(event);
    }
}
