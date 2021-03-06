package pl.picture.puzzles.common;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public abstract class Puzzle {

	private JFileChooser fc;

	public abstract JPanel getPanel();

	public abstract File openPuzzle();

	public abstract void check();

	public abstract void solve();

	public abstract void drawPazzle(File f);

	public abstract String getName();

	protected File openPuzzle(final String type) {
		// fc = new JFileChooser("G:\\Praca Mgr\\workspace\\Puzzles");
		fc = new JFileChooser(".\\Puzzles");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter() {
			private final String txt = type;

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String fn = f.getName();

				if (fn.length() > ("." + txt).length()
						&& fn.endsWith("." + txt)) {
					return true;
				}

				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*." + txt;
			}

		});
		int akcja = fc.showOpenDialog(null);
		if (akcja == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;
	}

}
