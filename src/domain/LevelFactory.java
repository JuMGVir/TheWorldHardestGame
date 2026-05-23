package domain;
import java.util.*;

public class LevelFactory {

    public static int getTotalLevels() {
        return 5;
    }

    public static Level createLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> createLevel1();
            case 2 -> createLevel2();
            case 3 -> createLevel3();
            case 4 -> createLevel4();
            case 5 -> createMiniLevel();
            default -> throw new IllegalArgumentException("FORMATO ERRÓNEO");
        };
    }

    /**
     * Crea el mini nivel laberíntico (Level 5) para la pantalla de inicio.
     * <p>
     * El nivel tiene 3 secciones verticales separadas por divisores con un
     * pasadizo central (gap en y=42-75) que obliga al jugador a navegar
     * por el laberinto para cruzar entre secciones. Cada sección tiene
     * muros internos en forma de L que crean corredores. En la parte
     * superior de cada sección hay una zona FINAL que redirige a
     * Settings (izquierda), Load (centro) y New Game (derecha).
     * </p>
     * <p>
     * El nivel no tiene monedas — las zonas FINAL se activan por
     * colisión directa. Hay 5 enemigos: tres verticales (uno por sección)
     * y dos horizontales que patrullan a través del pasadizo central.
     * </p>
     * 
     * @name createMiniLevel
     * @return Un Level de 245×120 unidades lógicas con diseño laberíntico
     */
    public static Level createMiniLevel() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(105, 80, 30, 30, 120, 98));
        safeZones.add(new FinalSafeZone(10, 3, 51, 13, 35, 9));
        safeZones.add(new FinalSafeZone(97, 3, 51, 13, 122, 9));
        safeZones.add(new FinalSafeZone(184, 3, 51, 13, 209, 9));

        List<Wall> walls = new ArrayList<>();
        // Borde exterior del nivel
        walls.add(new Wall(0, 0, 245, 3));
        walls.add(new Wall(0, 0, 3, 120));
        walls.add(new Wall(242, 0, 3, 120));
        walls.add(new Wall(0, 117, 245, 3));

        // Divisores entre secciones — gap vertical en y=42-75 como único pasadizo
        walls.add(new Wall(82, 3, 3, 39));
        walls.add(new Wall(82, 75, 3, 42));
        walls.add(new Wall(163, 3, 3, 39));
        walls.add(new Wall(163, 75, 3, 42));

        // Muros internos en forma de L — cada sección tiene horizontal + vertical
        walls.add(new Wall(5, 30, 35, 3));   // izquierda: horizontal
        walls.add(new Wall(45, 30, 3, 35));  // izquierda: vertical
        walls.add(new Wall(90, 30, 35, 3));  // centro: horizontal
        walls.add(new Wall(130, 30, 3, 35)); // centro: vertical
        walls.add(new Wall(168, 30, 35, 3)); // derecha: horizontal
        walls.add(new Wall(208, 30, 3, 35)); // derecha: vertical

        List<Coin> coins = new ArrayList<>(); // Sin monedas — FINAL se activa por colisión directa

        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(30, 70, 8, 8, new LinearMovement(0, -1.0, 0.8)));  // izquierda: vertical
        enemies.add(new Enemy(60, 50, 8, 8, new LinearMovement(1.0, 0, 0.7)));   // cruza izquierda ↔ centro
        enemies.add(new Enemy(110, 70, 8, 8, new LinearMovement(0, -1.0, 0.8))); // centro: vertical
        enemies.add(new Enemy(170, 50, 8, 8, new LinearMovement(1.0, 0, 0.7)));  // cruza centro ↔ derecha
        enemies.add(new Enemy(220, 70, 8, 8, new LinearMovement(0, -1.0, 0.8))); // derecha: vertical

        return new Level(safeZones, walls, coins, enemies, new ArrayList<>(), 120);
    }

    public static Level createLevelFromImport(List<SafeZone> safeZones, List<Wall> walls,
            List<Coin> coins, List<Enemy> enemies, List<Bomb> bombs, List<Life> lives, int timeLimit) {
        List<Coin> allCoins = new ArrayList<>(coins);
        allCoins.addAll(lives);
        return new Level(safeZones, walls, allCoins, enemies, bombs, timeLimit);
    }

    private static Level createLevel1() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(5, 55, 30, 30, 20, 70));
        safeZones.add(new FinalSafeZone(185, 55, 30, 30, 200, 70));

        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(0, 45, 250, 8));
        walls.add(new Wall(0, 87, 250, 8));
        walls.add(new Wall(0, 0, 220, 3));
        walls.add(new Wall(0, 147, 220, 3));
        walls.add(new Wall(0, 0, 3, 150));
        walls.add(new Wall(217, 0, 3, 150));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(55, 62));
        coins.add(new Coin(65, 62));
        coins.add(new Coin(75, 62));
        coins.add(new SkinCoin(100, 62, PlayerType.INKY));
        coins.add(new SkinCoin(130, 62, PlayerType.CLYDE));
        coins.add(new SkinCoin(160, 62, PlayerType.INKY));
        coins.add(new Coin(175, 62));
        coins.add(new Life(95, 75));
 
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(50, 53, 8, 8, new LinearMovement(0.8, 0)));
        enemies.add(new Enemy(90, 79, 8, 8, new LinearMovement(1.0, 0)));
        enemies.add(new Enemy(120, 53, 8, 8, new LinearMovement(0, 0.8)));
        enemies.add(new Enemy(150, 79, 8, 8, new LinearMovement(0, 1.0)));
        
        List<Bomb> bombs = new ArrayList<>();
 
        return new Level(safeZones, walls, coins, enemies, bombs, 30);
    }
    private static Level createLevel2() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(5, 5, 25, 25, 15, 17));
        safeZones.add(new IntermediateSafeZone(95, 55, 25, 25, 107, 67));
        safeZones.add(new FinalSafeZone(185, 55, 30, 30, 200, 70));

        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(30, 0, 8, 70));
        walls.add(new Wall(30, 90, 8, 60));
        walls.add(new Wall(100, 40, 80, 8));
        walls.add(new Wall(160, 48, 8, 60));
        walls.add(new Wall(0, 0, 220, 3));
        walls.add(new Wall(0, 97, 220, 3));
        walls.add(new Wall(0, 0, 3, 100));
        walls.add(new Wall(217, 0, 3, 100));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(55, 30));
        coins.add(new Coin(55, 40));
        coins.add(new SkinCoin(70, 60, PlayerType.INKY));
        coins.add(new SkinCoin(120, 20, PlayerType.CLYDE));
        coins.add(new SkinCoin(140, 70, PlayerType.BLINKY));
        coins.add(new Coin(175, 30));
        coins.add(new Coin(175, 90));
        coins.add(new Life(95, 72));

        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(38, 30, 8, 8, new LinearMovement(1.0, 0)));
        enemies.add(new Enemy(80, 10, 8, 8, new LinearMovement(0, 1.0, 2.0)));
        enemies.add(new Enemy(110, 70, 8, 8, new CircularMovement(110, 70, 20, 0.06)));
        enemies.add(new Enemy(150, 90, 8, 8, new CircularMovement(150, 90, 25, 0.05)));
        enemies.add(new Enemy(168, 30, 8, 8, new LinearMovement(1.0, 0, 2.0)));

        List<Bomb> bombs = new ArrayList<>();
        bombs.add(new Bomb(55, 50));
        bombs.add(new Bomb(145, 15));

        return new Level(safeZones, walls, coins, enemies, bombs, 45);
    }

    private static Level createLevel3() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(5, 5, 25, 25, 15, 17));
        safeZones.add(new IntermediateSafeZone(95, 10, 25, 25, 107, 18));
        safeZones.add(new IntermediateSafeZone(95, 70, 25, 25, 107, 82)); 
        safeZones.add(new FinalSafeZone(190, 60, 25, 25, 202, 60));

        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(30, 0, 8, 55));
        walls.add(new Wall(30, 85, 8, 65));
        walls.add(new Wall(80, 35, 60, 8));
        walls.add(new Wall(80, 100, 60, 8));
        walls.add(new Wall(160, 0, 8, 55));
        walls.add(new Wall(160, 85, 8, 65));
        walls.add(new Wall(0, 0, 220, 3));
        walls.add(new Wall(0, 97, 220, 3));
        walls.add(new Wall(0, 0, 3, 100));
        walls.add(new Wall(217, 0, 3, 100));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(50, 30));
        coins.add(new Coin(50, 80));
        coins.add(new SkinCoin(110, 60, PlayerType.CLYDE));
        coins.add(new SkinCoin(140, 20, PlayerType.BLINKY));
        coins.add(new SkinCoin(140, 85, PlayerType.INKY));
        coins.add(new Coin(175, 40));
        coins.add(new Coin(175, 80));
        coins.add(new Life(70, 55));
        coins.add(new Life(140, 15));
 
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(38, 25, 8, 8, new LinearMovement(1.0, 0)));
        enemies.add(new Enemy(38, 80, 8, 8, new LinearMovement(1.0, 0)));
        enemies.add(new Enemy(110, 43, 8, 8, new LinearMovement(0, 1.0)));
        enemies.add(new Enemy(140, 43, 8, 8, new LinearMovement(0, 1.0, 2.0)));
        enemies.add(new Enemy(168, 60, 8, 8, new LinearMovement(1.0, 0, 2.5)));
        enemies.add(new Enemy(60, 70, 8, 8, new CircularMovement(60, 70, 20, 0.07)));
        enemies.add(new Enemy(110, 70, 8, 8, new CircularMovement(110, 70, 25, 0.05)));
        enemies.add(new Enemy(168, 72, 8, 8, new CircularMovement(168, 70, 30, 0.06)));
        
        List<Bomb> bombs = new ArrayList<>();
        bombs.add(new Bomb(55, 20));
        bombs.add(new Bomb(170, 50));

        return new Level(safeZones, walls, coins, enemies, bombs, 60);
    }
    private static Level createLevel4() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(5, 60, 20, 20, 10, 70));
        safeZones.add(new IntermediateSafeZone(65, 5, 20, 20, 75, 15));
        safeZones.add(new IntermediateSafeZone(130, 80, 20, 20, 140, 90));
        safeZones.add(new FinalSafeZone(195, 60, 20, 20, 200, 70));

        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(0, 0, 5, 150));
        walls.add(new Wall(215, 0, 5, 150));
        walls.add(new Wall(25, 30, 8, 110));
        walls.add(new Wall(55, 0, 8, 80));
        walls.add(new Wall(55, 100, 8, 50));
        walls.add(new Wall(100, 25, 60, 8));
        walls.add(new Wall(100, 110, 60, 8));
        walls.add(new Wall(155, 33, 8, 75));
        walls.add(new Wall(175, 0, 8, 50));
        walls.add(new Wall(175, 90, 8, 60));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(35, 15));
        coins.add(new Coin(35, 85));
        coins.add(new Coin(75, 90));
        coins.add(new SkinCoin(120, 60, PlayerType.BLINKY));
        coins.add(new SkinCoin(130, 90, PlayerType.INKY));
        coins.add(new SkinCoin(165, 60, PlayerType.CLYDE));
        coins.add(new Coin(185, 20));
        coins.add(new Coin(185, 80));
        coins.add(new Life(90, 80));
        coins.add(new Life(150, 15));

        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(30, 20, 8, 8, new LinearMovement(1.0, 0)));
        enemies.add(new Enemy(63, 85, 8, 8, new LinearMovement(0, 1.0)));
        enemies.add(new Enemy(100, 60, 8, 8, new LinearMovement(1.0, 0, 2.0)));
        enemies.add(new Enemy(120, 33, 8, 8, new LinearMovement(0, 1.0, 2.5)));
        enemies.add(new Enemy(163, 70, 8, 8, new LinearMovement(1.2, 0, 3.0)));
        enemies.add(new Enemy(75, 40, 8, 8, new CircularMovement(75, 40, 18, 0.08)));
        enemies.add(new Enemy(120, 80, 8, 8, new CircularMovement(120, 80, 25, 0.06)));
        enemies.add(new Enemy(163, 40, 8, 8, new CircularMovement(163, 40, 30, 0.07)));
        
        List<Bomb> bombs = new ArrayList<>();
        bombs.add(new Bomb(70, 70));
        bombs.add(new Bomb(130, 70));

        return new Level(safeZones, walls, coins, enemies, bombs, 60);
    }


}
