package se.awesomeness;

import robocode.*;
import se.awesomeness.geometry.Point;
import se.awesomeness.geometry.Tools;
import se.awesomeness.geometry.Vector;
import se.awesomeness.crew.Driver;
import se.awesomeness.crew.RadarOperator;
import se.awesomeness.crew.Gunner;

import java.awt.*;
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
    private final ArrayList<Point> pastPositions = new ArrayList<>();
    private final Point gotHitPosition = new Point();


    int turnsToFire = 16;
    boolean firing;
    private int wallHits = 0;

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
            printDiagnostics();
            shouldFire(fireSolution, target);

            driver.drive();
            nextPosition = driver.getNextPosition();

            gunner.updateInfo(nextPosition, turnsToFire);

            if (turnsToFire > 6 || target == null || deadRobots.contains(target.getName()) || fireSolution.getMagnitude() < 1) {
                target = selectTarget();
            }


            fireSolution = gunner.findFireSolution(target);
            System.out.println("fireSolution: " + fireSolution);

            gunner.takeAim(fireSolution);

            radarOp.monitor(target,turnsToFire, getOthers());

            execute();
        }

        endOfRoundAction();
    }

    private void shouldFire(Vector fireSolution, EnemyRobot target){
        firing = gunner.isAligned(fireSolution) && turnsToFire <= 0 && fireSolution.getMagnitude() >= 1 && target.isTargetLocked() && getEnergy() > fireSolution.getMagnitude();
        if (firing){
            System.out.println("firing bulletSize: " + fireSolution.getMagnitude());
        }
    }

    private EnemyRobot selectTarget(){
        EnemyRobot target = null;
        double biggestBullet = -1;
        for (Map.Entry<String, EnemyRobot> entry : enemyRobots.entrySet()) {
            EnemyRobot enemyRobot = entry.getValue();
            double bulletPower = gunner.findFireSolution(enemyRobot).getMagnitude();
            if ( bulletPower > biggestBullet || biggestBullet == -1){
                biggestBullet = bulletPower;
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

        setColors(Color.DARK_GRAY, Color.GREEN, Color.YELLOW);
        setBulletColor(Color.GREEN);

        driver = new Driver(
                position,
                normalVelocity,
                enemyRobots,
                pastPositions,
                gotHitPosition,
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
        System.out.println("[^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^]");
    }

    @Override
    public void onStatus(StatusEvent event) {
        super.onStatus(event);
        if(position.getX() == 0 && position.getY() == 0){
            position.set(getX(), getY());
        }
        pastPositions.add(new Point(position));
        position.set(getX(), getY());
        normalVelocity.set(getVelocity(), Tools.convertAngle(getHeading()));
        gunHeading.set(1,Tools.convertAngle(getGunHeading()));
        radarHeading.set(1,Tools.convertAngle(getRadarHeading()));
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
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        Vector addVector = position.vectorTo(enemyRobots.get(event.getName()).estimatedPosition(0));
        Point gotHitPosition1 = position.addVector(new Vector(36, addVector.getDirection()+90));
        Point gotHitPosition2 = position.addVector(new Vector(36, addVector.getDirection()-90));
        List<Point> candidates = List.of(gotHitPosition1.addVector(normalVelocity.multiply(4)), gotHitPosition2.addVector(normalVelocity.multiply(4)));
        System.out.println(candidates);
        gotHitPosition.set(new Point(maxPoint.getX()/2, maxPoint.getY()/2).furthestPoint(candidates));
    }

    @Override
    public void execute(){
        setVelocityRate(driver.getNextSpeed());
        setTurnRate(driver.getNextTurn());
        setGunRotationRate(gunner.getAdjustGunAngle());
        setRadarRotationRate(radarOp.getNextRadarTurn());
        if(firing){
            if (gunner.getBulletPower() > 2.2){
                setBulletColor(Color.RED);
            }else if(gunner.getBulletPower() > 1.1) {
                setBulletColor(Color.ORANGE);
            }else{
                setBulletColor(Color.GREEN);
            }
            setFire(gunner.getBulletPower());
        }
        cleanUp();
        super.execute();
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        wallHits++;
    }

    public void printDiagnostics(){
        System.out.println();
        System.out.println("[------------------------------------------------------------------]");
        System.out.println("[---------------------------------------------------------------------------]");
        System.out.println("[------------------------------------------------------------------]");
        System.out.println("position: " + position);
        System.out.println("velocity: " + normalVelocity);
        System.out.println("gunHeading: " + gunHeading);
        System.out.println("turnsToFire: " + turnsToFire);
        System.out.println("firing: " + firing);
        System.out.println("wallHits: " + wallHits);
        System.out.println("gotHitPosition: " + gotHitPosition);

        System.out.println();
        System.out.println("[vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv]");

    }

}
