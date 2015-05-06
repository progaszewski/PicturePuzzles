package pl.picture.puzzles.fillapix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class FillAPixPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 2268149613323761057L;

	public FillAPixArea fillAPixArea;
	private int marginLeft = 10, marginTop = 10, gridSize = 18;
	private int width, height;

	public FillAPixPanel() {
		addMouseListener(this);
	}

	public void drawArea(FillAPixArea fillAPixArea) {
		this.fillAPixArea = fillAPixArea;

		width = 2 * marginLeft + this.fillAPixArea.x * gridSize
				+ this.fillAPixArea.x;
		height = 2 * marginTop + this.fillAPixArea.y * gridSize
				+ this.fillAPixArea.y;
		setPreferredSize(new Dimension(width, height));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		if (fillAPixArea != null) {
			grid(g2d, fillAPixArea.y + 1, fillAPixArea.x + 1, gridSize);
		}

	}

	// Rysowanie siatki (tabelki)
	private void grid(Graphics2D g2d, int linesX, int linesY, int gridSize) {
		Color digitColor, gridColor;
		int x1, y1, x2, y2;

		g2d.setColor(Color.BLACK);
		// Linie poziome
		for (int i = 0; i < linesX; i++) {
			x1 = marginLeft;
			y1 = y2 = marginTop + gridSize * i + i;
			x2 = marginLeft + (linesY - 1) * gridSize + linesY - 1;
			g2d.drawLine(x1, y1, x2, y2);
		}
		// Linie pionowe
		for (int i = 0; i < linesY; i++) {
			y1 = marginTop;
			x1 = x2 = marginLeft + gridSize * i + i;
			y2 = marginTop + (linesX - 1) * gridSize + linesX - 1;
			g2d.drawLine(x1, y1, x2, y2);

		}
		// Rysowanie cyfr i zamalowywanie kratek
		for (int i = 0; i < linesY - 1; i++) {
			for (int j = 0; j < linesX - 1; j++) {
				if (fillAPixArea.area[j][i].val == FillAPixArea.ABSENCE) {
					digitColor = Color.BLACK;
				} else {
					if (fillAPixArea.area[j][i].val == FillAPixArea.EMPTY) {
						gridColor = Color.LIGHT_GRAY;
					} else {
						gridColor = Color.DARK_GRAY;
					}
					digitColor = Color.WHITE;
					g2d.setColor(gridColor);
					g2d.fillRect(marginLeft + 1 + gridSize * i + i, marginTop
							+ 1 + gridSize * j + j, gridSize, gridSize);
				}

				g2d.setColor(digitColor);
				g2d.setFont(new Font(null, Font.BOLD, 13));
				// Cyfry
				if (fillAPixArea.area[j][i].number != null) {
					g2d.drawString(fillAPixArea.area[j][i].number + "",
							marginLeft + 7 + gridSize * i + i, marginTop + 15
									+ gridSize * j + j);
				}

			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x, y, i, j;

		x = e.getX();
		y = e.getY();

		i = (x - marginLeft) / (gridSize + 1);
		j = (y - marginTop) / (gridSize + 1);

		// System.out.println(x + " " + y);

		if (x - marginLeft > 0 && y - marginTop > 0
				&& i * (gridSize + 1) + marginLeft != x
				&& j * (gridSize + 1) + marginTop != y && i < fillAPixArea.x
				&& j < fillAPixArea.y) {

			if (e.getButton() == MouseEvent.BUTTON3) {
				if (fillAPixArea.area[j][i].val == FillAPixArea.EMPTY) {
					fillAPixArea.area[j][i].val = FillAPixArea.ABSENCE;
				} else {
					fillAPixArea.area[j][i].val = FillAPixArea.EMPTY;
				}

			} else {
				if (fillAPixArea.area[j][i].val == FillAPixArea.SELECTED) {
					fillAPixArea.area[j][i].val = FillAPixArea.ABSENCE;
				} else {
					fillAPixArea.area[j][i].val = FillAPixArea.SELECTED;
				}
			}
			repaint();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
