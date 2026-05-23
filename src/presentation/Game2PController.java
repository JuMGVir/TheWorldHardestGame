package presentation;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.GuardadoBinario;
import domain.Game2P;
import domain.MementoManager;
import domain.Memento;
import domain.MementoDAO;

/**
 * Controlador para el modo de dos jugadores (PvP y PvM).
 * Orquesta las operaciones de archivo entre la vista (GAME_2P) y el modelo (Game2P),
 * utilizando el patrón DAO para la persistencia.
 */
public class Game2PController {

    private Game2P game;
    private GAME_2P view;
    private MementoManager binaryManager;
    private MementoManager mementoManager;

    public Game2PController(Game2P game, GAME_2P view) {
        this.game = game;
        this.view = view;
        this.binaryManager = new GuardadoBinario();
        this.mementoManager = new MementoDAO();
    }

    // Guarda la partida PvP/PvM en formato binario .dat
    public void optionSaveAs() {
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setSelectedFile(new File("partida_pvp"));
        selector.setFileFilter(new FileNameExtensionFilter("Archivos de guardado", "dat"));
        int resultado = selector.showSaveDialog(view.getFrame());
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".dat")) archivo = new File(ruta + ".dat");
            try {
                binaryManager.saveGame(game.toMemento(), archivo);
                System.out.println("Partida guardada en: " + archivo.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Exporta la partida PvP/PvM en formato texto .txt
    public void optionExportAs() {
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setSelectedFile(new File("partida_pvp_exportada"));
        selector.setFileFilter(new FileNameExtensionFilter("Archivos exportados", "txt"));
        int resultado = selector.showSaveDialog(view.getFrame());
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) archivo = new File(ruta + ".txt");
            try {
                mementoManager.saveGame(game.toMemento(), archivo);
                System.out.println("Partida exportada en: " + archivo.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Abre y restaura una partida .dat guardada previamente
    public void optionOpen() {
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos de guardado", "dat"));
        int resultado = selector.showOpenDialog(view.getFrame());
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                Memento data = binaryManager.loadGame(selector.getSelectedFile());
                game.fromMemento(data);
                System.out.println("Partida cargada.");
            } catch (Exception ex) {
                System.out.println("Error al abrir: " + ex.getMessage());
            }
        }
    }

    // Importa y restaura una partida .txt exportada previamente
    public void optionImport() {
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos exportados", "txt"));
        int resultado = selector.showOpenDialog(view.getFrame());
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                Memento data = mementoManager.loadGame(selector.getSelectedFile());
                game.fromMemento(data);
                System.out.println("Partida importada.");
            } catch (Exception ex) {
                System.out.println("Error al importar: " + ex.getMessage());
            }
        }
    }

    public void optionNew() {
        view.getFrame().dispose();
        new Character2P();
    }

    public void optionExit() {
        view.getFrame().dispose();
        System.exit(0);
    }
}
