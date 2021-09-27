package se.awesomeness.geometry;

public class Tools {

    public static double shortestAngle(double angle){
        angle = angle % 360;

        if ( angle > 180) {
            angle -= 360;
        }else if (angle < -180){
            angle += 360;
        }
        return angle;
    }

    public static double convertAngle(double angle){
        return shortestAngle(90-angle);
    }

    public static double round(double number){
        return (double)Math.round(number*1_000_000)/1_000_000;
    }

    public static double oppositeAngle(double angle){
        return shortestAngle(angle-180);
    }
}