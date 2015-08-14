package pl.fillapix.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class EditorPanel extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1777049554609162436L;

	public LinkAPixArea lap;
	private int marginLeft = 10, marginTop = 10, gridSize = 18;
	private int width, height;
	private int lapX, lapY;

	private int selectedX, selectedY;
	private boolean isSelected = false;

	public EditorPanel() {

		this.lap = new LinkAPixArea();
		addMouseListener(this);
		addKeyListener(this);

	}

	public void setSizeArea(int x, int y) {
		this.lap.setArea(x, y);

		this.lapX = x;
		this.lapY = y;

		width = 2 * marginLeft + this.lapX * gridSize + this.lapX;
		height = 2 * marginTop + this.lapY * gridSize + this.lapY;
		setPreferredSize(new Dimension(width, height));
		repaint();
		requestFocus();
	}

	public void loadAreaFromFile(int x, int y) {

		this.lapX = x;
		this.lapY = y;

		width = 2 * marginLeft + this.lapX * gridSize + this.lapX;
		height = 2 * marginTop + this.lapY * gridSize + this.lapY;
		setPreferredSize(new Dimension(width, height));
		repaint();
		requestFocus();
	}

	// Rysowanie siatki (tabelki)
	private void grid(Graphics2D g2d, int horizontalLines, int verticalLines,
			int gridSize) {
		Color digitColor, gridColor;
		int x1, y1, x2, y2;

		g2d.setColor(Color.BLACK);
		// Linie poziome
		for (int i = 0; i < horizontalLines; i++) {
			x1 = marginLeft;
			y1 = y2 = marginTop + gridSize * i + i;
			x2 = marginLeft + (verticalLines - 1) * gridSize + verticalLines
					- 1;
			g2d.drawLine(x1, y1, x2, y2);
		}
		// Linie pionowe
		for (int i = 0; i < verticalLines; i++) {
			y1 = marginTop;
			x1 = x2 = marginLeft + gridSize * i + i;
			y2 = marginTop + (horizontalLines - 1) * gridSize + horizontalLines
					- 1;
			g2d.drawLine(x1, y1, x2, y2);

		}
		// Rysowanie cyfr i zamalowywanie kratek
		for (int i = 0; i < horizontalLines - 1; i++) {
			for (int j = 0; j < verticalLines - 1; j++) {
				if (isSelected && i == this.selectedY && j == this.selectedX) { // pole
																				// zaznaczone
					gridColor = Color.BLACK;

					digitColor = Color.WHITE;
					g2d.setColor(gridColor);
					g2d.fillRect(marginLeft + 1 + gridSize * j + j, marginTop
							+ 1 + gridSize * i + i, gridSize, gridSize);
				} else {
					digitColor = Color.BLACK;
				}

				g2d.setColor(digitColor);
				g2d.setFont(new Font(null, Font.BOLD, 13));
				// Cyfry
				if (lap.area[i][j] != -1) {

					int moveDigitRight = 7;
					if (lap.area[i][j] > 9) {
						moveDigitRight = 4;
					}
					g2d.drawString(lap.area[i][j] + "", marginLeft
							+ moveDigitRight + gridSize * j + j, marginTop + 15
							+ gridSize * i + i);
				}

			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		if (this.lap != null) {
			grid(g2d, this.lapY + 1, this.lapX + 1, this.gridSize);
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (this.isSelected
				&& ((e.getKeyCode() >= 96 && e.getKeyCode() <= 105) || (e
						.getKeyCode() >= 48 && e.getKeyCode() <= 57))) {

			if (lap.area[this.selectedY][this.selectedX] == -1) {
				lap.area[this.selectedY][this.selectedX] = Byte.parseByte(e
						.getKeyChar() + "");
			} else {
				String number = String
						.valueOf(lap.area[this.selectedY][this.selectedX]);

				if (number.length() == 2) {
					lap.area[this.selectedY][this.selectedX] = Byte.parseByte(e
							.getKeyChar() + "");
				} else if (number.length() == 1) {
					number = number + e.getKeyChar();
					lap.area[this.selectedY][this.selectedX] = Byte
							.parseByte(number);
				}
			}
			repaint();
			return;
		}

		if (this.isSelected && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (lap.area[this.selectedY][this.selectedX] == -1) {
				return;
			}

			String number = String
					.valueOf(lap.area[this.selectedY][this.selectedX]);

			if (number.length() == 1) {
				lap.area[this.selectedY][this.selectedX] = -1;
			} else if (number.length() == 2) {
				number = number.charAt(0) + "";

				lap.area[this.selectedY][this.selectedX] = Byte
						.parseByte(number);
			}

			repaint();
			return;
		}
		if (!this.isSelected
				&& (e.getKeyCode() == KeyEvent.VK_LEFT
						|| e.getKeyCode() == KeyEvent.VK_RIGHT
						|| e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) {

			this.selectedX = 0;
			this.selectedY = 0;
			this.isSelected = true;
			repaint();
			return;
		}
		if (this.isSelected && e.getKeyCode() == KeyEvent.VK_LEFT
				&& this.selectedX != 0) {
			selectedX--;
			repaint();
			return;
		}
		if (this.isSelected && e.getKeyCode() == KeyEvent.VK_RIGHT
				&& this.selectedX != this.lapX - 1) {
			selectedX++;
			repaint();
			return;
		}
		if (this.isSelected && e.getKeyCode() == KeyEvent.VK_UP
				&& this.selectedY != 0) {
			selectedY--;
			repaint();
			return;
		}
		if (this.isSelected && e.getKeyCode() == KeyEvent.VK_DOWN
				&& this.selectedY != this.lapY - 1) {
			selectedY++;
			repaint();
			return;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x, y, i, j;

		x = e.getX();
		y = e.getY();

		j = (x - marginLeft) / (gridSize + 1);
		i = (y - marginTop) / (gridSize + 1);

		if (x - marginLeft > 0 && y - marginTop > 0
				&& i * (gridSize + 1) + marginLeft != y
				&& j * (gridSize + 1) + marginTop != x && i < this.lapY
				&& j < this.lapX) {

			if (e.getButton() == MouseEvent.BUTTON3) {
				lap.area[i][j] = -1;

			} else {
				if (this.isSelected && this.selectedX == j
						&& this.selectedY == i) {
					this.isSelected = false;
				} else {
					this.selectedX = j;
					this.selectedY = i;
					this.isSelected = true;
				}
			}

			repaint();
			requestFocus();
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
