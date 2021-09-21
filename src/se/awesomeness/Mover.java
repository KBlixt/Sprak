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
        forces.add( new Vector(3, 90));
        forces.add(new Vector(-4, 0.2));
        forces.add(new Vector(8, -45));

        move(Vector.addAll(forces));
    }

    
    public void addWallForces(List<Vector> forces){
        double sprakX = sprak.position.getX();
        double sprakY = sprak.position.getY();
        double maxX = sprak.getBattleFieldWidth();
        double maxY =sprak.getBattleFieldHeight();

        Point top = new Point(sprakX, maxY);
        Point bott = new Point(sprakX, 0);
        Point left = new Point(0, sprakY);
        Point right = new Point(maxX, sprakY);
    }


    public void move(Vector vector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();

        vector = getMaxVector(vector);
        Vector moveVector = toMoveVector(vector, speed , heading );

        if (moveVector.getDirection() < -90 || moveVector.getDirection() > 90){
            sprak.setVelocityRate(-moveVector.getMagnitude());
            sprak.setTurnRate(Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            sprak.setVelocityRate(moveVector.getMagnitude());
            sprak.setTurnRate(-moveVector.getDirection());
        }
    }

    public Vector getMaxVector(Vector vector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();

        Point targetPoint = new Point(toMoveVector(vector, speed, heading));
        List<Point> candidatePoints = new ArrayList<>();

        double upperAccLimit = Math.min(2, Math.max(-speed,Math.min(1, -speed+8)));
        double lowerAccLimit = Math.max(-8-speed,Math.min(-1,Math.max(-2, -speed)));
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
        return fromMoveVector(new Vector(candidatePoint), speed, heading);
    }

    public static Vector toMoveVector(Vector vector, double speed, double heading){
        Vector moveVector = new Vector(vector.getMagnitude(), vector.getDirection() - heading);
        moveVector = moveVector.add(new Vector(speed,0));
        return moveVector;
    }

    public static Vector fromMoveVector(Vector moveVector, double speed, double heading){
        Vector vector = moveVector.subtract(new Vector(speed,0));
        vector = new Vector(vector.getMagnitude(), vector.getDirection() + heading);
        return vector;
    }
}