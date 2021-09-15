package se.awesomeness;

import robocode.RobotStatus;
import robocode.ScannedRobotEvent;

import java.util.Map;
import java.util.Vector;

public class ThreatBasedMovement {
    Map<String, EnemyRobot> enemyRobots;
    RobotStatus status;

    public ThreatBasedMovement(Map<String, EnemyRobot> enemyRobots, RobotStatus status) {
        this.enemyRobots = enemyRobots;
        this.status = status;
    }

    public void UpdateThreats(Vector<ScannedRobotEvent> scannedRobotEvents){
        for (ScannedRobotEvent scan : scannedRobotEvents) {
            EnemyRobot enemyRobot = enemyRobots.get(scan.getName());

            double threatRadius = 0;
            for (Map.Entry<String, EnemyRobot> entry2 : enemyRobots.entrySet()) {
                if (scan.getName().equals(entry2.getValue().name)) continue;

                double distance = getDistanceBetweenPoints(
                        enemyRobot.getEstimatedPosition(status.getTime()),
                        entry2.getValue().getEstimatedPosition(status.getTime()));

                if (distance < threatRadius || distance <= 0) {
                    threatRadius = distance;
                }
            }

            double newThreatPointDistance = getDistanceBetweenPoints(
                    new Point(status.getX(), status.getY()),
                    enemyRobot.getEstimatedPosition(status.getTime()))
                    - threatRadius;

            ThreatInfo info = enemyRobot.getThreatInfo();
            if (enemyRobot.LastUpdate < 12) {
                info.threatPointSpeed = (
                        newThreatPointDistance - info.threatPointDistance)
                        / (enemyRobot.LastUpdate - status.getTime());
            }
            info.threatPointDistance = newThreatPointDistance;

        }
    }

    public double getDistanceBetweenPoints(Point fromPoint, Point toPoint){
        return Math.sqrt(Math.pow((toPoint.x- fromPoint.x),2) + Math.pow((toPoint.y- fromPoint.y),2));
    }

    public Acceleration getDesiredAcceleration(){

    }

    public long fastestThreatEscape(EnemyRobot enemyRobot){
        double tmp = enemyRobot.threatInfo.threatPointDistance / enemyRobot.threatInfo.threatPointSpeed;
    }

    public double getLargestAllowedAcceleration(Acceleration){

    }

}
