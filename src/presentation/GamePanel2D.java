package presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import domain.Game;
import domain.Game2P;

/**
 * Panel personalizado que dibuja la escena del juego para dos jugadores.
 * Escala y centra el sistema de coordenadas lógicas (220×100) al tamaño real del panel.
 */
public class GamePanel2D extends JPanel {

    private Game2P game;

    public GamePanel2D(Game2P game) {
        this.game = game;
        setPreferredSize(new java.awt.Dimension(989, 521));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int panelW = getWidth();
        int panelH = getHeight();
        int logicalW = 220;
        int logicalH = 100;
        double scale = Math.min((double) panelW / logicalW, (double) panelH / logicalH);
        int scaledW = (int) (logicalW * scale);
        int scaledH = (int) (logicalH * scale);
        int offsetX = (panelW - scaledW) / 2;
        int offsetY = (panelH - scaledH) / 2;

        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, logicalW, logicalH);

        GameRenderer.drawSafeZones(g2, game.getLevel());
        GameRenderer.drawWalls(g2, game.getLevel());
        GameRenderer.drawCoins(g2, game.getLevel());
        GameRenderer.drawEnemies(g2, game.getLevel());
        GameRenderer.drawBombs(g2, game.getLevel());
        GameRenderer.drawLives(g2, game.getLevel());
        drawPlayers(g2);
    }

    private void drawPlayers(Graphics g) {
        GameRenderer.drawPlayer(g, game.getPlayer1());
        GameRenderer.drawPlayer(g, game.getPlayer2());
    }
}
