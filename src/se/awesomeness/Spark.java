package se.awesomeness;

import robocode.*;

import java.util.List;

public class Spark extends AdvancedRobot {
    RobotStatus status;

    List<MovePolicy> movePolicies = List.of(
            MovePolicy.MOVE_TO_RANDOM_POINTS,
            MovePolicy.ALLOW_FAST_COURSE_CHANGE);

    public void run(){
        MoveGenerator mover = new MoveGenerator(getBattleFieldWidth(), getBattleFieldHeight());

        //noinspection InfiniteLoopStatement
        while (true){

            calculateMove(mover);
            calculateRadar();
            calculateFire();
            execute();
        }

    }

    public void calculateMove(MoveGenerator mover){
        mover.updateStatus(status);

        Velocity targetVelocity = mover.getNextMovement(movePolicies);

        setMaxTurnRate(targetVelocity.direction);
        setTurnRight(targetVelocity.direction);
        setMaxVelocity(targetVelocity.speed);
        setAhead(800*targetVelocity.speed);
    }

    public void calculateRadar(){
        setTurnRadarLeft(90);
    }

    public void calculateFire(){
        setFire(3);
        setTurnGunLeft(20);
    }

    /** Uppdaterar statusen för roboten i början av varje runda */
    public void onStatus(StatusEvent e) {
        this.status = e.getStatus();
    }


}
