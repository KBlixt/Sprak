package se.awesomeness.geometry;

public class Tools {

    public static double reduceAngle(double angle){
        angle = angle % 360;

        if ( angle > 180) {
            angle -= 360;
        }else if (angle < -180){
            angle += 360;
        }
        return angle;
    }

    public static double convertAngle(double angle){
        return reduceAngle(90-angle);
    }

    public static double oppositeAngle(double angle){
        return reduceAngle(angle-180);
    }
}