package presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SelectLevel {

	private JDialog dialog;
	private int selectedLevel = -1;

	private static final int FRAME_W = 818;
	private static final int BTN_W = 175;
	private static final int BTN_H = 90;
	private static final int GAP = 25;

	// Crea el diálogo modal con 4 botones de nivel + botón BACK
	public SelectLevel(JFrame parent) {
		dialog = new JDialog(parent, "Seleccionar Nivel", true);
		dialog.setBounds(100, 100, FRAME_W, 450);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(null);

		ImageIcon icon = new ImageIcon(getClass().getResource("/FONDO.png"));
		Image imgEscalada = icon.getImage().getScaledInstance(FRAME_W, 460, Image.SCALE_SMOOTH);

		Image tittles = new ImageIcon(getClass().getResource("/Select Level/tittle.png")).getImage();
		Image tittle = tittles.getScaledInstance(700, 83, Image.SCALE_SMOOTH);
		JLabel title = new JLabel("");
		title.setIcon(new ImageIcon(tittle));
		title.setBounds((FRAME_W - 700) / 2, 15, 700, 83);
		dialog.getContentPane().add(title);

		int totalW = BTN_W * 4 + GAP * 3;
		int startX = (FRAME_W - totalW) / 2;
		String[] labels = {"NIVEL 1", "NIVEL 2", "NIVEL 3", "NIVEL 4"};
		Color[][] gradients = {
			{new Color(25, 41, 149), new Color(6, 12, 59)},
			{new Color(180, 24, 27), new Color(67, 8, 9)},
			{new Color(40, 127, 47), new Color(11, 46, 15)},
			{new Color(180, 100, 20), new Color(80, 40, 5)}
		};
		for (int i = 0; i < 4; i++) {
			int x = startX + i * (BTN_W + GAP);
			addLevelButton(labels[i], i + 1, x, 125, gradients[i][0], gradients[i][1]);
		}

		addBackButton(260);

		JLabel fondo = new JLabel("");
		fondo.setBounds(0, 0, 802, 411);
		fondo.setIcon(new ImageIcon(imgEscalada));
		dialog.getContentPane().add(fondo);

		dialog.setLocationRelativeTo(parent);
	}

	// Muestra el diálogo y devuelve el nivel seleccionado (1-4) o -1 si cancela
	public int showDialog() {
		dialog.setVisible(true);
		return selectedLevel;
	}

	private void addLevelButton(String text, int level, int x, int y, Color top, Color bottom) {
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
		btn.setFont(new Font("Impact", Font.BOLD, 24));
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setBounds(x, y, BTN_W, BTN_H);
		btn.addActionListener(e -> {
			selectedLevel = level;
			dialog.dispose();
		});
		dialog.getContentPane().add(btn);
	}

	private void addBackButton(int y) {
		JButton btn = new JButton("BACK") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setPaint(new GradientPaint(0, 0, new Color(80, 80, 80), 0, getHeight(), new Color(30, 30, 30)));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Impact", Font.BOLD, 28));
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setBounds((FRAME_W - BTN_W) / 2, y, BTN_W, BTN_H);
		btn.addActionListener(e -> dialog.dispose());
		dialog.getContentPane().add(btn);
	}
}
