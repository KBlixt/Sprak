package se.awesomeness;

import robocode.ScannedRobotEvent;

public class EnemyBot {

    private double distance;
    private String name;

    public double getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public void update(ScannedRobotEvent event) {
        event.getDistance();
    }
    public boolean none() {
        return name.equals("");
    }



    public void reset() {
        distance = 0.0;
    }

    public EnemyBot() {
        reset();
    }
}
