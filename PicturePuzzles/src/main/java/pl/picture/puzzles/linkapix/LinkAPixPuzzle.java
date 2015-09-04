package pl.picture.puzzles.linkapix;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.picture.puzzles.common.Puzzle;
import pl.picture.puzzles.windows.Messages;

public class LinkAPixPuzzle extends Puzzle {

	private LinkAPixPanel laPanel;
	private LinkAPixArea linkAPixArea;

	public LinkAPixPuzzle() {

		this.laPanel = new LinkAPixPanel();
	}

	@Override
	public JPanel getPanel() {
		return this.laPanel;
	}

	@Override
	public File openPuzzle() {
		return openPuzzle("laptxt");
	}

	@Override
	public void check() {
		if (linkAPixArea == null) {
			return;
		}

		// Sprawdzanie czy gra została rozwiązana, jeżeli tak to true, w p.p.
		// false
		if (linkAPixArea.checkSolve()) {
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.success.text"),
					Messages.getString("Puzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.failure.text"),
					Messages.getString("Puzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);

		}

	}

	@Override
	public void solve() {
		if (linkAPixArea == null) {
			return;
		}

		if (linkAPixArea.solvePuzzle(laPanel)) {
			laPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.success.solved.text"),
					Messages.getString("Puzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			laPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.failure.solved.text"),
					Messages.getString("Puzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void drawPazzle(File f) {
		linkAPixArea = new LinkAPixArea(f);
		laPanel.drawArea(linkAPixArea);
	}

	@Override
	public String getName() {
		return "Link-a-Pix";
	}

}
