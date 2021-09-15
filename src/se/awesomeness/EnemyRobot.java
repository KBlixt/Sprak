package se.awesomeness;

import robocode.ScannedRobotEvent;

/** innehåller information kring motståndarna */
public class EnemyRobot {
    Point position;
    Velocity velocity;
    Acceleration acceleration;
    double energy;
    String name;
    ThreatInfo threatInfo;
    int LastUpdate;



    public EnemyRobot(String name){
        this.name = name;
    }


    /** metoderna under här är för avancerad rörelse*/
    public Point getEstimatedPosition(long turn){
        double[] velVector = Velocity.getVelocityVector(velocity);
        double[] accVector = Acceleration.getAccelerationVector(acceleration);
        double x = position.x + velVector[0]*(turn-LastUpdate) + accVector[0]*Math.pow(turn-LastUpdate, 1.5);
        double y = position.y + velVector[1]*(turn-LastUpdate) + accVector[1]*Math.pow(turn-LastUpdate, 1.5);
        return new Point(x,y);

    }
    public Velocity getEstimatedVelocity(long turn){
        double[] velVector = Velocity.getVelocityVector(velocity);
        double[] accVector = Acceleration.getAccelerationVector(acceleration);
        return Velocity.getVelocityWithVector(
                velVector[0]+accVector[0]*(turn-LastUpdate),
                velVector[1]+accVector[1]*(turn-LastUpdate));
    }
    public ThreatInfo getThreatInfo(){
        return threatInfo;
    }
    public void setThreatInfo(ThreatInfo threatInfo){
        this.threatInfo = threatInfo;
    }


}
