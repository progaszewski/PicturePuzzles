package pl.picture.puzzles.picapix;

import java.io.File;

import javax.swing.JPanel;

import pl.picture.puzzles.common.Puzzle;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void solve() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPazzle(File f) {
		this.paArea = new PicAPixArea(f);
		paPanel.drawArea(paArea);
	}

}
