package presentation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import domain.Game;
import domain.LevelFactory;
import domain.Memento;
import domain.Player;
import domain.PlayerType;
import domain.SafeZone;

/**
 * Panel empotrado en la pantalla de inicio que ejecuta un mini nivel
 * del juego (Level 5) con las 3 zonas FINAL que redirigen a
 * Settings, Load y New Game respectivamente.
 * 
 * La lógica del juego (colisiones, enemigos, power-ups, tiempo) es
 * delegada completamente a {@link domain.Game} y sus clases del
 * paquete domain. Este panel solo se encarga de:
 * <ul>
 *   <li>Capturar teclado (WASD / Flechas)</li>
 *   <li>Pintar el nivel escalado</li>
 *   <li>Interceptar el estado "pendingBoard" de Game para redirigir</li>
 * </ul>
 * 
 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
 * @version 1.0
 */
public class MiniGamePanel extends JPanel implements KeyListener {

    private static final int LOGICAL_W = 245; // Ancho lógico del nivel en unidades del juego
    private static final int LOGICAL_H = 120; // Alto lógico del nivel

    private Game game; // Instancia de Game que maneja el estado del mini nivel
    private Memento initialState; // Snapshot del estado inicial para reiniciar tras game-over o navegación
    private Consumer<String> callback; // Función que recibe la acción ("SETTINGS", "LOAD", "NEW_GAME")
    private boolean[] keys = new boolean[256]; // Arreglo de teclas presionadas (KeyEvent.VK_*)
    private Timer timer; // Timer de 60 FPS (16ms) para el bucle principal
    private boolean stopped; // Bandera para detener el timer al cerrar
    private Image[] goalImgs; // Imágenes de botones para las 3 zonas FINAL

    /**
     * Constructor del mini panel de juego.
     * Inicializa el timer a 60 FPS, configura el KeyListener, inyecta el
     * mini nivel en una instancia de {@link domain.Game} y comienza el bucle.
     * 
     * @name MiniGamePanel
     * @param callback Función que recibe "SETTINGS", "LOAD" o "NEW_GAME"
     *                 cuando el jugador alcanza una zona FINAL.
     */
    public MiniGamePanel(Consumer<String> callback) {
        this.callback = callback;
        setFocusable(true);
        addKeyListener(this);
        initGame();
        timer = new Timer(16, e -> { if (!stopped) { tick(); repaint(); } });
        timer.start();
    }

    /**
     * Inicializa la instancia de Game con el mini nivel (Level 5) a través
     * del patrón Memento (fromMemento). También guarda un initialState para
     * reiniciar el nivel cuando sea necesario.
     * 
     * @name initGame
     */
    private void initGame() {
        game = new Game(); // Crea Game (carga level 1 por defecto)

        // Prepara un Memento con el mini nivel y el jugador en la posición inicial
        Memento m = new Memento();
        m.setLevel(LevelFactory.createMiniLevel()); // Nivel laberíntico con 3 zonas FINAL
        m.setPlayer1(new Player(125, 87, PlayerType.BLINKY)); // Jugador rojo en el centro
        m.setCurrentLevel(5); // Nivel 5 = mini level
        m.setLevelTimeRemaining(7200); // 120 segundos × 60 FPS
        game.fromMemento(m); // Inyecta el estado en Game

        // Snapshot del estado inicial para reinicios
        initialState = new Memento();
        initialState.setLevel(LevelFactory.createMiniLevel());
        initialState.setPlayer1(new Player(125, 87, PlayerType.BLINKY));
        initialState.setCurrentLevel(5);
        initialState.setLevelTimeRemaining(7200);

        // Carga las imágenes de los botones de cada zona FINAL
        goalImgs = new Image[]{
            new ImageIcon(getClass().getResource("/home screen/settings.png")).getImage(),
            new ImageIcon(getClass().getResource("/home screen/load game.png")).getImage(),
            new ImageIcon(getClass().getResource("/home screen/new game.png")).getImage()
        };
    }

    /**
     * Detiene el timer y marca el panel como detenido.
     * Llamado automáticamente desde {@link #removeNotify()} cuando el
     * JFrame padre se cierra.
     * 
     * @name stop
     */
    public void stop() {
        stopped = true;
        if (timer != null) timer.stop();
    }

    /**
     * Bucle principal ejecutado 60 veces por segundo.
     * Lee el teclado, mueve al jugador, delega la lógica a
     * {@link domain.Game#update()} y verifica si el jugador
     * alcanzó una zona FINAL o si el tiempo se agotó.
     * 
     * @name tick
     */
    private void tick() {
        Player p = game.getPlayer(); // Referencia al jugador actual

        // Dirección según teclas presionadas
        int dx = 0, dy = 0;
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) dy = -1;
        if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) dy = 1;
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) dx = -1;
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) dx = 1;

        if (dx != 0 || dy != 0) {
            p.move(dx, dy, game.getLevel().getWalls()); // Mueve respetando paredes
            // Clamp dentro de los límites lógicos
            p.setPosition(
                Math.max(0, Math.min(LOGICAL_W - p.width, p.getX())),
                Math.max(0, Math.min(LOGICAL_H - p.height, p.getY()))
            );
        }

        game.update(); // Game maneja monedas, enemigos, bombas, vidas, zonas seguras y tiempo

        // Si el tiempo se agotó → reinicia el nivel desde el initialState
        if (game.isGameOver()) {
            game.fromMemento(initialState);
        }

        // Si el jugador llegó a una zona FINAL (pendingBoard = true)
        if (game.isPendingBoard()) {
            String action = null;
            // Identifica cuál de las 3 zonas FINAL se activó por su coordenada X
            for (SafeZone sz : game.getLevel().getSafeZones()) {
                if (sz.isFinal() && p.collides(sz)) {
                    if (sz.getX() < 60) action = "SETTINGS"; // Izquierda → Settings
                    else if (sz.getX() > 150) action = "NEW_GAME"; // Derecha → New Game
                    else action = "LOAD"; // Centro → Load
                    break;
                }
            }
            game.fromMemento(initialState); // Reinicia el nivel para la próxima vez
            if (action != null && callback != null) {
                callback.accept(action); // Dispara la navegación
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Escala para mantener la relación de aspecto 245:120 dentro del panel
        double sc = Math.min(getWidth() / (double) LOGICAL_W,
                getHeight() / (double) LOGICAL_H);
        int ox = (int) ((getWidth() - LOGICAL_W * sc) / 2);
        int oy = (int) ((getHeight() - LOGICAL_H * sc) / 2);

        g2.translate(ox, oy);
        g2.scale(sc, sc);

        // Fondo blanco
        g2.setColor(java.awt.Color.WHITE);
        g2.fillRect(0, 0, LOGICAL_W, LOGICAL_H);

        GameRenderer.drawSafeZones(g, game.getLevel());
        GameRenderer.drawWalls(g, game.getLevel());
        GameRenderer.drawEnemies(g, game.getLevel());
        GameRenderer.drawCoins(g, game.getLevel());
        GameRenderer.drawPlayer(g, game.getPlayer());

        // Dibuja las imágenes de botón sobre cada zona FINAL
        int i = 0;
        for (SafeZone sz : game.getLevel().getSafeZones()) {
            if (!sz.isFinal()) continue;
            if (i < goalImgs.length && goalImgs[i] != null) {
                g2.drawImage(goalImgs[i], (int) sz.getX(), (int) sz.getY(),
                        (int) sz.width, (int) sz.height, null);
            }
            i++;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void removeNotify() {
        super.removeNotify();
        stop();
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        return new java.awt.Dimension(490, 240);
    }
}
