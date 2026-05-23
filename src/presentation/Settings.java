package presentation;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * Settings window for adjusting music volume, SFX volume, and navigating
 * to the key-binding configuration screen.
 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
 * @version 1.0
 */
public class Settings {

	JFrame frame;

	/**
	 * Launches the settings window.
	 * @param args command-line arguments (unused)
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings window = new Settings();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructs the Settings window.
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	public Settings() {
		initialize();
	}

	/**
	 * Initializes the contents of the settings frame, including background,
	 * title, sliders for music/SFX, and navigation buttons.
	 * @author ESCUELA COLOMBIANA DE INGENIERIA JULIO GARAVITO
	 * @version 1.0
	 */
	void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game"); //
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png")); 
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);
		
	    JButton BACKTOMENU = new JButton("");
	    BACKTOMENU.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new homescreen().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image BACKTOMENUS = new ImageIcon(this.getClass().getResource("/settings/back to menu.png")).getImage();
	    BACKTOMENU.setIcon(new ImageIcon(BACKTOMENUS.getScaledInstance(278,150, Image.SCALE_SMOOTH)));
	    BACKTOMENU.setContentAreaFilled(false);
	    BACKTOMENU.setBorderPainted(false);
	    BACKTOMENU.setBounds(31, 250, 278,150);
	    frame.getContentPane().add(BACKTOMENU);
	    
	    JButton BINDS = new JButton("");
	    BINDS.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new KeyBinds().frame.setVisible(true);
	    		frame.dispose();
	    	}
	    });
	    Image BINDSs = new ImageIcon(this.getClass().getResource("/settings/set key binds.png")).getImage();
	    BINDS.setIcon(new ImageIcon(BINDSs.getScaledInstance(278,150, Image.SCALE_SMOOTH)));
	    BINDS.setContentAreaFilled(false);
	    BINDS.setBorderPainted(false);
	    BINDS.setBounds(469, 250, 278,150);
	    frame.getContentPane().add(BINDS);
	    
	    frame.getContentPane().setLayout(null);
	    Image tittles = new ImageIcon(this.getClass().getResource("/settings/tittle.png")).getImage();
	    Image tittle = tittles.getScaledInstance(800, 100, Image.SCALE_SMOOTH);
	      JLabel title = new JLabel("");
	      title.setIcon(new ImageIcon(tittle));
	      title.setBounds(0, 0, 850, 100);
	      frame.getContentPane().add(title);
	      
	      JLabel MUSIC = new JLabel("MUSIC");
	      MUSIC.setForeground(new Color(58, 58, 58));
	      MUSIC.setFont(new Font("Impact", Font.BOLD, 50));
	      MUSIC.setBounds(10, 89, 168, 61);
	      frame.getContentPane().add(MUSIC);
	      
	      JLabel SFC = new JLabel("SFX");
	      SFC.setForeground(new Color(58, 58, 58));
	      SFC.setFont(new Font("Impact", Font.BOLD, 50));
	      SFC.setBounds(10, 176, 168, 61);
	      frame.getContentPane().add(SFC);
	      
	      JSlider MUSIC_slider = new JSlider(0, 100, (int)(SoundEffect.getMusicVolume() * 100));
	      MUSIC_slider.setForeground(Color.RED);
	      MUSIC_slider.setPaintTicks(true);
	      MUSIC_slider.setBackground(Color.GRAY);
	      MUSIC_slider.setBounds(162, 109, 559, 26);
	      MUSIC_slider.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              SoundEffect.setMusicVolume(MUSIC_slider.getValue() / 100f);
	          }
	      });
	      frame.getContentPane().add(MUSIC_slider);
	      
	      JSlider SFX_slider = new JSlider(0, 100, (int)(SoundEffect.getSFXVolume() * 100));
	      SFX_slider.setPaintTicks(true);
	      SFX_slider.setBackground(Color.GRAY);
	      SFX_slider.setBounds(162, 192, 559, 26);
	      SFX_slider.addChangeListener(new ChangeListener() {
	          public void stateChanged(ChangeEvent e) {
	              SoundEffect.setSFXVolume(SFX_slider.getValue() / 100f);
	          }
	      });
	      frame.getContentPane().add(SFX_slider);
	      
		  JLabel fondo = new JLabel("ew");
		  fondo.setBounds(0, 0, 802, 411);
		  fondo.setIcon(new ImageIcon( imgEscalada ));
		  frame.getContentPane().add(fondo);
	}
}
