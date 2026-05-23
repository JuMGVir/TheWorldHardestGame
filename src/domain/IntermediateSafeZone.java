package domain;

public class IntermediateSafeZone extends SafeZone {

    public IntermediateSafeZone(double x, double y, double w, double h, double respawnX, double respawnY) {
        super(x, y, w, h, respawnX, respawnY);
    }

    @Override
    public boolean onPlayerEnter1P(Game game, Player player) {
        saveLevelState(game.getLevel(), player);
        player.updateRespawnPoint(this);
        game.setLastCheckpoint(this);
        return false;
    }

    @Override
    public boolean onPlayerEnter2P(Game2P game2p, Player player, boolean isPlayer1) {
        if (!isPlayer1) return false;
        saveLevelState(game2p.getLevel(), player);
        player.updateRespawnPoint(this);
        game2p.setCheckpoint(this);
        return false;
    }

    @Override
    public String getTypeName() {
        return "INTERMEDIATE";
    }

    @Override
    public boolean isIntermediate() { return true; }
}
