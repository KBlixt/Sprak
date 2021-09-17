package se.awesomeness;

import robocode.*;

import java.util.List;

public class Spark extends RateControlRobot {
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

        setTurnRate(targetVelocity.direction);
        setVelocityRate(targetVelocity.speed);
    }

    public void calculateRadar(){
    }

    public void calculateFire(){
    }

    public void onStatus(StatusEvent e) {
        this.status = e.getStatus();
    }




}
