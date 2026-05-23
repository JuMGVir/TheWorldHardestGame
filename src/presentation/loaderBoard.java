package presentation;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.GuardadoBinario;
import domain.Game;
import domain.Game2P;
import domain.Memento;
import domain.MementoDAO;

/**
 * GUI window for loading previously saved game boards from files.
 * Displays up to three quick-load buttons for recent saves and an "Open other" 
 * file chooser for loading .txt or .dat files.
 * @author Juan José
 * @version 1.0
 */
public class loaderBoard {

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
					loaderBoard window = new loaderBoard();
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
	public loaderBoard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @author Juan José
	 * @version 1.0
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("The World's Hardest Game");
	    frame.setBounds(100, 100, 818, 450);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png"));
	    Image imgEscalada = icon.getImage().getScaledInstance(818, 460, Image.SCALE_SMOOTH);

	    frame.getContentPane().setLayout(null);
	    Image tittles = new ImageIcon(this.getClass().getResource("/loader board/tittle.png")).getImage();
	    Image tittle = tittles.getScaledInstance(800, 100, Image.SCALE_SMOOTH);
	    JLabel title = new JLabel("");
	    title.setIcon(new ImageIcon(tittle));
	    title.setBounds(0, 0, 850, 100);
	    frame.getContentPane().add(title);

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

	    File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
	    File[] allFiles = savesDir.isDirectory() ? savesDir.listFiles((d, n) ->
	        n.toLowerCase().endsWith(".txt") || n.toLowerCase().endsWith(".dat")) : null;
	    if (allFiles != null) Arrays.sort(allFiles);

	    int[] btnX = {45, 279, 513};
	    Color[][] btnGradients = {
	        {new Color(25, 41, 149), new Color(6, 12, 59)},
	        {new Color(180, 24, 27), new Color(67, 8, 9)},
	        {new Color(40, 127, 47), new Color(11, 46, 15)}
	    };
	    for (int i = 0; i < 3; i++) {
	        String label = "VACIO";
	        File file = null;
	        if (allFiles != null && i < allFiles.length) {
	            file = allFiles[i];
	            String name = file.getName();
	            int dot = name.lastIndexOf('.');
	            if (dot > 0) name = name.substring(0, dot);
	            label = name.length() > 9 ? name.substring(0, 9) + ".." : name;
	        }
	        addLoadButton(label, file, btnX[i], 108, btnGradients[i][0], btnGradients[i][1]);
	    }

	    JButton OTHERS = new JButton("");
	    OTHERS.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    	    File savesDir = new File(System.getProperty("user.dir") + File.separator + "saves");
	    	    JFileChooser selector = new JFileChooser();
	    	    selector.setCurrentDirectory(savesDir);
	    	    FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos compatibles (*.txt, *.dat)", "txt", "dat");
	    	    selector.setFileFilter(filtro);
	    	    int resultado = selector.showOpenDialog(frame);
	    	    if (resultado == JFileChooser.APPROVE_OPTION) {
	    	        File archivo = selector.getSelectedFile();
	    	        try {
	    	            String name = archivo.getName().toLowerCase();
	    	            Memento data;
	    	            if (name.endsWith(".txt")) {
	    	                data = new MementoDAO().loadGame(archivo);
	    	            } else {
	    	                data = new GuardadoBinario().loadGame(archivo);
	    	            }
	    	            if (data.isPvP()) {
	    	                Game2P game2p = new Game2P();
	    	                game2p.fromMemento(data);
	    	                new GAME_2P(game2p);
	    	            } else {
	    	                Game game = new Game();
	    	                game.fromMemento(data);
	    	                new GAME_1P(game);
	    	            }
	    	            frame.dispose();
	    	        } catch (Exception ex) {
	    	            System.out.println("Error al cargar: " + ex.getMessage());
	    	        }
	    	    }
	    	}
	    });
	    Image OTHER = new ImageIcon(this.getClass().getResource("/loader board/open others.png")).getImage();
	    OTHERS.setIcon(new ImageIcon(OTHER.getScaledInstance(278,150, Image.SCALE_SMOOTH)));
	    OTHERS.setContentAreaFilled(false);
	    OTHERS.setBorderPainted(false);
	    OTHERS.setBounds(469, 250, 278,150);
	    frame.getContentPane().add(OTHERS);

	    JLabel fondo = new JLabel("");
	    fondo.setBounds(0, 0, 802, 411);
	    fondo.setIcon(new ImageIcon(imgEscalada));
	    frame.getContentPane().add(fondo);
	}

	/**
	 * Adds a load-game button with a gradient background to the frame.
	 * @param text  the label text displayed on the button
	 * @param file  the file to load when clicked, or null to disable the button
	 * @param x     the x-coordinate of the button
	 * @param y     the y-coordinate of the button
	 * @param top   the top color of the gradient
	 * @param bottom the bottom color of the gradient
	 * @author Juan José
	 * @version 1.0
	 */
	private void addLoadButton(String text, File file, int x, int y, Color top, Color bottom) {
	    JButton btn = new JButton(text) {
	        @Override
	        protected void paintComponent(Graphics g) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
	            g2.fillRect(0, 0, getWidth(), getHeight());
	            g2.dispose();
	            super.paintComponent(g);
	        }
	    };
	    btn.setForeground(Color.WHITE);
	    btn.setFont(new Font("Impact", Font.BOLD, 32));
	    btn.setContentAreaFilled(false);
	    btn.setFocusPainted(false);
	    btn.setBounds(x, y, 220, 100);
	    btn.setEnabled(file != null);
	    btn.addActionListener(e -> {
	        if (file == null) return;
	        try {
	            String fn = file.getName().toLowerCase();
	            Memento data;
	            if (fn.endsWith(".dat")) {
	                data = new GuardadoBinario().loadGame(file);
	            } else {
	                data = new MementoDAO().loadGame(file);
	            }
	            if (data.isPvP()) {
	                Game2P game2p = new Game2P();
	                game2p.fromMemento(data);
	                new GAME_2P(game2p);
	            } else {
	                Game game = new Game();
	                game.fromMemento(data);
	                new GAME_1P(game);
	            }
	            frame.dispose();
	        } catch (Exception ex) {
	            System.out.println("Error al cargar: " + ex.getMessage());
	        }
	    });
	    frame.getContentPane().add(btn);
	}
}
