package se.awesomeness;

import org.jetbrains.annotations.NotNull;
import robocode.RobotStatus;

import java.util.Random;

public class MoveGenerator {
    RobotStatus status;
    boolean reversing;
    Point robotPoint;
    double currentHeading;
    double speed;

    public MoveGenerator() {
        reversing = false;
    }

    public double[] moveTowardsPoint(Point targetPoint){
        double desiredSpeed = 8;
        double desiredAngle = getDesiredAngle(targetPoint);

        if (speed < 4 && Math.abs(desiredAngle) > 90) {
            reversing = true;
        } else if(speed > -4 && Math.abs(desiredAngle) < 90){
            reversing = false;
        }

        if (reversing){
            desiredSpeed *= -1;

            desiredAngle += 180;
            if (desiredAngle >= 180) {
                desiredAngle -= 360;
            }
        }

        return new double[]{desiredAngle, desiredSpeed};

    }

    public double[] moveAwayFromPoint(Point targetPoint){
        double desiredSpeed = 8;
        double desiredAngle = getDesiredAngle(targetPoint);

        desiredAngle += 180;
        if (desiredAngle >= 180) {
            desiredAngle -= 360;
        }

        if (speed < 4 && Math.abs(desiredAngle) > 90) {
            reversing = true;
        } else if(speed < 4 && Math.abs(desiredAngle) < 90){
            reversing = false;
        }

        if (reversing){
            desiredAngle += 180;
            if (desiredAngle >= 180) {
                desiredAngle -= 360;
            }

            desiredSpeed *= -1;
        }

        return new double[]{desiredAngle, desiredSpeed};
    }

    public static Point getNewTargetPositionRandom(double maxX, double maxY){
        Random ran = new Random();
        return new Point((maxX - 80) * ran.nextDouble() + 40,(maxY - 80) * ran.nextDouble() + 40);
    }

    private double getDesiredAngle(Point targetPoint){
        double desiredAngle = Algebra.getAngleToPoint(robotPoint, targetPoint)-currentHeading;

        if (desiredAngle < -180){
            desiredAngle = desiredAngle + 360;
        }else if (desiredAngle > 180){
            desiredAngle = desiredAngle - 360;
        }
        return desiredAngle;
    }

    public void updateStatus(RobotStatus status){
        this.status = status;
        robotPoint = new Point(status.getX(), status.getY());
        currentHeading = status.getHeading();
        speed = status.getVelocity();
    }

}
