package pl.picture.puzzles.windows;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import pl.picture.puzzles.common.Puzzle;
import pl.picture.puzzles.fillapix.FillAPixPuzzle;

public class PicturePuzzelsMainFrame extends JFrame {

	private static final long serialVersionUID = 8119467365664162607L;
	private JPanel contentPane;
	private Puzzle puzzle;

	/**
	 * Create the frame.
	 */
	public PicturePuzzelsMainFrame() {
		setTitle(Messages.getString("PicturePuzzelsMainFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu(
				Messages.getString("PicturePuzzelsMainFrame.mnFile.text")); //$NON-NLS-1$
		menuBar.add(fileMenu);

		JMenuItem open = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmOpen.text")); //$NON-NLS-1$
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File selectedPuzzleFile = puzzle.openPuzzle();
				if (selectedPuzzleFile != null) {
					puzzle.drawPazzle(selectedPuzzleFile);
					pack();
				}
			}
		});
		fileMenu.add(open);

		JSeparator separator = new JSeparator();
		fileMenu.add(separator);

		JMenuItem exit = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmExit.text")); //$NON-NLS-1$
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exit);

		JMenu puzzleMenu = new JMenu(
				Messages.getString("PicturePuzzelsMainFrame.mPuzzle.text")); //$NON-NLS-1$
		menuBar.add(puzzleMenu);

		JMenuItem selectFillAPix = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmFillapix.text")); //$NON-NLS-1$
		selectFillAPix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle = new FillAPixPuzzle();
				replacePanel(puzzle.getPanel());
			}
		});
		puzzleMenu.add(selectFillAPix);

		JMenuItem selectLinkAPix = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmLinkapix.text")); //$NON-NLS-1$
		puzzleMenu.add(selectLinkAPix);

		JMenuItem selectPicAPix = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmPicapix.text")); //$NON-NLS-1$
		puzzleMenu.add(selectPicAPix);

		JMenu actionMenu = new JMenu(
				Messages.getString("PicturePuzzelsMainFrame.mnActions.text")); //$NON-NLS-1$
		menuBar.add(actionMenu);

		JMenuItem check = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmCheck.text")); //$NON-NLS-1$
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.check();
			}
		});
		actionMenu.add(check);

		JMenuItem solve = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmSolve.text")); //$NON-NLS-1$
		solve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.solve();
			}
		});
		actionMenu.add(solve);

		JMenu helpMenu = new JMenu(
				Messages.getString("PicturePuzzelsMainFrame.mnHelp.text")); //$NON-NLS-1$
		menuBar.add(helpMenu);

		JMenuItem aboutAplication = new JMenuItem(
				Messages.getString("PicturePuzzelsMainFrame.mntmAboutAplication.text")); //$NON-NLS-1$
		helpMenu.add(aboutAplication);
		// domyslna lamiglowka: Fill-a-Pix
		puzzle = new FillAPixPuzzle();
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel = puzzle.getPanel();
		contentPane.add(panel);
	}

	private void replacePanel(JPanel panel) {
		contentPane.removeAll();
		contentPane.add(panel);
		contentPane.repaint();
	}
}
