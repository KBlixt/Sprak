package se.awesomeness;

import java.util.ArrayList;
import java.util.List;

public class Mover {

        Point position;
        Point nextPosition;
        Vector velocity;
        Vector nextMove;
        double wallHeight;
        double wallWidth;

    public Mover(Point position, Vector velocity, Point nextPosition, Vector nextMove, double wallWidth, double wallHeight) {
        this.position = position;
        this.velocity = velocity;
        this.nextPosition = nextPosition;
        this.nextMove = nextMove;
        this.wallWidth = wallWidth;
        this.wallHeight = wallHeight;
    }

    public void testMoving(){
        double speed = velocity.getMagnitude();
        double heading = velocity.getDirection();
        List<Vector> forces = new ArrayList<>();
        System.out.println("[-----------------------------------------------]");
        System.out.println("speed: " + speed);
        System.out.println("heading: " + heading);
        System.out.println("position: " + position);
        forces.add(velocity);
        forces.add(new Vector(1,40));

        Vector forceSum = Vector.addAll(forces);
        System.out.println("forceSum: " + forceSum);

        Vector forceAfterWall = wallSurfing(forceSum);
        System.out.println("forceAfterWall: " + forceAfterWall);

        move(forceAfterWall);
    }

    
    private Vector wallSurfing(Vector force){
        if(force.getMagnitude()< 0){
            force = force.negative();
        }

        double sprakX = position.getX();
        double sprakY = position.getY();
        double minX = 19.5;
        double minY = 19.5;
        double maxX = wallWidth - 19.5;
        double maxY = wallHeight - 19.5;
        double magnitude = calculateLookAhead((minX + maxX)/2, (minY+maxY)/2);
        force = new Vector(magnitude,force.getDirection());
        System.out.println("magnitude: " + magnitude);

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
        if (Math.abs(distToWallX) < magnitude) {
            double y = magnitude*Math.sqrt(1-Math.pow(distToWallX/magnitude,2));
            pointsOnWall.add(new Point(distToWallX, magnitude * y));
            pointsOnWall.add(new Point(distToWallX, magnitude * -y));
        }
        if (Math.abs(distToWallY) < magnitude){
            double x = Math.sqrt(1-Math.pow(distToWallY/magnitude,2));
            pointsOnWall.add(new Point(magnitude*x, distToWallY));
            pointsOnWall.add(new Point(magnitude*-x, distToWallY));
        }
        System.out.println(pointsOnWall);
        pointsOnWall.add(force.getFreeForm());
        pointsOnWall.add(new Point(new Vector(magnitude, 0)));
        pointsOnWall.add(new Point(new Vector(magnitude, 90)));
        pointsOnWall.add(new Point(new Vector(magnitude, 180)));
        pointsOnWall.add(new Point(new Vector(magnitude, -90)));

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
                    edgesOnMap.add(position.addVector(new Vector(edge)));
                }
                Point candidatePointOnMap = new Point(maxX/2, maxY/2).closestPoint(edgesOnMap);
                candidatePoint = candidatePointOnMap.subtractVector(new Vector(position));
                break;
            }
            Point checker = position.addVector(new Vector(candidatePoint));
            double margin = 1;
            boolean insideX = checker.getX()  - margin<= maxX && checker.getX() + margin>= minX;
            boolean insideY = checker.getY() - margin<= maxY && checker.getY() + margin>= minY;

           withinLimits = insideX && insideY;
        }
        force = new Vector(candidatePoint);
        return force;
    }

    private double calculateLookAhead(double midX, double midY){
        if (velocity.getMagnitude()==0){
            return 120;
        }
        double magnitudeXpart;
        double magnitudeYpart;

        if(position.getX()>midX){
            magnitudeXpart = Math.max(velocity.getX(), 0);
        }else{
            magnitudeXpart = Math.min(velocity.getX(), 0);
        }
        if(position.getY()>midY){
            magnitudeYpart = Math.max(velocity.getY(), 0);
        }else{
            magnitudeYpart = Math.min(velocity.getY(), 0);
        }
        double targetMagnitude = 120;
        magnitudeXpart = Math.abs(magnitudeXpart/velocity.getMagnitude())*targetMagnitude;
        magnitudeYpart = Math.abs(magnitudeYpart/velocity.getMagnitude())*targetMagnitude;

        return magnitudeXpart+magnitudeYpart+10;
    }

    private void move(Vector force){
        double speed = velocity.getMagnitude();
        double heading = velocity.getDirection();
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
            nextMove.setVector(
                    -moveVector.getMagnitude(),
                    Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            nextMove.setVector(
                    moveVector.getMagnitude(),
                    -moveVector.getDirection());
        }
        nextPosition = position.addVector(new Vector(
                moveVector.getMagnitude(),
                moveVector.getDirection() + velocity.getDirection()));
    }
}