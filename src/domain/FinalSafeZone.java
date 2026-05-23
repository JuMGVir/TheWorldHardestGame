package domain;

public class FinalSafeZone extends SafeZone {

    public FinalSafeZone(double x, double y, double w, double h, double respawnX, double respawnY) {
        super(x, y, w, h, respawnX, respawnY);
    }

    @Override
    public boolean onPlayerEnter1P(Game game, Player player) {
        if (!game.getLevel().allCoinsCollected()) return false;
        game.completeLevel();
        return true;
    }

    @Override
    public boolean onPlayerEnter2P(Game2P game2p, Player player, boolean isPlayer1) {
        if (!isPlayer1) return false;
        if (!game2p.getLevel().allCoinsCollected()) return false;
        game2p.player1Wins();
        return true;
    }

    @Override
    public String getTypeName() {
        return "FINAL";
    }

    @Override
    public boolean isFinal() { return true; }
}
