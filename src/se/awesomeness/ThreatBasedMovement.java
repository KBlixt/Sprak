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


}
