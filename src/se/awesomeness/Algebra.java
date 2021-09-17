package se.awesomeness;

import java.util.List;

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

    public static double distanceToPoint(Point fromPoint, Point toPoint){
        double deltaX = Math.abs(fromPoint.x - toPoint.x);
        double deltaY = Math.abs(fromPoint.y - toPoint.y);

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public static double shortestAngle(double fromAngle, double toAngle){
        double angleDelta = (toAngle - fromAngle) %360;
        if (angleDelta < -180){
            return angleDelta + 360;
        }else if ( angleDelta > 180){
            return angleDelta - 360 ;
        }else{
            return angleDelta;
        }
    }

    private static Point closestPoint(Point fromPoint, List<Point> points){
        int closestPointIndex = 0;
        double fromX = fromPoint.x;
        double fromY = fromPoint.y;
        double shortestDistance = Math.sqrt(Math.pow(
                points.get(closestPointIndex).x - fromX,2)
                + Math.pow(points.get(closestPointIndex).y - fromY,2));

        double distance;
        for ( int i = 1; i < points.size(); i++){

            distance = Math.sqrt(Math.pow(
                    points.get(i).x - fromX,2)
                    + Math.pow(points.get(i).y - fromY,2));

            if (distance < shortestDistance){
                closestPointIndex = i;
                shortestDistance = distance;
            }
        }
        return points.get(closestPointIndex);
    }

}
