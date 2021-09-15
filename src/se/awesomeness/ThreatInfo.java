package se.awesomeness;

import java.util.Map;

public class ThreatInfo{
    double threatPointDistance;
    double threatPointSpeed;

    public ThreatInfo(double threatPointDistance, double threatPointSpeed) {
        this.threatPointDistance = threatPointDistance;
        this.threatPointSpeed = threatPointSpeed;
    }

    public double projectedDistance(long turnDelta){
        return threatPointDistance + turnDelta * threatPointSpeed;
    }
}
