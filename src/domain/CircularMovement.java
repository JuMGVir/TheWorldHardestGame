package domain;

import java.util.List;

public class CircularMovement implements MovementStrategy {
    private double centerX, centerY;
    private double radius;
    private double angle;
    private double angularSpeed; // radianes por frame
    
    /**
     * Constructor for Enemy with circular Movement
     * @name CircularMovement
     * @param centerX
     * @param centerY
     * @param radius
     * @param angularSpeed
     */
    public CircularMovement(double centerX, double centerY, double radius, double angularSpeed) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.angle = 0;
        this.angularSpeed = angularSpeed;
    }
    
    /**
     * Updates the position with the Circular movement
     * @name update
     * @param enemy
     * @param walls
     */
    public double getCenterX() { return centerX; }
    public double getCenterY() { return centerY; }
    public double getRadius() { return radius; }
    public double getAngularSpeed() { return angularSpeed; }

    @Override
    // Exporta configuración: "CircularMovement:cx,cy,radius,angularSpeed"
    public String toExportString() {
        return "CircularMovement:" + centerX + "," + centerY + "," + radius + "," + angularSpeed;
    }

    @Override
    public void update(Enemy enemy, List<Wall> walls) {
        angle += angularSpeed;
        enemy.x = centerX + Math.cos(angle) * radius - enemy.width / 2;
        enemy.y = centerY + Math.sin(angle) * radius - enemy.height / 2;
    }
}