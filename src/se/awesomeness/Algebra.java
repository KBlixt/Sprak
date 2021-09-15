package se.awesomeness;

public class Algebra {


    public static double getAngleToPoint(Point fromPoint, Point toPoint){
        double angleToPoint = Math.atan2(
                toPoint.y - fromPoint.y,
                toPoint.x - fromPoint.x);

        angleToPoint = Math.toDegrees(-angleToPoint) + 90;
        if (angleToPoint > 180){
            angleToPoint = angleToPoint - 360;
        }
        return angleToPoint;
    }

    public static double getDistanceToPoint(Point robotPoint, Point targetPoint){
        double deltaX = Math.abs(robotPoint.x - targetPoint.x);
        double deltaY = Math.abs(robotPoint.y - targetPoint.y);

        return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
    }


}
