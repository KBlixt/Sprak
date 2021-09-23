package se.awesomeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Mover {

        Sprak sprak;

    public Mover(Sprak sprak) {
        this.sprak = sprak;
    }

    public void UpdateThreats(long time){
        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            robotEntry.getValue().updateThreatDistance(sprak.enemyRobots, time);
        }
    }

    public void testMoving(){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();
        List<Vector> forces = new ArrayList<>();
        System.out.println("[-------------------------------------]");
        System.out.println("speed: " + speed);
        System.out.println("heading: " + heading);
        forces.add(sprak.normalVelocity);
        forces.add(new Vector(9,45));


        Vector forceSum = Vector.addAll(forces);
        System.out.println("forceSum: " + forceSum);

        expandForce(forceSum);
        System.out.println("forceSum after exp: " + forceSum);

        Vector forceAfterWall = wallSurfing(forceSum);
        System.out.println("forceAfterWall: " + forceAfterWall);
        move(forceAfterWall);
    }

    
    public Vector wallSurfing(Vector force){
        double magnitude = force.getMagnitude();
        double wallOffset = 2;
        Point position = new Point(
                sprak.position.getX(),
                sprak.position.getY());
        System.out.println("position: " + sprak.position);
        System.out.println("positionInternal: " + position);
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
        System.out.println(distToWallY);
        List<Point> pointsOnWall = new ArrayList<>();

        pointsOnWall.add(force.getFreeForm());
        if (Math.abs(distToWallX) < magnitude) {
            pointsOnWall.add(new Point(distToWallX, magnitude*Math.sqrt(1-(distToWallX/magnitude))));
            pointsOnWall.add(new Point(distToWallX, -magnitude*Math.sqrt(1-(distToWallX/magnitude))));
        }
        if (Math.abs(distToWallY) < magnitude){
            System.out.println("magnitude: " + magnitude);
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
            candidatePoint = force.getFreeForm().closestPoint(pointsOnWall);
            pointsOnWall.remove(candidatePoint);
            Point checker = sprak.position.addVector(new Vector(candidatePoint));
            double margin = 1;
            boolean insideX = checker.getX()  - margin<= maxX && checker.getX() + margin>= minX;
            boolean insideY = checker.getY() - margin<= maxY && checker.getY() + margin>= minY;

           withinLimits = insideX && insideY;
        }
        System.out.println("ooooooooooooooooooooooooooo");
        force = new Vector(candidatePoint);
        return force;
    }

    public void expandForce(Vector force){
        if (force.getMagnitude() < 0){
            force = force.negative();
        }
        force.setVector(150, force.getDirection());
    }


    public void move(Vector forceVector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();

        forceVector = MaxForceVector(forceVector);
        Vector moveVector = toMoveVector(forceVector, speed , heading );
        System.out.println("MoveVector: " + moveVector);

        if (moveVector.getDirection() < -90 || moveVector.getDirection() > 90){
            sprak.setVelocityRate(-moveVector.getMagnitude());
            sprak.setTurnRate(Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            sprak.setVelocityRate(moveVector.getMagnitude());
            sprak.setTurnRate(-moveVector.getDirection());
        }
    }

    public Vector MaxForceVector(Vector forceVector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();
        Point targetPoint = new Point(toMoveVector(forceVector, speed, heading));

        double upperAccLimit = Math.min(2, Math.max(-speed,Math.min(1, -speed+8)));
        double lowerAccLimit = Math.max(-8-speed,Math.min(-1,Math.max(-2, -speed)));
        double leftTurnLimit = 10 - 0.75 * Math.abs(speed);
        double rightTurnLimit = -leftTurnLimit;

        List<Limit> limits = List.of(
                new Limit(LimitType.ACCELERATE_LIMIT, upperAccLimit + speed),
                new Limit(LimitType.DECELERATE_LIMIT, lowerAccLimit + speed),
                new Limit(LimitType.TURN_LEFT_LIMIT, leftTurnLimit),
                new Limit(LimitType.TURN_RIGHT_LIMIT, rightTurnLimit)
        );

        List<Point> candidatePoints = new ArrayList<>();
        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }
        candidatePoints.add(new Point(new Vector(speed +upperAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +lowerAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +upperAccLimit,rightTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +lowerAccLimit,rightTurnLimit)));

        boolean withinLimits = false;
        Point candidatePoint = new Point();
        while(!withinLimits){
            withinLimits = true;
            candidatePoint = targetPoint.closestPoint(candidatePoints);
            for (Limit limit : limits) {
                withinLimits &= limit.withinLimit(candidatePoint);
            }
            candidatePoints.remove(candidatePoint);
        }

        return toForceVector(new Vector(candidatePoint), speed, heading);
    }

    public static Vector toMoveVector(Vector forceVector, double speed, double heading){
        Vector moveVector = new Vector(forceVector.getMagnitude(), forceVector.getDirection() - heading);
        moveVector = moveVector.add(new Vector(speed,0));
        return moveVector;
    }

    public static Vector toForceVector(Vector moveVector, double speed, double heading){
        Vector vector = moveVector.subtract(new Vector(speed,0));
        vector = new Vector(vector.getMagnitude(), vector.getDirection() + heading);
        return vector;
    }
}