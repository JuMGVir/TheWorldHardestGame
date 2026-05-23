package domain;

import java.io.Serializable;

public abstract class Entity implements Serializable{
	public double x, y, width, height;
	
	/**
	 * Constructor for Entity (template for every player, enemy, wall and safeZone)
	 * @name Entity
	 * @param x X position of the Entity
	 * @param y Y position of the Entity
	 * @param w Width of the Entity
	 * @param h Height of the Entity
	 */
	public Entity(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	/**
	 * Checks if the actual entity is colliding with another
	 * @name collides
	 * @param other The other entity that could be colliding
	 * @return The boolean value for colliding
	 */
	public boolean collides (Entity other) {
		return x < other.x + other.width &&
			   x + width > other.x &&
			   y < other.y + other.height &&
			   y + height > other.y;
	}
	
	/**
	 * Set a new position for the entity
	 * @param x new X position for entity
	 * @param y new Y position for entity
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Checks if this entity collides with a rectangular area
	 * @param ox X of the area
	 * @param oy Y of the area
	 * @param ow Width of the area
	 * @param oh Height of the area
	 * @return true if colliding
	 */
	public boolean collides(double ox, double oy, double ow, double oh) {
		return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
	}

	//Getters for position
	public double getX() {return x;}
	public double getY() {return y;}
}
