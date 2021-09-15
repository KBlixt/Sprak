package se.awesomeness;

public class Velocity {
    double direction;
    double speed;
    public Velocity(double angle, double speed){
        this.direction = angle;
        this.speed = speed;
    }

    public static double[] getVelocityVector(Velocity velocity){
        double x = velocity.speed * Math.cos(velocity.direction);
        double y = velocity.speed * Math.sin(velocity.direction);
        return new double[]{x,y};

    }

    public static Velocity getVelocityWithVector(double x, double y){
        double speed = Math.max(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)),8);
        double angle = Math.asin(y/speed);
        return new Velocity(angle, speed);

    }
}
