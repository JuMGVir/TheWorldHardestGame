package domain;

import java.util.List;

public class LinearMovement implements MovementStrategy {
    private double vx, vy;
    private double speedMultiplier;

    /**
     * Constructor for Enemy with Linear movement and no acceleration
     * @name LinearMovement
     * @param vx
     * @param vy
     */
    public LinearMovement(double vx, double vy) {
        this(vx, vy, 1.0);
    }
    
    /**
     * Constructor for Enemy with LinearMovement with acceleration
     * @name LinearMovement
     * @param vx
     * @param vy
     * @param speedMultiplier
     */
    public LinearMovement(double vx, double vy, double speedMultiplier) {
        this.vx = vx * speedMultiplier;
        this.vy = vy * speedMultiplier;
        this.speedMultiplier = speedMultiplier;
    }
    
    /**
     * Updates the enemy position without acceleration
     * @name
     * @param The enemy we are updating
     * @walls The walls we want to 
     */
    @Override
    public void update(Enemy enemy, List<Wall> walls) {
        // Guardar posición anterior
        double oldX = enemy.x;
        double oldY = enemy.y;

        // Mover en X
        enemy.x += vx;
        if (collidesWithAnyWall(enemy, walls)) {
            enemy.x = oldX;
            vx *= -1;
        }

        // Mover en Y
        enemy.y += vy;
        if (collidesWithAnyWall(enemy, walls)) {
            enemy.y = oldY;
            vy *= -1;
        }
    }
    
    /**
     * Auxiliar method for update to check if enemy collides with any wall
     * @param enemy
     * @param walls
     * @return
     */
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getSpeedMultiplier() { return speedMultiplier; }

    @Override
    // Exporta configuración: "LinearMovement:vx,vy,speedMultiplier"
    public String toExportString() {
        return "LinearMovement:" + vx + "," + vy + "," + speedMultiplier;
    }

    private boolean collidesWithAnyWall(Enemy enemy, List<Wall> walls) {
        for (Wall w : walls) {
            if (enemy.collides(w)) {
                return true;
            }
        }
        return false;
    }
}