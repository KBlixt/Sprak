package se.awesomeness.geometry;

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
        magnitude = vector.magnitude;
        direction = vector.direction;
    }

    public void setMagnitude(double magnitude){
        this.magnitude = magnitude;
    }

    public void setDirection(double direction){
        this.direction = Tools.shortestAngle(direction);
    }

    public void setX(double x){
        Point newFreeform = getPoint();
        newFreeform.setX(x);
        setVector(newFreeform);
    }

    public void setY(double y){
        Point newFreeform = getPoint();
        newFreeform.setY(y);
        setVector(newFreeform);
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

    public Point getPoint() {
        return new Point(getX(),getY());
    }


    public Vector add(Vector vector){
        Point vectorPoint = getPoint();
        Point vectorAddPoint = vector.getPoint();

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
            denominator = 0.000001;
        }
        return new Vector(magnitude/denominator, direction);
    }

    public double angleToPoint(Point toPoint){
        Vector toPointVector = new Vector(toPoint);
        return angleToVector(toPointVector);
    }

    public double angleToVector(Vector toVector){
        double angle = toVector.getDirection() - direction;
        return Tools.shortestAngle(angle);
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