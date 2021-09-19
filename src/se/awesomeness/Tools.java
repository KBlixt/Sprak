package se.awesomeness;

public class Tools {

    public static double shortestAngle(double angle){
        angle = angle % 360;
        if ( angle > 180) {
            angle -= 360;
        }
        return angle;
    }

    public static double convertAngle(double angle){
        return 90-angle;
    }

    public static double round(double number){
        return (double)Math.round(number*1_000_000)/1_000_000;
    }
}