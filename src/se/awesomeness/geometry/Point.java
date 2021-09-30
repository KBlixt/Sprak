package se.awesomeness.geometry;

import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Point {

    private double x;
    private double y;


    public Point(double x, double y){
        set(x,y);
    }

    public Point(Point point){
        set(point);
    }

    public Point(Vector vector){
        set(vector);
    }

    public Point(){
        x = 0;
        y = 0;
    }


    public Point set(double x, double y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Point set(Point point){
        x = point.x;
        y = point.y;
        return this;
    }

    public Point set(Vector vector){
        return set(vector.toPoint());
    }

    public Point setX(double x){
        this.x = x;
        return this;
    }

    public Point setY(double y){
        this.y = y;
        return this;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector toVector(){
        return new Vector(this);
    }


    public Point addX(double x){
        return new Point(this.x + x, y);
    }
    public Point addY(double y){
        return new Point(x, this.y + y);
    }
    public Point subtractX(double x){
        return addX(-x);
    }
    public Point subtractY(double y){
        return addY(-y);
    }


    public Point addVector(Vector vector){
        return new Point(
                getX() + vector.toPoint().getX(),
                getY() + vector.toPoint().getY());
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
        return "[point]: (X: " + getX() + " , Y: " + getY() + ")\n";
    }
}