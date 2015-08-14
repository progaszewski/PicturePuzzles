package pl.fillapix.editor;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.UIManager;

import pl.fillapix.editor.window.EditorWindow;

public class EditorRun {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (!Locale.getDefault().toLanguageTag()
							.startsWith("pl-PL")) {
						Locale.setDefault(new Locale("en", "US"));
					}

					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					EditorWindow frame = new EditorWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
