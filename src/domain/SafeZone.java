package domain;

public abstract class SafeZone extends Entity {
    private double respawnX;
    private double respawnY;
    private LevelState savedState;

    // Crea una zona segura con posición de respawn explícita
    public SafeZone(double x, double y, double w, double h, double respawnX, double respawnY) {
        super(x, y, w, h);
        this.respawnX = respawnX;
        this.respawnY = respawnY;
        this.savedState = null;
    }

    // Crea una zona segura con respawn en el centro de la zona
    public SafeZone(double x, double y, double w, double h) {
        this(x, y, w, h, x + w / 2, y + h / 2);
    }

    // Acción al entrar el jugador en modo 1 jugador (retorna true si detiene el update)
    public abstract boolean onPlayerEnter1P(Game game, Player player);

    // Acción al entrar en modo 2 jugadores (retorna true si detiene el update)
    public abstract boolean onPlayerEnter2P(Game2P game2p, Player player, boolean isPlayer1);

    // Guarda el estado del nivel en este checkpoint
    public void saveLevelState(Level level, Player player) {
        this.savedState = level.saveState(this.respawnX, this.respawnY);
    }

    // Restaura el estado del nivel desde este checkpoint
    public void restoreLevelState(Level level, Player player) {
        if (savedState != null) {
            level.restoreState(savedState);
            player.setRespawnPoint(savedState.getRespawnX(), savedState.getRespawnY());
            player.setPosition(savedState.getRespawnX(), savedState.getRespawnY());
        }
    }

    // Indica si hay un estado guardado en este checkpoint
    public boolean hasSavedState() {
        return savedState != null;
    }

    // Devuelve el nombre del tipo para serialización
    public abstract String getTypeName();

    // Retorna true si es la zona de inicio
    public boolean isStart() { return false; }

    // Retorna true si es zona intermedia (checkpoint)
    public boolean isIntermediate() { return false; }

    // Retorna true si es la zona final
    public boolean isFinal() { return false; }

    // Getters
    public double getRespawnX() { return respawnX; }
    public double getRespawnY() { return respawnY; }
}
