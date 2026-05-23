package domain;

public class Game {
    private static SoundCallback soundCallback = new SoundCallback() {
        public void playCoin() {}
        public void playDeath() {}
        public void playSfx() {}
        public void playNextLevel() {}
    };

    public static void setSoundCallback(SoundCallback cb) { soundCallback = cb; }

    private Player player;
    private Player player2;
    private Level level;
    private int score;
    private int score2;
    private int deaths;
    private int deaths2;
    private int currentLevel;
    private boolean gameCompleted;
    private boolean gameOver;
    private boolean pendingBoard;
    private SafeZone lastCheckpoint;
    private int levelTimeRemaining;
    private boolean isPvP;
    private boolean levelCompleted; // Bandera: true cuando el jugador completa el nivel actual
    
    /**
     * Constructor for game, set current level in 1, gameCompleted in false and loads the level 1
     * @name Game
     */
    public Game() {
        currentLevel = 1;
        gameCompleted = false;
        gameOver = false;
        pendingBoard = false;
        loadLevel(currentLevel);
    }
    
    /**
     * loads a new level based on the levelNumber
     * @name loadLevel
     * @param levelNumber The level we want to load
     */
    private void loadLevel(int levelNumber) {
        int savedShieldCharges = (player != null) ? player.getShieldCharges() : 0;
        PlayerType savedType = (player != null) ? player.getOriginalType() : PlayerType.BLINKY;

        level = LevelFactory.createLevel(levelNumber);
        lastCheckpoint = null;
        levelTimeRemaining = level.getTimeLimit() * 60;

        for (SafeZone sz : level.getSafeZones()) {
            if (sz.isStart()) {
                player = new Player(sz.getRespawnX(), sz.getRespawnY(), savedType);
                player.setRespawnPoint(sz.getRespawnX(), sz.getRespawnY());
                player.setShieldCharges(savedShieldCharges);
                break;
            }
        }
    }
    
    /**
     * Updates the current level and checks possible collisions
     * @name update
     */
    public void update() {
        if (gameCompleted || gameOver) return;

        if (levelTimeRemaining > 0) {
            levelTimeRemaining--;
        } else {
            gameOver = true;
            return;
        }

        level.update();
        player.updateInvincibility();

        for (Bomb b : level.getBombs()) {
            b.updateExplosion();
        }

        checkCollisions();
        
        if (player.isAI()) {
            player.updateAI(level.getWalls(), level.getCoins(), level.getEnemies(), level.getSafeZones());
        }
    }
    
    /**
     * Checks if player is colliding with coins, safeZones or enemies
     * @name checkCollisions	
     */
    private void checkCollisions() {
        // Coins (including SkinCoin and Life, which both extend Coin)
        for (Coin c : level.getCoins()) {
            if (!c.isCollected() && player.collides(c)) {
                c.collect();
                c.onCollect(player, soundCallback);
                if (c.isRequired()) score++;
            }
        }

        // Safe Zones — cada subclase de SafeZone define su comportamiento vía polimorfismo
        for (SafeZone sz : level.getSafeZones()) {
            if (player.collides(sz) && sz.onPlayerEnter1P(this, player)) {
                return; // La zona detuvo el procesamiento (ej: nivel completado)
            }
        }

        // Safe zone protection: si el jugador está dentro de cualquier safe zone, ignora daño
        boolean playerSafe = isInSafeZone(player);

        // Enemies
        for (Enemy e : level.getEnemies()) {
            if (!playerSafe && player.collides(e) && e.isAlive()) {
                if (player.takeDamage()) {
                    // Muerte real
                    deaths++;
                    soundCallback.playDeath();
                    if (lastCheckpoint != null && lastCheckpoint.hasSavedState()) {
                        lastCheckpoint.restoreLevelState(level, player);
                    } else {
                        loadLevel(currentLevel);
                        lastCheckpoint = null;
                    }
                }
                return;
            }
        }

        // Bombs - explosion zone damages player every frame
        for (Bomb b : level.getBombs()) {
            if (!playerSafe && b.isExploding() && player.collides(b.getExplosionX(), b.getExplosionY(),
                    b.getExplosionWidth(), b.getExplosionHeight())) {
                if (player.takeDamage()) {
                    deaths++;
                    if (lastCheckpoint != null && lastCheckpoint.hasSavedState()) {
                        lastCheckpoint.restoreLevelState(level, player);
                    } else {
                        loadLevel(currentLevel);
                        lastCheckpoint = null;
                    }
                }
                return;
            }
        }

        // Bombs - initial contact with unexploded bomb
        for (Bomb b : level.getBombs()) {
            if (!b.isExploded() && player.collides(b)) {
                b.explode();
                soundCallback.playSfx();
                return;
            }
        }

        // Bomb vs Enemy - direct collision + explosion zone
        for (Bomb b : level.getBombs()) {
            if (!b.isExploded() && !b.isExploding()) {
                for (Enemy e : level.getEnemies()) {
                    if (b.collides(e) && e.isAlive()) {
                        b.explode();
                        soundCallback.playSfx();
                        e.setAlive(false);
                        break;
                    }
                }
            }
            if (b.isExploding()) {
                for (Enemy e : level.getEnemies()) {
                    if (e.isAlive() && e.collides(b.getExplosionX(), b.getExplosionY(),
                            b.getExplosionWidth(), b.getExplosionHeight())) {
                        e.setAlive(false);
                    }
                }
            }
        }
    }

    
    // Verifica si el jugador está dentro de alguna safe zone (protección contra enemigos/bombas)
    private boolean isInSafeZone(Player p) {
        for (SafeZone sz : level.getSafeZones()) {
            if (p.collides(sz)) return true;
        }
        return false;
    }

    // Crea un Memento (DTO) con el estado actual de la partida (single-player)
    public Memento toMemento() {
        return new Memento(this);
    }

    // Restaura el estado de la partida desde un Memento
    public void fromMemento(Memento data) {
        this.level = data.getLevel();
        this.currentLevel = data.getCurrentLevel();
        this.levelTimeRemaining = data.getLevelTimeRemaining();
        this.player = data.getPlayer1();
        this.deaths = data.getDeaths1();
        this.score = data.getScore1();
        this.player2 = data.getPlayer2();
        this.deaths2 = data.getDeaths2();
        this.score2 = data.getScore2();
        this.gameCompleted = data.isGameCompleted();
        this.gameOver = data.isGameOver();
        this.pendingBoard = data.isPendingBoard();
        this.isPvP = data.isPvP();
        this.lastCheckpoint = null;
        if (data.hasCheckpoint()) {
            for (SafeZone sz : level.getSafeZones()) {
                if (sz.isIntermediate()) {
                    sz.saveLevelState(level, player);
                    this.lastCheckpoint = sz;
                    break;
                }
            }
            player.setPosition(data.getCheckpointX(), data.getCheckpointY());
        }
    }

    // -- Métodos package-private usados por las subclases de SafeZone --

    public void setPlayer(Player player) { this.player = player; }

    // Guarda la referencia al último checkpoint alcanzado
    void setLastCheckpoint(SafeZone sz) { this.lastCheckpoint = sz; }

    // Completa el nivel actual: suena, marca banderas y verifica si es el último
    void completeLevel() {
        soundCallback.playNextLevel();
        levelCompleted = true;
        if (currentLevel >= LevelFactory.getTotalLevels()) {
            pendingBoard = true;
        }
    }

    /**
     * Reads current key states and applies the resulting movement vector
     * to the player, accounting for wall collision.
     * Skips when the player is AI-controlled (movement handled by updateAI()).
     */
    public void updateInput(boolean up, boolean down, boolean left, boolean right) {
        if (player.isAI()) return;
        double dx = 0, dy = 0;
        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;
        player.move(dx, dy, level.getWalls());
    }

    // Devuelve true si el nivel actual acaba de ser completado (el jugador llegó a la meta)
    public boolean isLevelCompleted() { return levelCompleted; }
    // Devuelve true si es el último nivel del modo principal (nivel 4 de 5, excluye el mini nivel)
    public boolean isLastLevel() { return currentLevel >= LevelFactory.getTotalLevels() - 1; }

    // Avanza al siguiente nivel: incrementa currentLevel, carga el nuevo nivel y resetea la bandera
    public void advanceToNextLevel() {
        levelCompleted = false; // Resetea la bandera para el nuevo nivel
        currentLevel++; // Pasa al siguiente nivel
        loadLevel(currentLevel); // Carga el nivel con la fábrica de niveles
    }

    // Marca el juego como completado (usado cuando el usuario elige "Finish" en el último nivel)
    public void finishGame() {
        levelCompleted = false; // Limpia la bandera de nivel completado
        gameCompleted = true; // Activa gameCompleted para que la vista detecte el fin del juego
    }

    // Reinicia el nivel actual (game over o muerte sin checkpoint)
    public void restartLevel() {
        gameOver = false;
        levelCompleted = false;
        pendingBoard = false;
        loadLevel(currentLevel);
    }

    // Cambia al nivel indicado (1-based) al iniciar una partida nueva
    public void setStartingLevel(int level) {
        if (level >= 1 && level <= LevelFactory.getTotalLevels()) {
            currentLevel = level;
            loadLevel(level);
        }
    }

    //Getters
    public boolean isGameCompleted() { return gameCompleted; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPendingBoard() { return pendingBoard; }
    public Player getPlayer() { return player; }
    public Player getPlayer2() { return player2; }
    public Level getLevel() { return level; }
    public int getDeaths() { return deaths; }
    public int getDeaths2() { return deaths2; }
    public int getScore() { return score; }
    public int getScore2() { return score2; }
    public int getCurrentLevel() { return currentLevel; }
    public boolean isPvP() { return isPvP; }
    public int getLevelTimeRemaining() { return levelTimeRemaining; }
    public int getLevelTimeRemainingSeconds() { return levelTimeRemaining / 60; }
    public SafeZone getLastCheckpoint() { return lastCheckpoint; }

}