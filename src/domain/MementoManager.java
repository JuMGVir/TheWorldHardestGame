package domain;

import java.io.File;
import java.io.IOException;

/**
 * MementoManager — interfaz del Caretaker en el patrón Memento.
 * 
 * Define las operaciones de persistencia para guardar y recuperar
 * objetos Memento. Separa por completo la lógica de acceso a datos
 * de la lógica de negocio (Originator).
 * 
 * Cada implementación concreta (BinaryMemento, MementoDAO) maneja
 * un formato de almacenamiento distinto (binario .dat, texto .txt).
 */
public interface MementoManager {

    /**
     * Guarda el estado completo de una partida en un archivo.
     * @param data el Memento con el snapshot del estado de la partida
     */
    void saveGame(Memento data, File file) throws IOException;

    /**
     * Carga el estado completo de una partida desde un archivo.
     * @param file el archivo origen
     * @return el Memento con el estado restaurado
     */
    Memento loadGame(File file) throws IOException, ClassNotFoundException;
}
