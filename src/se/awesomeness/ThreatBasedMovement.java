package se.awesomeness;

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
        Vector maxMoveVector = getMaxVector(moveVector, speed);
        move(maxMoveVector, speed);
    }


    public Vector getMaxVector(Vector vector, double speed){
        Point targetPoint = toMoveVector(vector,speed).getFreeForm();


        List<Function> functions = List.of(
                new Function(FunctionType.X_ACC_LIMIT, speed, Math.min(2, Math.max(-speed,Math.min(1, -speed+8)))),
                new Function(FunctionType.X_ACC_LIMIT, speed, Math.max(-8-speed,Math.min(-1,Math.max(-2, -speed)))),
                new Function(FunctionType.Y_ACC_LIMIT, speed, 1),
                new Function(FunctionType.Y_ACC_LIMIT, speed, -1)
        );

        double shortestDistance = -1;
        Point closestCandidatePoint = new Point();

        for (Function function : functions) {
            Point candidatePoint = function.closestPoint(targetPoint);
            double distance = targetPoint.distanceToPoint(candidatePoint);

            boolean isCloser = distance < shortestDistance;
            boolean withinLimits = true;
            for (Function testFunction : functions) {
                withinLimits = withinLimits && testFunction.withinFunction(candidatePoint);
            }

            if (withinLimits && (isCloser || shortestDistance == -1)) {
                shortestDistance = distance;
                closestCandidatePoint = candidatePoint;
            }
        }

        return fromMoveVector(new Vector(closestCandidatePoint), sprak.normalVelocity.getMagnitude());
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