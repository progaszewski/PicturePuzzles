package pl.picture.puzzles.fillapix;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.picture.puzzles.common.Puzzle;
import pl.picuter.puzzles.windows.Messages;

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
		return selectPuzzle("faptxt");
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
					Messages.getString("FillAPixPuzzle.success.text"),
					Messages.getString("FillAPixPuzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null,
					Messages.getString("FillAPixPuzzle.failure.text"),
					Messages.getString("FillAPixPuzzle.failure.title.text"),
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
					Messages.getString("FillAPixPuzzle.success.solved.text"),
					Messages.getString("FillAPixPuzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			faPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("FillAPixPuzzle.failure.solved.text"),
					Messages.getString("FillAPixPuzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void drawPazzle(File f) {
		fillAPixArea = new FillAPixArea(f);
		faPanel.drawArea(fillAPixArea);
	}
}
