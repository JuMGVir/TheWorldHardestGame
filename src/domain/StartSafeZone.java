package domain;

public class StartSafeZone extends SafeZone {

    public StartSafeZone(double x, double y, double w, double h, double respawnX, double respawnY) {
        super(x, y, w, h, respawnX, respawnY);
    }

    public StartSafeZone(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    @Override
    public boolean onPlayerEnter1P(Game game, Player player) {
        return false;
    }

    @Override
    public boolean onPlayerEnter2P(Game2P game2p, Player player, boolean isPlayer1) {
        if (!isPlayer1 && game2p.getLevel().allCoinsCollected()) {
            game2p.player2Wins();
            return true;
        }
        return false;
    }

    @Override
    public String getTypeName() {
        return "START";
    }

    @Override
    public boolean isStart() { return true; }
}
