package pl.picture.puzzles.app;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.UIManager;

import pl.picuter.puzzles.windows.PicturePuzzelsMainFrame;

/**
 * Picture Puzzles!
 *
 */
public class PicturePuzzlesRun {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (!Locale.getDefault().toLanguageTag()
							.startsWith("pl-PL")) {
						Locale.setDefault(new Locale("en", "US"));
					}

					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					PicturePuzzelsMainFrame frame = new PicturePuzzelsMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
