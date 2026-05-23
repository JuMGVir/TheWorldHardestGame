package presentation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import domain.*;

/**
 * Main game panel for single-player mode.
 * Handles rendering of all game elements (player, enemies, coins, walls, bombs, lives)
 * and processes keyboard input for player 1 movement using WASD keys.
 * Implements a singleton pattern via getInstance().
 * @author 
 * @version 1.0
 */
public class GamePanel extends JPanel {
    private Game game;
    private boolean up, down, left, right;
    // P1 keys (static para compartir con KeyBinds: la ventana de config escribe aquí)
    private static int p1KeyUp    = KeyEvent.VK_W;
    private static int p1KeyDown  = KeyEvent.VK_S;
    private static int p1KeyLeft  = KeyEvent.VK_A;
    private static int p1KeyRight = KeyEvent.VK_D;
    private static int p1KeyPause = KeyEvent.VK_P;
    // P2 keys (static para que KeyBinds y GAME_2P Panel2D usen el mismo valor)
    private static int p2KeyUp    = KeyEvent.VK_UP;
    private static int p2KeyDown  = KeyEvent.VK_DOWN;
    private static int p2KeyLeft  = KeyEvent.VK_LEFT;
    private static int p2KeyRight = KeyEvent.VK_RIGHT;
    private static int p2KeyPause = KeyEvent.VK_P;

    /**
     * Constructs a GamePanel with the specified game instance.
     * Initializes the panel dimensions, background color, and keyboard listener
     * for player 1 directional input.
     * @param game the game instance to render and control
     */
    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(989, 521));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Usa las variables estáticas p1KeyUp/Down/Left/Right que KeyBinds puede modificar
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int kc = e.getKeyCode();
                if (kc == p1KeyUp)    up = true;
                if (kc == p1KeyDown)  down = true;
                if (kc == p1KeyLeft)  left = true;
                if (kc == p1KeyRight) right = true;
            }
            public void keyReleased(KeyEvent e) {
                int kc = e.getKeyCode();
                if (kc == p1KeyUp)    up = false;
                if (kc == p1KeyDown)  down = false;
                if (kc == p1KeyLeft)  left = false;
                if (kc == p1KeyRight) right = false;
            }
        });
    }
    /**
     * Reads current key states (up, down, left, right) and applies the resulting
     * movement vector to the player, accounting for wall collision.
     * Skips keyboard input when the player is AI-controlled (movement is handled
     * by Game.update() via updateAI()).
     */
    public void updateInput() {
        game.updateInput(up, down, left, right);
    }

    /**
     * Paints the game scene by scaling the logical coordinate system (220x100)
     * uniformly to fit the panel, centering it, then drawing all game elements
     * in order: safe zones, walls, player, enemies, coins, bombs, lives, shield charges.
     * @param g the Graphics context to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int panelW = getWidth();
        int panelH = getHeight();
        int logicalW = 220;
        int logicalH = 100;

        // Calcula escala uniforme (la misma para X e Y)
        double scale = Math.min((double) panelW / logicalW, (double) panelH / logicalH);
        int scaledW = (int) (logicalW * scale);
        int scaledH = (int) (logicalH * scale);
        // Centra el juego en el panel
        int offsetX = (panelW - scaledW) / 2;
        int offsetY = (panelH - scaledH) / 2;

        // Aplica traslación y escala uniforme
        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);

        // Dibuja el fondo del juego (blanco en el área de juego)
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, logicalW, logicalH);

        drawSafeZones(g2);
        drawWalls(g2);
        drawPlayer(g2);
        drawEnemies(g2);
        drawCoins(g2);
        drawBombs(g2);
        drawLives(g2);
        drawShieldCharges(g2);
    }

    /**
     * Draws the player as a filled rectangle in the color corresponding to the
     * player's type (RED, BLUE, or GREEN).
     * @param g the Graphics context to draw on
     */
    private void drawPlayer(Graphics g) { GameRenderer.drawPlayer(g, game.getPlayer()); }
    private void drawEnemies(Graphics g) { GameRenderer.drawEnemies(g, game.getLevel()); }
    private void drawCoins(Graphics g) { GameRenderer.drawCoins(g, game.getLevel()); }
    private void drawWalls(Graphics g) { GameRenderer.drawWalls(g, game.getLevel()); }
    private void drawSafeZones(Graphics g) { GameRenderer.drawSafeZones(g, game.getLevel()); }
    private void drawLives(Graphics2D g2) { GameRenderer.drawLives(g2, game.getLevel()); }
    private void drawShieldCharges(Graphics2D g2) { GameRenderer.drawShieldCharges(g2, game.getPlayer()); }
    private void drawBombs(Graphics2D g2) { GameRenderer.drawBombs(g2, game.getLevel()); }

    // Setters/getters estáticos: KeyBinds.java los llama cuando el usuario presiona una tecla nueva
    // Al ser static, todas las ventanas GamePanel/GamePanel2D ven el mismo valor actualizado
    public static void setP1KeyUp(int key)    { p1KeyUp = key; }
    public static void setP1KeyDown(int key)  { p1KeyDown = key; }
    public static void setP1KeyLeft(int key)  { p1KeyLeft = key; }
    public static void setP1KeyRight(int key) { p1KeyRight = key; }
    public static void setP1KeyPause(int key) { p1KeyPause = key; }
    public static int getP1KeyUp()    { return p1KeyUp; }
    public static int getP1KeyDown()  { return p1KeyDown; }
    public static int getP1KeyLeft()  { return p1KeyLeft; }
    public static int getP1KeyRight() { return p1KeyRight; }
    public static int getP1KeyPause() { return p1KeyPause; }
    // P2 static: igual que P1 pero para el segundo jugador (usado por GAME_2P y KeyBinds)
    public static void setP2KeyUp(int key)    { p2KeyUp = key; }
    public static void setP2KeyDown(int key)  { p2KeyDown = key; }
    public static void setP2KeyLeft(int key)  { p2KeyLeft = key; }
    public static void setP2KeyRight(int key) { p2KeyRight = key; }
    public static void setP2KeyPause(int key) { p2KeyPause = key; }
    public static int getP2KeyUp()    { return p2KeyUp; }
    public static int getP2KeyDown()  { return p2KeyDown; }
    public static int getP2KeyLeft()  { return p2KeyLeft; }
    public static int getP2KeyRight() { return p2KeyRight; }
    public static int getP2KeyPause() { return p2KeyPause; }
}