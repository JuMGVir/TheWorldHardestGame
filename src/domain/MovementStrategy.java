package domain;
import java.io.Serializable;
import java.util.*;

public interface MovementStrategy extends Serializable {
    // Actualiza la posición del enemigo según la estrategia de movimiento
    void update(Enemy enemy, List<Wall> walls);
    // Exporta la configuración del movimiento como String (para guardar/importar)
    String toExportString();
}