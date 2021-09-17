package se.awesomeness;

public class Vector2D {
    private double direction;
    private double magnitude;
    private Point normalForm;

    public Vector2D(double magnitude, double direction) {
        setVector(magnitude, direction);
    }

    public Vector2D(Point normalForm){
        setVectorFromPoint(normalForm);
    }


    public double getDirection() {
        return direction;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public Point getNormalForm() {
        return normalForm;
    }


    public void setVector(double magnitude, double direction) {
        this.direction = direction;
        this.magnitude = magnitude;
        normalForm = new Point(magnitude * Math.cos(Math.toRadians(direction)), magnitude * Math.sin(Math.toRadians(direction)));
    }

    public void setVectorFromPoint(Point normalForm) {
        this.normalForm = normalForm;
        magnitude = Math.sqrt(Math.pow(normalForm.x,2) + Math.pow(normalForm.x,2));
        direction = Math.asin(normalForm.y/magnitude);
    }


    public void addVector(Vector2D vectorToAdd){
        setVectorFromPoint(new Point(
                normalForm.x + vectorToAdd.getNormalForm().x,
                normalForm.y + vectorToAdd.getNormalForm().y));
    }

    public void subtractVector(Vector2D VectorToSubtract){
        setVectorFromPoint(new Point(
                normalForm.x - VectorToSubtract.getNormalForm().x,
                normalForm.y - VectorToSubtract.getNormalForm().y));
    }


    public double getAngleToPoint(Point toPoint){
        double absoluteAngle = Math.atan2(
                toPoint.y - normalForm.y,
                toPoint.x - normalForm.x);

        absoluteAngle = Math.toDegrees(-absoluteAngle) + 90;

        double angleToPoint = direction + absoluteAngle;

        return Algebra.shortestAngle(angleToPoint);
    }
}
