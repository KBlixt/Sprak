package se.awesomeness;

import robocode.Robot;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mover {

    Map<String, double[]> enemyPositions;
    Spark spark;

    public Mover(Spark spark){
        enemyPositions = new HashMap<>();
        this.spark = spark;
    }
    public void updateEnemyPosition(ScannedRobotEvent robot){
        double X =  spark.getX() + robot.getDistance() * Math.sin(Math.toRadians(spark.getHeading() + robot.getBearing()));
        double Y =  spark.getY() + robot.getDistance() * Math.cos(Math.toRadians(spark.getHeading() + robot.getBearing()));
        enemyPositions.put(robot.getName(), new double[]{X, Y});
        System.out.println( X + " " + Y);
    }

    public void moveToClosestRobot(double stopEarlyOffset){
        String closestRobotName = "";
        double shortestDistance = 0;

        for (Map.Entry<String, double[]> robotPosition : enemyPositions.entrySet()) {

            double robotDistance = distanceToPoint(robotPosition.getValue()[0], robotPosition.getValue()[1]);
            String robotName = robotPosition.getKey();

            if (closestRobotName.equals("")){
                closestRobotName = robotName;
                shortestDistance = robotDistance;
                continue;
            }

            if (robotDistance < shortestDistance){
                shortestDistance = robotDistance;
                closestRobotName = robotName;
            }
        }
        double[] target = enemyPositions.get(closestRobotName);


        spark.turnRight(getAngleToPoint(target[0], target[1]));
        spark.ahead(shortestDistance - stopEarlyOffset -1);
    }

    /**returns an angle to the closest wall, need some information about the robot and walls.*/
    public void turnTowardsClosestWall(){
        ArrayList<double[]> wallPoints = generateWallPoints();
        double angleToWall = 1;
        switch (closestPoint(wallPoints)){
            case 0 -> angleToWall = 180;
            case 1 -> angleToWall = 0;
            case 2 -> angleToWall = -90;
            case 3 -> angleToWall = 90;
        }
        angleToWall = angleToWall - spark.status.getHeading();
        if (angleToWall > 180){
            angleToWall = angleToWall - 360;
        } else if (angleToWall < -180){
            angleToWall = angleToWall + 360;
        }
        spark.turnRight(angleToWall);
    }

    public void moveToClosestWall(){
        turnTowardsClosestWall();

        ArrayList<double[]> wallPoints = generateWallPoints();

        double[] closestPoint = wallPoints.get(closestPoint(wallPoints));

        spark.ahead(distanceToPoint(closestPoint[0], closestPoint[1]) - 50);
    }

    private double distanceToPoint(double toX, double toY){
        return Math.sqrt(Math.pow(toX - spark.status.getX(),2) + Math.pow(toY - spark.status.getY(),2));
    }

    private int closestPoint(List<double[]> points){
        int closestPointIndex = 0;

        double shortestDistance = Math.sqrt(Math.pow(
                points.get(closestPointIndex)[0] - spark.status.getX(),2)
                + Math.pow(points.get(closestPointIndex)[1] - spark.status.getY(),2));

        double distance;
        for ( int i = 1; i < points.size(); i++){

            distance = Math.sqrt(Math.pow(
                    points.get(i)[0] - spark.status.getX(),2)
                    + Math.pow(points.get(i)[1] - spark.status.getY(),2));

            if (distance < shortestDistance){
                closestPointIndex = i;
                shortestDistance = distance;
            }
        }
        return closestPointIndex;
    }

    private ArrayList<double[]> generateWallPoints() {
        ArrayList<double[]> wallPoints = new ArrayList<>();
        wallPoints.add(new double[]{spark.status.getX(), 0}); // nordliga väggen
        wallPoints.add(new double[]{spark.status.getX(), spark.getBattleFieldHeight()}); // sydliga väggen
        wallPoints.add(new double[]{0, spark.status.getY()}); // västra väggen
        wallPoints.add(new double[]{spark.getBattleFieldWidth(), spark.status.getY()}); // östra väggen
        return wallPoints;
    }

    private double getAngleToPoint(double toX, double toY){
        double angleToPoint = Math.atan2(
                toY - spark.getY(),
                toX - spark.getX());

        angleToPoint = Math.toDegrees(-angleToPoint) + 90;
        if (angleToPoint > 180){
            angleToPoint = angleToPoint - 360;
        }
        System.out.println(angleToPoint);
        return angleToPoint;
    }

}
