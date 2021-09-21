package se.awesomeness;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mover extends Robot {

    Map<String, double[]> enemyPositions;
    Spark spark;

    /** Creates a mover object that can move Spark.
     * @param spark the Spark to move.
     */
    public Mover(Spark spark){
        enemyPositions = new HashMap<>();
        this.spark = spark;
    }

    /** Moves Spark to the known robot, stops <stopEarlyOffset> pixels early.
     * @param stopEarlyOffset pixels to stop early.
     */
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

        double turnAngle = shortestAngle(spark.getHeading(), getAngleToPoint(target[0], target[1]));

        spark.turnRight(turnAngle);
        spark.ahead(shortestDistance - stopEarlyOffset);
    }

    /** Moves Spark to the closest Wall, stops <stopEarlyOffset> pixels early.
     * @param stopEarlyOffset pixels to stop early.
     */
    public void moveToClosestWall(double stopEarlyOffset){

        ArrayList<double[]> wallPoints = generateWallPoints();
        double[] closestPoint = wallPoints.get(closestPoint(wallPoints));

        spark.turnRight(angleToClosestWall());
        spark.ahead(distanceToPoint(closestPoint[0], closestPoint[1]) - stopEarlyOffset);
    }

    public void moveToMidPointOfQuadrant() {

        double maxX = getBattleFieldWidth();
        double maxY = getBattleFieldHeight();
        double midX = maxX / 2;
        double midY = maxY / 2;

        // Coordinates for each quadrant of the map.
        // (0.25X,0Y)
        double lowerLeftQuadrantSideX = maxX * 0.25;
        double lowerLeftQuadrantSideY = maxY * 0;

        // (0.25X,1Y)
        double upperLeftQuadrantSideX = maxX * 0.25;
        double upperLeftQuadrantSideY = maxY * 1;

        // (0.75X,0Y)
        double lowerRightQuadrantSideX = maxX * 0.75;
        double lowerRightQuadrantSideY = maxY * 0;

        // (0.75X,0Y)
        double upperRightQuadrantSideX = maxX * 0.75;
        double upperRightQuadrantSideY = maxY * 1;



    }

    /** Moves Spark nothing.
     *
     */
    public void doNotMove(){}

    /** updates enemyPosition for the enemy scanned.
     * @param robot ScannedRobotEvent information to update with.
     */
    public void updateEnemyPosition(ScannedRobotEvent robot){

        double angle = Math.toRadians(spark.getHeading() + robot.getBearing());

        double X =  spark.getX() + robot.getDistance() * Math.sin(angle);
        double Y =  spark.getY() + robot.getDistance() * Math.cos(angle);

        enemyPositions.put(robot.getName(), new double[]{X, Y});
    }


    private double angleToClosestWall(){
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
        return angleToWall;
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
      //  wallPoints.add(new double[]{0, spark.status.getY()}); // västra väggen
      //  wallPoints.add(new double[]{spark.getBattleFieldWidth(), spark.status.getY()}); // östra väggen

        return wallPoints;
    }
    private double getAngleToPoint(double toX, double toY) {
        double angleToPoint = Math.atan2(
                toY - spark.getY(),
                toX - spark.getX()
        );

        angleToPoint = Math.toDegrees(-angleToPoint) + 90;
        if (angleToPoint > 180) {
            angleToPoint = angleToPoint - 360;
        }
        return angleToPoint;
    }
    private double shortestAngle(double fromAngle, double toAngle){
        double angleDelta = (toAngle - fromAngle) %360;
        if (angleDelta < -180){
            return angleDelta + 360;
        }else if ( angleDelta > 180){
            return angleDelta - 360 ;
        }else{
            return angleDelta;
        }
    }

}
