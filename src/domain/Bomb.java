package domain;


public class Bomb extends Entity {
    private boolean exploded = false;
    private int explosionTimer = 0;

    private static final int EXPLOSION_DURATION = 180;
    private static final double EXPLOSION_RADIUS = 25.0;

    /**
     * Constructor for bomb
     * @name Bomb
     * @param x X position for Bomb
     * @param y Y position for Bomb
     */
    public Bomb(double x, double y) {
        super(x, y, 8, 8);
    }

    /**
     * Explode boolean value to true
     * @name explode
     */
    public void explode() {
        exploded = true;
        explosionTimer = EXPLOSION_DURATION;
    }

    public void updateExplosion() {
        if (explosionTimer > 0) {
            explosionTimer--;
        }
    }

    public boolean isExploding() {
        return explosionTimer > 0;
    }

    public double getExplosionX() {
        return x - EXPLOSION_RADIUS + width / 2;
    }

    public double getExplosionY() {
        return y - EXPLOSION_RADIUS + height / 2;
    }

    public double getExplosionWidth() {
        return EXPLOSION_RADIUS * 2;
    }

    public double getExplosionHeight() {
        return EXPLOSION_RADIUS * 2;
    }

    /**
     * Getter for exploded variable
     * @name isExploded
     * @return Exploded boolean value
     */
    public boolean isExploded() {
        return exploded;
    }
    
    /**
     * Setter for exploded variable
     * @name setExploded
     * @param exploded
     */
    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}