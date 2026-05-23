package domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * GuardadoBinario — implementación de MementoManager que usa serialización
 * binaria de Java (.dat). Guarda y carga objetos Memento mediante
 * ObjectOutputStream / ObjectInputStream.
 */
public class GuardadoBinario implements MementoManager {

    @Override
    public void saveGame(Memento data, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        }
    }

    @Override
    public Memento loadGame(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Memento) ois.readObject();
        }
    }
}
