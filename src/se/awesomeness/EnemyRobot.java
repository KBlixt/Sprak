package se.awesomeness;

import robocode.ScannedRobotEvent;

import java.util.Map;

public class EnemyRobot {

    private final String name;
    private double energy;
    private long infoAge;

    private Point position;
    private Vector velocity;
    private Vector acceleration;

    private double threatDistance;
    private double threatDistanceSpeed;
    private double threatLastUpdateTime;


    public EnemyRobot(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){
        name = scannedRobot.getName();
        threatDistance = -1;
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));
        updateData(scannedRobot, sparkPosition, sparkHeading);
    }


    public void updateData(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){

        energy = scannedRobot.getEnergy();
        position = sparkPosition.addVector(
                new Vector(
                        scannedRobot.getDistance(),
                        sparkHeading-scannedRobot.getBearing()
                )
        );
        Vector oldVelocity = velocity;
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));

        long timeDelta = scannedRobot.getTime() - infoAge;
        acceleration = velocity.subtract(oldVelocity).divide(timeDelta);

        infoAge = 0;

    }

    public void updateAge(){
        infoAge++;
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

    public Point getPosition(){
        return position;
    }


    public Point estimatedPosition(long time){
            return new Point(
                    position.addVector(velocity.multiply(infoAge + time))
            );
            // todo: adjust for acceleration?
    }

    public Vector estimatedVelocity(long time){
        Vector estimatedVelocity = new Vector(
                velocity.add(acceleration.multiply(infoAge + time))
        );
        estimatedVelocity.setVector(Math.max(8,estimatedVelocity.getMagnitude()), estimatedVelocity.getDirection());
        return estimatedVelocity;
    }

    public String toString(){
        String out = "";
        out += "Nme : " + name + "\n";
        out += "Pos : " + position + "\n";
        out += "Vel : " + velocity + "\n";
        out += "Acc : " + acceleration + "\n";
        out += "Upd : " + infoAge + "\n";
        return out;

    }
}