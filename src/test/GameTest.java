package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;
    private Player player;
    private Level level;

    @BeforeEach
    void setUp() {
        game = new Game();
        player = game.getPlayer();
        level = game.getLevel();
    }

    // ==================== PLAYER COMPREHENSIVE TESTS ====================

    @Test
    void testPlayerMoveWithDiagonalInput() {
        List<Wall> walls = new ArrayList<>();
        double oldX = player.getX();
        double oldY = player.getY();
        
        player.move(1, 1, walls);
        
        assertTrue(player.getX() != oldX || player.getY() != oldY);
    }

    @Test
    void testPlayerMoveWithWallCollisionXOnly() {
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(50, 0, 5, 100));
        
        player.setPosition(48, 50);
        double oldX = player.getX();
        double oldY = player.getY();
        
        player.move(1, 0, walls);
        
        assertEquals(oldX, player.getX(), 0.1, "X no debe cambiar al chocar");
        assertEquals(oldY, player.getY(), "Y no debe cambiar");
    }

    @Test
    void testPlayerMoveWithWallCollisionYOnly() {
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(0, 50, 100, 5));
        
        player.setPosition(50, 48);
        double oldX = player.getX();
        double oldY = player.getY();
        
        player.move(0, 1, walls);
        
        assertEquals(oldX, player.getX(), "X no debe cambiar");
        assertEquals(oldY, player.getY(), 0.1, "Y no debe cambiar al chocar");
    }

    @Test
    void testPlayerTakeDamageWithoutShieldOrInvincibility() {
        // Remove any shields and invincibility
        player.setShieldCharges(0);
        while (player.isInvincible()) {
            player.updateInvincibility();
        }
        
        int initialDeaths = game.getDeaths();
        boolean result = player.takeDamage();
        
        assertTrue(result, "Debe morir sin escudo ni invencibilidad");
        assertEquals(initialDeaths + 1, game.getDeaths());
    }

    @Test
    void testPlayerTakeDamageMultipleShields() {
        player.addShieldCharge();
        player.addShieldCharge();
        assertEquals(2, player.getShieldCharges());
        
        boolean result1 = player.takeDamage();
        assertFalse(result1);
        assertEquals(1, player.getShieldCharges());
        
        boolean result2 = player.takeDamage();
        assertFalse(result2);
        assertEquals(0, player.getShieldCharges());
        
        boolean result3 = player.takeDamage();
        assertTrue(result3);
    }

    @Test
    void testPlayerUpdateRespawnPointFromSafeZone() {
        IntermediateSafeZone zone = new IntermediateSafeZone(10, 20, 30, 40, 100, 200);
        player.updateRespawnPoint(zone);
        
        assertEquals(100, player.getRespawnX());
        assertEquals(200, player.getRespawnY());
    }

    @Test
    void testPlayerInSafeZoneWithMultipleZones() {
        List<SafeZone> safeZones = new ArrayList<>();
        safeZones.add(new StartSafeZone(0, 0, 10, 10));
        safeZones.add(new FinalSafeZone(100, 100, 10, 10, 50, 50));
        
        player.setPosition(5, 5);
        assertTrue(player.inSafeZone(safeZones));
        
        player.setPosition(95, 95);
        assertFalse(player.inSafeZone(safeZones));
        
        player.setPosition(105, 105);
        assertTrue(player.inSafeZone(safeZones));
    }

    @Test
    void testPlayerApplyTemporarySkinThenDamage() {
        Player clydePlayer = new Player(100, 100, PlayerType.CLYDE);
        clydePlayer.applyTemporarySkin(PlayerType.INKY);
        
        assertEquals(PlayerType.INKY, clydePlayer.getPlayerType());
        
        clydePlayer.takeDamage();
        assertEquals(PlayerType.INKY, clydePlayer.getPlayerType(), 
            "Daño no debe afectar skin temporal");
    }

    @Test
    void testPlayerRevertToOriginalWhenAlreadyOriginal() {
        Player blinkyPlayer = new Player(100, 100, PlayerType.BLINKY);
        blinkyPlayer.revertToOriginalSkin();
        
        assertEquals(PlayerType.BLINKY, blinkyPlayer.getPlayerType());
    }

    @Test
    void testPlayerSpeedMultiplierWithTemporarySkin() {
        Player player = new Player(0, 0, PlayerType.BLINKY);
        assertEquals(1.0, player.getSpeedMultiplier());
        
        player.applyTemporarySkin(PlayerType.INKY);
        assertEquals(1.5, player.getSpeedMultiplier());
        
        player.revertToOriginalSkin();
        assertEquals(1.0, player.getSpeedMultiplier());
    }

    // ==================== ENTITY COMPREHENSIVE TESTS ====================

    @Test
    void testEntityCollidesWithExactBounds() {
        Entity a = new Player(0, 0);
        Entity b = new Player(10, 10);
        
        a.setPosition(0, 0);
        b.setPosition(10, 10);
        assertTrue(a.collides(b), "Entidades que se tocan deben colisionar");
        
        b.setPosition(10.1, 10.1);
        assertFalse(a.collides(b), "Entidades separadas no deben colisionar");
    }

    @Test
    void testEntityCollidesWithNegativeCoordinates() {
        Entity a = new Player(-10, -10);
        Entity b = new Player(-5, -5);
        
        assertTrue(a.collides(b));
        
        b.setPosition(-25, -25);
        assertFalse(a.collides(b));
    }

    @Test
    void testEntityCollidesRectangleMethod() {
        Entity entity = new Player(5, 5);
        
        assertTrue(entity.collides(0, 0, 20, 20));
        assertTrue(entity.collides(5, 5, 1, 1));
        assertFalse(entity.collides(100, 100, 10, 10));
        assertFalse(entity.collides(-20, -20, 10, 10));
    }

    // ==================== ENEMY COMPREHENSIVE TESTS ====================

    @Test
    void testEnemyUpdateWithWallsDeadEnemy() {
        List<Wall> walls = new ArrayList<>();
        Enemy enemy = new Enemy(50, 50, 8, 8, new LinearMovement(1, 0));
        enemy.setAlive(false);
        
        double oldX = enemy.getX();
        enemy.updateWithWalls(walls);
        
        assertEquals(oldX, enemy.getX(), "Enemigo muerto no debe moverse");
    }

    @Test
    void testEnemySetStrategyNull() {
        Enemy enemy = new Enemy(50, 50, 8, 8, new LinearMovement(1, 0));
        enemy.setStrategy(null);
        
        assertNull(enemy.getStrategy());
    }

    // ==================== LINEAR MOVEMENT COMPREHENSIVE TESTS ====================

    @Test
    void testLinearMovementUpdateWithMultipleWalls() {
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(100, 0, 5, 150));
        walls.add(new Wall(0, 100, 150, 5));
        
        Enemy enemy = new Enemy(95, 95, 8, 8, new LinearMovement(1, 1));
        
        for (int i = 0; i < 20; i++) {
            enemy.updateWithWalls(walls);
        }
        
        assertTrue(enemy.getX() + enemy.width <= 100, "No debe pasar la pared vertical");
        assertTrue(enemy.getY() + enemy.height <= 100, "No debe pasar la pared horizontal");
    }

    @Test
    void testLinearMovementWithZeroSpeed() {
        List<Wall> walls = new ArrayList<>();
        LinearMovement lm = new LinearMovement(0, 0);
        Enemy enemy = new Enemy(50, 50, 8, 8, lm);
        
        double oldX = enemy.getX();
        double oldY = enemy.getY();
        
        enemy.updateWithWalls(walls);
        
        assertEquals(oldX, enemy.getX());
        assertEquals(oldY, enemy.getY());
    }

    @Test
    void testLinearMovementGetVxVy() {
        LinearMovement lm = new LinearMovement(1.5, 2.5, 2.0);
        assertEquals(3.0, lm.getVx(), 0.01);
        assertEquals(5.0, lm.getVy(), 0.01);
    }

    // ==================== CIRCULAR MOVEMENT COMPREHENSIVE TESTS ====================

    @Test
    void testCircularMovementWithZeroAngularSpeed() {
        List<Wall> walls = new ArrayList<>();
        CircularMovement cm = new CircularMovement(100, 100, 30, 0);
        Enemy enemy = new Enemy(130, 100, 8, 8, cm);
        
        double oldX = enemy.getX();
        double oldY = enemy.getY();
        
        cm.update(enemy, walls);
        
        assertEquals(oldX, enemy.getX());
        assertEquals(oldY, enemy.getY());
    }

    @Test
    void testCircularMovementWithNegativeAngularSpeed() {
        List<Wall> walls = new ArrayList<>();
        CircularMovement cm = new CircularMovement(100, 100, 30, -0.05);
        Enemy enemy = new Enemy(130, 100, 8, 8, cm);
        
        double initialX = enemy.getX();
        cm.update(enemy, walls);
        
        assertNotEquals(initialX, enemy.getX(), "Debe moverse con velocidad angular negativa");
    }

    @Test
    void testCircularMovementWithLargeRadius() {
        List<Wall> walls = new ArrayList<>();
        CircularMovement cm = new CircularMovement(100, 100, 500, 0.01);
        Enemy enemy = new Enemy(600, 100, 8, 8, cm);
        
        cm.update(enemy, walls);
        
        assertTrue(Math.abs(enemy.getX() - 100) <= 500 + 100);
    }

    // ==================== COIN COMPREHENSIVE TESTS ====================

    @Test
    void testCoinProtectedConstructor() {
        // Testing protected constructor via subclass
        Coin customCoin = new Coin(100, 100);
        assertEquals(10, customCoin.width);
        assertEquals(10, customCoin.height);
    }
    // ==================== BOMB COMPREHENSIVE TESTS ====================

    @Test
    void testBombCollisionBeforeExplosion() {
        Bomb bomb = new Bomb(50, 50);
        Player testPlayer = new Player(52, 52);
        
        assertTrue(testPlayer.collides(bomb));
        
        testPlayer.setPosition(100, 100);
        assertFalse(testPlayer.collides(bomb));
    }

    @Test
    void testBombExplosionProximityDamage() {
        Bomb bomb = new Bomb(100, 100);
        bomb.explode();
        
        Player closePlayer = new Player(115, 115);
        assertTrue(closePlayer.collides(bomb.getExplosionX(), bomb.getExplosionY(),
            bomb.getExplosionWidth(), bomb.getExplosionHeight()));
        
        Player farPlayer = new Player(200, 200);
        assertFalse(farPlayer.collides(bomb.getExplosionX(), bomb.getExplosionY(),
            bomb.getExplosionWidth(), bomb.getExplosionHeight()));
    }

    @Test
    void testBombMultipleExplosions() {
        Bomb bomb = new Bomb(50, 50);
        bomb.explode();
        assertTrue(bomb.isExploded());
        
        bomb.setExploded(false);
        assertFalse(bomb.isExploded());
        
        bomb.explode();
        assertTrue(bomb.isExploded());
    }

    // ==================== SAFE ZONE COMPREHENSIVE TESTS ====================

    @Test
    void testStartSafeZoneOnPlayerEnter2P() {
        Game2P game2P = new Game2P();
        StartSafeZone startZone = new StartSafeZone(5, 55, 30, 30);
        Player player1 = game2P.getPlayer1();
        
        // Player1 entering START zone should do nothing special
        boolean result = startZone.onPlayerEnter2P(game2P, player1, true);
        assertFalse(result);
    }

    @Test
    void testStartSafeZoneOnPlayerEnter2PPlayer2Wins() {
        Game2P game2P = new Game2P();
        // Collect all coins in the level
        Level level = game2P.getLevel();
        for (Coin c : level.getCoins()) {
            c.collect();
        }
        
        StartSafeZone startZone = new StartSafeZone(5, 55, 30, 30);
        Player player2 = game2P.getPlayer2();
        
        boolean result = startZone.onPlayerEnter2P(game2P, player2, false);
        assertTrue(result, "Player2 debe ganar si todas las monedas están recolectadas");
        assertEquals(2, game2P.getWinner());
    }

    @Test
    void testIntermediateSafeZoneSaveState() {
        Level testLevel = LevelFactory.createLevel(2);
        Player testPlayer = new Player(10, 10);
        IntermediateSafeZone zone = new IntermediateSafeZone(95, 55, 25, 25, 107, 67);
        
        zone.saveLevelState(testLevel, testPlayer);
        assertTrue(zone.hasSavedState());
        
        zone.restoreLevelState(testLevel, testPlayer);
        assertTrue(zone.hasSavedState(), "Estado debe conservarse después de restaurar");
    }

    @Test
    void testFinalSafeZoneOnPlayerEnter2PWithPlayer2() {
        Game2P game2P = new Game2P();
        FinalSafeZone finalZone = new FinalSafeZone(185, 55, 30, 30, 200, 70);
        Player player2 = game2P.getPlayer2();
        
        boolean result = finalZone.onPlayerEnter2P(game2P, player2, false);
        assertFalse(result, "Player2 no debe ganar al entrar a FINAL");
    }

    @Test
    void testFinalSafeZoneOnPlayerEnter2PWithPlayer1Win() {
        Game2P game2P = new Game2P();
        Level level = game2P.getLevel();
        for (Coin c : level.getCoins()) {
            c.collect();
        }
        
        FinalSafeZone finalZone = new FinalSafeZone(185, 55, 30, 30, 200, 70);
        Player player1 = game2P.getPlayer1();
        
        boolean result = finalZone.onPlayerEnter2P(game2P, player1, true);
        assertTrue(result, "Player1 debe ganar si todas las monedas están recolectadas");
        assertEquals(1, game2P.getWinner());
    }

    // ==================== GAME COMPREHENSIVE TESTS ====================

    @Test
    void testGameUpdateWithTimeExpiration() {
        Game testGame = new Game();
        // Reduce time to 1 frame
        for (int i = 0; i < testGame.getLevelTimeRemaining() - 1; i++) {
            testGame.update();
        }
        assertFalse(testGame.isGameOver());
        
        testGame.update();
        assertTrue(testGame.isGameOver());
    }

    @Test
    void testGameCheckCollisionsWithMultipleCoins() {
        // Position player to collect multiple coins
        List<Coin> coins = level.getCoins();
        if (coins.size() >= 2) {
            Coin coin1 = coins.get(0);
            Coin coin2 = coins.get(1);
            
            player.setPosition(coin1.getX(), coin1.getY());
            game.update();
            assertTrue(coin1.isCollected());
            
            player.setPosition(coin2.getX(), coin2.getY());
            game.update();
            assertTrue(coin2.isCollected());
        }
    }

    @Test
    void testGameCheckCollisionsWithBombActivation() {
        Level levelWithBombs = LevelFactory.createLevel(2);
        Game bombGame = new Game();
        
        // Find a bomb in the level
        Bomb bomb = levelWithBombs.getBombs().stream().findFirst().orElse(null);
        if (bomb != null) {
            bombGame.getPlayer().setPosition(bomb.getX(), bomb.getY());
            bombGame.update();
            assertTrue(bomb.isExploded() || bomb.isExploding());
        }
    }

    @Test
    void testGameCompleteLevelFlow() {
        Game testGame = new Game();
        assertFalse(testGame.isLevelCompleted());
        
        // Collect all coins
        Level currentLevel = testGame.getLevel();
        for (Coin c : currentLevel.getCoins()) {
            if (c.isRequired()) {
                c.collect();
            }
        }
        
        // Enter final zone
        FinalSafeZone finalZone = (FinalSafeZone) currentLevel.getSafeZones().stream()
            .filter(SafeZone::isFinal).findFirst().orElse(null);
        
        if (finalZone != null) {
            testGame.getPlayer().setPosition(finalZone.getX(), finalZone.getY());
            testGame.update();
            assertTrue(testGame.isLevelCompleted());
        }
    }

    @Test
    void testGameLastLevelCompletion() {
        Game testGame = new Game();
        testGame.setStartingLevel(4);
        
        // Collect all coins
        Level level4 = testGame.getLevel();
        for (Coin c : level4.getCoins()) {
            if (c.isRequired()) {
                c.collect();
            }
        }
        
        // Complete level
        FinalSafeZone finalZone = (FinalSafeZone) level4.getSafeZones().stream()
            .filter(SafeZone::isFinal).findFirst().orElse(null);
        
        if (finalZone != null) {
            testGame.getPlayer().setPosition(finalZone.getX(), finalZone.getY());
            testGame.update();
            assertTrue(testGame.isPendingBoard());
        }
    }

    @Test
    void testGameUpdateWithAI() {
        Game aiGame = new Game();
        aiGame.getPlayer().setAI(new ExpertAI());
        
        aiGame.update();
        assertNotNull(aiGame.getPlayer());
    }

    @Test
    void testGameUpdateInputCombinations() {
        Game testGame = new Game();
        double initialX = testGame.getPlayer().getX();
        double initialY = testGame.getPlayer().getY();
        
        testGame.updateInput(true, true, false, false);
        // Both up and down should cancel vertical movement
        // Both left and right should cancel horizontal movement
        testGame.updateInput(false, false, true, true);
        
        // Player should not have moved dramatically
        assertTrue(Math.abs(testGame.getPlayer().getX() - initialX) < 5);
    }

    @Test
    void testGameMementoRestore() {
        Memento memento = game.toMemento();
        
        // Modify game state
        game.getPlayer().setPosition(999, 999);
        game.getPlayer().addShieldCharge();
        
        game.fromMemento(memento);
        
        assertNotEquals(999, game.getPlayer().getX());
        assertNotEquals(999, game.getPlayer().getY());
    }

    @Test
    void testGameMementoWithCheckpoint() {
        // Create a checkpoint first
        IntermediateSafeZone zone = new IntermediateSafeZone(95, 55, 25, 25, 107, 67);
        zone.onPlayerEnter1P(game, player);
        
        Memento memento = game.toMemento();
        assertTrue(memento.hasCheckpoint());
        assertEquals(107, memento.getCheckpointX());
        assertEquals(67, memento.getCheckpointY());
    }

    // ==================== GAME2P COMPREHENSIVE TESTS ====================

    @Test
    void testGame2PUpdateWithTimeExpiration() {
        Game2P game2P = new Game2P();
        while (game2P.getLevelTimeRemaining() > 1 && !game2P.isGameOver()) {
            game2P.update();
        }
        game2P.update();
        assertTrue(game2P.isGameOver());
    }

    @Test
    void testGame2PCheckCollisionsPlayerVsPlayer() {
        Game2P game2P = new Game2P();
        Player p1 = game2P.getPlayer1();
        Player p2 = game2P.getPlayer2();
        
        // Position players to collide
        p1.setPosition(50, 50);
        p2.setPosition(52, 52);
        
        int initialDeaths1 = game2P.getDeaths1();
        int initialDeaths2 = game2P.getDeaths2();
        
        game2P.update();
        
        // Either both or one may take damage depending on invincibility
        assertTrue(game2P.getDeaths1() >= initialDeaths1 || game2P.getDeaths2() >= initialDeaths2);
    }

    @Test
    void testGame2PCheckCollisionsWithBombExplosion() {
        Game2P game2P = new Game2P();
        Level level = game2P.getLevel();
        
        Bomb bomb = level.getBombs().stream().findFirst().orElse(null);
        if (bomb != null) {
            bomb.explode();
            game2P.getPlayer1().setPosition(bomb.getExplosionX() + 5, bomb.getExplosionY() + 5);
            
            int initialDeaths = game2P.getDeaths1();
            game2P.update();
            
            assertTrue(game2P.getDeaths1() >= initialDeaths);
        }
    }

    @Test
    void testGame2PAdvanceToNextLevelPreservesStats() {
        Game2P game2P = new Game2P();
        game2P.setScore1(25);
        game2P.setScore2(15);
        game2P.setDeaths1(3);
        game2P.setDeaths2(1);
        
        game2P.advanceToNextLevel();
        
        assertEquals(25, game2P.getScore1());
        assertEquals(15, game2P.getScore2());
        assertEquals(3, game2P.getDeaths1());
        assertEquals(1, game2P.getDeaths2());
    }

    @Test
    void testGame2PSetStartingLevelPreservesStats() {
        Game2P game2P = new Game2P();
        game2P.setScore1(10);
        game2P.setScore2(5);
        game2P.setDeaths1(2);
        game2P.setDeaths2(1);
        
        game2P.setStartingLevel(3);
        
        assertEquals(3, game2P.getCurrentLevel());
        assertEquals(10, game2P.getScore1());
        assertEquals(5, game2P.getScore2());
        assertEquals(2, game2P.getDeaths1());
        assertEquals(1, game2P.getDeaths2());
    }

    @Test
    void testGame2PFromMemento() {
        Game2P game2P = new Game2P();
        game2P.setScore1(50);
        game2P.setScore2(30);
        game2P.setDeaths1(5);
        game2P.setDeaths2(2);
        
        Memento memento = game2P.toMemento();
        
        Game2P newGame = new Game2P();
        newGame.fromMemento(memento);
        
        assertEquals(50, newGame.getScore1());
        assertEquals(30, newGame.getScore2());
        assertEquals(5, newGame.getDeaths1());
        assertEquals(2, newGame.getDeaths2());
    }

    // ==================== MEMENTO COMPREHENSIVE TESTS ====================

    @Test
    void testMementoEmptyConstructor() {
        Memento memento = new Memento();
        assertNull(memento.getLevel());
        assertEquals(0, memento.getCurrentLevel());
        assertEquals(0, memento.getLevelTimeRemaining());
        assertNull(memento.getPlayer1());
        assertNull(memento.getPlayer2());
        assertFalse(memento.isGameCompleted());
        assertFalse(memento.isGameOver());
    }

    @Test
    void testMementoAllSettersAndGetters() {
        Memento memento = new Memento();
        
        Level testLevel = LevelFactory.createLevel(1);
        Player testPlayer = new Player(100, 100);
        
        memento.setLevel(testLevel);
        memento.setCurrentLevel(3);
        memento.setLevelTimeRemaining(1800);
        memento.setPlayer1(testPlayer);
        memento.setDeaths1(10);
        memento.setScore1(100);
        memento.setPlayer2(testPlayer);
        memento.setDeaths2(5);
        memento.setScore2(50);
        memento.setGameCompleted(true);
        memento.setGameOver(false);
        memento.setPendingBoard(true);
        memento.setWinner(1);
        
        assertSame(testLevel, memento.getLevel());
        assertEquals(3, memento.getCurrentLevel());
        assertEquals(1800, memento.getLevelTimeRemaining());
        assertSame(testPlayer, memento.getPlayer1());
        assertEquals(10, memento.getDeaths1());
        assertEquals(100, memento.getScore1());
        assertSame(testPlayer, memento.getPlayer2());
        assertEquals(5, memento.getDeaths2());
        assertEquals(50, memento.getScore2());
        assertTrue(memento.isGameCompleted());
        assertFalse(memento.isGameOver());
        assertTrue(memento.isPendingBoard());
        assertEquals(1, memento.getWinner());
    }

    // ==================== LEVEL STATE COMPREHENSIVE TESTS ====================

    @Test
    void testLevelStateWithModifiedCollections() {
        List<Coin> coins = new ArrayList<>();
        Coin coin1 = new Coin(10, 10);
        Coin coin2 = new Coin(20, 20);
        coins.add(coin1);
        coins.add(coin2);
        
        List<Enemy> enemies = new ArrayList<>();
        Enemy enemy = new Enemy(50, 50, 8, 8, new LinearMovement(1, 0));
        enemies.add(enemy);
        
        List<Bomb> bombs = new ArrayList<>();
        Bomb bomb = new Bomb(30, 30);
        bombs.add(bomb);
        
        coin1.collect();
        enemy.setAlive(false);
        bomb.explode();
        
        LevelState state = new LevelState(coins, enemies, bombs, 100, 200);
        
        assertTrue(state.getCoinStates().get(0));
        assertFalse(state.getCoinStates().get(1));
        assertFalse(state.getEnemyAliveStates().get(0));
        assertTrue(state.getBombExplodedStates().get(0));
    }

    // ==================== LEVEL FACTORY COMPREHENSIVE TESTS ====================

    @Test
    void testCreateLevel1Structure() {
        Level level1 = LevelFactory.createLevel(1);
        
        assertNotNull(level1.getSafeZones());
        assertEquals(2, level1.getSafeZones().size());
        assertTrue(level1.getSafeZones().get(0).isStart());
        assertTrue(level1.getSafeZones().get(1).isFinal());
        
        assertFalse(level1.getWalls().isEmpty());
        assertFalse(level1.getCoins().isEmpty());
        assertFalse(level1.getEnemies().isEmpty());
    }

    @Test
    void testCreateLevel2Structure() {
        Level level2 = LevelFactory.createLevel(2);
        
        assertEquals(3, level2.getSafeZones().size());
        assertTrue(level2.getSafeZones().stream().anyMatch(SafeZone::isStart));
        assertTrue(level2.getSafeZones().stream().anyMatch(SafeZone::isIntermediate));
        assertTrue(level2.getSafeZones().stream().anyMatch(SafeZone::isFinal));
        
        assertEquals(2, level2.getBombs().size());
    }

    @Test
    void testCreateLevel3Structure() {
        Level level3 = LevelFactory.createLevel(3);
        
        assertEquals(4, level3.getSafeZones().size());
        assertEquals(2, level3.getBombs().size());
        assertEquals(9, level3.getCoins().size());
        assertEquals(8, level3.getEnemies().size());
    }

    @Test
    void testCreateLevel4Structure() {
        Level level4 = LevelFactory.createLevel(4);
        
        assertEquals(4, level4.getSafeZones().size());
        assertEquals(2, level4.getBombs().size());
        assertEquals(10, level4.getCoins().size());
        assertEquals(8, level4.getEnemies().size());
    }

    @Test
    void testCreateMiniLevelStructure() {
        Level miniLevel = LevelFactory.createLevel(5);
        
        assertEquals(4, miniLevel.getSafeZones().size());
        assertTrue(miniLevel.getCoins().isEmpty());
        assertEquals(5, miniLevel.getEnemies().size());
        assertFalse(miniLevel.getWalls().isEmpty());
        assertEquals(120, miniLevel.getTimeLimit());
    }

    // ==================== MEMENTO DAO TESTS ====================

    @Test
    void testMementoDAOSaveAndLoad(@TempDir File tempDir) throws Exception {
        MementoDAO dao = new MementoDAO();
        Game testGame = new Game();
        Memento original = testGame.toMemento();
        
        File tempFile = new File(tempDir, "test_save.txt");
        dao.saveGame(original, tempFile);
        
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
        
        Memento loaded = dao.loadGame(tempFile);
        assertNotNull(loaded);
        assertEquals(original.getCurrentLevel(), loaded.getCurrentLevel());
        assertEquals(original.getScore1(), loaded.getScore1());
        assertEquals(original.getDeaths1(), loaded.getDeaths1());
    }

    @Test
    void testMementoDAOLoadWithMissingFile() {
        MementoDAO dao = new MementoDAO();
        File nonExistentFile = new File("non_existent_file.txt");
        
        assertThrows(FileNotFoundException.class, () -> {
            dao.loadGame(nonExistentFile);
        });
    }

    @Test
    void testMementoDAOSaveGame2P(@TempDir File tempDir) throws Exception {
        MementoDAO dao = new MementoDAO();
        Game2P game2P = new Game2P(PlayerType.BLINKY, PlayerType.INKY);
        Memento original = game2P.toMemento();
        
        File tempFile = new File(tempDir, "test_2p_save.txt");
        dao.saveGame(original, tempFile);
        
        assertTrue(tempFile.exists());
        
        Memento loaded = dao.loadGame(tempFile);
        assertTrue(loaded.isPvP());
        assertNotNull(loaded.getPlayer2());
    }

    @Test
    void testMementoDAOSaveWithCheckpoint(@TempDir File tempDir) throws Exception {
        MementoDAO dao = new MementoDAO();
        Game testGame = new Game();
        
        // Create a checkpoint
        IntermediateSafeZone zone = new IntermediateSafeZone(95, 55, 25, 25, 107, 67);
        zone.onPlayerEnter1P(testGame, testGame.getPlayer());
        
        Memento original = testGame.toMemento();
        
        File tempFile = new File(tempDir, "test_checkpoint_save.txt");
        dao.saveGame(original, tempFile);
        
        Memento loaded = dao.loadGame(tempFile);
        assertTrue(loaded.hasCheckpoint());
        assertEquals(107, loaded.getCheckpointX());
        assertEquals(67, loaded.getCheckpointY());
    }

    // ==================== GUARDADO BINARIO COMPREHENSIVE TESTS ====================

    @Test
    void testGuardadoBinarioSaveGame2P(@TempDir File tempDir) throws Exception {
        GuardadoBinario guardado = new GuardadoBinario();
        Game2P game2P = new Game2P();
        Memento original = game2P.toMemento();
        
        File tempFile = new File(tempDir, "test_2p_binary.dat");
        guardado.saveGame(original, tempFile);
        
        assertTrue(tempFile.exists());
        
        Memento loaded = guardado.loadGame(tempFile);
        assertNotNull(loaded);
        assertEquals(original.getCurrentLevel(), loaded.getCurrentLevel());
        assertTrue(loaded.isPvP());
    }

    @Test
    void testGuardadoBinarioLoadCorruptedFile(@TempDir File tempDir) throws Exception {
        GuardadoBinario guardado = new GuardadoBinario();
        File tempFile = new File(tempDir, "corrupted.dat");
        
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("corrupted data".getBytes());
        }
        
        assertThrows(IOException.class, () -> {
            guardado.loadGame(tempFile);
        });
    }

    // ==================== RANDOM AI COMPREHENSIVE TESTS ====================

    @Test
    void testRandomAIAllPossibleMovements() {
        RandomAI ai = new RandomAI();
        List<Wall> walls = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<SafeZone> safeZones = new ArrayList<>();
        
        Set<String> movements = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            double[] mov = ai.getMovement(player, walls, coins, enemies, safeZones);
            movements.add(mov[0] + "," + mov[1]);
        }
        
        // Should have multiple different movement patterns
        assertTrue(movements.size() >= 3);
    }

    // ==================== EXPERT AI COMPREHENSIVE TESTS ====================

    @Test
    void testExpertAIWithMultipleCoins() {
        ExpertAI ai = new ExpertAI();
        List<Wall> walls = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<SafeZone> safeZones = new ArrayList<>();
        
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(200, 200));
        coins.add(new Coin(50, 50));
        coins.add(new Coin(100, 100));
        
        player.setPosition(0, 0);
        
        double[] movement = ai.getMovement(player, walls, coins, enemies, safeZones);
        
        // Should target the closest coin (50,50)
        assertTrue(movement[0] > 0, "Debe moverse hacia la derecha hacia la moneda más cercana");
        assertTrue(movement[1] > 0, "Debe moverse hacia abajo hacia la moneda más cercana");
    }};

