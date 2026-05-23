package domain;

import java.util.List;

// Interfaz que define cómo una IA calcula el movimiento de un jugador
public interface PlayerMovementAI {
    // Calcula la dirección {dx, dy} a mover basada en el jugador y el entorno
    double[] getMovement(Player player, List<Wall> walls, List<Coin> coins, 
                         List<Enemy> enemies, List<SafeZone> safeZones);
}