package se.awesomeness;

import robocode.ScannedRobotEvent;

public class EnemyRobot {

    String name;
    double energy;
    long lastUpdate;

    Point position;
    Vector2D velocity;
    Vector2D acceleration;

    double threatPointDistance;
    double threatPointSpeed;

    public EnemyRobot(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){
        name = scannedRobot.getName();
        velocity = new Vector2D(scannedRobot.getVelocity(), scannedRobot.getHeading());
        threatPointDistance = 0;
        threatPointSpeed = 0;
        updateData(scannedRobot, sparkPosition, sparkHeading);
    }

    public void updateData(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){

        energy = scannedRobot.getEnergy();

        position = sparkPosition.addVector(
                new Vector2D(
                        scannedRobot.getDistance(),
                        sparkHeading-scannedRobot.getBearing()
                )
        );
        Vector2D oldVelocity = velocity;
        velocity = new Vector2D(scannedRobot.getVelocity(), scannedRobot.getHeading());

        long timeDelta = scannedRobot.getTime() - lastUpdate;
        acceleration = velocity.subtractVector(oldVelocity).divide(timeDelta);

        lastUpdate = scannedRobot.getTime();

    }


    public Point estimatedPosition(long time){
            return new Point(
                    position.addVector(velocity.multiply(time - lastUpdate))
            );
            // todo: adjust for acceleration?

    }

    public Vector2D estimatedVelocity(long time){
        Vector2D estimatedVelocity = new Vector2D(
                velocity.addVector(acceleration.multiply(time - lastUpdate))
        );
        estimatedVelocity.setVector(Math.max(8,estimatedVelocity.getMagnitude()), estimatedVelocity.getDirection());
        return estimatedVelocity;
    }
    
    public double getThreatPointDistance() {
        return threatPointDistance;
    }

    public void setThreatPointDistance(double threatPointDistance) {
        this.threatPointDistance = threatPointDistance;
    }

    public double getThreatPointSpeed() {
        return threatPointSpeed;
    }

    public void setThreatPointSpeed(double threatPointSpeed) {
        this.threatPointSpeed = threatPointSpeed;
    }

    public String getName() {
        return name;
    }
}
