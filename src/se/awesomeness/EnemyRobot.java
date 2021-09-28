package se.awesomeness;

import robocode.ScannedRobotEvent;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Tools;
import se.awesomeness.geometry.Vector;

import java.util.ArrayList;
import java.util.Map;

public class EnemyRobot {

    private final String name;
    private double energy;
    private int infoAge;

    private Point position;
    private Vector velocity;
    private Vector acceleration;

    private double threatDistance;
    private double threatDistanceSpeed;
    private double threatInfoAge;

    private final ArrayList<Point> estimatedPositions = new ArrayList<>();
    private final ArrayList<Vector> estimatedVelocities = new ArrayList<>();
    private final double maxX;
    private final double maxY;
    private final double minX;
    private final double minY;





    public EnemyRobot(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading, Point minPoint, Point maxPoint){
        name = scannedRobot.getName();
        threatDistance = -1;
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));
        minX = minPoint.getX();
        minY = minPoint.getY();
        maxX = maxPoint.getX();
        maxY = maxPoint.getY();
        updateData(scannedRobot, sparkPosition, sparkHeading);
    }


    public void updateData(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){
        estimatedPositions.clear();
        estimatedVelocities.clear();
        energy = scannedRobot.getEnergy();
        position = sparkPosition.addVector(
                new Vector(
                        scannedRobot.getDistance(),
                        sparkHeading-scannedRobot.getBearing()
                )
        );
        Vector oldVelocity = velocity;
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));
        acceleration = velocity.subtract(oldVelocity).divide(infoAge);

        infoAge = 0;
        estimatedPositions.add(position);
        estimatedVelocities.add(velocity);

    }

    public void updateAge(){
        infoAge++;
        threatInfoAge++;
    }

    public void updateThreatDistance(Map<String, EnemyRobot> enemyRobots) {
        double shortestDistance = 600;

        for (Map.Entry<String, EnemyRobot> robotEntry : enemyRobots.entrySet()) {

            if (robotEntry.getValue().getName().equals(name)){
                continue;
            }

            double distance = estimatedPosition(0).distanceTo(robotEntry.getValue().estimatedPosition(0));
            if (distance < shortestDistance){
                shortestDistance = distance;
            }
        }
        if (threatDistance == -1){
            threatDistance = shortestDistance;
            threatInfoAge = 0;
        }
        threatDistanceSpeed = (shortestDistance - threatDistance) / threatInfoAge;
        threatDistance = shortestDistance;
        threatInfoAge = 0;
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

    public long getInfoAge() {
        return infoAge;
    }

    public Point estimatedPosition(int time){
            if (estimatedPositions.size() <= time+infoAge){
                Point newEstimatedPosition = estimatedPosition(time-1).addVector(estimatedVelocity(time-1));
                if (newEstimatedPosition.getX() < minX) newEstimatedPosition.setX(minX);
                if (newEstimatedPosition.getY() < minY) newEstimatedPosition.setY(minY);
                if (newEstimatedPosition.getX() > maxX) newEstimatedPosition.setX(maxX);
                if (newEstimatedPosition.getY() > maxY) newEstimatedPosition.setY(maxY);
                estimatedPositions.add(newEstimatedPosition);
            }
        return estimatedPositions.get(time+infoAge);
    }

    public Vector estimatedVelocity(int time){
        if (estimatedVelocities.size() <= time+infoAge){
            Vector newEstimatedVelocity = estimatedVelocity(time-1).add(acceleration);
            if (newEstimatedVelocity.getMagnitude() > 8){
                newEstimatedVelocity.setMagnitude(8);
            }
            estimatedVelocities.add(newEstimatedVelocity);
        }
        return estimatedVelocities.get(time+infoAge);
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