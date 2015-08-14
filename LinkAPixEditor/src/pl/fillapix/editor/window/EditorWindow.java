package pl.fillapix.editor.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import pl.fillapix.editor.EditorPanel;

public class EditorWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textFieldY;
	private JTextField textFieldX;
	private EditorPanel panel_2;
	private JScrollPane jsp;

	/**
	 * Create the frame.
	 */
	public EditorWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(System
						.getProperty("user.home") + "\\Desktop");
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileFilter() {
					private final String txt = "laptxt";

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
				int action = fc.showSaveDialog(null);
				if (action == JFileChooser.APPROVE_OPTION) {
					String areaFile = "";
					String dimensions = "";
					try {
						Scanner s = new Scanner(fc.getSelectedFile());

						int x = 0, y = 0;
						if (s.hasNextLine()) {
							dimensions = s.nextLine();
							String dim[] = dimensions.split(" ");
							x = Integer.valueOf(dim[0]);
							y = Integer.valueOf(dim[1]);

						}

						panel_2.lap.area = new byte[y][x];

						for (int i = 0; i < y; i++) {
							for (int j = 0; j < x; j++) {
								panel_2.lap.area[i][j] = -1;
							}
						}

						while (s.hasNextLine()) {
							areaFile = s.nextLine();
							String[] values = areaFile.split(" ");

							int i = Integer.valueOf(values[0]);
							int j = Integer.valueOf(values[1]);
							byte val = Byte.valueOf(values[2]);

							panel_2.lap.area[i][j] = val;
						}
						s.close();

						// for (int i = 0; i < y; i++) {
						// for (int j = 0; j < x; j++) {
						// if (areaFile.charAt(i * x + j) != '.') {
						// // System.out.println(p.charAt(i*x + j) +
						// // "");
						// panel_2.lap.area[i][j] = Byte
						// .parseByte(String.valueOf(areaFile
						// .charAt(i * x + j)));
						// } else {
						// panel_2.lap.area[i][j] = -1;
						// }
						// }
						//
						// }

						textFieldX.setText(x + "");
						textFieldY.setText(y + "");
						panel_2.loadAreaFromFile(x, y);

						pack();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnOpen);

		textFieldX = new JTextField();
		textFieldX.setText("0");
		panel.add(textFieldX);
		textFieldX.setColumns(3);

		JLabel lblX = new JLabel("x");
		panel.add(lblX);

		textFieldY = new JTextField();
		textFieldY.setText("0");
		panel.add(textFieldY);
		textFieldY.setColumns(3);

		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel_2.setSizeArea(Integer.parseInt(textFieldX.getText()),
						Integer.parseInt(textFieldY.getText()));
				// jsp.setPreferredSize(panel_2.getPreferredSize());
				if (panel_2.getPreferredSize().height > 700) {
					jsp.setPreferredSize(new Dimension(panel_2
							.getPreferredSize().width + 30, 750));
				} else {
					jsp.setPreferredSize(new Dimension(panel_2
							.getPreferredSize().width + 15, panel_2
							.getPreferredSize().height + 15));
				}
				pack();
			}
		});
		panel.add(btnSet);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (panel_2.lap.area == null) {
					return;
				}
				JFileChooser fc = new JFileChooser(System
						.getProperty("user.home") + "\\Desktop");
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileFilter() {
					private final String txt = "laptxt";

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
				int akcja = fc.showSaveDialog(null);
				if (akcja == JFileChooser.APPROVE_OPTION) {

					try {
						File selectedFile = fc.getSelectedFile();

						if (!selectedFile.getName().endsWith(".laptxt")) {
							selectedFile = new File(selectedFile.getPath()
									+ ".laptxt");

						}
						PrintWriter out = new PrintWriter(selectedFile);
						out.print(panel_2.lap.area[0].length + " "
								+ panel_2.lap.area.length + "\n");

						for (int i = 0; i < panel_2.lap.area.length; i++) {
							for (int j = 0; j < panel_2.lap.area[i].length; j++) {
								if (panel_2.lap.area[i][j] != -1) {
									out.print(i + " " + j + " "
											+ panel_2.lap.area[i][j]);

									if (i != panel_2.lap.area.length
											&& j != panel_2.lap.area[0].length) {
										out.print("\n");
									}
								}

							}

						}

						out.close();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnSave);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		panel_2 = new EditorPanel();
		panel_1.add(panel_2);

		panel_1.setAutoscrolls(true);
		jsp = new JScrollPane(panel_1);
		jsp.setMaximumSize(new Dimension(1000, 700));
		this.add(jsp);
	}
}
