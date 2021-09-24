package se.awesomeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Mover {

        Sprak sprak;

    public Mover(Sprak sprak) {
        this.sprak = sprak;
    }

    public Vector testMoving(){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();
        List<Vector> forces = new ArrayList<>();
        System.out.println("[-----------------------------------------------]");
        System.out.println("speed: " + speed);
        System.out.println("heading: " + heading);
        System.out.println("position: " + sprak.position);
        forces.add(sprak.normalVelocity);
        //forces.add(new Point(sprak.getBattleFieldWidth()/2,sprak.getBattleFieldHeight()/2).vectorTo(sprak.position));


        Vector forceSum = Vector.addAll(forces);
        System.out.println("forceSum: " + forceSum);

        double lookAhead = 90;
        double wallBuffer = 25;

        expandForce(forceSum, lookAhead);
        Vector forceAfterWall = wallSurfing(forceSum, wallBuffer);
        System.out.println("forceAfterWall: " + forceAfterWall);

        return move(forceAfterWall);
    }

    
    private Vector wallSurfing(Vector force, double wallOffset){
        double magnitude = force.getMagnitude();
        Point position = sprak.position;

        double sprakX = position.getX();
        double sprakY = position.getY();
        double minX = 18+wallOffset;
        double minY = 18+wallOffset;
        double maxX = sprak.getBattleFieldWidth() - 18-wallOffset;
        double maxY = sprak.getBattleFieldHeight() - 18-wallOffset;

        double distToWallX;
        if (maxX - sprakX < Math.abs(minX- sprakX)){
            distToWallX = maxX - sprakX;
        }else{
            distToWallX = minX - sprakX;
        }
        double distToWallY;
        if (maxY - sprakY < Math.abs(minY - sprakY)){
            distToWallY = maxY - sprakY;
        }else{
            distToWallY = minY - sprakY;
        }

        List<Point> pointsOnWall = new ArrayList<>();
        pointsOnWall.add(force.getFreeForm());
        pointsOnWall.add(new Point(new Vector(magnitude, 0)));
        pointsOnWall.add(new Point(new Vector(magnitude, 90)));
        pointsOnWall.add(new Point(new Vector(magnitude, 180)));
        pointsOnWall.add(new Point(new Vector(magnitude, -90)));

        if (Math.abs(distToWallX) < magnitude) {
            pointsOnWall.add(new Point(distToWallX, magnitude*Math.sqrt(1-(distToWallX/magnitude))));
            pointsOnWall.add(new Point(distToWallX, -magnitude*Math.sqrt(1-(distToWallX/magnitude))));
        }
        if (Math.abs(distToWallY) < magnitude){
            pointsOnWall.add(new Point(magnitude*Math.sqrt(1-(distToWallY/magnitude)), distToWallY));
            pointsOnWall.add(new Point(-magnitude*Math.sqrt(1-(distToWallY/magnitude)), distToWallY));
        }
        System.out.println(pointsOnWall);
        if (pointsOnWall.isEmpty()){
            return force;
        }

        boolean withinLimits = false;
        Point candidatePoint = new Point();
        while (!withinLimits) {
            if (!pointsOnWall.isEmpty()) {
                candidatePoint = force.getFreeForm().closestPoint(pointsOnWall);
                pointsOnWall.remove(candidatePoint);
            }else{
                List<Point> edges = List.of(
                        new Point(new Vector(magnitude, 0)),
                        new Point(new Vector(magnitude, 90)),
                        new Point(new Vector(magnitude, 180)),
                        new Point(new Vector(magnitude, -90)));
                List<Point> edgesOnMap = new ArrayList<>();
                for (Point edge : edges) {
                    edgesOnMap.add(sprak.position.addVector(new Vector(edge)));
                }
                Point candidatePointOnMap = new Point(maxX/2, maxY/2).closestPoint(edgesOnMap);
                candidatePoint = candidatePointOnMap.subtractVector(new Vector(sprak.position));
                break;
            }
            Point checker = sprak.position.addVector(new Vector(candidatePoint));
            double margin = 1;
            boolean insideX = checker.getX()  - margin<= maxX && checker.getX() + margin>= minX;
            boolean insideY = checker.getY() - margin<= maxY && checker.getY() + margin>= minY;

           withinLimits = insideX && insideY;
        }
        force = new Vector(candidatePoint);
        return force;
    }

    private void expandForce(Vector force, double targetMagnitude){
        if (force.getMagnitude() < 0){
            force = force.negative();
        }
        force.setVector(targetMagnitude, force.getDirection()); //118 safe in 90degree turns at speed 8.
    }

    private Vector move(Vector force){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();
        if (force.getMagnitude() < 0){
            force = force.negative();
        }
        force = new Vector(force.getMagnitude(), force.getDirection() - heading);

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

        Vector zeroOffset = new Vector((upperSpeedLimit + lowerSpeedLimit)/2, (leftTurnLimit + rightTurnLimit)/2);
        double circleProjectionRadius = zeroOffset.getFreeForm().furthestPoint(candidatePoints).distanceToPoint(zeroOffset.getFreeForm());
        double largestTurnLimit = Math.max(Math.abs(leftTurnLimit), Math.abs(rightTurnLimit));
        double distanceToUpperLineProjection = Math.cos(Math.toRadians(largestTurnLimit))*upperSpeedLimit;
        double distanceToLowerLineProjection = Math.cos(Math.toRadians(largestTurnLimit))*lowerSpeedLimit;

        double forceLineProjectionDistance;
        if (force.getDirection()< 90 && force.getDirection() > -90){
            forceLineProjectionDistance = distanceToUpperLineProjection/Math.cos(Math.toRadians(force.getDirection()));
        }else if (force.getDirection()> 90 || force.getDirection() < -90){
            forceLineProjectionDistance = distanceToLowerLineProjection/Math.cos(Math.toRadians(force.getDirection()));
        }else{
            forceLineProjectionDistance = Double.MAX_VALUE;
        }
        Vector forceLineProjection = new Vector(forceLineProjectionDistance, force.getDirection());
        Vector zeroToForceLineProjection = forceLineProjection.subtract(zeroOffset);
        Vector forceCircleProjection = new Vector(circleProjectionRadius, zeroToForceLineProjection.getDirection());
        Point targetPoint = forceCircleProjection.add(zeroOffset).getFreeForm();

        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }

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
        Vector moveVector = new Vector(candidatePoint);
        System.out.println("moveVector: " + moveVector);

        if (moveVector.getDirection() < -90 || moveVector.getDirection() > 90){
            sprak.setVelocityRate(-moveVector.getMagnitude());
            sprak.setTurnRate(Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            sprak.setVelocityRate(moveVector.getMagnitude());
            sprak.setTurnRate(-moveVector.getDirection());
        }
        return moveVector;
    }
}