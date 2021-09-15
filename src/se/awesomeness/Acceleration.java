package se.awesomeness;

public class Acceleration {
    double direction;
    double magnitude;

    public Acceleration(double direction, double magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }

    public static double[] getAccelerationVector(Acceleration acceleration){
        double x = acceleration.magnitude * Math.cos(acceleration.direction);
        double y = acceleration.magnitude * Math.sin(acceleration.direction);
        return new double[]{x,y};

    }

    public static Acceleration getAccelerationWithVector(double x, double y){
        double magnitude = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double angle = Math.asin(y/magnitude);
        return new Acceleration(angle, magnitude);

    }
}
