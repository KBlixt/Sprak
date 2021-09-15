package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.StatusEvent;

public class Spark extends Robot {

    RobotStatus status;

    public void run(){
        turnRight(Tools.angleToWall(
                status.getX(),
                status.getY(),
                status.getHeading(),
                getBattleFieldWidth(),
                getBattleFieldHeight()));
    }

    @Override
    public void onStatus(StatusEvent e) {
        status = e.getStatus();
    }
}

