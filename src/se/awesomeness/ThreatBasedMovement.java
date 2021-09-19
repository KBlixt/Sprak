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
        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            //todo: more fleshed out algorithm to weigh moveVector.
            Point robotEstimatedPosition = robotEntry.getValue().estimatedPosition(sprak.getTime());

            double distanceFromRobot = robotEstimatedPosition.distanceToPoint(sprak.position);
            double angleFromRobot = new Vector().angleToPoint(new Point(
                    sprak.getX()-robotEstimatedPosition.getX(),
                    sprak.getY()-robotEstimatedPosition.getY())
            ); //todo: make into operator? Point.angleToPoint(Point)?

            Vector robotForce = new Vector(7*Math.sqrt(100/distanceFromRobot), angleFromRobot);
            moveVector = moveVector.add(robotForce);
        }
        sprak.setTurnRate(Tools.convertAngle(Tools.shortestAngle(moveVector.getDirection()- sprak.normalVelocity.getDirection())));
        sprak.setVelocityRate(8);
    }

    public Vector getMaxVector(Vector desiredVector){
        double speed = sprak.normalVelocity.getMagnitude();
        List<Function> limits = List.of(
                new Function(FunctionType.X_ACC_LIMIT, speed, Math.min(8-speed,1)),
                new Function(FunctionType.X_ACC_LIMIT, speed, Math.min(-1, Math.max(-speed,-2))),
                new Function(FunctionType.Y_ACC_LIMIT, speed, 1),
                new Function(FunctionType.Y_ACC_LIMIT, speed, -1)
        );

        double shortestDistance = -1;
        Point closestPoint = new Point();

        for (Function function : limits) {
            Point point = function.closestPoint(desiredVector.getFreeForm());
            double distance = desiredVector.getFreeForm().distanceToPoint(point);

            boolean isCloser = distance < shortestDistance;
            boolean withinLimits = true;
            for (Function testFunction : limits) {
                withinLimits = withinLimits && testFunction.withinFunction(point);
            }

            if (withinLimits && (isCloser || shortestDistance == -1)) {
                shortestDistance = distance;
                closestPoint = point;
            }
        }
        return new Vector(closestPoint);
    }
}