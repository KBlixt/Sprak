package se.awesomeness;

public class Vector2D {
    double direction;
    double magnitude;

    public Vector2D(double direction, double magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }

    public static double[] getVectorCoordinates(Vector2D vector2D){
        double x = vector2D.magnitude * Math.cos(vector2D.direction);
        double y = vector2D.magnitude * Math.sin(vector2D.direction);
        return new double[]{x,y};

    }

    public static Vector2D getVector(double x, double y){
        double magnitude = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double angle = Math.asin(y/magnitude);
        return new Vector2D(angle, magnitude);

    }
}
