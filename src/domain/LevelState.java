package domain;

import java.io.Serializable;
import java.util.*;

public class LevelState implements Serializable {
    private final List<Boolean> coinStates;
    private final List<Boolean> enemyAliveStates;
    private final List<Boolean> bombExplodedStates;
    private final double playerRespawnX;
    private final double playerRespawnY;

    public LevelState(List<Coin> coins, List<Enemy> enemies, List<Bomb> bombs,
                      double respawnX, double respawnY) {
        this.coinStates = new ArrayList<>();
        for (Coin c : coins) {
            this.coinStates.add(c.isCollected());
        }
        this.enemyAliveStates = new ArrayList<>();
        for (Enemy e : enemies) {
            this.enemyAliveStates.add(e.isAlive());
        }
        this.bombExplodedStates = new ArrayList<>();
        for (Bomb b : bombs) {
            this.bombExplodedStates.add(b.isExploded());
        }
        this.playerRespawnX = respawnX;
        this.playerRespawnY = respawnY;
    }

    public List<Boolean> getCoinStates() { return coinStates; }
    public List<Boolean> getEnemyAliveStates() { return enemyAliveStates; }
    public List<Boolean> getBombExplodedStates() { return bombExplodedStates; }
    public double getRespawnX() { return playerRespawnX; }
    public double getRespawnY() { return playerRespawnY; }
}
