package se.awesomeness;

import robocode.*;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Tools;
import se.awesomeness.geometry.Vector;
import se.awesomeness.operators.Driver;
import se.awesomeness.operators.RadarControl;
import se.awesomeness.operators.Shooter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sprak extends RateControlRobot {

    private final Point position = new Point();
    private final Vector normalVelocity = new Vector();
    private final Map<String, EnemyRobot> enemyRobots = new HashMap<>();
    private final List<String> deadRobots = new ArrayList<>();
    private final Vector gunHeading = new Vector();

    Integer turnsToFire = 16;

    //operators
    private Driver driver;
    private Shooter shooter;
    private RadarControl radarControl;

    public void run(){
        startOfRoundAction();

        Point nextPosition;
        EnemyRobot target;
        while (getOthers() > 0) {
            driver.drive();
            nextPosition = driver.getNextPosition();

            shooter.prepareShot(nextPosition, turnsToFire);

            radarControl.monitor(turnsToFire);

            cleanUp();
            execute();
        }

        endOfRoundAction();
    }

    private void startOfRoundAction(){
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        driver = new Driver(
                position,
                normalVelocity,
                getBattleFieldWidth(),
                getBattleFieldHeight()
        );

        shooter = new Shooter(
                gunHeading,
                enemyRobots
        );

        radarControl = new RadarControl(
        );

        setGunRotationRate(20);
        setRadarRotationRate(45);
        setTurnRate(10);
        setVelocityRate(8);
        while (enemyRobots.isEmpty()){
            super.execute();
        }
    }

    private void endOfRoundAction(){
        setVelocityRate(0);
        setTurnRate(10);
        setGunRotationRate(-20);
        while(enemyRobots.isEmpty()){
            super.execute();
        }
    }

    private void cleanUp(){
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

    @Override
    public void execute(){
        setVelocityRate(driver.getNextSpeed());
        setTurnRate(driver.getNextTurn());
        setGunRotationRate(shooter.getAdjustGunAngle());
        setRadarRotationRate(radarControl.getNextRadarTurn());
        if(shooter.getSetFire()){
            setFire(shooter.getBulletPower());
        }
        super.execute();
    }

}
