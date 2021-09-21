package se.awesomeness;

import java.util.List;

public class Vector {

    private double magnitude;
    private double direction;


    public Vector(double magnitude, double direction) {
        setVector(magnitude, direction);
    }

    public Vector(Point freeForm){
        setVector(freeForm);
    }

    public Vector(Vector vector){
        setVector(vector);
    }

    public Vector(){
        direction = 0;
        magnitude = 0;
    }


    public void setVector(double magnitude, double direction) {
        this.magnitude = magnitude;
        this.direction = Tools.shortestAngle(direction);
    }

    public void setVector(Point freeForm) {
        double x = freeForm.getX();
        double y = freeForm.getY();

        magnitude = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        if (magnitude != 0) {
            direction = Tools.shortestAngle(Math.toDegrees(Math.atan2(y,x)));
        }else{
            direction = 0;
        }
    }

    public void setVector(Vector vector){
        setVector(
                vector.getMagnitude(),
                vector.getDirection());
    }


    public double getMagnitude() {
        return magnitude;
    }

    public double getDirection() {
        return direction;
    }

    public Point getFreeForm() {
        double x = magnitude * Math.cos(Math.toRadians(direction));
        double y = magnitude * Math.sin(Math.toRadians(direction));
        return new Point(x, y);
    }


    public Vector add(Vector vectorToAdd){
        Point freeForm = getFreeForm();
        Point vectorToAddFreeForm = vectorToAdd.getFreeForm();

        return new Vector( new Point(
                freeForm.getX() + vectorToAddFreeForm.getX(),
                freeForm.getY() + vectorToAddFreeForm.getY()));
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
            denominator = 0.000001;
        }
        return new Vector(magnitude/denominator, direction);
    }

    public double angleToPoint(Point toPoint){
        Point freeForm = getFreeForm();
        double absoluteAngle = Math.atan2(
                toPoint.getY() - freeForm.getY(),
                toPoint.getX() - freeForm.getX());

        absoluteAngle = Math.toDegrees(absoluteAngle);

        double angleToPoint = direction + absoluteAngle;

        return Tools.shortestAngle(angleToPoint);
    }

    public String toString(){
        return "[vector]: (Magnitude: " + getMagnitude() + " , Direction: " + getDirection() + ")";
    }

    public static Vector addAll(List<Vector> vectors){
        Vector sumVector = new Vector();
        for (Vector vector : vectors) {
            sumVector = sumVector.add(vector);
        }
        return sumVector;
    }
}