package se.awesomeness.crew;

import se.awesomeness.Battlefield;
import se.awesomeness.Enemy;
import se.awesomeness.forceTranslator.ForceTranslator;
import se.awesomeness.geometry.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Driver {

    private final Point position;
    private final Vector velocity;
    private final ArrayList<Point> pastPositions;
    private final Point gotHitPosition;

    private final Battlefield battlefield;

    private Vector moveCommand;
    private Point randomPoint = null;
    private int turnsStandingStill;




    public Driver(Point position, Vector velocity, ArrayList<Point> pastPositions, Point gotHitPosition, Battlefield battlefield) {
        this.position = position;
        this.velocity = velocity;
        this.pastPositions = pastPositions;
        this.gotHitPosition = gotHitPosition;
        this.battlefield = battlefield;
        turnsStandingStill = 0;
    }

    public void drive(){
        Random rand = new Random();
        List<Vector> forces = new ArrayList<>();
        double weight;

        if (Math.abs(velocity.getMagnitude()) < 2){
            turnsStandingStill = Math.min(turnsStandingStill+1, 20);
        }else{
            turnsStandingStill = Math.max(turnsStandingStill-1, 0);
        }

        Vector forceFromGotHit = gotHitPosition.vectorTo(position);
        weight = Math.max(Math.min(50/Math.sqrt(forceFromGotHit.getMagnitude()/50)-15 ,100),0);
        forces.add(forceFromGotHit.setMagnitude(weight));

        //add force toward random point
        if((randomPoint == null || position.distanceTo(randomPoint) < 100) && turnsStandingStill > 7){
            randomPoint = new Point(rand.nextDouble()*battlefield.maxX, rand.nextDouble()*battlefield.maxY);
        }else if (turnsStandingStill <= 0){
            randomPoint = null;
        }
        if (randomPoint != null){
            forces.add(position.vectorTo(randomPoint).setMagnitude(Math.abs(25)));
        }

        //add forces from opponents.
        for (String enemyName : battlefield.getEnemies()) {
            Enemy enemy = battlefield.getEnemy(enemyName);
            Vector forceFromEnemy = enemy.getPosition(12).vectorTo(position);
            weight = Math.max(Math.min(100/Math.sqrt(forceFromEnemy.getMagnitude()/100)-40 ,100),0);
            forces.add(forceFromEnemy.setMagnitude(weight));

            forceFromEnemy = enemy.getPosition(0).vectorTo(position);
            weight = Math.max(Math.min(100/Math.sqrt(forceFromEnemy.getMagnitude()/100)-40 ,100),0);
            forces.add(forceFromEnemy.setMagnitude(weight));

            forceFromEnemy =  position.vectorTo(enemy.getPosition(40));
            weight = Math.max(Math.min(100/Math.sqrt(forceFromEnemy.getMagnitude()/100)-30 ,100),0);
            forces.add(forceFromEnemy.setMagnitude(weight));
        }
        //add forces: want to be chased, don't want to be chasing them.
        //add force perpendicular to target.
        System.out.println(forces);
        Vector forceSum = Vector.addAll(forces);
        System.out.println("forceSum: " + forceSum);
        Vector forceAfterWall = wallSurfing(forceSum);

        moveCommand = ForceTranslator.translateForce(forceAfterWall,velocity);
    }

    private Vector wallSurfing(Vector force){
        if(force.getMagnitude()< 0){
            force = force.negative();
        }

        double sprakX = position.getX();
        double sprakY = position.getY();
        double minX = battlefield.minX + 1.5;
        double minY = battlefield.minY + 1.5;
        double maxX = battlefield.maxX - 1.5;
        double maxY = battlefield.maxY - 1.5;
        double magnitude = calculateLookAhead((minX + maxX)/2, (minY+maxY)/2);
        force = new Vector(magnitude,force.getDirection());

        double distToWallX;
        if (maxX - sprakX < Math.abs(minX- sprakX)){
            distToWallX = maxX - sprakX;
        }else{
            distToWallX = minX - sprakX;
        }
        double distToWallY;
        if (maxY - sprakY < Math.abs(minY - sprakY)){
            distToWallY = maxY - sprakY;
        }else{
            distToWallY = minY - sprakY;
        }

        List<Point> pointsOnWall = new ArrayList<>();
        if (Math.abs(distToWallX) < magnitude) {
            double y = magnitude*Math.sqrt(1-Math.pow(distToWallX/magnitude,2));
            pointsOnWall.add(new Point(distToWallX, y));
            pointsOnWall.add(new Point(distToWallX, -y));
        }
        if (Math.abs(distToWallY) < magnitude){
            double x = magnitude*Math.sqrt(1-Math.pow(distToWallY/magnitude,2));
            pointsOnWall.add(new Point(x, distToWallY));
            pointsOnWall.add(new Point(-x, distToWallY));
        }
        System.out.println(pointsOnWall);
        pointsOnWall.add(force.toPoint());
        pointsOnWall.add(new Point(new Vector(magnitude, 0)));
        pointsOnWall.add(new Point(new Vector(magnitude, 90)));
        pointsOnWall.add(new Point(new Vector(magnitude, 180)));
        pointsOnWall.add(new Point(new Vector(magnitude, -90)));

        boolean withinLimits = false;
        Point candidatePoint = new Point();
        while (!withinLimits) {
            if (!pointsOnWall.isEmpty()) {
                candidatePoint = force.toPoint().closestPoint(pointsOnWall);
                pointsOnWall.remove(candidatePoint);
            }else{
                List<Point> edges = List.of(
                        new Point(new Vector(magnitude, 0)),
                        new Point(new Vector(magnitude, 90)),
                        new Point(new Vector(magnitude, 180)),
                        new Point(new Vector(magnitude, -90)));
                List<Point> edgesOnMap = new ArrayList<>();
                for (Point edge : edges) {
                    edgesOnMap.add(position.addVector(new Vector(edge)));
                }
                Point candidatePointOnMap = new Point(maxX/2, maxY/2).closestPoint(edgesOnMap);
                candidatePoint = candidatePointOnMap.subtractVector(new Vector(position));
                break;
            }
            Point checker = position.addVector(new Vector(candidatePoint));
            double margin = 1;
            boolean insideX = checker.getX()  - margin<= maxX && checker.getX() + margin>= minX;
            boolean insideY = checker.getY() - margin<= maxY && checker.getY() + margin>= minY;

           withinLimits = insideX && insideY;
        }
        force = new Vector(candidatePoint);
        return force;
    }

    private double calculateLookAhead(double midX, double midY){
        if (velocity.getMagnitude()==0){
            return 120;
        }
        double magnitudePartX;
        double magnitudePartY;

        if(position.getX()>midX){
            magnitudePartX = Math.max(velocity.getX(), 0);
        }else{
            magnitudePartX = Math.min(velocity.getX(), 0);
        }
        if(position.getY()>midY){
            magnitudePartY = Math.max(velocity.getY(), 0);
        }else{
            magnitudePartY = Math.min(velocity.getY(), 0);
        }
        double targetMagnitude = 120;
        magnitudePartX = Math.abs(magnitudePartX/velocity.getMagnitude())*targetMagnitude;
        magnitudePartY = Math.abs(magnitudePartY/velocity.getMagnitude())*targetMagnitude;

        return (magnitudePartX+magnitudePartY+10)*(Math.abs(velocity.getMagnitude())/8) + 4;
    }

    public Point getNextPosition() {
        return position.addVector(new Vector(
                moveCommand.getMagnitude(),
                moveCommand.getDirection() + velocity.getDirection()
        ));
    }

    public Vector getNextVelocity() {
        return new Vector(
                moveCommand.getMagnitude(),
                moveCommand.getDirection() + velocity.getDirection()
        );
    }

    public double getNextSpeed() {
        if (moveCommand.getDirection() < -90 || moveCommand.getDirection() > 90){
            return -moveCommand.getMagnitude();
        }else {
            return moveCommand.getMagnitude();
        }
    }

    public double getNextTurn() {
        if (moveCommand.getDirection() < -90 || moveCommand.getDirection() > 90){
            return Tools.reduceAngle(180-moveCommand.getDirection());
        }else {
            return -moveCommand.getDirection();
        }
    }
}