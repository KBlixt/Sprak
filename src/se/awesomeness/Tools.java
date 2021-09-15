package se.awesomeness;

import java.util.ArrayList;
import java.util.List;

public class Tools {

    public static double angleToWall(double fromX, double fromY, double heading, double wallWidth, double wallHeight){
        ArrayList<double[]> wallPoints = new ArrayList<>();
        wallPoints.add(new double[]{fromX, 0}); // nordliga väggen
        wallPoints.add(new double[]{fromX, wallHeight}); // sydliga väggen
        wallPoints.add(new double[]{0, fromY}); // västra väggen
        wallPoints.add(new double[]{wallWidth, fromY}); // östra väggen

        double angleToWall = 1;


        switch (closestPoint(fromX, fromY, wallPoints)){
            case 0 -> angleToWall = 180;
            case 1 -> angleToWall = 0;
            case 2 -> angleToWall = -90;
            case 3 -> angleToWall = 90;

        }
        angleToWall = angleToWall - heading;
        if (angleToWall > 180){
            angleToWall = angleToWall - 360;
        } else if (angleToWall < -180){
            angleToWall = angleToWall + 360;
        }
        return angleToWall;

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
