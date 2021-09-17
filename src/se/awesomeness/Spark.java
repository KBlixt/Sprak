package se.awesomeness;

import robocode.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Spark extends RateControlRobot {

    Point position = new Point(0,0);
    Vector2D velocityVector = new Vector2D(0,0);

    Map<String, EnemyRobot> enemyRobots;
    List<String> deadRobots = new ArrayList<>();

    public void run(){
        ThreatBasedMovement mover = new ThreatBasedMovement(this);

        //noinspection InfiniteLoopStatement
        while (true){
            mover.UpdateThreats();
            //do stuff
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
