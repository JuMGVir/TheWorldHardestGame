package presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import domain.*;
import presentation.SoundEffect;

/**
 * Vista principal para el modo de dos jugadores (PvP y PvM).
 * Gestiona la ventana del juego, entrada de teclado para ambos jugadores,
 * el bucle principal del juego y las etiquetas HUD.
 * Delega las operaciones de archivo al Game2PController.
 */
public class GAME_2P {

    private JFrame frame; // Ventana principal del juego
    private Game2P game; // Lógica del juego para dos jugadores
    private Game2PController controller; // Controlador para operaciones de archivo
    private GamePanel2D panel; // Panel personalizado que dibuja el escenario
    private boolean isPaused; // true cuando el juego está en pausa
    // Etiquetas de la interfaz para mostrar estadísticas de ambos jugadores
    private JLabel p1Vidas, p1Fails, p1Coins, p2Vidas, p2Fails, p2Coins;
    private JLabel timeLabel; // Etiqueta del tiempo restante
    // Estados de las teclas: w/a/s/d = P1, up/down/left/right = P2
    private boolean w, a, s, d, up, down, left, right;

    // Constructor por defecto: P1 rojo, P2 azul
    public GAME_2P() {
        game = new Game2P();
        initialize();
        controller = new Game2PController(game, this);
    }

    // Constructor con tipos específicos: cada jugador elige su personaje (BLINKY/INKY/CLYDE)
    public GAME_2P(PlayerType p1Type, PlayerType p2Type) {
        game = new Game2P(p1Type, p2Type);
        initialize();
        controller = new Game2PController(game, this);
    }

    // Constructor con tipos y colores de borde personalizados (usado por Character2P)
    // Asigna colores de borde a cada jugador inmediatamente despues de crear Game2P
    // Los colores vienen del JColorChooser en formato "R,G,B"
    public GAME_2P(PlayerType p1Type, PlayerType p2Type, String p1Border, String p2Border) {
        game = new Game2P(p1Type, p2Type); // Crea la partida con los tipos elegidos
        game.getPlayer1().setBorderColor(p1Border); // Color de borde para P1
        game.getPlayer2().setBorderColor(p2Border); // Color de borde para P2
        initialize(); // Configura la ventana y el panel de juego
        controller = new Game2PController(game, this); // Enlaza el controlador de archivos
    }

    // Constructor para modo PvM: P1 humano con el tipo elegido, P2 controlado por la IA indicada
    public GAME_2P(PlayerType humanType, PlayerMovementAI ai) {
        game = new GamePvM(humanType, ai);
        initialize();
        controller = new Game2PController(game, this);
    }

    // Constructor para modo PvM con color de borde personalizado para el jugador humano
    public GAME_2P(PlayerType humanType, String humanBorder, PlayerMovementAI ai) {
        game = new GamePvM(humanType, ai);
        game.getPlayer1().setBorderColor(humanBorder);
        initialize();
        controller = new Game2PController(game, this);
    }

    // Constructor que recibe un Game2P ya configurado (usado por DAO al cargar partidas)
    public GAME_2P(Game2P game2p) {
        this.game = game2p;
        initialize();
        controller = new Game2PController(game, this);
    }

    // Inicializa la ventana: botones, etiquetas, panel de juego, teclado y bucle principal
    private void initialize() {
        Game2P.setSoundCallback(new SoundCallback() {
            public void playCoin() { SoundEffect.COIN.play(); }
            public void playDeath() { SoundEffect.DEATH.play(); }
            public void playSfx() { SoundEffect.SFX.play(); }
            public void playNextLevel() { SoundEffect.NEXT_LEVEL.play(); }
        });
        frame = new JFrame();
        frame.setTitle("The World's Hardest Game - PvP");
        frame.setBounds(100, 100, 1005, 679); // Tamaño fijo de la ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(179, 172, 250));
        frame.getContentPane().setLayout(null);

        // Botón MENU: vuelve a la pantalla de inicio
        JButton BACKTOMENU = new JButton("MENU");
        BACKTOMENU.setFocusable(false);
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

        // Botón PAUSE: pausa/reanuda el juego
        JButton pause = new JButton("PAUSE");
        pause.setFocusable(false);
        pause.addActionListener(e -> {
            isPaused = !isPaused;
            if (!isPaused) {
                panel.requestFocusInWindow(); // Devuelve el foco al panel al reanudar
            }
        });
        pause.setBounds(167, -5, 167, 71);
        pause.setForeground(Color.WHITE);
        pause.setFont(new Font("Impact", Font.BOLD, 50));
        pause.setContentAreaFilled(false);
        pause.setBorderPainted(false);
        frame.getContentPane().add(pause);

        // Etiqueta TIME: muestra el tiempo restante del nivel
        timeLabel = new JLabel("TIME: 0");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Impact", Font.BOLD, 50));
        timeLabel.setBounds(364, -5, 295, 71);
        frame.getContentPane().add(timeLabel);

        // Botón SAVE: guarda la partida en un archivo .dat (serialización binaria)
        JButton save = new JButton("SAVE");
        save.setFocusable(false);
        save.addActionListener(e -> controller.optionSaveAs());
        save.setForeground(Color.WHITE);
        save.setFont(new Font("Impact", Font.BOLD, 50));
        save.setContentAreaFilled(false);
        save.setBorderPainted(false);
        save.setBounds(635, -5, 167, 71);
        frame.getContentPane().add(save);

        // Botón EXPORT: exporta la partida a un archivo .txt legible
        JButton export = new JButton("EXPORT");
        export.setFocusable(false);
        export.addActionListener(e -> controller.optionExportAs());
        export.setForeground(Color.WHITE);
        export.setFont(new Font("Impact", Font.BOLD, 50));
        export.setContentAreaFilled(false);
        export.setBorderPainted(false);
        export.setBounds(800, -5, 189, 71);
        frame.getContentPane().add(export);

        // Panel de juego: donde se dibuja el escenario
        panel = new GamePanel2D(game);
        panel.setBounds(0, 60, 989, 521);
        panel.setFocusable(true);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.requestFocusInWindow(); // Recupera el foco al hacer clic
            }
        });
        frame.getContentPane().add(panel);

        // Barra negra superior: fondo de los botones del HUD
        JPanel margenSuperior = new JPanel();
        margenSuperior.setBackground(Color.BLACK);
        margenSuperior.setBounds(0, 0, 989, 60);
        margenSuperior.setFocusable(false);
        frame.getContentPane().add(margenSuperior);

        // Barra inferior negra: muestra estadísticas de ambos jugadores
        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(Color.BLACK);
        bottomBar.setLayout(null);
        bottomBar.setBounds(0, 580, 989, 60);
        bottomBar.setFocusable(false);
        frame.getContentPane().add(bottomBar);

        // Configuración de fuentes y posiciones de las etiquetas de la barra inferior
        Font lblFont = new Font("Impact", Font.BOLD, 28);
        int lblw = 150;
        int gap = 10;
        int startX = 10;

        // Etiqueta de escudos (VIDAS) del jugador 1 en rojo
        p1Vidas = new JLabel("P1 VIDAS: 0");
        p1Vidas.setForeground(new Color(180, 24, 27));
        p1Vidas.setFont(lblFont);
        p1Vidas.setBounds(startX, 0, lblw, 60);
        bottomBar.add(p1Vidas);

        // Etiqueta de muertes (FAILS) del jugador 1
        p1Fails = new JLabel("P1 FAILS: 0");
        p1Fails.setForeground(new Color(180, 24, 27));
        p1Fails.setFont(lblFont);
        p1Fails.setBounds(startX + lblw + gap, 0, lblw, 60);
        bottomBar.add(p1Fails);

        // Etiqueta de monedas (COINS) del jugador 1
        p1Coins = new JLabel("P1 COINS: 0");
        p1Coins.setForeground(new Color(180, 24, 27));
        p1Coins.setFont(lblFont);
        p1Coins.setBounds(startX + (lblw + gap) * 2, 0, lblw, 60);
        bottomBar.add(p1Coins);

        // Las etiquetas de P2 empiezan después de las de P1
        int startX2 = startX + (lblw + gap) * 3 + gap * 2;

        // Etiqueta de escudos del jugador 2 en azul
        p2Vidas = new JLabel("P2 VIDAS: 0");
        p2Vidas.setForeground(new Color(25, 41, 149));
        p2Vidas.setFont(lblFont);
        p2Vidas.setBounds(startX2, 0, lblw, 60);
        bottomBar.add(p2Vidas);

        // Etiqueta de muertes del jugador 2
        p2Fails = new JLabel("P2 FAILS: 0");
        p2Fails.setForeground(new Color(25, 41, 149));
        p2Fails.setFont(lblFont);
        p2Fails.setBounds(startX2 + lblw + gap, 0, lblw, 60);
        bottomBar.add(p2Fails);

        // Etiqueta de monedas del jugador 2
        p2Coins = new JLabel("P2 COINS: 0");
        p2Coins.setForeground(new Color(25, 41, 149));
        p2Coins.setFont(lblFont);
        p2Coins.setBounds(startX2 + (lblw + gap) * 2, 0, lblw, 60);
        bottomBar.add(p2Coins);

        // Listeners de teclado: captura teclas de ambos jugadores
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int kc = e.getKeyCode(); // Código de la tecla presionada
                // Teclas de movimiento del jugador 1 (configurables en KeyBinds)
                if (kc == GamePanel.getP1KeyUp())    { w = true; }
                if (kc == GamePanel.getP1KeyDown())  { s = true; }
                if (kc == GamePanel.getP1KeyLeft())  { a = true; }
                if (kc == GamePanel.getP1KeyRight()) { d = true; }
                if (kc == GamePanel.getP1KeyPause()) { isPaused = !isPaused; } // Pausa con tecla de P1
                // Teclas de movimiento del jugador 2 (configurables en KeyBinds)
                if (kc == GamePanel.getP2KeyUp())    { up = true; }
                if (kc == GamePanel.getP2KeyDown())  { down = true; }
                if (kc == GamePanel.getP2KeyLeft())  { left = true; }
                if (kc == GamePanel.getP2KeyRight()) { right = true; }
                if (kc == GamePanel.getP2KeyPause()) { isPaused = !isPaused; } // Pausa con tecla de P2
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int kc = e.getKeyCode(); // Código de la tecla liberada
                // Libera el estado de las teclas de P1
                if (kc == GamePanel.getP1KeyUp())    { w = false; }
                if (kc == GamePanel.getP1KeyDown())  { s = false; }
                if (kc == GamePanel.getP1KeyLeft())  { a = false; }
                if (kc == GamePanel.getP1KeyRight()) { d = false; }
                // Libera el estado de las teclas de P2
                if (kc == GamePanel.getP2KeyUp())    { up = false; }
                if (kc == GamePanel.getP2KeyDown())  { down = false; }
                if (kc == GamePanel.getP2KeyLeft())  { left = false; }
                if (kc == GamePanel.getP2KeyRight()) { right = false; }
            }
        });

        // Bucle principal del juego: se ejecuta cada ~16ms (60 FPS)
        Timer gameLoop = new Timer(16, e -> {
            if (!isPaused && !game.isGameCompleted() && !game.isGameOver()) {
                pollInput(); // Lee el estado actual de las teclas
                game.update(); // Avanza un tick la lógica del juego
                panel.repaint(); // Redibuja el panel de juego
                // Actualiza las etiquetas con los valores actuales de cada jugador
                p1Vidas.setText("P1 VIDAS: " + game.getPlayer1().getShieldCharges());
                p1Fails.setText("P1 FAILS: " + game.getDeaths1());
                p1Coins.setText("P1 COINS: " + game.getScore1());
                p2Vidas.setText("P2 VIDAS: " + game.getPlayer2().getShieldCharges());
                p2Fails.setText("P2 FAILS: " + game.getDeaths2());
                p2Coins.setText("P2 COINS: " + game.getScore2());
                timeLabel.setText("TIME: " + game.getLevelTimeRemainingSeconds()); // Actualiza el tiempo

                if (game.isLevelCompleted()) { // Un jugador acaba de completar el nivel
                    ((Timer) e.getSource()).stop(); // Detiene el bucle del juego
                    boolean isLast = game.isLastLevel(); // ¿Es el último nivel?
                    String winnerText = "Player " + game.getWinner(); // Jugador ganador de la ronda
                    // Opciones: "Finish" si es el último nivel, "Continue" si hay más
                    String[] options = isLast
                        ? new String[]{"Finish", "Menu", "Close"}
                        : new String[]{"Continue", "Menu", "Close"};
                    int choice = JOptionPane.showOptionDialog(frame,
                        winnerText + " gana el nivel " + game.getCurrentLevel() + "!",
                        "Nivel Completado",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);
                    if (choice == 0) { // Continue / Finish
                        if (isLast) { // Último nivel: determina ganador por monedas y vuelve al menú
                            game.finishGame(); // Marca el juego como completado
                            int finalWinner = game.getFinalWinner(); // Ganador según monedas totales
                            String msg = (finalWinner == 0)
                                ? "¡EMPATE!" // Empate: misma cantidad de monedas
                                : "¡JUGADOR " + finalWinner + " GANA LA PARTIDA!";
                            JOptionPane.showMessageDialog(frame, msg,
                                "Campeón", JOptionPane.INFORMATION_MESSAGE);
                            new homescreen().frame.setVisible(true);
                            frame.dispose();
                        } else { // Hay más niveles: avanza al siguiente y reanuda el timer
                            resetKeys(); // Limpia teclas que quedaron "clavadas" por el diálogo modal
                            game.advanceToNextLevel();
                            ((Timer)e.getSource()).start();
                        }
                    } else if (choice == 1) { // Menu: vuelve al homescreen
                        new homescreen().frame.setVisible(true);
                        frame.dispose();
                    } else { // Close: muestra mensaje del ganador y al aceptar cierra la ventana
                        int finalWinner = game.getFinalWinner();
                        String msg = (finalWinner == 0)
                            ? "¡EMPATE!"
                            : "¡JUGADOR " + finalWinner + " GANA LA PARTIDA!";
                        JOptionPane.showMessageDialog(frame, msg,
                            "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose(); // Cierra la ventana del juego (no toda la aplicación)
                    }
                    return;
                }

                if (game.isGameCompleted()) { // Todos los niveles completados
                    ((Timer) e.getSource()).stop(); // Detiene el bucle
                    System.out.println("Gana jugador " + game.getWinner());
                    // Muestra un mensaje indicando qué jugador ganó
                    JOptionPane.showMessageDialog(frame, "PLAYER " + game.getWinner() + " WINS!",
                        "PvP - Game Over", JOptionPane.INFORMATION_MESSAGE);
                    new homescreen().frame.setVisible(true); // Vuelve al menú principal
                    frame.dispose(); // Cierra la ventana actual
                }
                if (game.isGameOver()) { // Tiempo agotado
                    resetKeys(); // Limpia teclas que quedaron "clavadas"
                    game.restartLevel();
                }
            }
        });
        gameLoop.start(); // Inicia el timer del juego

        // Cualquier clic en la ventana devuelve el foco al panel de juego
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.requestFocusInWindow();
            }
        });

        SoundEffect.loadVolumes(); // Aplica los volúmenes guardados en preferencias

        frame.setVisible(true);
        panel.requestFocusInWindow(); // Asegura que el panel reciba eventos de teclado
    }

    // Lee el estado actual de las teclas presionadas y mueve a los jugadores
    private void pollInput() {
        double dx1 = 0, dy1 = 0;
        if (w) dy1 -= 1; // Arriba para P1
        if (s) dy1 += 1; // Abajo para P1
        if (a) dx1 -= 1; // Izquierda para P1
        if (d) dx1 += 1; // Derecha para P1
        game.movePlayer1(dx1, dy1); // Aplica el movimiento a P1

        if (!game.getPlayer2().isAI()) { // Solo lee teclado para P2 si no tiene IA (modo PvM)
            double dx2 = 0, dy2 = 0;
            if (up) dy2 -= 1; // Arriba para P2
            if (down) dy2 += 1; // Abajo para P2
            if (left) dx2 -= 1; // Izquierda para P2
            if (right) dx2 += 1; // Derecha para P2
            game.movePlayer2(dx2, dy2); // Aplica el movimiento a P2
        }
    }

    // Resetea los flags de teclas para evitar movimiento fantasma tras diálogos modales
    private void resetKeys() {
        w = false; a = false; s = false; d = false;
        up = false; down = false; left = false; right = false;
    }

    // Expone la ventana para que el controlador pueda usarla en los diálogos
    public JFrame getFrame() { return frame; }

}
