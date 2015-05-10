package pl.picture.puzzles.picapix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;

import pl.picture.puzzles.fillapix.FillAPixArea;

public class PicAPixPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -5652831501965895968L;

	private int gridSize = 16;
	private int fontSize = 11;

	public PicAPixArea picAPixArea;
	private int width, height;
	private int a, b; // Szerokosc numerow poziomych, wysokosc numerow pionowych

	public PicAPixPanel() {
		addMouseListener(this);
	}

	public void drawArea(final PicAPixArea paArea) {
		this.picAPixArea = paArea;
		// Ilosc grubych linii pionowych
		int boldVerticaLines = (this.picAPixArea.x / 5) + 1;

		// Ilosc grubych linii poziomych
		int boldHorizontaLines = (this.picAPixArea.y / 5) + 1;

		// Szerokosc numerów poziomych
		this.a = this.picAPixArea.maxHorizontalNumbers * (this.gridSize + 1);

		// Wysokosc numerów pionowych
		this.b = this.picAPixArea.maxVerticalNumbers * (this.gridSize + 1);

		this.width = this.a + (this.picAPixArea.x * (this.gridSize + 1))
				+ boldVerticaLines + 1;
		this.height = this.b + (this.picAPixArea.y * (this.gridSize + 1))
				+ boldHorizontaLines + 1;

		setPreferredSize(new Dimension(this.width, this.height));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.width, this.height);

		if (this.picAPixArea != null) {
			drawPuzzle(g2d, this.picAPixArea.x, this.picAPixArea.y);
		}

	}

	// Rysowanie lamiglowki Pic a Pic - numery pion i pozi oraz siatka
	private void drawPuzzle(Graphics2D g2d, int linesX, int linesY) {
		Color gridColor, numColor;
		int x1, x2, y1, y2;

		int boldLine = 5;
		int countBoldLines = 0;

		g2d.setColor(Color.BLACK);
		// Linie poziome
		for (int i = 0; i < linesY + 1; i++) {
			if (boldLine++ == 5) {
				boldLine -= 5;

				g2d.fillRect(0, this.b + (i * this.gridSize + i)
						+ countBoldLines++, this.width, 2);

			} else {
				x1 = 0;
				x2 = this.width - 1;
				y1 = y2 = this.b + (i * this.gridSize + i) + countBoldLines;
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
		boldLine = 5;
		countBoldLines = 0;
		// Linie pionowe
		for (int i = 0; i < linesX + 1; i++) {
			if (boldLine++ == 5) {
				boldLine -= 5;

				g2d.fillRect(this.a + (i * this.gridSize + i)
						+ countBoldLines++, 0, 2, this.height);

			} else {
				y1 = 0;
				y2 = this.height - 1;
				x1 = x2 = this.a + (i * this.gridSize + i) + countBoldLines;
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
		boldLine = 5;
		countBoldLines = 0;
		int posX = 0;
		// Numery pionowe
		for (PicAPixArea.ListOfNumber listOfNumber : this.picAPixArea.verticalListsOfNumbers) {
			if (boldLine++ == 5) {
				boldLine -= 5;
				countBoldLines++;
			}
			int posY = this.picAPixArea.maxVerticalNumbers
					- listOfNumber.numbers.size();
			for (PicAPixArea.PaNumber number : listOfNumber.numbers) {
				if (number.enable) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.LIGHT_GRAY);
				}

				// System.out.println(posY * this.gridSize + posY + 1);

				int moveDigitX = 0;

				if (Byte.toString(number.val).length() == 1)
					moveDigitX = 5;
				else
					moveDigitX = 2;

				int moveDigitY = -4;

				g2d.setFont(new Font(null, Font.BOLD, this.fontSize));
				g2d.drawString(number.val + "", this.a
						+ (posX * this.gridSize + posX) + countBoldLines + 1
						+ moveDigitX, (posY + 1) * (this.gridSize + 1)
						+ moveDigitY);

				posY++;
			}
			posX++;
		}
		boldLine = 5;
		countBoldLines = 0;
		int posY = 0;
		// Numery poziome
		for (PicAPixArea.ListOfNumber listOfNumber : this.picAPixArea.horizontalListsOfNumbers) {
			if (boldLine++ == 5) {
				boldLine -= 5;
				countBoldLines++;
			}
			posX = this.picAPixArea.maxHorizontalNumbers
					- listOfNumber.numbers.size();
			for (PicAPixArea.PaNumber number : listOfNumber.numbers) {
				if (number.enable) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.LIGHT_GRAY);
				}

				int moveDigitX = 0;

				if (Byte.toString(number.val).length() == 1)
					moveDigitX = 5;
				else
					moveDigitX = 2;

				int moveDigitY = -4;

				g2d.setFont(new Font(null, Font.BOLD, this.fontSize));
				g2d.drawString(number.val + "", posX * this.gridSize + posX + 1
						+ moveDigitX, this.b
						+ ((posY + 1) * (this.gridSize + 1)) + countBoldLines
						+ moveDigitY);

				posX++;
			}
			posY++;
		}

		int boldLineY = 5;
		int countBoldLinesY = 0;

		// Malowanie kratek
		for (int i = 0; i < this.picAPixArea.y; i++) {
			if (boldLineY++ == 5) {
				boldLineY -= 5;
				countBoldLinesY++;
			}

			int boldLineX = 5;
			int countBoldLinesX = 0;
			for (int j = 0; j < this.picAPixArea.x; j++) {
				if (boldLineX++ == 5) {
					boldLineX -= 5;
					countBoldLinesX++;
				}

				if (this.picAPixArea.area[i][j].val != PicAPixArea.ABSENCE) {
					if (this.picAPixArea.area[i][j].val == PicAPixArea.SELECTED) {
						g2d.setColor(Color.BLACK);
					} else {
						g2d.setColor(Color.GRAY);
					}

					g2d.fillRect(this.a + countBoldLinesX
							+ (j * (this.gridSize + 1)) + 1, this.b
							+ countBoldLinesY + (i * (this.gridSize + 1)) + 1,
							this.gridSize, this.gridSize);
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// Klikniecie w pusty prostokat
		if (x - this.a < 0 && y - this.b < 0)
			return;

		// Klikniecie w kratke
		if (x - this.a > 0 && y - this.b > 0) {
			int i = (5 * (y - this.b)) / (5 * this.gridSize + 6);
			int j = (5 * (x - this.a)) / (5 * this.gridSize + 6);

			int il = i / 5 + 1;
			int jl = j / 5 + 1;

			if (i * (this.gridSize + 1) + this.b + il != y
					&& i < this.picAPixArea.y
					&& j * (this.gridSize + 1) + this.a + jl != x
					&& j < this.picAPixArea.x) {

				if (e.getButton() == MouseEvent.BUTTON3) {
					if (picAPixArea.area[i][j].val == PicAPixArea.EMPTY) {
						picAPixArea.area[i][j].val = PicAPixArea.ABSENCE;
					} else {
						picAPixArea.area[i][j].val = PicAPixArea.EMPTY;
					}

				} else {
					if (picAPixArea.area[i][j].val == PicAPixArea.SELECTED) {
						picAPixArea.area[i][j].val = PicAPixArea.ABSENCE;
					} else {
						picAPixArea.area[i][j].val = FillAPixArea.SELECTED;
					}
				}
				repaint();
				return;
			}
		}

		// Klikniecie na liczbe pionowa
		if (x - this.a > 0 && y - this.b < 0) {
			int i = y / (this.gridSize + 1);
			int j = (5 * (x - this.a) / (5 * this.gridSize + 6));

			int jl = (j / 5) + 1;

			if (i * (this.gridSize + 1) != y
					&& j * (this.gridSize + 1) + jl + this.a != x) {
				List<PicAPixArea.PaNumber> numbers = this.picAPixArea.verticalListsOfNumbers
						.get(j).numbers;
				int countVerticalNumbers = numbers.size();

				int iPrim = i - this.picAPixArea.maxVerticalNumbers
						+ countVerticalNumbers;
				if (iPrim >= 0) {
					PicAPixArea.PaNumber number = numbers.get(iPrim);
					number.enable = !number.enable;

					repaint();
					return;
				}
			}
		}

		// Klikniecie na liczbe pozioma
		if (x - this.a < 0 && y - this.b > 0) {
			int i = (5 * (y - this.b) / (5 * this.gridSize + 6));
			int j = x / (this.gridSize + 1);

			int il = (i / 5) + 1;

			if (j * (this.gridSize + 1) != x
					&& i * (this.gridSize + 1) + il + this.b != y) {
				List<PicAPixArea.PaNumber> numbers = this.picAPixArea.horizontalListsOfNumbers
						.get(i).numbers;
				int countHorizontalNumbers = numbers.size();

				int jPrim = j - this.picAPixArea.maxHorizontalNumbers
						+ countHorizontalNumbers;
				if (jPrim >= 0) {
					PicAPixArea.PaNumber number = numbers.get(jPrim);
					number.enable = !number.enable;

					repaint();
					return;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
