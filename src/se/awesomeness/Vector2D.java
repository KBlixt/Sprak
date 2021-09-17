package se.awesomeness;

public class Vector2D {

    private double magnitude;
    private double direction;


    public Vector2D(double magnitude, double direction) {
        setVector(magnitude, direction);
    }

    public Vector2D(Point freeForm){
        setVector(freeForm);
    }

    public Vector2D(Vector2D vector){
        setVector(vector);
    }

    public Vector2D(){
        direction = 0;
        magnitude = 0;
    }


    public void setVector(double magnitude, double direction) {
        this.magnitude = magnitude;
        this.direction = Tools.shortestAngle(direction);
    }

    public void setVector(Point freeForm) {
        magnitude = Math.sqrt(Math.pow(freeForm.getX(),2) + Math.pow(freeForm.getY(),2));
        if (magnitude != 0) {
            direction = Tools.shortestAngle(Math.toDegrees(Math.asin(freeForm.getX() / magnitude)));
        }else{
            direction = 0;
        }
    }
    
    public void setVector(Vector2D vector){
        setVector(
                vector.getDirection(),
                vector.getMagnitude());
    }
    

    public double getDirection() {
        return direction;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public Point getFreeForm() {
        return new Point(
                magnitude * Math.sin(Math.toRadians(direction)),
                magnitude * Math.cos(Math.toRadians(direction)));
    }
    

    public Vector2D addVector(Vector2D vectorToAdd){
        Point freeForm = getFreeForm();
        return new Vector2D( new Point(
                freeForm.getX() + vectorToAdd.getFreeForm().getX(),
                freeForm.getY() + vectorToAdd.getFreeForm().getY()));
    }

    public Vector2D subtractVector(Vector2D vector){
        return addVector(vector.negate());
    }

    public Vector2D negate(){
        return new Vector2D(magnitude, -direction);
    }

    public Vector2D multiply(double factor){
        return new Vector2D(magnitude*factor, direction);
    }

    public Vector2D divide(double denominator){
        if(denominator == 0){
            denominator = 0.000001;
        }
        return new Vector2D(magnitude/denominator, direction);
    }
    
    public double angleToPoint(Point toPoint){
        Point freeForm = getFreeForm();
        double absoluteAngle = Math.atan2(
                toPoint.getY() - freeForm.getY(),
                toPoint.getX() - freeForm.getX());

        absoluteAngle = Math.toDegrees(-absoluteAngle) + 90;

        double angleToPoint = direction + absoluteAngle;

        return Tools.shortestAngle(angleToPoint);
    }

    public static Vector2D addAll(Vector2D[] vectors){
        Vector2D sumVector = new Vector2D();
        for (Vector2D vector : vectors) {
            sumVector = sumVector.addVector(vector);
        }
        return sumVector;
    }

    public String toString(){
        return "[vector]: (Magnitude: " + getMagnitude() + " , Direction: " + getDirection() + ")";
    }

}
