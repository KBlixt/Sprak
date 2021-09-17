package se.awesomeness;

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
        Vector2D moveVector = new Vector2D();
        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            //todo: more fleshed out algorithm to weigh moveVector.
            Point robotEstimatedPosition = robotEntry.getValue().estimatedPosition(sprak.getTime());

            double distanceFromRobot = robotEstimatedPosition.distanceToPoint(sprak.position);
            double angleFromRobot = new Vector2D().angleToPoint(new Point(
                    sprak.getX()-robotEstimatedPosition.getX(),
                    sprak.getY()-robotEstimatedPosition.getY())
            ); //todo: make into operator? Point.angleToPoint(Point)?

            Vector2D robotForce = new Vector2D(7*Math.sqrt(100/distanceFromRobot), angleFromRobot);
            moveVector = moveVector.addVector(robotForce);
        }
        sprak.setTurnRate(Tools.shortestAngle(moveVector.getDirection()- sprak.velocityVector.getDirection()));
        sprak.setVelocityRate(8);
    }
}
