package se.awesomeness.geometry;

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
        x = point.x;
        y = point.y;
    }

    public void setPoint(Vector vector){
        setPoint(vector.getPoint());
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public Point addVector(Vector vector){
        return new Point(
                getX() + vector.getPoint().getX(),
                getY() + vector.getPoint().getY());
    }

    public Point subtractVector(Vector vector){
        return addVector(vector.negative());
    }

    public Point closestPoint(List<Point> points){
        Point closestPoint = points.get(0);
        double shortestDistance = distanceTo(closestPoint);

        for ( int i = 1; i < points.size(); i++){
            Point candidate = points.get(i);
            double distance = distanceTo(candidate);

            if (distance < shortestDistance){
                closestPoint = candidate;
                shortestDistance = distance;
            }
        }
        return closestPoint;
    }

    public Point furthestPoint(List<Point> points){
        Point furthestPoint = points.get(0);
        double longestDistance = distanceTo(furthestPoint);

        for ( int i = 1; i < points.size(); i++){
            Point candidate = points.get(i);
            double distance = distanceTo(candidate);

            if (distance > longestDistance){
                furthestPoint = candidate;
                longestDistance = distance;
            }
        }
        return furthestPoint;
    }

    public double distanceTo(Point toPoint){
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