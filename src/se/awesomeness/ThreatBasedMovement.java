package se.awesomeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThreatBasedMovement {

        Sprak sprak;

    public ThreatBasedMovement(Sprak sprak) {
        this.sprak = sprak;
    }

    public void UpdateThreats(long time){
        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            robotEntry.getValue().updateThreatDistance(sprak.enemyRobots, time);
        }
    }

    public void testMoving(){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();
        System.out.println("[-------------------------------------]");
        System.out.println("speed: " + speed);
        System.out.println("heading: " + heading);
        Vector target = new Vector(0.1, 0.2);
        System.out.println("target: " + target);


        move(target);
    }


    public void moveAway(){
        Vector moveVector = new Vector();
        List<Vector> wallForces = generateWallForces();
        for (Vector wallForce : wallForces) {
            moveVector = moveVector.add(wallForce);
            System.out.println(moveVector);

        }

        for (Map.Entry<String, EnemyRobot> robotEntry : sprak.enemyRobots.entrySet()) {
            //todo: more fleshed out algorithm to weigh moveVector.
            Point enemyPosition = robotEntry.getValue().estimatedPosition(sprak.getTime());

            double distanceFromRobot = enemyPosition.distanceToPoint(sprak.position);
            double angleFromRobot = new Vector().angleToPoint(new Point(
                    sprak.position.getX()-enemyPosition.getX(),
                    sprak.position.getY()-enemyPosition.getY())
            ); //todo: make into operator? Point.angleToPoint(Point)?

            double finalAngle = Tools.shortestAngle(angleFromRobot-sprak.normalVelocity.getDirection());
            Vector robotForce = new Vector(7*Math.sqrt(100/distanceFromRobot), finalAngle);
            moveVector = moveVector.add(robotForce);
            System.out.println(moveVector);
        }
        if (sprak.enemyRobots.size() != 0) {
            Vector maxMoveVector = getMaxVector(moveVector);
            System.out.println(maxMoveVector);
            System.out.println("speed: " + sprak.normalVelocity.getMagnitude());
            System.out.println("-------");
            move(maxMoveVector);
        }
    }


    public Vector getMaxVector(Vector vector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();

        Point targetPoint = new Point(toMoveVector(vector, speed, heading));
        System.out.println("target in movepace: " + new Vector(targetPoint));

        List<Point> candidatePoints = new ArrayList<>();

        double upperAccLimit = Math.min(2, Math.max(-speed,Math.min(1, -speed+8)));
        double lowerAccLimit = Math.max(-8-speed,Math.min(-1,Math.max(-2, -speed)));
        double leftTurnLimit = 10 - 0.75 * Math.abs(speed);
        double rightTurnLimit = -leftTurnLimit;

        List<Limit> limits = List.of(
                new Limit(LimitType.ACCELERATION_LIMIT, upperAccLimit, speed),
                new Limit(LimitType.ACCELERATION_LIMIT, lowerAccLimit, speed),
                new Limit(LimitType.TURN_LIMIT, leftTurnLimit, speed),
                new Limit(LimitType.TURN_LIMIT, rightTurnLimit, speed)
        );

        for (Limit limit : limits) {
            candidatePoints.add(limit.closestPoint(targetPoint));
        }
        candidatePoints.add(new Point(new Vector(speed +upperAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +lowerAccLimit,leftTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +upperAccLimit,rightTurnLimit)));
        candidatePoints.add(new Point(new Vector(speed +lowerAccLimit,rightTurnLimit)));

        boolean withinLimits = false;
        Point candidatePoint = new Point();

        while(!withinLimits){
            withinLimits = true;
            candidatePoint = targetPoint.closestPoint(candidatePoints);
            for (Limit limit : limits) {
                withinLimits &= limit.withinLimit(candidatePoint);
            }
            candidatePoints.remove(candidatePoint);
        }
        return fromMoveVector(new Vector(candidatePoint), speed, heading);
    }

    public List<Vector> generateWallForces(){
        double sprakX = sprak.position.getX();
        double sprakY = sprak.position.getY();
        Vector sprakPositionVector = new Vector(sprak.position);
        double maxX = sprak.getBattleFieldWidth();
        double maxY =sprak.getBattleFieldHeight();

        Point top = new Point(sprakX, maxY);
        Point bott = new Point(sprakX, 0);
        Point left = new Point(0, sprakY);
        Point right = new Point(maxX, sprakY);



        List<Vector> wallForces = List.of(
                new Vector(new Point(top)).subtract(sprakPositionVector)
                //new Vector(new Point(bott)).subtract(sprakPositionVector),
                //new Vector(new Point(left)).subtract(sprakPositionVector),
                //new Vector(new Point(right)).subtract(sprakPositionVector)
                );
        List<Vector> wallForcesFinal = new ArrayList<>();
        for (Vector wallForce : wallForces) {
            wallForcesFinal.add(wallForce.multiply(100/ Math.sqrt(wallForce.getMagnitude())));
        }
        return wallForcesFinal;
    }

    public void move(Vector vector){
        double speed = sprak.normalVelocity.getMagnitude();
        double heading = sprak.normalVelocity.getDirection();

        vector = getMaxVector(vector);
        Vector moveVector = toMoveVector(vector, speed , heading );

        if (moveVector.getDirection() < -90 || moveVector.getDirection() > 90){
            sprak.setVelocityRate(-moveVector.getMagnitude());
            sprak.setTurnRate(Tools.shortestAngle(-moveVector.getDirection()-180));
        }else {
            sprak.setVelocityRate(moveVector.getMagnitude());
            sprak.setTurnRate(-moveVector.getDirection());
        }
    }

    public static Vector toMoveVector(Vector vector, double speed, double heading){
        Vector moveVector = new Vector(vector.getMagnitude(), vector.getDirection() - heading);
        moveVector = moveVector.add(new Vector(speed,0));
        return moveVector;
    }

    public static Vector fromMoveVector(Vector moveVector, double speed, double heading){
        Vector vector = moveVector.subtract(new Vector(speed,0));
        vector = new Vector(vector.getMagnitude(), vector.getDirection() + heading);
        return vector;
    }
}