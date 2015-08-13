package pl.picture.puzzles.linkapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinkAPixArea {

	public static final byte ABSENCE = -1; // Nieoznaczone pole
	public static final byte SELECTED = 1; // Zaznaczone

	public Field[][] area;
	public Map<Byte, ArrayList<LaNumber>> numbers;
	public int x = 0, y = 0; // Wymiary lamiglowki

	public LinkAPixArea(File f) {
		init(f);
	}

	private void init(File f) {

		String areaFile = "";
		numbers = new HashMap<Byte, ArrayList<LaNumber>>();
		try {
			Scanner s = new Scanner(f);

			if (s.hasNextLine()) {
				areaFile += s.nextLine();
				x = areaFile.length();
				y++;
			}
			while (s.hasNextLine()) {
				areaFile += s.nextLine();
				y++;
			}
			s.close();

			area = new Field[y][x];

			for (int i = 0; i < y; i++) {
				for (int j = 0; j < x; j++) {

					area[i][j] = new Field();
					if (areaFile.charAt(i * x + j) != '.') {
						// System.out.println(p.charAt(i*x + j) + "");
						byte value = Byte.parseByte(String.valueOf(areaFile
								.charAt(i * x + j)));
						LaNumber laNumber = new LaNumber(value, i, j);
						area[i][j].number = laNumber;

						ArrayList<LaNumber> listNumber = numbers.get(value);
						if (listNumber == null) {
							listNumber = new ArrayList<LaNumber>();
							numbers.put(value, listNumber);
						}

						listNumber.add(laNumber);
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean solvePuzzle() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkSolve() {
		// TODO Auto-generated method stub
		return false;
	}

	// Klasa reprezentujaca pole (kratke do zanzaczenia)
	class Field {
		public Byte val = -1; // -1: BRAK, 1: ZAZNACZONE
		public LaNumber number; // Liczba

		public Field() {

		}

		public Field(Field field) {
			this.number = field.number;
			this.val = field.val;
		}
	}

	// Klasa reprezentujaca liczbe, ktora okresla ile pol nalezy zaznaczyc
	class LaNumber {
		public final byte value; // Wartosc liczby

		public final int i, j; // pozycja liczny

		public LaNumber(byte value, int i, int j) {
			this.value = value;
			this.i = i;
			this.j = j;
		}

		public LaNumber(LaNumber number) {
			this.value = number.value;
			this.i = number.i;
			this.j = number.j;
		}
	}

}
