package domain;

import java.util.List;

/**
 * Lógica del juego para dos jugadores (PvP y PvM).
 * Maneja dos jugadores, niveles, puntuación, muertes y condiciones de victoria.
 */
public class Game2P {
    private static SoundCallback soundCallback = new SoundCallback() {
        public void playCoin() {}
        public void playDeath() {}
        public void playSfx() {}
        public void playNextLevel() {}
    };

    public static void setSoundCallback(SoundCallback cb) { soundCallback = cb; }

    // Dos jugadores independientes, cada uno con su tipo, posición y estadísticas
    private Player player1, player2;
    private Level level; // Nivel actual que se está jugando
    private int score1, score2; // Monedas recogidas por cada jugador
    private int deaths1, deaths2; // Muertes de cada jugador
    private int currentLevel; // Número de nivel actual (1-based)
    private boolean gameCompleted; // true cuando se completan todos los niveles
    private boolean gameOver; // true cuando el tiempo se agota
    private int levelTimeRemaining; // Tiempo restante del nivel en frames (60 frames = 1 segundo)
    private int winner; // 0=ninguno, 1=jugador1, 2=jugador2
    private SafeZone checkpoint; // Checkpoint guardado en zona INTERMEDIATE
    private boolean levelCompleted; // true cuando un jugador completa el nivel actual

    // Crea una partida con tipos por defecto (P1 rojo, P2 azul)
    public Game2P() {
        this(PlayerType.BLINKY, PlayerType.INKY);
    }

    // Crea una partida con los tipos de personaje especificados para cada jugador
    public Game2P(PlayerType p1Type, PlayerType p2Type) {
        currentLevel = 1; // Empieza en el nivel 1
        gameCompleted = false; 
        gameOver = false; 
        winner = 0; // Sin ganador aún
        loadLevel(currentLevel, p1Type, p2Type); // Carga el primer nivel con ambos jugadores
    }

    // Carga un nivel conservando los tipos originales de los jugadores existentes
    private void loadLevel(int levelNumber) {
        PlayerType t1 = (player1 != null) ? player1.getOriginalType() : PlayerType.BLINKY; // Tipo original de P1 o rojo por defecto
        PlayerType t2 = (player2 != null) ? player2.getOriginalType() : PlayerType.INKY; // Tipo original de P2 o azul por defecto
        loadLevel(levelNumber, t1, t2); // Delega al método con tipos explícitos
    }

    // Carga un nivel con tipos específicos, preservando escudos acumulados
    private void loadLevel(int levelNumber, PlayerType type1, PlayerType type2) {
        int savedShield1 = (player1 != null) ? player1.getShieldCharges() : 0; // Escudos acumulados de P1
        int savedShield2 = (player2 != null) ? player2.getShieldCharges() : 0; // Escudos acumulados de P2

        level = LevelFactory.createLevel(levelNumber); // Crea el nivel desde la fábrica
        levelTimeRemaining = level.getTimeLimit() * 60; // Convierte segundos a frames

        List<SafeZone> zones = level.getSafeZones();
        SafeZone start = null, end = null;
        for (SafeZone sz : zones) { // Busca las zonas de inicio y final
            if (sz.isStart()) start = sz; // Zona de salida (P1 empieza aquí)
            if (sz.isFinal()) end = sz; // Zona de meta (P2 empieza aquí)
        }

        if (start != null) { // Crea al jugador 1 en la zona de inicio
            player1 = new Player(start.getRespawnX(), start.getRespawnY(), type1);
            player1.setRespawnPoint(start.getRespawnX(), start.getRespawnY()); // Su respawn es la salida
            player1.setShieldCharges(savedShield1); // Restaura sus escudos acumulados
        }
        if (end != null) { // Crea al jugador 2 en la zona final
            player2 = new Player(end.getRespawnX(), end.getRespawnY(), type2);
            player2.setRespawnPoint(end.getRespawnX(), end.getRespawnY()); // Su respawn es la meta
            player2.setShieldCharges(savedShield2); // Restaura sus escudos acumulados
        }
    }

    // Actualiza el estado del juego en cada tick (16ms): tiempo, entidades, colisiones
    public void update() {
        if (gameCompleted || gameOver) return; // Si el juego terminó, no hace nada

        if (levelTimeRemaining > 0) {
            levelTimeRemaining--; // Decrementa el tiempo restante
        } else {
            gameOver = true; // Se acabó el tiempo
            return;
        }

        level.update(); // Actualiza enemigos y monedas del nivel
        player1.updateInvincibility(); // Reduce los frames de invencibilidad de P1
        player2.updateInvincibility(); // Reduce los frames de invencibilidad de P2

        for (Bomb b : level.getBombs()) {
            b.updateExplosion(); // Actualiza las explosiones de bombas activas
        }

        checkCollisions(); // Verifica todas las colisiones del frame actual
    }

    // Verifica todas las colisiones posibles entre jugadores y elementos del nivel
    private void checkCollisions() {
        // Coins: cada jugador recoge monedas (incluyendo SkinCoin y Life); las monedas con skin aplican efecto temporal
        for (Coin c : level.getCoins()) {
            if (!c.isCollected()) {
                if (player1.collides(c)) {
                    c.collect();
                    c.onCollect(player1, soundCallback);
                    if (c.isRequired()) score1++;
                } else if (player2.collides(c)) {
                    c.collect();
                    c.onCollect(player2, soundCallback);
                    if (c.isRequired()) score2++;
                }
            }
        }

        // Safe zones — cada subclase define su comportamiento vía polimorfismo
        for (SafeZone sz : level.getSafeZones()) {
            if (player1.collides(sz) && sz.onPlayerEnter2P(this, player1, true)) return;
            if (player2.collides(sz) && sz.onPlayerEnter2P(this, player2, false)) return;
        }

        // Safe zone protection: si un jugador está dentro de cualquier safe zone, ignora daño
        boolean p1Safe = isInSafeZone(player1);
        boolean p2Safe = isInSafeZone(player2);

        // Enemies: colisión causa daño o muerte, con los escudos como protección
        for (Enemy e : level.getEnemies()) {
            if (!e.isAlive()) continue; // Enemigos muertos no hacen daño
            if (!p1Safe && player1.collides(e) && player1.takeDamage()) { // P1 no tiene escudos: muere
                deaths1++; soundCallback.playDeath(); resetLevel(player1); return;
            }
            if (!p2Safe && player2.collides(e) && player2.takeDamage()) { // P2 no tiene escudos: muere
                deaths2++; soundCallback.playDeath(); resetLevel(player2); return;
            }
        }

        // Bombs: zona de explosión daña cada frame, contacto directo activa la explosión
        for (Bomb b : level.getBombs()) {
            if (b.isExploding()) { // Si la bomba está explotando, daña a quien esté dentro
                if (!p1Safe && player1.collides(b.getExplosionX(), b.getExplosionY(),
                        b.getExplosionWidth(), b.getExplosionHeight()) && player1.takeDamage()) {
                    deaths1++; soundCallback.playDeath(); resetLevel(player1); return; // P1 muere por explosión
                }
                if (!p2Safe && player2.collides(b.getExplosionX(), b.getExplosionY(),
                        b.getExplosionWidth(), b.getExplosionHeight()) && player2.takeDamage()) {
                    deaths2++; soundCallback.playDeath(); resetLevel(player2); return; // P2 muere por explosión
                }
            }
            if (!b.isExploded() && !b.isExploding()) { // Bomba sin explotar: el contacto la activa
                if (player1.collides(b)) { b.explode(); soundCallback.playSfx(); } // P1 activa la bomba
                if (player2.collides(b)) { b.explode(); soundCallback.playSfx(); } // P2 activa la bomba
            }
        }

        // Bomb vs Enemy: las bombas matan enemigos al explotar cerca de ellos
        for (Bomb b : level.getBombs()) {
            if (!b.isExploded() && !b.isExploding()) { // Bomba sin explotar
                for (Enemy e : level.getEnemies()) {
                    if (b.collides(e) && e.isAlive()) { b.explode(); soundCallback.playSfx(); e.setAlive(false); break; } // Colisión directa mata al enemigo
                }
            }
            if (b.isExploding()) { // Bomba explotando: su zona de fuego mata enemigos
                for (Enemy e : level.getEnemies()) {
                    if (e.isAlive() && e.collides(b.getExplosionX(), b.getExplosionY(),
                            b.getExplosionWidth(), b.getExplosionHeight())) { e.setAlive(false); } // Enemigo muere por explosión
                }
            }
        }

        // Player vs player collision: ambos jugadores se dañan al chocar
        if (player1.collides(player2)) {
            boolean d1 = !p1Safe && player1.takeDamage();
            boolean d2 = !p2Safe && player2.takeDamage();
            if (d1) { deaths1++; soundCallback.playDeath(); resetLevel(player1); }
            if (d2) { deaths2++; soundCallback.playDeath(); resetLevel(player2); }
            return;
        }
    }

    // Verifica si un jugador está dentro de alguna safe zone (protección contra enemigos/bombas)
    private boolean isInSafeZone(Player p) {
        for (SafeZone sz : level.getSafeZones()) {
            if (p.collides(sz)) return true;
        }
        return false;
    }

    // Mueve al jugador 1 en la dirección dada, respetando las paredes
    public void movePlayer1(double dx, double dy) {
        if (!gameCompleted && !gameOver) { // Solo si el juego sigue activo
            player1.move(dx, dy, level.getWalls());
        }
    }

    // Mueve al jugador 2 en la dirección dada, respetando las paredes
    public void movePlayer2(double dx, double dy) {
        if (!gameCompleted && !gameOver) { // Solo si el juego sigue activo
            player2.move(dx, dy, level.getWalls());
        }
    }

    // Getters
    public boolean isGameCompleted() { return gameCompleted; }
    public boolean isGameOver() { return gameOver; }
    public int getWinner() { return winner; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public void setDeaths1(int d) { deaths1 = d; }
    public void setDeaths2(int d) { deaths2 = d; }
    public void setScore1(int s) { score1 = s; }
    public void setScore2(int s) { score2 = s; }
    public void setLevelTimeRemaining(int t) { levelTimeRemaining = t; }
    public Level getLevel() { return level; }
    public int getDeaths1() { return deaths1; }
    public int getDeaths2() { return deaths2; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
    public int getCurrentLevel() { return currentLevel; }
    public int getLevelTimeRemaining() { return levelTimeRemaining; }
    public int getLevelTimeRemainingSeconds() { return levelTimeRemaining / 60; }

    // Reinicia solo al jugador que murió: el otro conserva su posición y estado
    private void resetLevel(Player deadPlayer) {
        int savedTime = levelTimeRemaining; // Tiempo restante antes de morir
        deadPlayer.respawn(); // Respawn del que murió en su propio punto de reaparición
        levelTimeRemaining = savedTime; // Restaura el tiempo
    }

    // -- Métodos package-private usados por las subclases de SafeZone --

    // Guarda la referencia al checkpoint alcanzado
    void setCheckpoint(SafeZone sz) { this.checkpoint = sz; }

    // Marca al jugador 1 como ganador del nivel
    void player1Wins() {
        winner = 1;
        soundCallback.playNextLevel();
        levelCompleted = true;
    }

    // Marca al jugador 2 como ganador del nivel
    void player2Wins() {
        winner = 2;
        soundCallback.playNextLevel();
        levelCompleted = true;
    }

    // Devuelve true si el nivel actual acaba de ser completado por algún jugador (P1 llegó a FINAL o P2 a START)
    public boolean isLevelCompleted() { return levelCompleted; }
    // Devuelve true si es el último nivel (nivel 4 de 5, el mini nivel no cuenta para el modo multijugador)
    public boolean isLastLevel() { return currentLevel >= LevelFactory.getTotalLevels() - 1; }

    // Avanza al siguiente nivel preservando escudos, tipos originales y la IA de P2
    public void advanceToNextLevel() {
        levelCompleted = false; // Resetea la bandera
        int savedShield1 = player1.getShieldCharges(); // Guarda escudos de P1
        int savedShield2 = player2.getShieldCharges(); // Guarda escudos de P2
        PlayerType savedType1 = player1.getOriginalType(); // Tipo original de P1
        PlayerType savedType2 = player2.getOriginalType(); // Tipo original de P2
        currentLevel++; // Incrementa el nivel
        loadLevel(currentLevel, savedType1, savedType2); // Carga el siguiente nivel con los tipos originales
        player1.setShieldCharges(savedShield1); // Restaura escudos de P1
        player2.setShieldCharges(savedShield2); // Restaura escudos de P2
    }

    // Marca el juego como completado cuando el usuario elige "Finish" en el último nivel
    public void finishGame() {
        levelCompleted = false; // Limpia la bandera de nivel completado
        gameCompleted = true; // Marca el juego como terminado
    }

    // Reinicia el nivel actual (se usa cuando se acaba el tiempo)
    public void restartLevel() {
        gameOver = false;
        levelCompleted = false;
        PlayerType savedType1 = player1.getOriginalType();
        PlayerType savedType2 = player2.getOriginalType();
        int savedShield1 = player1.getShieldCharges();
        int savedShield2 = player2.getShieldCharges();
        loadLevel(currentLevel, savedType1, savedType2);
        player1.setShieldCharges(savedShield1);
        player2.setShieldCharges(savedShield2);
    }

    // Cambia al nivel indicado (1-based) preservando tipos y escudos
    public void setStartingLevel(int level) {
        if (level >= 1 && level <= LevelFactory.getTotalLevels()) {
            int savedShield1 = (player1 != null) ? player1.getShieldCharges() : 0;
            int savedShield2 = (player2 != null) ? player2.getShieldCharges() : 0;
            PlayerType savedType1 = (player1 != null) ? player1.getOriginalType() : PlayerType.BLINKY;
            PlayerType savedType2 = (player2 != null) ? player2.getOriginalType() : PlayerType.INKY;
            currentLevel = level;
            loadLevel(level, savedType1, savedType2);
            if (player1 != null) player1.setShieldCharges(savedShield1);
            if (player2 != null) player2.setShieldCharges(savedShield2);
        }
    }

    // Determina el ganador final comparando monedas totales (score1 vs score2)
    // Devuelve 1 si gana P1, 2 si gana P2, 0 si empate
    // Se usa al terminar el último nivel para mostrar quién ganó la partida completa
    public int getFinalWinner() {
        if (score1 > score2) return 1; // P1 tiene más monedas → gana P1
        if (score2 > score1) return 2; // P2 tiene más monedas → gana P2
        return 0; // Empate: misma cantidad de monedas
    }

    // Crea un Memento con el estado actual de la partida (PvP/PvM)
    public Memento toMemento() {
        return new Memento(this);
    }

    // Restaura el estado de la partida desde un Memento
    public void fromMemento(Memento data) {
        this.level = data.getLevel();
        this.currentLevel = data.getCurrentLevel();
        this.levelTimeRemaining = data.getLevelTimeRemaining();
        this.player1 = data.getPlayer1();
        this.deaths1 = data.getDeaths1();
        this.score1 = data.getScore1();
        this.player2 = data.getPlayer2();
        this.deaths2 = data.getDeaths2();
        this.score2 = data.getScore2();
        this.gameCompleted = data.isGameCompleted();
        this.gameOver = data.isGameOver();
        this.winner = data.getWinner();
    }
}
