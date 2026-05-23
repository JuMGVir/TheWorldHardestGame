package domain;

import java.util.List;

public class Enemy extends Entity {
    private boolean alive = true;
	private MovementStrategy strategy;
    
    /**
     * Constructor for Enemy
     * @name Enemy
     * @param x
     * @param y
     * @param width
     * @param height
     * @param strategy
     */
    public Enemy(double x, double y, double width, double height, MovementStrategy strategy) {
        super(x, y, width, height);
        this.strategy = strategy;
    }
    
    /**
     * 	Update the enemy position based on the strategy that enemy currently have and the walls of the level
     * @name updateWithWalls
     * @param walls The walls of the current level
     */
    public void updateWithWalls(List<Wall> walls) {
        strategy.update(this, walls);
    }
    
    /**
     * Set a new strategy for the enemy
     * @name setStrategy
     * @param strategy
     */
    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Getter for alive variable
     * @name isAlive
     * @return boolean value in alive variable
     */
    public boolean isAlive() { return alive;}
    
    /**
     * Setter for alive variable
     * @name setAlive
     * @param alive
     */
    public void setAlive(boolean alive) { this.alive = alive;}
    public MovementStrategy getStrategy() { return strategy; }
}