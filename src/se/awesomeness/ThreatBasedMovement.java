package se.awesomeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThreatBasedMovement {

        Sprak sprak;

    public ThreatBasedMovement(Sprak sprak) {
        this.sprak = sprak;
    }

    public void UpdateThreats(long time){
        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            robotEntry.getValue().updateThreatDistance(sprak.enemyRobots, time);
        }
    }

    public void moveAway(){
        Vector moveVector = new Vector();
        double speed = sprak.normalVelocity.getMagnitude();

        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            //todo: more fleshed out algorithm to weigh moveVector.
            Point enemyPosition = robotEntry.getValue().estimatedPosition(sprak.getTime());

            double distanceFromRobot = enemyPosition.distanceToPoint(sprak.position);
            double angleFromRobot = new Vector().angleToPoint(new Point(
                    sprak.position.getX()-enemyPosition.getX(),
                    sprak.position.getY()-enemyPosition.getY())
            ); //todo: make into operator? Point.angleToPoint(Point)?

            double finalAngle = Tools.shortestAngle(angleFromRobot-sprak.normalVelocity.getDirection());
            Vector robotForce = new Vector(7*Math.sqrt(100/distanceFromRobot), finalAngle);
            moveVector = moveVector.add(robotForce);
        }
        if (sprak.enemyRobots.size() != 0) {
            Vector maxMoveVector = getMaxVector(moveVector, speed);
            move(maxMoveVector, speed);
        }
    }


    public Vector getMaxVector(Vector vector, double speed){
        Point targetPoint = new Point(toMoveVector(vector, speed));
        System.out.println("speed: " + speed);
        System.out.println("targetVector: " + vector);

        double upperAccLimit = Math.min(2, Math.max(-speed,Math.min(1, -speed+8)));
        double lowerAccLimit = Math.max(-8-speed,Math.min(-1,Math.max(-2, -speed)));
        double leftTurnLimit = 10 - 0.75 * speed;
        double rightTurnLimit = -leftTurnLimit;


        List<Limit> limits = List.of(
                new Limit(LimitType.ACCELERATION_LIMIT, upperAccLimit, speed),
                new Limit(LimitType.ACCELERATION_LIMIT, lowerAccLimit, speed),
                new Limit(LimitType.TURN_LIMIT, leftTurnLimit, speed),
                new Limit(LimitType.TURN_LIMIT, rightTurnLimit, speed)
        );

        List<Point> candidatePoints = new ArrayList<>();
        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }
        candidatePoints.add(new Point(new Vector(speed + upperAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + lowerAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + upperAccLimit,rightTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed + lowerAccLimit,rightTurnLimit)));

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

        System.out.println("result: " + new Vector(candidatePoint));
        return fromMoveVector(new Vector(candidatePoint), sprak.normalVelocity.getMagnitude());
    }

    public void move(Vector vector, double speed){
        Vector moveVector = toMoveVector(vector, speed);

        if (moveVector.getDirection() < -90 || moveVector.getDirection() > 90){
            sprak.setVelocityRate(-moveVector.getMagnitude());
            sprak.setTurnRate(Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            sprak.setVelocityRate(moveVector.getMagnitude());
            sprak.setTurnRate(-moveVector.getDirection());
        }
    }

    public Vector toMoveVector(Vector vector, double speed){
        return vector.add(new Vector(speed, 0));
    }

    public Vector fromMoveVector(Vector vector, double speed){
        return vector.subtract(new Vector(speed, 0));
    }
}