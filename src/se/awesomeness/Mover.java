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

    /**
     * Creates a mover object that can move Spark.
     *
     * @param spark the Spark to move.
     */
    public Mover(Spark spark) {
        enemyPositions = new HashMap<>();
        this.spark = spark;
    }

    /**
     * Moves Spark to the closest Wall, stops <stopEarlyOffset> pixels early.
     *
     * @param stopEarlyOffset pixels to stop early.
     */
    public void moveToClosestWall(double stopEarlyOffset) {

        ArrayList<double[]> wallPoints = generateWallPoints();
        double[] closestPoint = wallPoints.get(closestPoint(wallPoints));

        spark.turnRight(angleToClosestWall());
        spark.ahead(distanceToPoint(closestPoint[0], closestPoint[1]) - stopEarlyOffset);
    }

    public void moveToMidPointOfQuadrant() {

        double maxX = spark.getBattleFieldWidth();
        double maxY = spark.getBattleFieldHeight();
        double midX = maxX / 2;
        double midY = maxY / 2;

        // X-coordinates to compare which side of the quadrant Sprak's on.
        double lowX = maxX * 0.25; // LOW X
        double highX = maxX * 0.75; // HIGH X

        // Sparks position.
        double sparkX = spark.getX();
        double sparkY = spark.getY();

        boolean isTopSide = sparkY > midY;
        boolean isRightSide = sparkX > midX;


        // Upper right quadrant.
        if (isTopSide && isRightSide) {
            // IF right side of the quadrant, turn left.
            if (sparkX >= highX) {
                spark.turnLeft(90);
            } else {
                spark.turnRight(90);
            }

            // Lower right quadrant.
        } else if (!isTopSide && isRightSide) {
            if (sparkX >= highX) {
                spark.turnRight(90);
            } else {
                spark.turnLeft(90);
            }

            // Upper left quadrant.
        } else if (isTopSide) {
            if (sparkX <= lowX) {
                spark.turnRight(90);
            } else {
                spark.turnLeft(90);
            }
            // Lower left quadrant.
        } else {
            if (sparkX <= lowX) {
                spark.turnLeft(90);
            } else {
                spark.turnRight(90);
            }
        }

    }

    /**
     * updates enemyPosition for the enemy scanned.
     *
     * @param robot ScannedRobotEvent information to update with.
     */
    public void updateEnemyPosition(ScannedRobotEvent robot) {

        double angle = Math.toRadians(spark.getHeading() + robot.getBearing());

        double X = spark.getX() + robot.getDistance() * Math.sin(angle);
        double Y = spark.getY() + robot.getDistance() * Math.cos(angle);

        enemyPositions.put(robot.getName(), new double[]{X, Y});
    }


    private double angleToClosestWall() {
        ArrayList<double[]> wallPoints = generateWallPoints();
        double angleToWall = 1;
        switch (closestPoint(wallPoints)) {
            case 0 -> angleToWall = 180;
            case 1 -> angleToWall = 0;
            case 2 -> angleToWall = -90;
            case 3 -> angleToWall = 90;
        }
        angleToWall = angleToWall - spark.status.getHeading();
        if (angleToWall > 180) {
            angleToWall = angleToWall - 360;
        } else if (angleToWall < -180) {
            angleToWall = angleToWall + 360;
        }
        return angleToWall;
    }

    private double distanceToPoint(double toX, double toY) {
        return Math.sqrt(Math.pow(toX - spark.status.getX(), 2) + Math.pow(toY - spark.status.getY(), 2));
    }

    private int closestPoint(List<double[]> points) {
        int closestPointIndex = 0;

        double shortestDistance = Math.sqrt(Math.pow(
                points.get(closestPointIndex)[0] - spark.status.getX(), 2)
                + Math.pow(points.get(closestPointIndex)[1] - spark.status.getY(), 2));

        double distance;
        for (int i = 1; i < points.size(); i++) {

            distance = Math.sqrt(Math.pow(
                    points.get(i)[0] - spark.status.getX(), 2)
                    + Math.pow(points.get(i)[1] - spark.status.getY(), 2));

            if (distance < shortestDistance) {
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

    private double shortestAngle(double fromAngle, double toAngle) {
        double angleDelta = (toAngle - fromAngle) % 360;
        if (angleDelta < -180) {
            return angleDelta + 360;
        } else if (angleDelta > 180) {
            return angleDelta - 360;
        } else {
            return angleDelta;
        }
    }

}
