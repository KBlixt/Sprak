package se.awesomeness.forceTranslator;

import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Tools;
import se.awesomeness.geometry.Vector;

import java.util.ArrayList;
import java.util.List;

public class ForceTranslator {

    public static Vector translateForce(Vector force, Vector velocity) {
        double speed = velocity.getMagnitude();
        double heading = velocity.getDirection();
        if (force.getMagnitude() < 0) {
            force = force.set(1, Tools.oppositeAngle(force.getDirection()));
        }
        force = new Vector(1, force.getDirection() - heading);

        double upperSpeedLimit = speed + Math.min(2, Math.max(-speed, Math.min(1, -speed + 8)));
        double lowerSpeedLimit = speed + Math.max(-8 - speed, Math.min(-1, Math.max(-2, -speed)));
        double leftTurnLimit = 10 - 0.75 * Math.abs(speed);
        double rightTurnLimit = -leftTurnLimit;

        List<Limit> limits = List.of(
                new Limit(LimitType.ACCELERATE_LIMIT, upperSpeedLimit),
                new Limit(LimitType.DECELERATE_LIMIT, lowerSpeedLimit),
                new Limit(LimitType.TURN_LEFT_LIMIT, leftTurnLimit),
                new Limit(LimitType.TURN_RIGHT_LIMIT, rightTurnLimit)
        );

        List<Point> candidatePoints = new ArrayList<>();
        candidatePoints.add(new Point(new Vector(upperSpeedLimit, leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(upperSpeedLimit, rightTurnLimit)));
        candidatePoints.add(new Point(new Vector(lowerSpeedLimit, leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(lowerSpeedLimit, rightTurnLimit)));

        Vector zeroOffset = new Vector((upperSpeedLimit + lowerSpeedLimit) / 2, (leftTurnLimit + rightTurnLimit) / 2);
        double circleProjectionRadius = zeroOffset.toPoint().furthestPoint(candidatePoints).distanceTo(zeroOffset.toPoint());
        double largestTurnLimit = Math.max(Math.abs(leftTurnLimit), Math.abs(rightTurnLimit));
        double distanceToUpperLineProjection = Math.cos(Math.toRadians(largestTurnLimit)) * upperSpeedLimit;
        double distanceToLowerLineProjection = Math.cos(Math.toRadians(largestTurnLimit)) * lowerSpeedLimit;

        double forceLineProjectionDistance;
        if (force.getDirection() < 90 && force.getDirection() > -90) {
            forceLineProjectionDistance = distanceToUpperLineProjection / Math.cos(Math.toRadians(force.getDirection()));
        } else if (force.getDirection() > 90 || force.getDirection() < -90) {
            forceLineProjectionDistance = distanceToLowerLineProjection / Math.cos(Math.toRadians(force.getDirection()));
        } else {
            forceLineProjectionDistance = Double.MAX_VALUE;
        }
        Vector forceLineProjection = new Vector(forceLineProjectionDistance, force.getDirection());
        Vector zeroToForceLineProjection = forceLineProjection.subtract(zeroOffset);
        Vector forceCircleProjection = new Vector(circleProjectionRadius, zeroToForceLineProjection.getDirection());
        Point targetPoint = forceCircleProjection.add(zeroOffset).toPoint();

        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }

        Point candidatePoint = new Point();
        boolean withinLimits = false;

        while (!withinLimits) {
            withinLimits = true;
            candidatePoint = targetPoint.closestPoint(candidatePoints);
            for (Limit limit : limits) {
                withinLimits &= limit.withinLimit(candidatePoint);
            }
            candidatePoints.remove(candidatePoint);
        }

        return new Vector(candidatePoint);
    }
}
