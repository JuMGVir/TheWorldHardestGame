package presentation;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/**
 * Home screen of the application. Provides buttons to start a new game,
 * load a saved game, or open the settings menu.
 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
 * @version 1.0
 */
public class homescreen {

	JFrame frame;

	/**
	 * Launches the home screen.
	 * @param args command-line arguments (unused)
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					homescreen window = new homescreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructs the home screen window.
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	public homescreen() {
		initialize();
	}

	/**
	 * Initializes the contents of the home screen frame, including
	 * background image, title, and navigation buttons.
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game"); //
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().setLayout(null);
	    
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png")); 
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);
	    
	    JLabel lblNewLabel = new JLabel("");
	    Image tittles = new ImageIcon(this.getClass().getResource("/home screen/title.png")).getImage();
	    Image tittle = tittles.getScaledInstance(818, 150, Image.SCALE_SMOOTH);
	    lblNewLabel.setIcon(new ImageIcon(tittle));
	    lblNewLabel.setBounds(0, 0, 802, 127);
	    
	    frame.getContentPane().add(lblNewLabel);
	    
	    JButton LoadGame = new JButton("");
	    LoadGame.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new loaderBoard().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image LoadGames = new ImageIcon(this.getClass().getResource("/home screen/load game.png")).getImage();
	    LoadGame.setIcon(new ImageIcon(LoadGames .getScaledInstance(89, 23, Image.SCALE_SMOOTH)));
	    LoadGame.setBounds(347, 162, 89, 23);
	    LoadGame.setBorderPainted(false);
	    frame.getContentPane().add(LoadGame);
	    
	    JButton newGame = new JButton("");
	    newGame.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new GAME_TUTO().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image newGames = new ImageIcon(this.getClass().getResource("/home screen/new game.png")).getImage();
	    newGame.setIcon(new ImageIcon(newGames.getScaledInstance(89, 23, Image.SCALE_SMOOTH)));
	    newGame.setBorderPainted(false);
	    newGame.setBounds(506, 162, 89, 23);
	    frame.getContentPane().add(newGame);
	    
	    JButton settings = new JButton("");
	    settings.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new Settings().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image settingss = new ImageIcon(this.getClass().getResource("/home screen/settings.png")).getImage();
	    settings.setIcon(new ImageIcon(settingss.getScaledInstance(89, 23, Image.SCALE_SMOOTH)));
	    settings.setBorderPainted(false);
	    settings.setBounds(170, 162, 89, 23);
	    frame.getContentPane().add(settings);
	    
	    JLabel fondo = new JLabel("ew");
	    fondo.setIcon(new ImageIcon(imgEscalada));
	    fondo.setBounds(0, 0, 802, 411);
	    frame.getContentPane().add(fondo);

        SoundEffect.loadVolumes();
        if (!SoundEffect.THEME.isPlaying()) {
            SoundEffect.THEME.loop();
        }

        JLabel miniLabel = new JLabel("TRY A LEVEL (WASD / ARROWS)", JLabel.CENTER);
        miniLabel.setFont(new Font("Impact", Font.BOLD, 18));
        miniLabel.setForeground(Color.WHITE);
        miniLabel.setBounds(0, 185, 818, 25);
        frame.getContentPane().add(miniLabel);

        MiniGamePanel miniGame = new MiniGamePanel(action -> {
            frame.dispose();
            switch (action) {
                case "SETTINGS" -> new Settings().frame.setVisible(true);
                case "LOAD" -> new loaderBoard().frame.setVisible(true);
                case "NEW_GAME" -> new GAME_TUTO().frame.setVisible(true);
            }
        });
        miniGame.setBounds(154, 200, 490, 210);
        frame.getContentPane().add(miniGame);
        SwingUtilities.invokeLater(() -> miniGame.requestFocusInWindow());
	}
}
