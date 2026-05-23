package domain;

import java.io.Serializable;
import java.util.List;

// IA experta: busca la moneda más cercana y se dirige a ella; si no hay monedas, va a la meta
public class ExpertAI implements PlayerMovementAI, Serializable {
    @Override
    public double[] getMovement(Player player, List<Wall> walls, List<Coin> coins,
                                List<Enemy> enemies, List<SafeZone> safeZones) {
        // Busca la moneda no recolectada más cercana al jugador
        Coin target = null;
        double minDist = Double.MAX_VALUE;
        for (Coin c : coins) {
            if (!c.isCollected()) { // Solo monedas que aún no se han recogido
                double dist = Math.hypot(c.x - player.x, c.y - player.y); // Distancia euclidiana
                if (dist < minDist) { // Actualiza si es más cercana
                    minDist = dist;
                    target = c;
                }
            }
        }
        // Si ya no quedan monedas, se dirige al centro de la zona START (P2 arranca en FINAL, su meta es START)
        if (target == null) {
            for (SafeZone sz : safeZones) {
                if (sz.isStart()) { // P2 arranca en FINAL, debe llegar a START
                    target = new Coin(sz.x + sz.width/2, sz.y + sz.height/2); // Usa el centro como objetivo ficticio
                    break;
                }
            }
        }
        if (target != null) { // Calcula el vector dirección normalizado hacia el objetivo
            double dx = target.x - player.x; // Diferencia en X
            double dy = target.y - player.y; // Diferencia en Y
            double len = Math.hypot(dx, dy); // Longitud del vector
            if (len > 0) { // Normaliza para que sea un vector unitario
                dx /= len;
                dy /= len;
            }
            return new double[]{dx, dy}; // Retorna la dirección a seguir
        }
        return new double[]{0, 0}; // Sin objetivo: no se mueve
    }
}