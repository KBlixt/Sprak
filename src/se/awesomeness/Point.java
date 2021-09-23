package se.awesomeness;

import java.util.List;

public class Point {

    private double x;
    private double y;


    public Point(double x, double y){
        setPoint(x,y);
    }

    public Point(Point point){
        setPoint(point);
    }

    public Point(Vector vector){
        setPoint(vector);
    }

    public Point(){
        x = 0;
        y = 0;
    }


    public void setPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setPoint(Point point){
        setPoint(point.getX(), point.getY());
    }

    public void setPoint(Vector vector){
        setPoint(vector.getFreeForm());
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public Point addVector(Vector vector){
        return new Point(
                getX() + vector.getFreeForm().getX(),
                getY() + vector.getFreeForm().getY());
    }

    public Point subtractVector(Vector vector){
        return addVector(vector.negative());
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
        double deltaX = x - toPoint.x;
        double deltaY = y - toPoint.y;

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public Vector vectorTo(Point point){
        double deltaX = point.getX() - x;
        double deltaY = point.getY() - y;
        return new Vector(new Point(deltaX,deltaY));
    }

    public String toString(){
        return "[point]: (X: " + getX() + " , Y: " + getY() + ")";
    }
}