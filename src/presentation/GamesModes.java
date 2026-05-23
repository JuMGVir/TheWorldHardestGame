package presentation;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * GUI window for selecting the game mode: Lonely (1P), Player vs Player (PVP),
 * or Player vs Machine (PvM).
 * @author Juan José
 * @version 1.0
 */
public class GamesModes {

	public JFrame frame;

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
					GamesModes window = new GamesModes();
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
	public GamesModes() {
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
	    Image tittles = new ImageIcon(this.getClass().getResource("/games modes/title.png")).getImage();
	    Image tittle = tittles.getScaledInstance(578, 56, Image.SCALE_SMOOTH);
	      JLabel title = new JLabel("");
	      title.setIcon(new ImageIcon(tittle));
	      title.setBounds(0, 0, 338, 56);
	      frame.getContentPane().add(title);
	      
		   

	      JLabel lblLoenly = new JLabel("");
	      Image imgLoenly = new ImageIcon(this.getClass().getResource("/games modes/loenly.png")).getImage();
	      lblLoenly.setIcon(new ImageIcon(imgLoenly.getScaledInstance(278, 93, Image.SCALE_SMOOTH)));
	      lblLoenly.setBounds(10, 89, 278, 93);
	      frame.getContentPane().add(lblLoenly);

	      JLabel lblPvp = new JLabel("");
	      Image imgPvp = new ImageIcon(this.getClass().getResource("/games modes/pvp.png")).getImage();
	      lblPvp.setIcon(new ImageIcon(imgPvp.getScaledInstance(196, 93, Image.SCALE_SMOOTH)));
	      lblPvp.setBounds(298, 89, 196, 93);
	      frame.getContentPane().add(lblPvp);

	      JLabel lblPvsM = new JLabel("");
	      Image imgPvsM = new ImageIcon(this.getClass().getResource("/games modes/pcvp.png")).getImage();
	      lblPvsM.setIcon(new ImageIcon(imgPvsM.getScaledInstance(278, 93, Image.SCALE_SMOOTH)));
	      lblPvsM.setBounds(514, 89, 278, 93);
	      frame.getContentPane().add(lblPvsM);
	      

	      JButton btnLoenly = new JButton("");
	      
	      Image imgLoenly1 = new ImageIcon(this.getClass().getResource("/games modes/loenly image.png")).getImage();
	      btnLoenly.setIcon(new ImageIcon(imgLoenly1.getScaledInstance(242, 152, Image.SCALE_SMOOTH)));
	      btnLoenly.setBounds(25, 209, 242, 152);
	      btnLoenly.setContentAreaFilled(false);
	      btnLoenly.setBorderPainted(false);
	      frame.getContentPane().add(btnLoenly);
	      btnLoenly.addActionListener(new ActionListener() {
	    	    public void actionPerformed(ActionEvent e) {
	    	        frame.dispose();
	    	        new Character().frame.setVisible(true);
	    	    }
	    	});
	      JButton btnPvp = new JButton("");
	      btnPvp.addActionListener(e -> {
	          frame.dispose();
	          new Character2P();
	      });
	      Image imgPvp1 = new ImageIcon(this.getClass().getResource("/games modes/pvpimage.png")).getImage();
	      btnPvp.setIcon(new ImageIcon(imgPvp1.getScaledInstance(186, 143, Image.SCALE_SMOOTH)));
	      btnPvp.setBounds(308, 193, 186, 143); 
	      btnPvp.setContentAreaFilled(false);
	      btnPvp.setBorderPainted(false);
	      frame.getContentPane().add(btnPvp);

	      JButton btnPvsM = new JButton("");
	      btnPvsM.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		new PVsM().frame.setVisible(true);
		    		frame.dispose();
		    	}
		    });
	      Image imgPvsM1 = new ImageIcon(this.getClass().getResource("/games modes/pcvpImagen.png")).getImage();
	      btnPvsM.setIcon(new ImageIcon(imgPvsM1.getScaledInstance(201, 152, Image.SCALE_SMOOTH)));
	      btnPvsM.setBounds(557, 193, 201, 152);  
	      btnPvsM.setContentAreaFilled(false);
	      btnPvsM.setBorderPainted(false);
	      frame.getContentPane().add(btnPvsM);
		  JLabel fondo = new JLabel("ew");
		  fondo.setBounds(0, 0, 802, 411);
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  frame.getContentPane().add(fondo);
	}

}
