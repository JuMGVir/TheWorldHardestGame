package presentation;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import domain.ExpertAI;
import domain.RandomAI;

/**
 * Pantalla de selección de dificultad para el modo Jugador vs Máquina (PvM).
 * Permite elegir entre modo Fácil (RandomAI) o Difícil (ExpertAI).
 */
public class PVsM {

	JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PVsM window = new PVsM();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PVsM() {
		initialize();
	}

	// Inicializa la ventana con el fondo, título y botones de dificultad
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game"); 
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png")); 
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);
	    
	    frame.getContentPane().setLayout(null);
	    // Título de la pantalla (imagen con texto "SELECT MODE")
	    Image tittles = new ImageIcon(this.getClass().getResource("/PVSM/tittle.png")).getImage();
	    Image tittle = tittles.getScaledInstance(390, 100, Image.SCALE_SMOOTH);
	      JLabel title = new JLabel("");
	      title.setIcon(new ImageIcon(tittle));
	      title.setBounds(0, 0, 802, 100);
	      frame.getContentPane().add(title);

	      // Botón EASY: asigna RandomAI como IA de P2 y abre selección de personaje
	      JButton btnEasy = new JButton("");
	      btnEasy.addActionListener(e -> {
	          Character.pvmAI = new RandomAI(); // IA fácil: movimientos aleatorios
	          new Character().frame.setVisible(true); // Abre selección de personaje para P1
	          frame.dispose(); // Cierra esta pantalla
	      });
	      Image imgEasy = new ImageIcon(this.getClass().getResource("/PVSM/easyimage.png")).getImage();
	      btnEasy.setIcon(new ImageIcon(imgEasy.getScaledInstance(201, 152, Image.SCALE_SMOOTH)));
	      btnEasy.setBounds(80, 236, 201, 152); 
	      btnEasy.setContentAreaFilled(false);
	      btnEasy.setBorderPainted(false);
	      frame.getContentPane().add(btnEasy);

	      // Botón HARD: asigna ExpertAI como IA de P2 y abre selección de personaje
	      JButton btnHard = new JButton("");
	      btnHard.addActionListener(e -> {
	          Character.pvmAI = new ExpertAI(); // IA difícil: comportamiento experto
	          new Character().frame.setVisible(true); // Abre selección de personaje para P1
	          frame.dispose(); // Cierra esta pantalla
	      });
	      Image imgHard = new ImageIcon(this.getClass().getResource("/PVSM/hardimage.png")).getImage();
	      btnHard.setIcon(new ImageIcon(imgHard.getScaledInstance(201, 152, Image.SCALE_SMOOTH)));
	      btnHard.setBounds(522, 236, 201, 152);
	      btnHard.setContentAreaFilled(false);
	      btnHard.setBorderPainted(false);
	      frame.getContentPane().add(btnHard);

	      // Etiqueta "EASY" sobre el botón
	      JLabel lblEasy = new JLabel("");
	      Image imgEasy1 = new ImageIcon(this.getClass().getResource("/PVSM/easy.png")).getImage();
	      lblEasy.setIcon(new ImageIcon(imgEasy1.getScaledInstance(290, 152, Image.SCALE_SMOOTH)));
	      lblEasy.setBounds(70, 84, 239, 152); 
	      frame.getContentPane().add(lblEasy);

	      // Etiqueta "HARD" sobre el botón
	      JLabel lblHard = new JLabel("");
	      Image imgHard1 = new ImageIcon(this.getClass().getResource("/PVSM/hard.png")).getImage();
	      lblHard.setIcon(new ImageIcon(imgHard1.getScaledInstance(290, 152, Image.SCALE_SMOOTH)));
	      lblHard.setBounds(501, 94, 248, 152); 
	      frame.getContentPane().add(lblHard);
	      
	      // Fondo de la ventana
		  JLabel fondo = new JLabel("ew");
		  fondo.setBounds(0, 0, 802, 411);
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  frame.getContentPane().add(fondo);
	}

}
