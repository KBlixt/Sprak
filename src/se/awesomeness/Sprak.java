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
import java.util.List;

@SuppressWarnings("unused")
public class Sprak extends RateControlRobot {

    private final Point position = new Point();
    private final Vector normalVelocity = new Vector();
    private final Vector gunHeading = new Vector();
    private final Vector radarHeading = new Vector();
    private final ArrayList<Point> pastPositions = new ArrayList<>();
    private final Point gotHitPosition = new Point();


    int turnsToFire = 16;
    boolean firing;
    private int wallHits = 0;

    private Battlefield battlefield;

    //crew
    private Driver driver;
    private Gunner gunner;
    private RadarOperator radarOp;

    public void run(){
        startOfRoundAction();

        Point nextPosition;
        Enemy target = null;
        Vector fireSolution = new Vector();
        while (getOthers() > 0) {
            printDiagnostics();
            shouldFire(fireSolution, target);

            driver.drive();
            nextPosition = driver.getNextPosition();
            gunner.updateInfo(nextPosition, turnsToFire);

            if (turnsToFire > 6 ||
                    target == null ||
                    !battlefield.getEnemies().contains(target.getName()) ||
                    fireSolution.getMagnitude() < 1
            ) {
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

    private void shouldFire(Vector fireSolution, Enemy target){
        firing = gunner.isAligned(fireSolution) && turnsToFire <= 0 && fireSolution.getMagnitude() >= 1 && target.isTargetLocked() && getEnergy() > fireSolution.getMagnitude();
        if (firing){
            System.out.println("firing bulletSize: " + fireSolution.getMagnitude());
        }
    }

    private Enemy selectTarget(){
        Enemy target = null;
        double biggestBullet = -1;
        for (String enemyName : battlefield.getEnemies()) {
            Enemy enemy = battlefield.getEnemy(enemyName);
            double bulletPower = gunner.findFireSolution(enemy).getMagnitude();
            if ( bulletPower > biggestBullet || biggestBullet == -1){
                biggestBullet = bulletPower;
                target = enemy;
            }
        }
        return target;
    }

    private void startOfRoundAction(){

        setColors(Color.DARK_GRAY, Color.GREEN, Color.YELLOW);
        setBulletColor(Color.GREEN);

        battlefield = new Battlefield(
                getBattleFieldWidth(),
                getBattleFieldHeight());

        Enemy.setBattlefield(battlefield);

        driver = new Driver(
                position,
                normalVelocity,
                pastPositions,
                gotHitPosition,
                battlefield
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
        while (battlefield.getEnemies().isEmpty()){
            super.execute();
        }
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
    }

    private void endOfRoundAction(){
        setAdjustGunForRobotTurn(false);
        setAdjustRadarForGunTurn(false);
        setVelocityRate(0);
        setTurnRate(10);
        setGunRotationRate(-20);
        setRadarRotationRate(45);
        while(battlefield.getEnemies().isEmpty()){
            super.execute();
        }
    }

    private void cleanUp(){
        for (String enemyName : battlefield.getEnemies()) {
            battlefield.getEnemy(enemyName).updateAge();
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
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        String enemyName = event.getName();

        if (battlefield.getEnemies().contains(enemyName)){
            battlefield.getEnemy(enemyName).updateIntel(event, position, normalVelocity.getDirection());
        }else{
            battlefield.newEnemy(new Enemy(event,position,normalVelocity.getDirection()));
        }

    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        battlefield.removeEnemy(event.getName());
        super.onRobotDeath(event);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        Vector addVector = position.vectorTo(battlefield.getEnemy(event.getName()).getPosition(0));
        Point gotHitPosition1 = position.addVector(new Vector(36, addVector.getDirection()+90));
        Point gotHitPosition2 = position.addVector(new Vector(36, addVector.getDirection()-90));
        List<Point> candidates = List.of(gotHitPosition1.addVector(normalVelocity.multiply(4)), gotHitPosition2.addVector(normalVelocity.multiply(4)));
        System.out.println(candidates);
        gotHitPosition.set(new Point(battlefield.maxX/2, battlefield.maxY/2).furthestPoint(candidates));
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
