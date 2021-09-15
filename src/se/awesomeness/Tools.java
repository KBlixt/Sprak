package se.awesomeness;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    /**returns an angle to the closest wall, need some information about the robot and walls.*/
    public static double angleToWall(double robotX, double robotY, double robotHeading, double wallWidth, double wallHeight){
        ArrayList<double[]> wallPoints = new ArrayList<>();
        wallPoints.add(new double[]{robotX, 0}); // nordliga väggen
        wallPoints.add(new double[]{robotX, wallHeight}); // sydliga väggen
        wallPoints.add(new double[]{0, robotY}); // västra väggen
        wallPoints.add(new double[]{wallWidth, robotY}); // östra väggen

        double angleToWall = 1;


        switch (closestPoint(robotX, robotY, wallPoints)){
            case 0 -> angleToWall = 180;
            case 1 -> angleToWall = 0;
            case 2 -> angleToWall = -90;
            case 3 -> angleToWall = 90;

        }
        angleToWall = angleToWall - robotHeading;
        if (angleToWall > 180){
            angleToWall = angleToWall - 360;
        } else if (angleToWall < -180){
            angleToWall = angleToWall + 360;
        }
        return angleToWall;

    }

    public static double distanceToClosestWall(double robotX, double robotY, double wallWidth, double wallHeight){
        ArrayList<double[]> wallPoints = new ArrayList<>();
        wallPoints.add(new double[]{robotX, 0}); // nordliga väggen
        wallPoints.add(new double[]{robotX, wallHeight}); // sydliga väggen
        wallPoints.add(new double[]{0, robotY}); // västra väggen
        wallPoints.add(new double[]{wallWidth, robotY}); // östra väggen

        double[] closestPoint = wallPoints.get(closestPoint(robotX, robotY, wallPoints));

        return distanceToPoint(robotX,robotY,closestPoint[0], closestPoint[1]);
    }
    public static double distanceToPoint(double fromX, double fromY, double toX, double toY){
        return Math.sqrt(Math.pow(toX - fromX,2) + Math.pow(toY - fromY,2));
    }

    /** returnerar den närmsta punkten mellan en given punkt och några kandidater.
      * @param fromX den givna punktens X värde.
     * @param fromY den givna punktens Y värde.
     * @param points koordinater i formen double[]{X,Y}.
     * @return indexet för points-listans närmsta koordinat till den givna punkten
     * */
    public static int closestPoint(double fromX, double fromY, List<double[]> points){
        int closestPointIndex = 0;

        double shortestDistance = Math.sqrt(Math.pow(
                points.get(closestPointIndex)[0] - fromX,2)
                + Math.pow(points.get(closestPointIndex)[1] - fromY,2));

        double distance;
        for ( int i = 1; i < points.size(); i++){

            distance = Math.sqrt(Math.pow(
                    points.get(i)[0] - fromX,2)
                    + Math.pow(points.get(i)[1] - fromY,2));

            if (distance < shortestDistance){
                closestPointIndex = i;
                shortestDistance = distance;
            }
        }
        return closestPointIndex;
    }
}
