package se.awesomeness;

import java.util.List;

public class Tools {

    public static double shortestAngle(double angle){
        angle = angle % 360;
        if ( angle > 180) {
            angle -= 360;
        }
        return angle;
    }
}
