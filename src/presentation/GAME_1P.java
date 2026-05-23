package presentation;

import java.awt.Color;
import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.*;

import presentation.SoundEffect;

/**
 * Main game window for single-player mode. Manages the game loop, timer,
 * UI elements (pause, save, export), and transitions between screens.
 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
 * @version 1.0
 */
public class GAME_1P {

    private boolean isPaused = false;
    public JFrame frame;
    private Game game;
    private GamePanel gamePanel;

    /**
     * Main entry point for single-player mode.
     * @param args command-line arguments (unused)
     * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
     * @version 1.0
     */
    public static void main(String[] args) {
        new GAME_1P();
    }

    /**
     * Creates a new single-player game with a default Game instance.
     * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
     * @version 1.0
     */
    public GAME_1P() {
        game = null;
        initialize();
    }

    /**
     * Creates a new single-player game from an imported Game instance.
     * @param importedGame the previously saved game to resume
     * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
     * @version 1.0
     */
    public GAME_1P(Game importedGame) {
        this.game = importedGame;
        initialize();
    }

    /**
     * Initializes the game window, UI components (menu, pause, save, export),
     * the game panel, and the game loop timer.
     * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
     * @version 1.0
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("The World's Hardest Game");
        frame.setBounds(100, 100, 1005, 679);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(179, 172, 250));
        frame.getContentPane().setLayout(null);

        // Botón MENU
        JButton BACKTOMENU = new JButton("MENU");
        BACKTOMENU.addActionListener(e -> {
            new homescreen().frame.setVisible(true);
            frame.dispose();
        });
        BACKTOMENU.setForeground(Color.WHITE);
        BACKTOMENU.setFont(new Font("Impact", Font.BOLD, 50));
        BACKTOMENU.setBounds(0, 0, 168, 61);
        BACKTOMENU.setContentAreaFilled(false);
        BACKTOMENU.setBorderPainted(false);
        frame.getContentPane().add(BACKTOMENU);

        // Botón PAUSE
        JButton pause = new JButton("PAUSE");
        pause.addActionListener(e -> {
            isPaused = !isPaused;
            System.out.println(isPaused ? "Juego Pausado" : "Juego Reanudado");
            if (!isPaused) {
                gamePanel.requestFocusInWindow();
            }
        });
        pause.setBounds(167, -5, 167, 71);
        pause.setForeground(Color.WHITE);
        pause.setFont(new Font("Impact", Font.BOLD, 50));
        pause.setContentAreaFilled(false);
        pause.setBorderPainted(false);
        frame.getContentPane().add(pause);

        // Etiqueta TIME
        JLabel time = new JLabel("TIME: 0");
        time.setForeground(Color.WHITE);
        time.setFont(new Font("Impact", Font.BOLD, 50));
        time.setBounds(364, -5, 295, 71);
        frame.getContentPane().add(time);

        // Botón SAVE: delega al método del controlador que usa el modelo
        JButton save = new JButton("SAVE");
        save.addActionListener(e -> optionSaveAs());
        save.setForeground(Color.WHITE);
        save.setFont(new Font("Impact", Font.BOLD, 50));
        save.setContentAreaFilled(false);
        save.setBorderPainted(false);
        save.setBounds(635, -5, 167, 71);
        frame.getContentPane().add(save);


        if (game == null) {
            game = new Game();
        }
        Game.setSoundCallback(new SoundCallback() {
            public void playCoin() { SoundEffect.COIN.play(); }
            public void playDeath() { SoundEffect.DEATH.play(); }
            public void playSfx() { SoundEffect.SFX.play(); }
            public void playNextLevel() { SoundEffect.NEXT_LEVEL.play(); }
        });
        gamePanel = new GamePanel(game);
        gamePanel.setBounds(0, 60, 989, 521);
        gamePanel.setFocusable(true);
        gamePanel.setBackground(Color.BLACK);
        
        // Para que el panel pueda recibir el foco fácilmente al hacer clic
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
        
        frame.getContentPane().add(gamePanel);

        // Paneles decorativos negros superior e inferior (simulan bordes)
        JPanel margenSuperior = new JPanel();
        margenSuperior.setBackground(Color.BLACK);
        margenSuperior.setBounds(0, 0, 989, 60);
        frame.getContentPane().add(margenSuperior);

        JPanel margenSuperior_1 = new JPanel();
        margenSuperior_1.setBackground(Color.BLACK);
        margenSuperior_1.setLayout(null);
        margenSuperior_1.setBounds(0, 580, 989, 60);
        frame.getContentPane().add(margenSuperior_1);
                
                int lblw = 235;
                int gap = 10;
                JLabel shields = new JLabel("VIDAS: 0");
                shields.setForeground(Color.WHITE);
                shields.setFont(new Font("Impact", Font.BOLD, 45));
                shields.setBounds(gap, 0, lblw, 60);
                margenSuperior_1.add(shields);

                JLabel fails = new JLabel("FAILS: 0");
                fails.setForeground(Color.WHITE);
                fails.setFont(new Font("Impact", Font.BOLD, 45));
                fails.setBounds(gap + lblw + gap, 0, lblw, 60);
                margenSuperior_1.add(fails);

                JLabel coins = new JLabel("COINS: 0");
                coins.setForeground(Color.WHITE);
                coins.setFont(new Font("Impact", Font.BOLD, 45));
                coins.setBounds(gap + (lblw + gap) * 2, 0, lblw, 60);
                margenSuperior_1.add(coins);

                JButton exportar = new JButton("EXPORT");
                exportar.setForeground(Color.WHITE);
                exportar.setFont(new Font("Impact", Font.BOLD, 45));
                exportar.setContentAreaFilled(false);
                exportar.setBorderPainted(false);
                exportar.setBounds(gap + (lblw + gap) * 3, 0, lblw, 60);
                margenSuperior_1.add(exportar);

                exportar.addActionListener(e -> optionExportAs());

        Timer gameLoop = new Timer(16, e -> {
            if (!isPaused && !game.isGameCompleted() && !game.isGameOver()) {
                gamePanel.updateInput();
                game.update();
                gamePanel.repaint();
                fails.setText("FAILS: " + game.getDeaths());
                time.setText("TIME: " + game.getLevelTimeRemainingSeconds());
                shields.setText("VIDAS: " + game.getPlayer().getShieldCharges());
                coins.setText("COINS: " + game.getScore());

                if (game.isLevelCompleted()) { // Se acaba de completar un nivel
                    ((Timer)e.getSource()).stop(); // Detiene el bucle del juego
                    boolean isLast = game.isLastLevel(); // ¿Es el último nivel del modo principal?
                    // Opciones del diálogo: "Finish" si es el último, "Continue" si hay más niveles
                    String[] options = isLast
                        ? new String[]{"Finish", "Menu", "Close"}
                        : new String[]{"Continue", "Menu", "Close"};
                    int choice = JOptionPane.showOptionDialog(frame,
                        "¡Nivel " + game.getCurrentLevel() + " completado!",
                        "¡Felicidades!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);
                    if (choice == 0) { // Continue / Finish
                        if (isLast) { // Último nivel: muestra mensaje de felicitación y vuelve al menú
                            game.finishGame(); // Marca el juego como completado
                            JOptionPane.showMessageDialog(frame,
                                "¡FELICIDADES! Completaste todos los niveles.", // Mensaje de victoria
                                "¡Ganaste!", JOptionPane.INFORMATION_MESSAGE);
                            new homescreen().frame.setVisible(true); // Vuelve a la pantalla de inicio
                            frame.dispose(); // Cierra la ventana del juego
                        } else { // Hay más niveles: avanza al siguiente y reanuda el timer
                            game.advanceToNextLevel();
                            ((Timer)e.getSource()).start();
                        }
                    } else if (choice == 1) { // Menu: vuelve al homescreen
                        new homescreen().frame.setVisible(true);
                        frame.dispose();
                    } else { // Close: muestra mensaje de victoria y al aceptar cierra la ventana
                        JOptionPane.showMessageDialog(frame,
                            "¡FELICIDADES! Completaste todos los niveles.",
                            "¡Ganaste!", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose(); // Cierra la ventana del juego (no toda la aplicación)
                    }
                    return;
                }

                if (game.isGameCompleted()) {
                    ((Timer)e.getSource()).stop();
                    if (game.isPendingBoard()) {
                        new loaderBoard().frame.setVisible(true);
                    } else {
                        new GamesModes().frame.setVisible(true);
                    }
                    frame.dispose();
                }

                if (game.isGameOver()) {
                    game.restartLevel();
                }
            }
        });
        gameLoop.start();

        // Forzar que el panel reciba el foco inicialmente
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        SoundEffect.loadVolumes(); // Aplica los volúmenes guardados en preferencias

        frame.setVisible(true);
        gamePanel.requestFocusInWindow(); // importante para que los WASD funcionen desde el inicio
    }

    private final GuardadoBinario binaryDAO = new GuardadoBinario();
    private final MementoDAO mementoDAO = new MementoDAO();

    // Guarda la partida en formato binario .dat (serialización Java)
    private void optionSaveAs() {
        isPaused = true;
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setSelectedFile(new File("partida"));
        selector.setFileFilter(new FileNameExtensionFilter("Archivos de guardado", "dat"));
        int resultado = selector.showSaveDialog(frame);
        isPaused = false;
        gamePanel.requestFocusInWindow();
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".dat")) archivo = new File(ruta + ".dat");
            try {
                binaryDAO.saveGame(game.toMemento(), archivo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Exporta la partida en formato texto .txt (legible, línea por línea)
    private void optionExportAs() {
        File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
        if (!savesDir.exists()) savesDir.mkdirs();
        JFileChooser selector = new JFileChooser();
        selector.setCurrentDirectory(savesDir);
        selector.setSelectedFile(new File("partida_exportada"));
        selector.setFileFilter(new FileNameExtensionFilter("Archivos exportados", "txt"));
        int resultado = selector.showSaveDialog(frame);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) archivo = new File(ruta + ".txt");
            try {
                mementoDAO.saveGame(game.toMemento(), archivo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}