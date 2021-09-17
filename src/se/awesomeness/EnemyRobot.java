package se.awesomeness;

import robocode.ScannedRobotEvent;

import java.util.Map;

public class EnemyRobot {

    private final String name;
    private double energy;
    private long lastUpdateTime;

    private Point position;
    private Vector2D velocity;
    private Vector2D acceleration;

    private double threatDistance;
    private double threatDistanceSpeed;
    private double threatLastUpdateTime;


    public EnemyRobot(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){
        name = scannedRobot.getName();
        threatDistance = -1;
        velocity = new Vector2D(scannedRobot.getVelocity(), scannedRobot.getHeading());
        updateData(scannedRobot, sparkPosition, sparkHeading);
    }


    public void updateData(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){

        energy = scannedRobot.getEnergy();
        position = sparkPosition.addVector(
                new Vector2D(
                        scannedRobot.getDistance(),
                        sparkHeading+scannedRobot.getBearing()
                )
        );
        Vector2D oldVelocity = velocity;
        velocity = new Vector2D(scannedRobot.getVelocity(), scannedRobot.getHeading());

        long timeDelta = scannedRobot.getTime() - lastUpdateTime;
        acceleration = velocity.subtractVector(oldVelocity).divide(timeDelta);

        lastUpdateTime = scannedRobot.getTime();

    }

    public void updateThreatDistance(Map<String, EnemyRobot> enemyRobots,long time) {
        double shortestDistance = 600;

        for (Map.Entry<String, EnemyRobot> robotEntry : enemyRobots.entrySet()) {

            if (robotEntry.getValue().getName().equals(name)){
                continue;
            }

            double distance = estimatedPosition(time).distanceToPoint(robotEntry.getValue().estimatedPosition(time));
            if (distance < shortestDistance){
                shortestDistance = distance;
            }
        }
        if (threatDistance == -1){
            threatDistance = shortestDistance;
            threatLastUpdateTime = time + 1;
        }
        threatDistanceSpeed = (shortestDistance - threatDistance) / (time - threatLastUpdateTime);
        threatDistance = shortestDistance;
        threatLastUpdateTime = time;

    }


    public String getName() {
        return name;
    }

    public double getEnergy(){
        return energy;
    }

    public double getThreatDistance() {
        return threatDistance;
    }

    public double getThreatDistanceSpeed() {
        return threatDistanceSpeed;
    }


    public Point estimatedPosition(long time){
            return new Point(
                    position.addVector(velocity.multiply(time - lastUpdateTime))
            );
            // todo: adjust for acceleration?

    }

    public Vector2D estimatedVelocity(long time){
        Vector2D estimatedVelocity = new Vector2D(
                velocity.addVector(acceleration.multiply(time - lastUpdateTime))
        );
        estimatedVelocity.setVector(Math.max(8,estimatedVelocity.getMagnitude()), estimatedVelocity.getDirection());
        return estimatedVelocity;
    }

}
