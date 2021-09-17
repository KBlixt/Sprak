package se.awesomeness;

import java.util.Map;

public class ThreatBasedMovement {

        Spark spark;


    public ThreatBasedMovement(Spark spark) {
        this.spark = spark;
    }

    public void UpdateThreats(long time){
        for (Map.Entry<String, EnemyRobot> robotEntry : spark.enemyRobots.entrySet()) {
            robotEntry.getValue().updateThreatDistance(spark.enemyRobots, time);
        }
    }

    public void moveAway(){
        Vector2D moveVector = new Vector2D();
        for (Map.Entry<String, EnemyRobot> robotEntry : spark.enemyRobots.entrySet()) {
            //todo: more fleshed out algorithm to weigh moveVector.
            Point robotEstimatedPosition = robotEntry.getValue().estimatedPosition(spark.getTime());

            double distanceFromRobot = robotEstimatedPosition.distanceToPoint(spark.position);
            double angleFromRobot = new Vector2D().angleToPoint(new Point(
                    spark.getX()-robotEstimatedPosition.getX(),
                    spark.getY()-robotEstimatedPosition.getY())
            ); //todo: make into operator? Point.angleToPoint(Point)?

            Vector2D robotForce = new Vector2D(7*Math.sqrt(100/distanceFromRobot), angleFromRobot);
            System.out.println("-------------");
            System.out.println(robotForce);
            moveVector = moveVector.addVector(robotForce);
            System.out.println(moveVector);
        };
        spark.setTurnRate(Tools.shortestAngle(moveVector.getDirection()-spark.velocityVector.getDirection()));
        spark.setVelocityRate(8);
    }
}
