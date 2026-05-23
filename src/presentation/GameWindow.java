package presentation;

import java.awt.event.KeyListener;

import javax.swing.*;
import domain.*;
import java.awt.BorderLayout;

/**
 * Main game window for "The World's Hardest Game".
 * Sets up the JFrame with a GamePanel, starts the game loop
 * using a Swing Timer at ~60 FPS.
 * @author Juan José
 * @version 1.0
 */
public class GameWindow extends JFrame {

    /**
     * Constructs the game window, creates the game model and panel,
     * and starts the game loop timer.
     */
    public GameWindow() {

        Game game = new Game();
        GamePanel panel = new GamePanel(game);

        getContentPane().add(panel, BorderLayout.NORTH);
        pack();

        setTitle("World's Hardest Game - Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        Timer timer = new Timer(16, e -> {
            panel.updateInput();
            game.update();
            panel.repaint();
        });

        timer.start();
    }

    /**
     * Application entry point. Creates and displays the GameWindow.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new GameWindow();
    }

}
