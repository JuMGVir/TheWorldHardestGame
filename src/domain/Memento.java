package domain;

import java.io.Serializable;

/**
 * Memento — patrón de diseño Memento.
 * 
 * Encapsula el estado completo de una partida en un instante dado,
 * tanto para un jugador (Game) como para dos jugadores (Game2P, PvP / PvM).
 * 
 * Es utilizado por los MementoManager (GuardadoBinario, MementoDAO) para persistir
 * y restaurar partidas sin acoplar la lógica de negocio al formato
 * de almacenamiento. El Originator (Game / Game2P) crea Mementos con
 * toMemento() y restaura su estado con fromMemento().
 */
public class Memento implements Serializable {
    private static final long serialVersionUID = 1L; // Versión para serialización

    // Campos de identificación del tipo de partida
    private boolean isPvP; // true = dos jugadores puede ser PvP o PvM, false = un jugador

    // Estado del nivel
    private Level level; // Nivel completo con todas sus entidades
    private int currentLevel; // Número de nivel actual (1-based)
    private int levelTimeRemaining; // Tiempo restante en frames (60 frames = 1 segundo)

    // Jugador 1
    private Player player1; // Datos completos del primer jugador (tipo, posición, vidas)
    private int deaths1; // Cantidad de muertes del jugador 1
    private int score1; // Monedas recolectadas por el jugador 1

    // Jugador 2 (null en Loenly)
    private Player player2; // Datos completos del segundo jugador
    private int deaths2; // Muertes del jugador 2
    private int score2; // Monedas del jugador 2

    // Checkpoint (solo Loenly)
    private boolean hasCheckpoint; // true si hay un checkpoint guardado
    private double checkpointX; // Posición X del checkpoint
    private double checkpointY; // Posición Y del checkpoint

    // Estado global de la partida
    private boolean gameCompleted; // true si se completaron todos los niveles
    private boolean gameOver; // true si el tiempo se agotó
    private boolean pendingBoard; // true si debe mostrarse la pantalla de puntuaciones
    private int winner; // 0=ninguno, 1=P1, 2=P2 (solo PvP)

    // Constructores

    /**
     * Constructor vacío requerido para la deserialización y para
     * construir el Memento manualmente desde el MementoDAO.
     */
    public Memento() {}

    /**
     * Construye un Memento a partir de una partida de un jugador (Game).
     * 
     * @param game Partida actual de un jugador cuyo estado se quiere capturar
     */
    public Memento(Game game) {
        this.isPvP = false; // Es single-player
        this.level = game.getLevel(); // Guarda el nivel actual
        this.currentLevel = game.getCurrentLevel(); // Guarda el número de nivel
        this.levelTimeRemaining = game.getLevelTimeRemaining(); // Guarda el tiempo restante
        this.player1 = game.getPlayer(); // Guarda al jugador 1
        this.deaths1 = game.getDeaths(); // Guarda las muertes de P1
        this.score1 = game.getScore(); // Guarda la puntuación de P1
        this.player2 = null; // No hay jugador 2
        this.deaths2 = 0;
        this.score2 = 0;
        this.gameCompleted = game.isGameCompleted(); // Guarda estado de finalización
        this.gameOver = game.isGameOver(); // Guarda si el tiempo se agotó
        this.pendingBoard = game.isPendingBoard(); // Guarda si hay pantalla pendiente
        this.winner = 0; // Sin ganador en single-player
        SafeZone cp = game.getLastCheckpoint(); // Obtiene el checkpoint actual si existe
        this.hasCheckpoint = (cp != null); // true si hay checkpoint
        if (cp != null) { // Si hay checkpoint, guarda su posición
            this.checkpointX = cp.getRespawnX();
            this.checkpointY = cp.getRespawnY();
        }
    }

    /**
     * Construye un Memento a partir de una partida de dos jugadores (Game2P).
     * 
     * @param game Partida PvP/PvM actual cuyo estado se quiere capturar
     */
    public Memento(Game2P game) {
        this.isPvP = true; // Es multijugador
        this.level = game.getLevel(); // Guarda el nivel actual
        this.currentLevel = game.getCurrentLevel(); // Guarda el número de nivel
        this.levelTimeRemaining = game.getLevelTimeRemaining(); // Guarda el tiempo restante
        this.player1 = game.getPlayer1(); // Guarda al jugador 1
        this.deaths1 = game.getDeaths1(); // Guarda las muertes de P1
        this.score1 = game.getScore1(); // Guarda la puntuación de P1
        this.player2 = game.getPlayer2(); // Guarda al jugador 2
        this.deaths2 = game.getDeaths2(); // Guarda las muertes de P2
        this.score2 = game.getScore2(); // Guarda la puntuación de P2
        this.gameCompleted = game.isGameCompleted(); // Guarda estado de finalización
        this.gameOver = game.isGameOver(); // Guarda si el tiempo se agotó
        this.winner = game.getWinner(); // Guarda qué jugador ganó
        this.hasCheckpoint = false; // El modo PvP no usa checkpoints
        this.pendingBoard = false; // No hay pantalla pendiente en PvP
    }

    // Getters — permiten al Originator y a los DAO leer el estado guardado

    /** @return true si la partida es de dos jugadores (PvP / PvM) */
    public boolean isPvP() { return isPvP; }

    /** @return El nivel completo con todas sus entidades */
    public Level getLevel() { return level; }

    /** @return El número de nivel actual (1-based) */
    public int getCurrentLevel() { return currentLevel; }

    /** @return Tiempo restante del nivel en frames */
    public int getLevelTimeRemaining() { return levelTimeRemaining; }

    /** @return Datos completos del jugador 1 */
    public Player getPlayer1() { return player1; }

    /** @return Cantidad de muertes del jugador 1 */
    public int getDeaths1() { return deaths1; }

    /** @return Monedas recolectadas por el jugador 1 */
    public int getScore1() { return score1; }

    /** @return Datos completos del jugador 2 (null en single-player) */
    public Player getPlayer2() { return player2; }

    /** @return Muertes del jugador 2 */
    public int getDeaths2() { return deaths2; }

    /** @return Monedas del jugador 2 */
    public int getScore2() { return score2; }

    /** @return true si la partida tenía un checkpoint guardado */
    public boolean hasCheckpoint() { return hasCheckpoint; }

    /** @return Posición X del checkpoint guardado */
    public double getCheckpointX() { return checkpointX; }

    /** @return Posición Y del checkpoint guardado */
    public double getCheckpointY() { return checkpointY; }

    /** @return true si la partida estaba completada */
    public boolean isGameCompleted() { return gameCompleted; }

    /** @return true si el tiempo se había agotado */
    public boolean isGameOver() { return gameOver; }

    /** @return true si hay una pantalla de puntuaciones pendiente */
    public boolean isPendingBoard() { return pendingBoard; }

    /** @return 0=ninguno, 1=P1, 2=P2 (solo PvP) */
    public int getWinner() { return winner; }

    // Setters — usados por el MementoDAO durante la importación desde .txt
    //           y por el Originator al construir desde datos parseados

    /** @param v true si la partida es de dos jugadores */
    public void setPvP(boolean v) { isPvP = v; }

    /** @param l El nivel a restaurar */
    public void setLevel(Level l) { level = l; }

    /** @param n Número de nivel actual */
    public void setCurrentLevel(int n) { currentLevel = n; }

    /** @param t Tiempo restante en frames */
    public void setLevelTimeRemaining(int t) { levelTimeRemaining = t; }

    /** @param p Jugador 1 a restaurar */
    public void setPlayer1(Player p) { player1 = p; }

    /** @param d Muertes del jugador 1 */
    public void setDeaths1(int d) { deaths1 = d; }

    /** @param s Puntuación del jugador 1 */
    public void setScore1(int s) { score1 = s; }

    /** @param p Jugador 2 a restaurar */
    public void setPlayer2(Player p) { player2 = p; }

    /** @param d Muertes del jugador 2 */
    public void setDeaths2(int d) { deaths2 = d; }

    /** @param s Puntuación del jugador 2 */
    public void setScore2(int s) { score2 = s; }

    /**
     * Establece un checkpoint en la partida.
     * @param x Posición X del checkpoint
     * @param y Posición Y del checkpoint
     */
    public void setCheckpoint(double x, double y) {
        hasCheckpoint = true; // Marca que existe un checkpoint
        checkpointX = x; // Guarda la coordenada X
        checkpointY = y; // Guarda la coordenada Y
    }

    /** @param v true si la partida está completada */
    public void setGameCompleted(boolean v) { gameCompleted = v; }

    /** @param v true si el tiempo se agotó */
    public void setGameOver(boolean v) { gameOver = v; }

    /** @param v true si hay pantalla de puntuaciones pendiente */
    public void setPendingBoard(boolean v) { pendingBoard = v; }

    /** @param w 0=ninguno, 1=P1, 2=P2 */
    public void setWinner(int w) { winner = w; }
}
