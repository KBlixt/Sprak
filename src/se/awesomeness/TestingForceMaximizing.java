
package se.awesomeness;


import java.util.ArrayList;
import java.util.List;

public class TestingForceMaximizing {

    public static void main(String[] args) {
        double speed = 8;
        double heading = -37;

        Vector targetVector = Mover.toMoveVector(new Vector(10000, 0), speed, heading);

        //Vector targetVector = ThreatBasedMovement.toMoveVector(new Vector(new Point(100, 0)));

        Point targetPoint = targetVector.add(new Vector(speed, 0)).getFreeForm();
        System.out.println("target in movepace: " + new Vector(targetPoint));

        List<Point> candidatePoints = new ArrayList<>();

        double upperAccLimit = Math.min(2, Math.max(-speed, Math.min(1, -speed + 8)));
        double lowerAccLimit = Math.max(-8 - speed, Math.min(-1, Math.max(-2, -speed)));
        double leftTurnLimit = 10 - 0.75 * Math.abs(speed);
        double rightTurnLimit = -leftTurnLimit;

        List<Limit> limits = List.of(
                new Limit(LimitType.ACCELERATION_LIMIT, upperAccLimit, speed),
                new Limit(LimitType.ACCELERATION_LIMIT, lowerAccLimit, speed),
                new Limit(LimitType.TURN_LIMIT, leftTurnLimit, speed),
                new Limit(LimitType.TURN_LIMIT, rightTurnLimit, speed)
        );

        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }
        candidatePoints.add(new Point(new Vector(speed + upperAccLimit, leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + lowerAccLimit, leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + upperAccLimit, rightTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + lowerAccLimit, rightTurnLimit)));

        boolean withinLimits = false;
        Point candidatePoint = new Point();

        while (!withinLimits) {
            withinLimits = true;
            candidatePoint = targetPoint.closestPoint(candidatePoints);
            for (Limit limit : limits) {
                withinLimits &= limit.withinLimit(candidatePoint);
            }
            candidatePoints.remove(candidatePoint);
        }

        System.out.println(new Vector(candidatePoint));
        candidatePoint = Mover.toForceVector(new Vector(candidatePoint), speed, heading).getFreeForm();

        System.out.println(Mover.toMoveVector(new Vector(candidatePoint),speed,heading));
/*
        for (Limit limit : limits) {
            Point point = limit.closestPoint(targetPoint);
            double distance = targetPoint.distanceToPoint(point);
            Vector t = new Vector(point);

            boolean isCloser = distance < shortestDistance;
            boolean withinLimits = true;
            for (Limit functioni : limits) {
                withinLimits = withinLimits && functioni.withinFunction(point);
            }

            if (withinLimits && (isCloser || shortestDistance == -1)) {
                shortestDistance = distance;
                closestPoint = point;
            }
        }

*/

    }
}