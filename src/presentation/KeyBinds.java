package presentation;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;

/**
 * GUI window for configuring keyboard bindings for both Player 1 and Player 2.
 * Displays text fields that capture key presses and maps them to movement actions.
 * @author Juan José
 * @version 1.0
 */
public class KeyBinds {

	JFrame frame;

	/**
	 * Launch the application.
	 * @param args command-line arguments
	 * @author Juan José
	 * @version 1.0
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KeyBinds window = new KeyBinds();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @author Juan José
	 * @version 1.0
	 */
	public KeyBinds() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @author Juan José
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
		    Image tittles = new ImageIcon(this.getClass().getResource("/Key binds/tittle.png")).getImage();
		    Image tittle = tittles.getScaledInstance(800, 100, Image.SCALE_SMOOTH);
		    
		    Image backTomenu = new ImageIcon(this.getClass().getResource("/Key binds/back to settiings.png")).getImage();
		    Image backTomenus = backTomenu.getScaledInstance(510, 100, Image.SCALE_SMOOTH);
		    JButton BACKTOMENU = new JButton("");
		    BACKTOMENU.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		new Settings().frame.setVisible(true);
		    		frame.dispose();
		    	}
		    });
		    BACKTOMENU.setContentAreaFilled(false);
		    BACKTOMENU.setBorderPainted(false);
		    BACKTOMENU.setBounds(306, 0, 496, 83);
		    BACKTOMENU.setIcon(new ImageIcon(backTomenus));
		    frame.getContentPane().add(BACKTOMENU);
		      
		      // Cada JFormattedTextField muestra la tecla actual y permite cambiarla con solo presionar otra
		      JFormattedTextField UP_P1 = new JFormattedTextField();
		      UP_P1.setHorizontalAlignment(SwingConstants.CENTER);
		      UP_P1.setFont(new Font("Impact", Font.PLAIN, 36));
		      UP_P1.setBounds(224, 154, 155, 33);
		      frame.getContentPane().add(UP_P1);
		      UP_P1.setEditable(false); // No se escribe con teclado, solo captura la tecla presionada
		      UP_P1.setFocusable(true);
		      UP_P1.setText(KeyEvent.getKeyText(GamePanel.getP1KeyUp())); // Muestra la tecla asignada actualmente
		      UP_P1.addKeyListener(new KeyAdapter() {
		          public void keyPressed(KeyEvent e) {
		              UP_P1.setText(KeyEvent.getKeyText(e.getKeyCode())); // Refleja la tecla presionada en el campo
		              if (e.getKeyCode() != 0) GamePanel.setP1KeyUp(e.getKeyCode()); // Guarda el nuevo código en el static
		              e.consume();
		          }
		      });
		      
		      JLabel PLAYER_2 = new JLabel("PLAYER 2");
		      PLAYER_2.setForeground(new Color(58, 58, 58));
		      PLAYER_2.setFont(new Font("Impact", Font.BOLD, 56));
		      PLAYER_2.setBounds(502, 82, 211, 61);
		      frame.getContentPane().add(PLAYER_2);
		      
		      JLabel PLAYER_1 = new JLabel("PLAYER 1");
		      PLAYER_1.setForeground(new Color(58, 58, 58));
		      PLAYER_1.setFont(new Font("Impact", Font.BOLD, 56));
		      PLAYER_1.setBounds(214, 82, 211, 61);
		      frame.getContentPane().add(PLAYER_1);
		      
		      JLabel PAUSE = new JLabel("PAUSE");
		      PAUSE.setFont(new Font("Impact", Font.BOLD, 50));
		      PAUSE.setForeground(new Color(58, 58, 58));
		      PAUSE.setBounds(10, 350, 168, 61);
		      frame.getContentPane().add(PAUSE);
		      
		      JLabel RIGTH = new JLabel("RIGTH");
		      RIGTH.setFont(new Font("Impact", Font.BOLD, 50));
		      RIGTH.setForeground(new Color(58, 58, 58));
		      RIGTH.setBounds(10, 300, 168, 61);
		      frame.getContentPane().add(RIGTH);
		      
		      JLabel LEFT = new JLabel("LEFT");
		      LEFT.setFont(new Font("Impact", Font.BOLD, 50));
		      LEFT.setForeground(new Color(58, 58, 58));
		      LEFT.setBounds(10, 248, 168, 61);
		      frame.getContentPane().add(LEFT);
		      
		      JLabel DOWN = new JLabel("DOWN");
		      DOWN.setFont(new Font("Impact", Font.BOLD, 50));
		      DOWN.setForeground(new Color(58, 58, 58));
		      DOWN.setBounds(10, 196, 168, 61);
		      frame.getContentPane().add(DOWN);
		      
		      JLabel UP = new JLabel("UP");
		      UP.setFont(new Font("Impact", Font.BOLD, 50));
		      UP.setForeground(new Color(58, 58, 58));
		      UP.setBounds(10, 145, 168, 61);
		      frame.getContentPane().add(UP);
		    
		      JLabel title = new JLabel("");
		      title.setIcon(new ImageIcon(tittle));
		      title.setBounds(0, 0, 850, 100);
		      frame.getContentPane().add(title);
		    
		    JFormattedTextField DOWN_P1 = new JFormattedTextField();
		    DOWN_P1.setHorizontalAlignment(SwingConstants.CENTER);
		    DOWN_P1.setFont(new Font("Impact", Font.PLAIN, 40));
		    DOWN_P1.setBounds(224, 215, 155, 33);
		    frame.getContentPane().add(DOWN_P1);
		    DOWN_P1.setEditable(false);
		    DOWN_P1.setFocusable(true);
		    DOWN_P1.setText(KeyEvent.getKeyText(GamePanel.getP1KeyDown()));
		    DOWN_P1.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            DOWN_P1.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP1KeyDown(e.getKeyCode());
		            e.consume();
		        }
		    });
		    
		    JFormattedTextField LEFT_P1 = new JFormattedTextField();
		    LEFT_P1.setHorizontalAlignment(SwingConstants.CENTER);
		    LEFT_P1.setFont(new Font("Impact", Font.PLAIN, 40));
		    LEFT_P1.setBounds(224, 259, 155, 33);
		    frame.getContentPane().add(LEFT_P1);
		    LEFT_P1.setEditable(false);
		    LEFT_P1.setFocusable(true);
		    LEFT_P1.setText(KeyEvent.getKeyText(GamePanel.getP1KeyLeft()));
		    LEFT_P1.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            LEFT_P1.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP1KeyLeft(e.getKeyCode());
		            e.consume();
		        }
		    });
		    JFormattedTextField RIGTH_P1 = new JFormattedTextField();
		    RIGTH_P1.setHorizontalAlignment(SwingConstants.CENTER);
		    RIGTH_P1.setFont(new Font("Impact", Font.PLAIN, 40));
		    RIGTH_P1.setBounds(224, 316, 155, 33);
		    frame.getContentPane().add(RIGTH_P1);
		    RIGTH_P1.setEditable(false);
		    RIGTH_P1.setFocusable(true);
		    RIGTH_P1.setText(KeyEvent.getKeyText(GamePanel.getP1KeyRight()));
		    RIGTH_P1.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            RIGTH_P1.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP1KeyRight(e.getKeyCode());
		            e.consume();
		        }
		    });
		    
		    JFormattedTextField PAUSE_P1 = new JFormattedTextField();
		    PAUSE_P1.setHorizontalAlignment(SwingConstants.CENTER);
		    PAUSE_P1.setFont(new Font("Impact", Font.PLAIN, 40));
		    PAUSE_P1.setBounds(224, 367, 155, 33);
		    frame.getContentPane().add(PAUSE_P1);
		    PAUSE_P1.setEditable(false);
		    PAUSE_P1.setFocusable(true);
		    PAUSE_P1.setText(KeyEvent.getKeyText(GamePanel.getP1KeyPause()));
		    PAUSE_P1.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		        	PAUSE_P1.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP1KeyPause(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    JFormattedTextField UP_P2 = new JFormattedTextField();
		    UP_P2.setHorizontalAlignment(SwingConstants.CENTER);
		    UP_P2.setFont(new Font("Impact", Font.PLAIN, 40));
		    UP_P2.setBounds(523, 154, 155, 33);
		    frame.getContentPane().add(UP_P2);
		    UP_P2.setEditable(false);
		    UP_P2.setFocusable(true);
		    UP_P2.setText(KeyEvent.getKeyText(GamePanel.getP2KeyUp()));
		    UP_P2.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            UP_P2.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP2KeyUp(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    JFormattedTextField DOWN_P2 = new JFormattedTextField();
		    DOWN_P2.setHorizontalAlignment(SwingConstants.CENTER);
		    DOWN_P2.setFont(new Font("Impact", Font.PLAIN, 40));
		    DOWN_P2.setBounds(523, 215, 155, 33);
		    frame.getContentPane().add(DOWN_P2);
		    DOWN_P2.setEditable(false);
		    DOWN_P2.setFocusable(true);
		    DOWN_P2.setText(KeyEvent.getKeyText(GamePanel.getP2KeyDown()));
		    DOWN_P2.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            DOWN_P2.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP2KeyDown(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    JFormattedTextField LEFT_P2 = new JFormattedTextField();
		    LEFT_P2.setHorizontalAlignment(SwingConstants.CENTER);
		    LEFT_P2.setFont(new Font("Impact", Font.PLAIN, 40));
		    LEFT_P2.setBounds(523, 259, 155, 33);
		    frame.getContentPane().add(LEFT_P2);
		    LEFT_P2.setEditable(false);
		    LEFT_P2.setFocusable(true);
		    LEFT_P2.setText(KeyEvent.getKeyText(GamePanel.getP2KeyLeft()));
		    LEFT_P2.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            LEFT_P2.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP2KeyLeft(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    JFormattedTextField RIGTH_P2 = new JFormattedTextField();
		    RIGTH_P2.setHorizontalAlignment(SwingConstants.CENTER);
		    RIGTH_P2.setFont(new Font("Impact", Font.PLAIN, 40));
		    RIGTH_P2.setBounds(523, 316, 155, 33);
		    frame.getContentPane().add(RIGTH_P2);
		    RIGTH_P2.setEditable(false);
		    RIGTH_P2.setFocusable(true);
		    RIGTH_P2.setText(KeyEvent.getKeyText(GamePanel.getP2KeyRight()));
		    RIGTH_P2.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            RIGTH_P2.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP2KeyRight(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    JFormattedTextField PAUSE_P2 = new JFormattedTextField();
		    PAUSE_P2.setHorizontalAlignment(SwingConstants.CENTER);
		    PAUSE_P2.setFont(new Font("Impact", Font.PLAIN, 40));
		    PAUSE_P2.setBounds(523, 367, 155, 33);
		    frame.getContentPane().add(PAUSE_P2);
		    PAUSE_P2.setEditable(false);
		    PAUSE_P2.setFocusable(true);
		    PAUSE_P2.setText(KeyEvent.getKeyText(GamePanel.getP2KeyPause()));
		    PAUSE_P2.addKeyListener(new KeyAdapter() {
		        public void keyPressed(KeyEvent e) {
		            PAUSE_P2.setText(KeyEvent.getKeyText(e.getKeyCode()));
		            if (e.getKeyCode() != 0) GamePanel.setP2KeyPause(e.getKeyCode());
		            e.consume();
		        }
		    });
		    		    
		    		    		    
		    		    		    JLabel fondo = new JLabel("ew");
		    		    		    fondo.setBounds(0, 0, 802, 411);
		    		    		    fondo.setIcon(new ImageIcon(imgEscalada));
		    		    		    frame.getContentPane().add(fondo);
		    
	}
		
}
