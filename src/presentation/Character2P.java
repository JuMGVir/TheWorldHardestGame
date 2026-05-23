package presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import domain.Game2P;

import domain.PlayerType;

public class Character2P {

    public JFrame frame;
    private PlayerType p1Type = null;
    private PlayerType p2Type = null;
    private Color p1Border = Color.BLACK;
    private Color p2Border = Color.WHITE;
    private JButton startBtn;
    private JLabel p1Status, p2Status;

    public Character2P() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("The World's Hardest Game - PvP");
        frame.setBounds(100, 100, 950, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png"));
        Image imgEscalada = icon.getImage().getScaledInstance(950, 480, Image.SCALE_SMOOTH);

        frame.getContentPane().setLayout(null);

        JLabel title = new JLabel("SELECT YOUR CHARACTER", JLabel.CENTER);
        title.setFont(new Font("Impact", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 950, 50);
        frame.getContentPane().add(title);

        JLabel p1Title = new JLabel("PLAYER 1", JLabel.CENTER);
        p1Title.setFont(new Font("Impact", Font.BOLD, 28));
        p1Title.setForeground(new Color(180, 24, 27));
        p1Title.setBounds(30, 80, 400, 40);
        frame.getContentPane().add(p1Title);

        JButton p1Blinky = makeCharButton("/character/blinky image.png", 45, 130, 110, 110);
        p1Blinky.addActionListener(e -> { p1Type = PlayerType.BLINKY; chooseBorder(1); updateUI(); });
        frame.getContentPane().add(p1Blinky);

        JLabel p1BlinkyLbl = new JLabel("BLINKY", JLabel.CENTER);
        p1BlinkyLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p1BlinkyLbl.setForeground(Color.WHITE);
        p1BlinkyLbl.setBounds(45, 245, 110, 25);
        frame.getContentPane().add(p1BlinkyLbl);

        JButton p1Inky = makeCharButton("/character/inkyimage.png", 170, 130, 110, 110);
        p1Inky.addActionListener(e -> { p1Type = PlayerType.INKY; chooseBorder(1); updateUI(); });
        frame.getContentPane().add(p1Inky);

        JLabel p1InkyLbl = new JLabel("INKY", JLabel.CENTER);
        p1InkyLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p1InkyLbl.setForeground(Color.WHITE);
        p1InkyLbl.setBounds(170, 245, 110, 25);
        frame.getContentPane().add(p1InkyLbl);

        JButton p1Clyde = makeCharButton("/character/clydeImage.png", 295, 130, 110, 110);
        p1Clyde.addActionListener(e -> { p1Type = PlayerType.CLYDE; chooseBorder(1); updateUI(); });
        frame.getContentPane().add(p1Clyde);

        JLabel p1ClydeLbl = new JLabel("CLYDE", JLabel.CENTER);
        p1ClydeLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p1ClydeLbl.setForeground(Color.WHITE);
        p1ClydeLbl.setBounds(295, 245, 110, 25);
        frame.getContentPane().add(p1ClydeLbl);

        p1Status = new JLabel("", JLabel.CENTER);
        p1Status.setFont(new Font("Impact", Font.BOLD, 20));
        p1Status.setBounds(30, 280, 400, 30);
        frame.getContentPane().add(p1Status);

        JLabel p2Title = new JLabel("PLAYER 2", JLabel.CENTER);
        p2Title.setFont(new Font("Impact", Font.BOLD, 28));
        p2Title.setForeground(new Color(25, 41, 149));
        p2Title.setBounds(520, 80, 400, 40);
        frame.getContentPane().add(p2Title);

        JButton p2Blinky = makeCharButton("/character/blinky image.png", 535, 130, 110, 110);
        p2Blinky.addActionListener(e -> { p2Type = PlayerType.BLINKY; chooseBorder(2); updateUI(); });
        frame.getContentPane().add(p2Blinky);

        JLabel p2BlinkyLbl = new JLabel("BLINKY", JLabel.CENTER);
        p2BlinkyLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p2BlinkyLbl.setForeground(Color.WHITE);
        p2BlinkyLbl.setBounds(535, 245, 110, 25);
        frame.getContentPane().add(p2BlinkyLbl);

        JButton p2Inky = makeCharButton("/character/inkyimage.png", 660, 130, 110, 110);
        p2Inky.addActionListener(e -> { p2Type = PlayerType.INKY; chooseBorder(2); updateUI(); });
        frame.getContentPane().add(p2Inky);

        JLabel p2InkyLbl = new JLabel("INKY", JLabel.CENTER);
        p2InkyLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p2InkyLbl.setForeground(Color.WHITE);
        p2InkyLbl.setBounds(660, 245, 110, 25);
        frame.getContentPane().add(p2InkyLbl);

        JButton p2Clyde = makeCharButton("/character/clydeImage.png", 785, 130, 110, 110);
        p2Clyde.addActionListener(e -> { p2Type = PlayerType.CLYDE; chooseBorder(2); updateUI(); });
        frame.getContentPane().add(p2Clyde);

        JLabel p2ClydeLbl = new JLabel("CLYDE", JLabel.CENTER);
        p2ClydeLbl.setFont(new Font("Impact", Font.BOLD, 18));
        p2ClydeLbl.setForeground(Color.WHITE);
        p2ClydeLbl.setBounds(785, 245, 110, 25);
        frame.getContentPane().add(p2ClydeLbl);

        p2Status = new JLabel("", JLabel.CENTER);
        p2Status.setFont(new Font("Impact", Font.BOLD, 20));
        p2Status.setBounds(520, 280, 400, 30);
        frame.getContentPane().add(p2Status);

        startBtn = new JButton("START");
        startBtn.setFont(new Font("Impact", Font.BOLD, 40));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBackground(new Color(0, 120, 0));
        startBtn.setBounds(325, 350, 300, 60);
        startBtn.setFocusPainted(false);
        startBtn.setVisible(false);
		startBtn.addActionListener(e -> {
			int level = showLevelSelection();
			if (level < 0) return;
			frame.dispose();
			Game2P game = new Game2P(p1Type, p2Type);
			game.setStartingLevel(level);
			game.getPlayer1().setBorderColor(
				p1Border.getRed() + "," + p1Border.getGreen() + "," + p1Border.getBlue());
			game.getPlayer2().setBorderColor(
				p2Border.getRed() + "," + p2Border.getGreen() + "," + p2Border.getBlue());
			new GAME_2P(game);
		});
        frame.getContentPane().add(startBtn);

        JLabel fondo = new JLabel();
        fondo.setBounds(0, 0, 950, 480);
        fondo.setIcon(new ImageIcon(imgEscalada));
        frame.getContentPane().add(fondo);

        frame.setVisible(true);
    }

    // Abre un selector de color (JColorChooser) para el borde del jugador indicado
    // Muestra un dialogo modal y guarda el color elegido en p1Border o p2Border
    // Si el usuario cancela, conserva el color anterior
    private void chooseBorder(int player) {
        Color initial = (player == 1) ? p1Border : p2Border; // Color actual del jugador
        Color chosen = JColorChooser.showDialog(frame,
            "Player " + player + " - Elige el color del borde", initial); // Dialogo de paleta de colores
        if (chosen != null) { // Si el usuario eligio un color (no cancelo)
            if (player == 1) p1Border = chosen;
            else p2Border = chosen;
        }
    }

    private JButton makeCharButton(String path, int x, int y, int w, int h) {
        JButton btn = new JButton();
        Image img = new ImageIcon(getClass().getResource(path)).getImage();
        btn.setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
        btn.setBounds(x, y, w, h);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    // Actualiza las etiquetas SELECTED con el color de borde elegido por cada jugador
    // Muestra el boton START solo cuando ambos jugadores han seleccionado personaje
    private void updateUI() {
        p1Status.setText(p1Type != null ? "SELECTED" : ""); // Muestra SELECTED si P1 ya eligio
        p1Status.setForeground(p1Border); // Color del texto = color de borde elegido
        p2Status.setText(p2Type != null ? "SELECTED" : ""); // Muestra SELECTED si P2 ya eligio
        p2Status.setForeground(p2Border); // Color del texto = color de borde elegido
		startBtn.setVisible(p1Type != null && p2Type != null); // START visible solo si ambos eligieron
	}

	private int showLevelSelection() {
		SelectLevel sl = new SelectLevel(frame);
		return sl.showDialog();
	}
}
