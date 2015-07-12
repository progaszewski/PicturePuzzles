package pl.picture.puzzles.fillapix;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.picture.puzzles.common.Puzzle;
import pl.picture.puzzles.windows.Messages;

public class FillAPixPuzzle extends Puzzle {

	private FillAPixPanel faPanel;
	private FillAPixArea fillAPixArea;

	public FillAPixPuzzle() {
		this.faPanel = new FillAPixPanel();
	}

	@Override
	public JPanel getPanel() {

		return faPanel;
	}

	@Override
	public File openPuzzle() {
		return openPuzzle("faptxt");
	}

	@Override
	public void check() {
		if (fillAPixArea == null)
			return;

		// Przygotowanie areny przed sprawdzeniem
		fillAPixArea.beforeCheck();

		faPanel.repaint();

		// Sprawdzanie czy gra została rozwiązana, jeżeli tak to true, w p.p.
		// false
		if (fillAPixArea.checkSolve()) {
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.success.text"),
					Messages.getString("Puzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.failure.text"),
					Messages.getString("Puzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);

			fillAPixArea.restoreArea();
			faPanel.repaint();

		}

	}

	@Override
	public void solve() {
		if (fillAPixArea == null)
			return;

		if (fillAPixArea.solvePuzzle()) {
			faPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.success.solved.text"),
					Messages.getString("Puzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			faPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.failure.solved.text"),
					Messages.getString("Puzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void drawPazzle(File f) {
		fillAPixArea = new FillAPixArea(f);
		faPanel.drawArea(fillAPixArea);
	}

	@Override
	public String getName() {
		return "Fill-a-Pix";

	}
}
