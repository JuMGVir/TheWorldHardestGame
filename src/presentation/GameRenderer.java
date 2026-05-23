package presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import domain.*;

public class GameRenderer {

    public static void drawPlayer(Graphics g, Player p) {
        if (p == null) return;
        switch (p.getPlayerType()) {
            case BLINKY: g.setColor(new Color(180, 24, 27)); break;
            case INKY: g.setColor(new Color(25, 41, 149)); break;
            case CLYDE: g.setColor(new Color(40, 127, 47)); break;
        }
        g.fillRect((int)p.x, (int)p.y, (int)p.width, (int)p.height);
        String bc = p.getBorderColor();
        if (bc != null) {
            String[] rgb = bc.split(",");
            g.setColor(new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
            g.drawRect((int)p.x, (int)p.y, (int)p.width, (int)p.height);
        }
    }

    public static void drawEnemies(Graphics g, Level level) {
        g.setColor(Color.BLUE);
        for (Enemy e : level.getEnemies()) {
            if (e.isAlive()) g.fillOval((int)e.x, (int)e.y, (int)e.width, (int)e.height);
        }
    }

    public static void drawCoins(Graphics g, Level level) {
        for (Coin c : level.getCoins()) {
            if (c.isCollected() || c.isLife()) continue;
            PlayerType skin = c.getSkinType();
            if (skin != null) {
                switch (skin) {
                    case BLINKY: g.setColor(new Color(180, 24, 27)); break;
                    case INKY: g.setColor(new Color(25, 41, 149)); break;
                    case CLYDE: g.setColor(new Color(40, 127, 47)); break;
                    default: g.setColor(Color.YELLOW); break;
                }
            } else {
                g.setColor(Color.YELLOW);
            }
            g.fillOval((int)c.x, (int)c.y, (int)c.width, (int)c.height);
        }
    }

    public static void drawWalls(Graphics g, Level level) {
        g.setColor(Color.BLACK);
        for (Wall w : level.getWalls()) g.fillRect((int)w.x, (int)w.y, (int)w.width, (int)w.height);
    }

    public static void drawSafeZones(Graphics g, Level level) {
        g.setColor(Color.GREEN);
        for (SafeZone z : level.getSafeZones()) g.fillRect((int)z.x, (int)z.y, (int)z.width, (int)z.height);
    }

    public static void drawLives(Graphics2D g2, Level level) {
        g2.setColor(new Color(200, 0, 0));
        for (Coin c : level.getCoins()) {
            if (c.isLife() && !c.isCollected()) {
                int cx = (int)(c.x + c.width / 2);
                int cy = (int)(c.y + c.height / 2);
                int s = (int)(c.width / 2);
                g2.fillOval(cx - s, cy - s / 2, s * 2, s);
                g2.fillOval(cx - s / 2, cy - s, s, s * 2);
            }
        }
    }

    public static void drawShieldCharges(Graphics2D g2, Player p) {
        int charges = p.getShieldCharges();
        if (charges > 0) {
            g2.setColor(new Color(200, 0, 0));
            for (int i = 0; i < charges; i++) {
                int hx = 5 + i * 12;
                int hy = 3;
                int s = 4;
                g2.fillOval(hx - s, hy - s / 2, s * 2, s);
                g2.fillOval(hx - s / 2, hy - s, s, s * 2);
            }
        }
    }

    public static void drawBombs(Graphics2D g2, Level level) {
        for (Bomb b : level.getBombs()) {
            if (b.isExploding()) {
                g2.setColor(new Color(255, 0, 0, 77));
                g2.fillOval((int)b.getExplosionX(), (int)b.getExplosionY(),
                            (int)b.getExplosionWidth(), (int)b.getExplosionHeight());
            } else if (!b.isExploded()) {
                g2.setColor(Color.ORANGE);
                g2.fillOval((int)b.x, (int)b.y, (int)b.width, (int)b.height);
            }
        }
    }
}
