package se.awesomeness;

import java.util.*;

public class Battlefield {
    public final double minX;
    public final double minY;
    public final double maxX;
    public final double maxY;
    private final Map<String, Enemy> enemies = new HashMap<>();
    private final List<String> deadEnemies = new ArrayList<>();
    private final List<String> aliveEnemies = new ArrayList<>();

    public Battlefield(double battlefieldWidth, double battlefieldHeight){
        minX = 18;
        minY = 18;
        maxX = battlefieldWidth - 18;
        maxY = battlefieldHeight - 18;
    }

    public void newEnemy(Enemy enemy){
        String enemyName = enemy.getName();
        enemies.put(enemyName, enemy);
        aliveEnemies.add(enemyName);
    }

    public void removeEnemy(String enemyName){
        aliveEnemies.remove(enemyName);
        enemies.remove(enemyName);
        deadEnemies.add(enemyName);
    }

    public List<String> getEnemies(){
        return getAliveEnemies();
    }

    public List<String> getAliveEnemies() {
        return new ArrayList<>(aliveEnemies);
    }

    public List<String> getDeadEnemies() {
        return new ArrayList<>(deadEnemies);
    }

    public Enemy getEnemy(String enemyName){
        return enemies.get(enemyName);
    }
}
