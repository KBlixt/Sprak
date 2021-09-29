package se.awesomeness;

import robocode.*;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Tools;
import se.awesomeness.geometry.Vector;
import se.awesomeness.crew.Driver;
import se.awesomeness.crew.RadarOperator;
import se.awesomeness.crew.Gunner;

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
    private final Vector radarHeading = new Vector();
    private final Point maxPoint = new Point();
    private final Point minPoint = new Point();


    int turnsToFire = 16;
    boolean firing;

    //crew
    private Driver driver;
    private Gunner gunner;
    private RadarOperator radarOp;

    public void run(){
        startOfRoundAction();

        Point nextPosition;
        EnemyRobot target = null;
        Vector fireSolution = new Vector();
        while (getOthers() > 0) {
            shouldFire(fireSolution);
            if (turnsToFire > 6 || target == null || deadRobots.contains(target.getName())) {
                target = selectTarget();
            }
            System.out.println(target);

            driver.drive();
            nextPosition = driver.getNextPosition();

            gunner.updateInfo(nextPosition, turnsToFire);
            fireSolution = gunner.findFireSolutionAverageLocation(target);

            gunner.takeAim(fireSolution);

            radarOp.monitor(target,turnsToFire);

            execute();
        }

        endOfRoundAction();
    }

    private void shouldFire(Vector fireSolution){
        firing = gunner.isAligned(fireSolution) && turnsToFire <= 0 && fireSolution.getMagnitude() >= 1;
    }
    private EnemyRobot selectTarget(){
        EnemyRobot target = null;
        double shortestDistance = -1;
        for (Map.Entry<String, EnemyRobot> entry : enemyRobots.entrySet()) {
            EnemyRobot enemyRobot = entry.getValue();
            Point enemyPosition = enemyRobot.estimatedPosition(turnsToFire);
            double distance = position.distanceTo(enemyPosition);
            if ( distance < shortestDistance || shortestDistance == -1){
                shortestDistance = distance;
                target = enemyRobot;
            }
        }
        return target;
    }

    private void startOfRoundAction(){
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        minPoint.setX(18);
        minPoint.setY(18);
        maxPoint.setX(getBattleFieldWidth()-18);
        maxPoint.setY(getBattleFieldHeight()-18);

        driver = new Driver(
                position,
                normalVelocity,
                getBattleFieldWidth(),
                getBattleFieldHeight()
        );

        gunner = new Gunner(
                gunHeading
        );

        radarOp = new RadarOperator(
                position,
                radarHeading
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
        radarHeading.setVector(1,Tools.convertAngle(getRadarHeading()));
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
                enemyRobots.put(robotName, new EnemyRobot(event, position, normalVelocity.getDirection(), minPoint, maxPoint));
            }else{
                enemyRobots.get(robotName).updateIntel(event, position, normalVelocity.getDirection());
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
        setGunRotationRate(gunner.getAdjustGunAngle());
        setRadarRotationRate(radarOp.getNextRadarTurn());
        if(firing){
            setFire(gunner.getBulletPower());
        }
        cleanUp();
        super.execute();
    }

}
