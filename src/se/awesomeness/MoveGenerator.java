package se.awesomeness;

import robocode.RobotStatus;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MoveGenerator {
    RobotStatus status;
    boolean reversing;
    Point robotPoint;
    Velocity currentVelocity;
    Velocity targetVelocity;
    double boardX;
    double boardY;
    Point targetPoint;

    public MoveGenerator(double boardWidth, double boardY) {
        this.boardY = boardY;
        this.boardX = boardWidth;
        reversing = false;
        currentVelocity = new Velocity(0, 0);
    }

    public void updateStatus(RobotStatus status) {
        this.status = status;
        robotPoint = new Point(status.getX(), status.getY());
        currentVelocity.direction = status.getHeading();
        currentVelocity.speed = status.getVelocity();
    }

    public Velocity getNextMovement(List<MovePolicy> movePolicies) {
        if (movePolicies.contains(MovePolicy.MOVE_FROM_CENTER)) {
            targetPoint = new Point(boardX / 2, boardY / 2);
            moveAwayFromTargetPoint(movePolicies.contains(MovePolicy.ALLOW_FAST_COURSE_CHANGE));
        }

        if (movePolicies.contains(MovePolicy.MOVE_TO_RANDOM_POINTS)) {
            if (targetPoint == null) {
                targetPoint = getRandomPoint(boardX, boardY);
            }
            if (Algebra.distanceToPoint(robotPoint, targetPoint) < 40) {
                targetPoint = getRandomPoint(boardX, boardY);
            }
            moveTowardsTargetPoint(movePolicies.contains(MovePolicy.ALLOW_FAST_COURSE_CHANGE));
        }

        return targetVelocity;
    }

    private void moveTowardsTargetPoint(boolean allowFastCoarseChange) {
        double desiredSpeed = 8;
        double desiredAngle = getDesiredAngleToTargetPoint();

        targetVelocity = findBestVelocity(desiredAngle, desiredSpeed, allowFastCoarseChange);
    }

    private void moveAwayFromTargetPoint(boolean allowFastCoarseChange) {
        double desiredSpeed = 8;
        double desiredAngle = getDesiredAngleToTargetPoint();

        desiredAngle += 180;
        if (desiredAngle >= 180) {
            desiredAngle -= 360;
        }

        targetVelocity = findBestVelocity(desiredAngle, desiredSpeed, allowFastCoarseChange);
    }

    private double getDesiredAngleToTargetPoint() {
        double desiredAngle = Algebra.getAngleToPoint(robotPoint, targetPoint) - currentVelocity.direction;

        if (desiredAngle < -180) {
            desiredAngle = desiredAngle + 360;
        } else if (desiredAngle > 180) {
            desiredAngle = desiredAngle - 360;
        }
        return desiredAngle;
    }

    private Velocity findBestVelocity(double desiredAngle, double desiredSpeed, boolean allowFastCoarseChange) {
        double limit = 4;
        if (allowFastCoarseChange) {
            limit = 0;
        }

        if (currentVelocity.speed < limit && Math.abs(desiredAngle) > 90) {
            reversing = true;
        } else if (currentVelocity.speed > -limit && Math.abs(desiredAngle) < 90) {
            reversing = false;
        }

        if (reversing) {
            desiredAngle += 180;
            if (desiredAngle >= 180) {
                desiredAngle -= 360;
            }
            desiredSpeed *= -1;
        }

        return new Velocity(desiredAngle, desiredSpeed);
    }

    public static Point getRandomPoint(double maxX, double maxY) {
        Random ran = new Random();
        return new Point((maxX - 80) * ran.nextDouble() + 40, (maxY - 80) * ran.nextDouble() + 40);
    }

    public Velocity threatAvoidance(Map<String, EnemyRobot> enemyRobots){
     return new Velocity(0,0);
    }
}
