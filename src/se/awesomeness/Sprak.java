package se.awesomeness;

import robocode.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sprak extends RateControlRobot {

    Point position = new Point();
    Vector normalVelocity = new Vector();
    Point nextPosition = new Point();

    Vector gunHeading = new Vector();

    Integer turnsToFire = 16;
    EnemyRobot targetRobot;

    Map<String, EnemyRobot> enemyRobots = new HashMap<>();
    List<String> deadRobots = new ArrayList<>();

    Mover mover;
    Shooter shooter;

    RadarControl radarControl = new RadarControl(this);

    public void run(){
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        mover = new Mover(
                position,
                normalVelocity,
                getBattleFieldWidth(),
                getBattleFieldHeight()
        );
        shooter = new Shooter(
                gunHeading,
                nextPosition,
                enemyRobots,
                targetRobot);

        setGunRotationRate(20);
        setRadarRotationRate(45);
        setTurnRate(10);
        setVelocityRate(8);
        while (enemyRobots.isEmpty()){
            execute();
        }

        while (!enemyRobots.isEmpty()) {
            mover.testMoving();
            setVelocityRate(mover.getNextSpeed());
            setTurnRate(mover.getNextTurn());
            nextPosition.setPoint(mover.getNextPosition());

            shooter.prepareShot(turnsToFire);
            setGunRotationRate(shooter.getAdjustGunAngle());
            if(shooter.getSetFire()){
                setFire(shooter.getBulletPower());
            }

            radarControl.defaultMonitoring(targetRobot);

            cleanUp();
            execute();
        }

        setVelocityRate(0);
        setTurnRate(10);
        setGunRotationRate(-20);
        while(enemyRobots.isEmpty()){
            execute();
        }
    }

    public void cleanUp(){
        for (Map.Entry<String, EnemyRobot> robotEntry : enemyRobots.entrySet()) {
            robotEntry.getValue().updateAge();
        }
    }

    @Override
    public void onStatus(StatusEvent event) {
        super.onStatus(event);
        position.setPoint(getX(), getY());
        normalVelocity.setVector(getVelocity(), Tools.convertAngle(getHeading()));
        gunHeading.setVector(1,Tools.convertAngle(getGunHeading()));
        turnsToFire = (int)Math.round(Math.ceil(getGunHeat()/0.1));
        System.out.println("realTurnsToFire" + turnsToFire);
        for (Map.Entry<String, EnemyRobot> robotEntry : enemyRobots.entrySet()) {
            robotEntry.getValue().updateThreatDistance(enemyRobots);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        String robotName = event.getName();

        if (!deadRobots.contains(robotName)){
            if (!enemyRobots.containsKey(robotName)){
                enemyRobots.put(robotName, new EnemyRobot(event, position, normalVelocity.getDirection()));
            }else{
                enemyRobots.get(robotName).updateData(event, position, normalVelocity.getDirection());
            }
        }

        super.onScannedRobot(event);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        String deadRobotName = event.getName();
        deadRobots.add(deadRobotName);
        enemyRobots.remove(deadRobotName);
        super.onRobotDeath(event);
    }
}