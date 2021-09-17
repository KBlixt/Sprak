package se.awesomeness;

public class Vector2D {
    private double direction;
    private double magnitude;
    private Point normalForm;

    public Vector2D(double magnitude, double direction) {
        setVector(magnitude, direction);
    }

    public Vector2D(Point normalForm){
        setVector(normalForm);
    }

    public Vector2D(Vector2D vector){
        direction = vector.getDirection();
        magnitude = vector.getMagnitude();
        normalForm = vector.getNormalForm();
    }


    public double getDirection() {
        return direction;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public Point getNormalForm() {
        return new Point(normalForm);
    }


    public void setVector(double magnitude, double direction) {
        this.direction = direction;
        this.magnitude = magnitude;
        normalForm = new Point(
                magnitude * Math.cos(Math.toRadians(direction)),
                magnitude * Math.sin(Math.toRadians(direction)));
    }
    public void setVector(Point normalForm) {
        this.normalForm = new Point(normalForm);
        magnitude = Math.sqrt(Math.pow(normalForm.x,2) + Math.pow(normalForm.x,2));
        if (magnitude != 0) {
            direction = Math.toDegrees(Math.asin(normalForm.y / magnitude));
        }else{
            direction = 0;
        }
    }


    public Vector2D addVector(Vector2D vectorToAdd){
        return new Vector2D( new Point(
                normalForm.x + vectorToAdd.getNormalForm().x,
                normalForm.y + vectorToAdd.getNormalForm().y));
    }

    public Vector2D subtractVector(Vector2D vector){
        return new Vector2D( new Point(
                normalForm.x + vector.getNormalForm().x,
                normalForm.y + vector.getNormalForm().y));
    }

    public Vector2D negativ(){
        return new Vector2D(magnitude, -direction);
    }


    public double getAngleToPoint(Point toPoint){
        double absoluteAngle = Math.atan2(
                toPoint.y - normalForm.y,
                toPoint.x - normalForm.x);

        absoluteAngle = Math.toDegrees(-absoluteAngle) + 90;

        double angleToPoint = direction + absoluteAngle;

        return Algebra.shortestAngle(angleToPoint);
    }

    public static Vector2D add(Vector2D[] vectors){
        Vector2D sumVector = new Vector2D(new Point(0,0));
        for (Vector2D vector : vectors) {
            sumVector.addVector(vector);
        }
        return sumVector;
    }

}
