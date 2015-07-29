package pl.picture.puzzles.picapix;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.picture.puzzles.common.Puzzle;
import pl.picture.puzzles.windows.Messages;

public class PicAPixPuzzle extends Puzzle {

	private PicAPixPanel paPanel;
	private PicAPixArea paArea;

	public PicAPixPuzzle() {
		this.paPanel = new PicAPixPanel();
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return this.paPanel;
	}

	@Override
	public File openPuzzle() {
		// TODO Auto-generated method stub
		return openPuzzle("paptxt");
	}

	@Override
	public void check() {
		if (paArea == null)
			return;

		if (paArea.checkPuzzle()) {

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
		if (paArea == null)
			return;

		if (paArea.solvePuzzle(paPanel)) {
			paPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.success.solved.text"),
					Messages.getString("Puzzle.success.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			paPanel.repaint();
			JOptionPane.showMessageDialog(null,
					Messages.getString("Puzzle.failure.solved.text"),
					Messages.getString("Puzzle.failure.title.text"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void drawPazzle(File f) {
		this.paArea = new PicAPixArea(f);
		paPanel.drawArea(paArea);
	}

	@Override
	public String getName() {
		return "Pic-a-Pix";
	}

}
