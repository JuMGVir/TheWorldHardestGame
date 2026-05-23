package domain;

/**
 * Lógica del juego para el modo Jugador vs Máquina (PvM).
 * Extiende Game2P añadiendo una IA que controla al jugador 2.
 * Se re-aplica la IA tras cargar/restaurar niveles para mantener
 * el control automático sobre el nuevo objeto Player.
 */
public class GamePvM extends Game2P {
    private PlayerMovementAI p2AI;

    // Crea una partida PvM: P1 humano del tipo elegido, P2 controlado por la IA indicada
    public GamePvM(PlayerType humanType, PlayerMovementAI ai) {
        super(humanType, PlayerType.INKY);
        this.p2AI = ai;
        if (getPlayer2() != null) getPlayer2().setAI(ai);
    }

    // Mueve la IA antes de la actualización normal del juego (colisiones, tiempo, etc.)
    @Override
    public void update() {
        if (getPlayer2() != null && getLevel() != null) {
            getPlayer2().updateAI(getLevel().getWalls(), getLevel().getCoins(),
                getLevel().getEnemies(), getLevel().getSafeZones());
        }
        super.update();
    }

    // Re-asigna la IA tras avanzar de nivel (el loadLevel crea un nuevo Player2)
    @Override
    public void advanceToNextLevel() {
        super.advanceToNextLevel();
        if (p2AI != null && getPlayer2() != null) getPlayer2().setAI(p2AI);
    }

    // Re-asigna la IA tras reiniciar el nivel
    @Override
    public void restartLevel() {
        super.restartLevel();
        if (p2AI != null && getPlayer2() != null) getPlayer2().setAI(p2AI);
    }

    // Re-asigna la IA tras cambiar de nivel desde la selección
    @Override
    public void setStartingLevel(int level) {
        super.setStartingLevel(level);
        if (p2AI != null && getPlayer2() != null) getPlayer2().setAI(p2AI);
    }

    // Re-asigna la IA tras restaurar desde un Memento (el Player2 restaurado no conserva la IA)
    @Override
    public void fromMemento(Memento data) {
        super.fromMemento(data);
        if (p2AI != null && getPlayer2() != null) getPlayer2().setAI(p2AI);
    }
}
