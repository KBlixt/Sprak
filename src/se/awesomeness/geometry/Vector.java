package se.awesomeness.geometry;

import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Vector {

    private double magnitude;
    private double direction;


    public Vector(double magnitude, double direction) {
        set(magnitude, direction);
    }

    public Vector(Point point){
        set(point);
    }

    public Vector(Vector vector){
        set(vector);
    }

    public Vector(){
        direction = 0;
        magnitude = 0;
    }


    public Vector set(double magnitude, double direction) {
        this.magnitude = magnitude;
        this.direction = Tools.shortestAngle(direction);
        return this;
    }

    public Vector set(Point point) {
        double x = point.getX();
        double y = point.getY();

        magnitude = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        if (magnitude != 0) {
            direction = Tools.shortestAngle(Math.toDegrees(Math.atan2(y,x)));
        }else{
            direction = 0;
        }
        return this;
    }

    public Vector set(Vector vector){
        magnitude = vector.magnitude;
        direction = vector.direction;
        return this;
    }

    public Vector setMagnitude(double magnitude){
        this.magnitude = magnitude;
        return this;
    }

    public Vector setDirection(double direction){
        this.direction = Tools.shortestAngle(direction);
        return this;
    }

    public Vector setX(double x){
        Point point = toPoint();
        point.setX(x);
        set(point);
        return this;
    }

    public Vector setY(double y){
        Point newFreeform = toPoint();
        newFreeform.setY(y);
        set(newFreeform);
        return this;
    }


    public double getMagnitude() {
        return magnitude;
    }

    public double getDirection() {
        return direction;
    }

    public double getX(){
        return magnitude * Math.cos(Math.toRadians(direction));
    }

    public double getY(){
        return magnitude * Math.sin(Math.toRadians(direction));
    }

    public Point toPoint() {
        return new Point(getX(),getY());
    }


    public Vector add(Vector vector){
        Point vectorPoint = toPoint();
        Point vectorAddPoint = vector.toPoint();

        return new Vector( new Point(
                vectorPoint.getX() + vectorAddPoint.getX(),
                vectorPoint.getY() + vectorAddPoint.getY()));
    }

    public Vector subtract(Vector vector){
        return add(vector.negative());
    }

    public Vector negative(){
        return new Vector(magnitude, Tools.shortestAngle(direction-180));
    }

    public Vector multiply(double factor){
        return new Vector(magnitude*factor, direction);
    }

    public Vector divide(double denominator){
        if(denominator == 0){
            denominator = 1/Double.MAX_VALUE;
        }
        return new Vector(magnitude/denominator, direction);
    }

    public Vector addMagnitude(double magnitude){
        return new Vector(this.magnitude + magnitude, direction);
    }

    public Vector addDirection(double direction){
        return new Vector(magnitude, this.direction + direction);
    }

    public Vector subtractMagnitude(double magnitude){
        return addMagnitude(-magnitude);
    }

    public Vector subtractDirection(double direction){
        return addDirection(-direction);
    }


    public double angleToPoint(Point point){
        Vector toPointVector = new Vector(point);
        return angleToVector(toPointVector);
    }

    public double angleToVector(Vector vector){
        double angle = vector.getDirection() - direction;
        return Tools.shortestAngle(angle);
    }

    public String toString(){
        return "[vector]: (Magnitude: " + getMagnitude() + " , Direction: " + getDirection() + ")\n";
    }


    public static Vector addAll(List<Vector> vectors){
        Vector sumVector = new Vector();
        for (Vector vector : vectors) {
            sumVector = sumVector.add(vector);
        }
        return sumVector;
    }
}