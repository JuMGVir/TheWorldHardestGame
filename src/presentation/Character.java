package presentation;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import domain.Game;
import domain.Player;
import domain.PlayerMovementAI;
import domain.PlayerType;

/**
 * Character selection screen. Allows the player to choose between
 * different character types (Red/Blinky, Blue/Inky, Green/Clyde)
 * before starting the game.
 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
 * @version 1.0
 */
public class Character {

	public JFrame frame;
	private static Game pendingGame;
	// Si no es null, el modo PvM está activo y se redirige a GAME_2P con IA
	public static PlayerMovementAI pvmAI = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Character window = new Character();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Guarda una partida pendiente para restaurarla después de elegir personaje
	public static void setPendingGame(Game game) {
		pendingGame = game;
	}

	public Character() {
		initialize();
	}

	// Cierra la selección de personaje e inicia el juego: PvM si pvmAI no es null, o 1 jugador normal
	private void startGameWithType(PlayerType type) {
		int level = showLevelSelection();
		if (level < 0) return; // Usuario canceló
		frame.dispose();
		if (pvmAI != null) { // Modo PvM: arranca GAME_2P con el humano como P1 y la IA como P2
			PlayerMovementAI ai = pvmAI;
			pvmAI = null; // Limpia la variable estática para no afectar futuras partidas
			new GAME_2P(type, ai); // Inicia juego PvM: P1 humano del tipo elegido, P2 con IA
			return;
		}
		Game game; // Modo 1 jugador normal (o con partida pendiente)
		if (pendingGame != null) { // Si hay partida guardada pendiente, la retoma
			game = pendingGame;
			Player old = game.getPlayer(); // Conserva posición y escudos del jugador anterior
			Player newPlayer = new Player(old.x, old.y, type); // Crea jugador con el nuevo tipo
			newPlayer.setRespawnPoint(old.getRespawnX(), old.getRespawnY());
			newPlayer.setShieldCharges(old.getShieldCharges()); // Mantiene escudos acumulados
			game.setPlayer(newPlayer); // Reemplaza el jugador en el juego
			pendingGame = null; // Limpia la partida pendiente
		} else { // Partida nueva: crea jugador con el tipo elegido desde cero
			game = new Game();
			Player old = game.getPlayer(); // Toma el jugador por defecto (rojo)
			Player newPlayer = new Player(old.x, old.y, type); // Lo reemplaza con el tipo elegido
			newPlayer.setRespawnPoint(old.getRespawnX(), old.getRespawnY());
			newPlayer.setShieldCharges(old.getShieldCharges());
			game.setPlayer(newPlayer);
		}
		game.setStartingLevel(level);
		new GAME_1P(game); // Inicia la ventana de 1 jugador con el juego configurado
	}

	private int showLevelSelection() {
		SelectLevel sl = new SelectLevel(frame);
		return sl.showDialog();
	}

	/**
	 * Initializes the contents of the character selection frame,
	 * including background, title, and character buttons.
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game"); //
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png")); 
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);
	    
	    frame.getContentPane().setLayout(null);
	    Image tittles = new ImageIcon(this.getClass().getResource("/character/title.png")).getImage();
	    Image tittle = tittles.getScaledInstance(900, 100, Image.SCALE_SMOOTH);
	      JLabel title = new JLabel("");
	      title.setIcon(new ImageIcon(tittle));
	      title.setBounds(0, 0, 802, 100);
	      frame.getContentPane().add(title);
		

	    JLabel lblBlinky = new JLabel("");
	    Image imgTitBlinky = new ImageIcon(this.getClass().getResource("/character/blinky.png")).getImage();
	    lblBlinky.setIcon(new ImageIcon(imgTitBlinky.getScaledInstance(200, 80, Image.SCALE_SMOOTH)));
	    lblBlinky.setBounds(53, 124, 200, 80);
	    frame.getContentPane().add(lblBlinky);


	    JButton btnBlinky = new JButton("");
	    Image imgBtnBlinky = new ImageIcon(this.getClass().getResource("/character/blinky image.png")).getImage();
	    btnBlinky.setIcon(new ImageIcon(imgBtnBlinky.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
	    btnBlinky.setBounds(59, 250, 150, 150);
	    btnBlinky.setContentAreaFilled(false);
	    btnBlinky.setBorderPainted(false);
	    frame.getContentPane().add(btnBlinky);
	    btnBlinky.addActionListener(e -> startGameWithType(PlayerType.BLINKY));
	    
	    

	    JLabel lblInky = new JLabel("");
	    Image imgTitInky = new ImageIcon(this.getClass().getResource("/character/inkly.png")).getImage();
	    lblInky.setIcon(new ImageIcon(imgTitInky.getScaledInstance(200, 80, Image.SCALE_SMOOTH)));
	    lblInky.setBounds(304, 124, 200, 80);
	    frame.getContentPane().add(lblInky);

	    JButton btnInky = new JButton("");
	    Image imgBtnInky = new ImageIcon(this.getClass().getResource("/character/inkyimage.png")).getImage();
	    btnInky.setIcon(new ImageIcon(imgBtnInky.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
	    btnInky.setBounds(323, 250, 150, 150);
	    btnInky.setContentAreaFilled(false);
	    btnInky.setBorderPainted(false);
	    frame.getContentPane().add(btnInky);
	    btnInky.addActionListener(e -> startGameWithType(PlayerType.INKY));
	    

	    JLabel lblClyde = new JLabel("");
	    Image imgTitClyde = new ImageIcon(this.getClass().getResource("/character/clyde.png")).getImage();
	    lblClyde.setIcon(new ImageIcon(imgTitClyde.getScaledInstance(200, 80, Image.SCALE_SMOOTH)));
	    lblClyde.setBounds(558, 124, 200, 80);
	    frame.getContentPane().add(lblClyde);


	    JButton btnClyde = new JButton("");
	    Image imgBtnClyde = new ImageIcon(this.getClass().getResource("/character/clydeImage.png")).getImage();
	    btnClyde.setIcon(new ImageIcon(imgBtnClyde.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
	    btnClyde.setBounds(574, 250, 150, 150);
	    btnClyde.setContentAreaFilled(false);
	    btnClyde.setBorderPainted(false);
	    frame.getContentPane().add(btnClyde);
	    btnClyde.addActionListener(e -> startGameWithType(PlayerType.CLYDE));
	    
		  JLabel fondo = new JLabel("ew");
		  fondo.setBounds(0, 0, 802, 411);
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  frame.getContentPane().add(fondo);
	}

}
