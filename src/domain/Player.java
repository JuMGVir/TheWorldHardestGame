package domain;

import java.util.List;

public class Player extends Entity {
    private PlayerType playerType;
    private PlayerType originalType;
    private int invincibilityFrames = 0;
    private int shieldCharges = 0;
    private double respawnX, respawnY;
    private PlayerMovementAI ai;
    private String borderColor = "0,0,0"; // Color del borde del jugador en formato "R,G,B"

    public Player(double x, double y, PlayerType type) {
        super(x, y, type.getWidth(), type.getHeight());
        this.playerType = type;
        this.originalType = type;
        setRespawnPoint(x, y);
    }

    public Player(double x, double y) {
        this(x, y, PlayerType.BLINKY);
    }

    public void move(double dx, double dy, List<Wall> walls) {
        double moveSpeed = 2.5 * playerType.getSpeedMultiplier();
        double nextX = x + dx * moveSpeed;
        double nextY = y + dy * moveSpeed;

        double oldX = x;
        x = nextX;
        for (Wall w : walls) {
            if (this.collides(w)) {
                x = oldX;
                break;
            }
        }

        double oldY = y;
        y = nextY;
        for (Wall w : walls) {
            if (this.collides(w)) {
                y = oldY;
                break;
            }
        }
    }

    public boolean takeDamage() {
        if (invincibilityFrames > 0) return false;

        if (shieldCharges > 0) {
            shieldCharges--;
            invincibilityFrames = 60;
            return false;
        }

        PlayerType newState = playerType.onTakeDamage();
        if (newState != null) {
            playerType = newState;
            width = playerType.getWidth();
            height = playerType.getHeight();
            invincibilityFrames = 60;
            return false;
        }

        return true;
    }

    public void addShieldCharge() { shieldCharges++; }

    public int getShieldCharges() { return shieldCharges; }
    public void setShieldCharges(int shieldCharges) { this.shieldCharges = shieldCharges; }

    public void updateInvincibility() {
        if (invincibilityFrames > 0) invincibilityFrames--;
    }

    public boolean isInvincible() { return invincibilityFrames > 0; }

    public void respawn() {
        this.x = respawnX;
        this.y = respawnY;
        revertToOriginalSkin();
    }

    public void setRespawnPoint(double x, double y) {
        this.respawnX = x;
        this.respawnY = y;
    }

    public void updateRespawnPoint(SafeZone zone) {
        this.respawnX = zone.getRespawnX();
        this.respawnY = zone.getRespawnY();
    }

    public void setAI(PlayerMovementAI ai) { this.ai = ai; }
    public boolean isAI() { return ai != null; }

    public void updateAI(List<Wall> walls, List<Coin> coins, List<Enemy> enemies, List<SafeZone> safeZones) {
        if (ai != null) {
            double[] mov = ai.getMovement(this, walls, coins, enemies, safeZones);
            move(mov[0], mov[1], walls);
        }
    }

    public boolean inSafeZone(List<SafeZone> safeZones) {
        for (SafeZone z : safeZones) {
            if (this.collides(z)) return true;
        }
        return false;
    }

    public void applyTemporarySkin(PlayerType newType) {
        this.playerType = newType;
        this.width = playerType.getWidth();
        this.height = playerType.getHeight();
    }

    public void revertToOriginalSkin() {
        if (playerType == originalType) return;
        this.playerType = originalType;
        this.width = playerType.getWidth();
        this.height = playerType.getHeight();
    }

    public PlayerType getPlayerType() { return playerType; }
    public PlayerType getOriginalType() { return originalType; }
    public double getSpeedMultiplier() { return playerType.getSpeedMultiplier(); }
    public double getRespawnX() { return respawnX; }
    public double getRespawnY() { return respawnY; }
    public String getBorderColor() { return borderColor; } // Devuelve el color del borde como string RGB
    public void setBorderColor(String borderColor) { this.borderColor = borderColor; } // Asigna el color del borde desde un string RGB
}
