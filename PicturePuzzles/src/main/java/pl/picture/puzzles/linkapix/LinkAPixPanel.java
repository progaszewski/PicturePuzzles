package pl.picture.puzzles.linkapix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import pl.picture.puzzles.linkapix.LinkAPixArea.LaNumber;

public class LinkAPixPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -8241717268623281581L;

	public LinkAPixArea linkAPixArea;
	private int marginLeft = 10, marginTop = 10, gridSize = 15;
	private int width, height;

	// private boolean isSelectNumber = false;
	public LinkAPixArea.LaNumber selectedNumber = null;
	public LinkAPixArea.Field lastPosition = null;

	// private int sizePath = 0;

	public LinkAPixPanel() {
		addMouseListener(this);
	}

	public void drawArea(LinkAPixArea linkAPixArea) {
		this.linkAPixArea = linkAPixArea;

		width = 2 * marginLeft + this.linkAPixArea.x * gridSize
				+ this.linkAPixArea.x;
		height = 2 * marginTop + this.linkAPixArea.y * gridSize
				+ this.linkAPixArea.y;
		setPreferredSize(new Dimension(width, height));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		if (linkAPixArea != null) {
			grid(g2d, linkAPixArea.y + 1, linkAPixArea.x + 1, gridSize);
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
		for (int i = 0; i < linesX - 1; i++) {
			for (int j = 0; j < linesY - 1; j++) {
				if (linkAPixArea.area[i][j].val == LinkAPixArea.ABSENCE) {
					digitColor = Color.BLACK;
				} else {

					int width = gridSize;
					int height = gridSize;
					int marginLeftPrim = marginLeft;
					int marginTopPrim = marginTop;

					if ((linkAPixArea.area[i][j].belongToNumber == selectedNumber || (selectedNumber != null && (linkAPixArea.area[i][j].belongToNumber == selectedNumber.secondNumbers || (selectedNumber.secondNumbers != null && linkAPixArea.area[i][j].belongToNumber == selectedNumber.secondNumbers
							.get(0)))))
							&& (linkAPixArea.area[i][j].number == null || linkAPixArea.area[i][j].number.value != 1)) {
						gridColor = Color.RED;

						if (linkAPixArea.area[i][j].next != null) {
							LinkAPixArea.Field next = linkAPixArea.area[i][j].next;

							if (i == next.i - 1 && j == next.j) {
								height++;
							} else if (i == next.i + 1 && j == next.j) {
								height++;
								marginTopPrim--;
							} else if (j == next.j - 1 && i == next.i) {
								width++;

							} else if (j == next.j + 1 && i == next.i) {
								width++;
								marginLeftPrim--;
							}
						}

					} else {
						if (j - 1 >= 0
								&& linkAPixArea.area[i][j - 1].val == LinkAPixArea.SELECTED
								&& linkAPixArea.area[i][j - 1] != linkAPixArea.area[i][j].next
								&& linkAPixArea.area[i][j - 1] != linkAPixArea.area[i][j].prev) {

							int x = marginLeftPrim + gridSize * j + j;
							int y = marginTopPrim + gridSize * i + i;

							g2d.setColor(Color.WHITE);
							g2d.drawLine(x, y, x, y + gridSize + 1);
						}

						if (i - 1 >= 0
								&& linkAPixArea.area[i - 1][j].val == LinkAPixArea.SELECTED
								&& linkAPixArea.area[i - 1][j] != linkAPixArea.area[i][j].next
								&& linkAPixArea.area[i - 1][j] != linkAPixArea.area[i][j].prev) {

							int x = marginLeftPrim + gridSize * j + j;
							int y = marginTopPrim + gridSize * i + i;

							g2d.setColor(Color.WHITE);
							g2d.drawLine(x, y, x + gridSize + 1, y);
						}

						gridColor = Color.BLACK;
					}

					if (linkAPixArea.area[i][j].number == selectedNumber) {
						digitColor = Color.BLACK;
					} else {
						digitColor = Color.WHITE;
					}
					g2d.setColor(gridColor);
					g2d.fillRect(marginLeftPrim + 1 + gridSize * j + j,
							marginTopPrim + 1 + gridSize * i + i, width, height);
				}

				g2d.setColor(digitColor);
				g2d.setFont(new Font(null, Font.BOLD, 11));
				// Cyfry
				if (linkAPixArea.area[i][j].number != null) {
					int moveDigitRigth = 6;
					if (linkAPixArea.area[i][j].number.value > 9) {
						moveDigitRigth = 3;
					}

					g2d.drawString(linkAPixArea.area[i][j].number.value + "",
							marginLeft + moveDigitRigth + gridSize * j + j,
							marginTop + 13 + gridSize * i + i);
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
		int x, y, j, i;

		x = e.getX();
		y = e.getY();

		j = (x - marginLeft) / (gridSize + 1);
		i = (y - marginTop) / (gridSize + 1);

		// System.out.println(x + " " + y);

		if (x - marginLeft > 0 && y - marginTop > 0
				&& j * (gridSize + 1) + marginLeft != x
				&& i * (gridSize + 1) + marginTop != y && j < linkAPixArea.x
				&& i < linkAPixArea.y) {

			// if (linkAPixArea.area[j][i].val == LinkAPixArea.SELECTED) {
			// linkAPixArea.area[j][i].val = LinkAPixArea.ABSENCE;
			// } else {
			// linkAPixArea.area[j][i].val = LinkAPixArea.SELECTED;
			// }

			if (e.getButton() == MouseEvent.BUTTON3) {
				if (selectedNumber == null
						&& linkAPixArea.area[i][j].number != null) {
					selectedNumber = linkAPixArea.area[i][j].number;

					if (selectedNumber.value == 1) {

						selectedNumber = null;
						return;
					}

					linkAPixArea.area[i][j].setBelongsToNumber(selectedNumber);
					lastPosition = linkAPixArea.area[i][j];
					repaint();
					return;
				}

				if (selectedNumber != null
						&& linkAPixArea.area[i][j].number == selectedNumber) {

					if (selectedNumber.secondNumbers == null) {
						selectedNumber.unselect();
					}
					selectedNumber = null;
					lastPosition = null;
					repaint();
					return;
				}
			} else {
				if (selectedNumber == null
						&& linkAPixArea.area[i][j].number != null
						&& linkAPixArea.area[i][j].number.value == 1) {
					if (linkAPixArea.area[i][j].val == LinkAPixArea.SELECTED) {
						linkAPixArea.area[i][j].val = LinkAPixArea.ABSENCE;
					} else {
						linkAPixArea.area[i][j].val = LinkAPixArea.SELECTED;
					}

					repaint();
					return;
				}

				if (selectedNumber != null) {
					System.out.println("Pressed: " + i + " " + j);
					System.out.println("LastPosition: " + lastPosition.i + " "
							+ lastPosition.j);

					System.out.println(linkAPixArea.area[i][j]);

					if (selectedNumber == linkAPixArea.area[i][j].number) {
						selectedNumber.unselect();
						selectedNumber = null;
						lastPosition = null;

						repaint();
						return;
					}

					if (i == lastPosition.i && j == lastPosition.j) {
						System.out.println("1");

						lastPosition = linkAPixArea.area[i][j].prev;

						linkAPixArea.area[i][j].val = LinkAPixArea.ABSENCE;
						linkAPixArea.area[i][j].belongToNumber = null;
						linkAPixArea.area[i][j].next = null;
						linkAPixArea.area[i][j].prev = null;

						repaint();
						return;
					}

					if ((((i == lastPosition.i - 1 || i == lastPosition.i + 1) && j == lastPosition.j) || (i == lastPosition.i && (j == lastPosition.j - 1 || j == lastPosition.j + 1)))
							&& linkAPixArea.area[i][j].val != LinkAPixArea.SELECTED
							&& (linkAPixArea.area[i][j].number == null || linkAPixArea.area[i][j].number.value == selectedNumber.value)
							&& selectedNumber.secondNumbers == null) {

						System.out.println("2");
						if (linkAPixArea.area[i][j].number != null
								&& linkAPixArea.area[i][j].number.value == selectedNumber.value) {

							selectedNumber.secondNumbers = new ArrayList<LaNumber>();
							selectedNumber.secondNumbers
									.add(linkAPixArea.area[i][j].number);
							linkAPixArea.area[i][j].number.secondNumbers = new ArrayList<LaNumber>();
							linkAPixArea.area[i][j].number.secondNumbers
									.add(selectedNumber);
							linkAPixArea.area[i][j]
									.setBelongsToNumber(selectedNumber);

							lastPosition.next = linkAPixArea.area[i][j];
							linkAPixArea.area[i][j].prev = lastPosition;
							selectedNumber = null;
							lastPosition = null;

							repaint();
							return;
						}

						lastPosition.next = linkAPixArea.area[i][j];
						linkAPixArea.area[i][j].prev = lastPosition;
						lastPosition = linkAPixArea.area[i][j];

						linkAPixArea.area[i][j]
								.setBelongsToNumber(selectedNumber);

						repaint();
						return;
					}
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
