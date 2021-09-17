package se.awesomeness;

import java.util.List;

public class Point {
    double x;
    double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }


    public void addVector(Vector2D vector){
        x = x + vector.getNormalForm().x;
        y = y + vector.getNormalForm().y;
    }

    public void subtractVector(Vector2D vector){
        x = x - vector.getNormalForm().x;
        y = y - vector.getNormalForm().y;
    }


    public Point closestPoint(List<Point> points){
        int closestPointIndex = 0;

        double shortestDistance = Math.sqrt(Math.pow(
                points.get(closestPointIndex).x - x,2)
                + Math.pow(points.get(closestPointIndex).y - y,2));

        double distance;
        for ( int i = 1; i < points.size(); i++){

            distance = Math.sqrt(Math.pow(
                    points.get(i).x - x,2)
                    + Math.pow(points.get(i).y - y,2));

            if (distance < shortestDistance){
                closestPointIndex = i;
                shortestDistance = distance;
            }
        }
        return points.get(closestPointIndex);
    }
    public double distanceToPoint(Point toPoint){
        double deltaX = Math.abs(x - toPoint.x);
        double deltaY = Math.abs(y - toPoint.y);

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }



}
