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
    private boolean targetLocked;

    private Point position;
    private Vector velocity;
    private Vector acceleration;

    private Point averagePosition;
    private Vector averagePositionVelocity;
    private Vector averagePositionAcceleration;

    private double threatDistance;
    private double threatDistanceSpeed;
    private double threatInfoAge;

    private final ArrayList<Point> estimatedPositions = new ArrayList<>();
    private final ArrayList<Vector> estimatedVelocities = new ArrayList<>();
    private final ArrayList<Point>  pastPositions= new ArrayList<>();
    private final ArrayList<Vector>  pastVelocities= new ArrayList<>();
    private final double maxX;
    private final double maxY;
    private final double minX;
    private final double minY;





    public EnemyRobot(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading, Point minPoint, Point maxPoint){
        minX = minPoint.getX()+10;
        minY = minPoint.getY()+10;
        maxX = maxPoint.getX()-10;
        maxY = maxPoint.getY()-10;
        name = scannedRobot.getName();
        threatDistance = -1;

        energy = scannedRobot.getEnergy();
        position = sparkPosition.addVector(
                new Vector(
                        scannedRobot.getDistance(),
                        sparkHeading-scannedRobot.getBearing()
                )
        );
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));
        if (velocity.getMagnitude() < 0){
            velocity.negative();
        }
        acceleration = new Vector();
        estimatedPositions.add(position);
        estimatedVelocities.add(velocity);

        averagePosition = new Point(position);
        averagePositionVelocity = new Vector();
        averagePositionAcceleration = new Vector();

        infoAge = 0;
    }


    public void updateIntel(ScannedRobotEvent scannedRobot, Point sparkPosition, double sparkHeading){
        estimatedPositions.clear();
        estimatedVelocities.clear();
        energy = scannedRobot.getEnergy();
        Point lastPosition = position;
        position = sparkPosition.addVector(
                new Vector(
                        scannedRobot.getDistance(),
                        sparkHeading-scannedRobot.getBearing()
                )
        );
        Vector oldVelocity = velocity;
        velocity = new Vector(scannedRobot.getVelocity(), Tools.convertAngle(scannedRobot.getHeading()));
        if (velocity.getMagnitude() < 0){
            velocity.negative();
        }
        acceleration = velocity.subtract(oldVelocity).divide(infoAge);

        estimatedPositions.add(position);
        estimatedVelocities.add(velocity);

        Vector addVector = lastPosition.vectorTo(position);
        for(int i = 0; i<=infoAge-1;i++){
            pastPositions.add(lastPosition.addVector(addVector.multiply((double)i/infoAge)));
        }
        for(int i = 0; i<=infoAge-1;i++){
            pastVelocities.add(oldVelocity.add(acceleration.multiply((double)i/infoAge)));
        }

        Vector sumVectorPositions = new Vector();
        int positionsToAverageOver = 110;
        for (int i = Math.max(pastPositions.size()-positionsToAverageOver, 0); i < pastPositions.size(); i++){
            sumVectorPositions = sumVectorPositions.add(new Vector(pastPositions.get(i)));
        }
        Point newAvgPos = sumVectorPositions.divide(positionsToAverageOver).getPoint();
        Vector newAvgPosSpeed = averagePosition.vectorTo(newAvgPos).divide(infoAge);
        averagePositionAcceleration = newAvgPosSpeed.subtract(averagePositionVelocity).divide(infoAge);
        averagePositionVelocity = newAvgPosSpeed;
        averagePosition = newAvgPos;

        targetLocked = infoAge <= 2;
        infoAge = 0;
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

    public int getClosestMatchingState(){
        int closestPointIndex = 0;
        double closestDistance = 50;
        for (int i = Math.max(pastVelocities.size()-140,0); i < pastVelocities.size()-30; i++){
            if (pastVelocities.get(i).subtract(velocity).getMagnitude() < 1.5){
                double distance = position.distanceTo(pastPositions.get(i));
                if ( distance < closestDistance){
                    closestPointIndex =i;
                    closestDistance = distance;
                }
            }
        }
        return closestPointIndex - pastVelocities.size();

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

    public boolean isTargetLocked(){
        return targetLocked && infoAge <= 1;
    }

    public Point estimatedPosition(int time){
        //if time+infoAge<
        if (time+infoAge<0){
            return pastPositions.get(pastPositions.size()+(time+infoAge));
        }
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
        if (time+infoAge<0){
            return pastVelocities.get(pastPositions.size()+(time+infoAge));
        }
        if (estimatedVelocities.size() <= time+infoAge){
            Vector newEstimatedVelocity = estimatedVelocity(time-1).add(acceleration);
            if (newEstimatedVelocity.getMagnitude() > 8){
                newEstimatedVelocity.setMagnitude(8);
            }
            estimatedVelocities.add(newEstimatedVelocity);
        }
        return estimatedVelocities.get(time+infoAge);
    }

    public boolean isStandingStill(){
        return pastPositions.size() > 50 && averagePositionVelocity.getMagnitude() < 1 && averagePositionAcceleration.getMagnitude() < 0.5;
               //&& (estimatedPosition(0).distanceTo(averagePosition) < averagePositionAverageDistance ||
                //Math.sin(Math.toRadians(acceleration.angleToVector(estimatedPosition(0).vectorTo(averagePosition)))) < 90);
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