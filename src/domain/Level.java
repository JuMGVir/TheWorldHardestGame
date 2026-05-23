package domain;
import java.io.Serializable;
import java.util.*;

public class Level implements Serializable {
    private List<Enemy> enemies;
    private List<Coin> coins;
    private List<Wall> walls;
    private List<SafeZone> safeZones;
    private List<Bomb> bombs;
    private int timeLimit;

    public Level(List<SafeZone> safeZones, List<Wall> walls, List<Coin> coins, List<Enemy> enemies) {
        this(safeZones, walls, coins, enemies, new ArrayList<>(), 60);
    }

    public Level(List<SafeZone> safeZones, List<Wall> walls, List<Coin> coins, List<Enemy> enemies, List<Bomb> bombs) {
        this(safeZones, walls, coins, enemies, bombs, 60);
    }

    public Level(List<SafeZone> safeZones, List<Wall> walls, List<Coin> coins, List<Enemy> enemies,
                 List<Bomb> bombs, int timeLimit) {
        this.safeZones = safeZones;
        this.walls = walls;
        this.coins = coins;
        this.enemies = enemies;
        this.bombs = bombs;
        this.timeLimit = timeLimit;
    }

    public LevelState saveState(double respawnX, double respawnY) {
        return new LevelState(coins, enemies, bombs, respawnX, respawnY);
    }

    public void restoreState(LevelState state) {
        List<Boolean> savedCoins = state.getCoinStates();
        for (int i = 0; i < coins.size() && i < savedCoins.size(); i++) {
            coins.get(i).setCollected(savedCoins.get(i));
        }
        List<Boolean> savedEnemies = state.getEnemyAliveStates();
        for (int i = 0; i < enemies.size() && i < savedEnemies.size(); i++) {
            enemies.get(i).setAlive(savedEnemies.get(i));
        }
        List<Boolean> savedBombs = state.getBombExplodedStates();
        for (int i = 0; i < bombs.size() && i < savedBombs.size(); i++) {
            bombs.get(i).setExploded(savedBombs.get(i));
        }
    }

    /**
     * Updates the enemies positions (only alive enemies)
     */
    public void update() {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.updateWithWalls(walls);
            }
        }
    }

    /**
     * Check if all required coins were collected (skips non-required like Life)
     * @return
     */
    public boolean allCoinsCollected() {
        for (Coin c : coins) {
            if (c.isRequired() && !c.isCollected()) return false;
        }
        return true;
    }

    // Getters y setters
    public List<Enemy> getEnemies() { return enemies; }
    public List<Coin> getCoins() { return coins; }
    public List<Wall> getWalls() { return walls; }
    public List<SafeZone> getSafeZones() { return safeZones; }
    public List<Bomb> getBombs() { return bombs; }
    public int getTimeLimit() { return timeLimit; }
}
