package pl.picture.puzzles.picapix;

import javax.swing.JPanel;

public class PicAPixPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5652831501965895968L;

	public PicAPixArea picAPixArea;

	public void drawArea(final PicAPixArea paArea) {
		this.picAPixArea = paArea;
	}

}
