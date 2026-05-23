package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

// IA fácil: mueve al jugador en direcciones aleatorias sin planificación
public class RandomAI implements PlayerMovementAI, Serializable {
    private Random rand = new Random();

    @Override
    public double[] getMovement(Player player, List<Wall> walls, List<Coin> coins,
                                List<Enemy> enemies, List<SafeZone> safeZones) {
        int r = rand.nextInt(10); // Genera un número aleatorio del 0 al 9
        double dx = 0, dy = 0;
        if (r < 4) {       // 40% de probabilidad: moverse a la derecha
            dx = 1;
        } else if (r < 7) { // 30% de probabilidad: moverse arriba
            dy = -1;
        } else if (r < 9) { // 20% de probabilidad: moverse abajo
            dy = 1;
        } // 10% restante: no moverse (se queda quieto o va a la izquierda)
        return new double[]{dx, dy};
    }
}