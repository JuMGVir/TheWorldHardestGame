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
 * Tutorial screen window for "The World's Hardest Game".
 * Displays tutorial images and navigation buttons to return to the
 * home screen or select a game mode.
 * @author Juan José
 * @version 1.0
 */
public class GAME_TUTO {

	public JFrame frame;

	/**
	 * Launches the application on the Event Dispatch Thread.
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GAME_TUTO window = new GAME_TUTO();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates the tutorial window and initializes its contents.
	 */
	public GAME_TUTO() {
		initialize();
	}

	/**
	 * Initializes the contents of the frame, including background image,
	 * title label, and navigation buttons.
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game"); //
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png")); 
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);
		
		
	    frame.getContentPane().setLayout(null);
	    Image tittles = new ImageIcon(this.getClass().getResource("/tuto/tittle.png")).getImage();
	    Image tittle = tittles.getScaledInstance(578, 56, Image.SCALE_SMOOTH);
	      JLabel title = new JLabel("");
	      title.setIcon(new ImageIcon(tittle));
	      title.setBounds(0, 0, 278, 56);
	      frame.getContentPane().add(title);
	      
	    JButton BACKTOMENU = new JButton("");
	    BACKTOMENU.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new homescreen().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image BACKTOMENUS = new ImageIcon(this.getClass().getResource("/settings/back to menu.png")).getImage();
	    BACKTOMENU.setIcon(new ImageIcon(BACKTOMENUS.getScaledInstance(278,160, Image.SCALE_SMOOTH)));
	    BACKTOMENU.setContentAreaFilled(false);
	    BACKTOMENU.setBorderPainted(false);
	    BACKTOMENU.setBounds(31, 250, 278,150);
	    frame.getContentPane().add(BACKTOMENU);
	    
	    JButton MODE = new JButton("");
	    MODE.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new GamesModes().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image MODEs = new ImageIcon(this.getClass().getResource("/tuto/select mode.png")).getImage();
	    MODE.setIcon(new ImageIcon(MODEs.getScaledInstance(278,150, Image.SCALE_SMOOTH)));
	    MODE.setContentAreaFilled(false);
	    MODE.setBorderPainted(false);
	    MODE.setBounds(469, 250, 278,150);
	    frame.getContentPane().add(MODE);
	    
	    JButton TUTO = new JButton("");
	    Image TUTOs = new ImageIcon(this.getClass().getResource("/tuto/tuto.png")).getImage();
	    TUTO.setIcon(new ImageIcon(TUTOs.getScaledInstance(802, 211, Image.SCALE_SMOOTH)));
	    TUTO.setContentAreaFilled(false);
	    TUTO.setBorderPainted(false);
	    TUTO.setBounds(0, 51, 802, 209);
	    frame.getContentPane().add( TUTO);
	    
		  JLabel fondo = new JLabel("ew");
		  fondo.setBounds(0, 0, 802, 411);
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  frame.getContentPane().add(fondo);
	}

}
