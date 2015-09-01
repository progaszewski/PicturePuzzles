package pl.picture.puzzles.linkapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

		numbers = new HashMap<Byte, ArrayList<LaNumber>>();
		try {
			Scanner s = new Scanner(f);

			if (s.hasNextLine()) {
				String[] dim = s.nextLine().split(" ");
				x = Integer.valueOf(dim[0]);
				y = Integer.valueOf(dim[1]);
			}

			area = new Field[y][x];

			for (int i = 0; i < y; i++) {
				for (int j = 0; j < x; j++) {
					area[i][j] = new Field(i, j);
				}
			}

			while (s.hasNextLine()) {
				String[] values = s.nextLine().split(" ");

				if (values.length != 3) {
					continue;
				}

				int i = Integer.valueOf(values[0]);
				int j = Integer.valueOf(values[1]);
				byte val = Byte.valueOf(values[2]);

				LaNumber laNumber = new LaNumber(val, i, j);
				area[i][j].number = laNumber;

				ArrayList<LaNumber> listNumber = numbers.get(val);
				if (listNumber == null) {
					listNumber = new ArrayList<LaNumber>();
					numbers.put(val, listNumber);
				}

				listNumber.add(laNumber);
			}
			s.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean solvePuzzle() {

		// numbers.keySet().toArray();
		boolean change = true;
		while (change) {
			change = false;
			for (Byte number : numbers.keySet()) {
				List<LaNumber> numbersByKey = numbers.get(number);
			}
		}
		return false;
	}

	public boolean checkSolve() {
		return false;
	}

	// Klasa reprezentujaca pole (kratke do zanzaczenia)
	class Field {
		public Byte val = -1; // -1: BRAK, 1: ZAZNACZONE
		public LaNumber number; // Liczba

		public int i, j; // pozycja pola

		public Field next, prev;
		public LaNumber belongsToNumber;

		public Field(int i, int j) {
			this.i = i;
			this.j = j;
		}

		public Field(Field field) {
			this.number = field.number;
			this.val = field.val;
			this.i = field.i;
			this.j = field.j;
		}

		public void setBelongsToNumber(LaNumber selectedNumber) {
			this.val = SELECTED;
			this.belongsToNumber = selectedNumber;

		}

		@Override
		public String toString() {
			String num = " number: ";

			if (number != null) {
				num = num + number.value + " " + number.secondNumber;
			} else {
				num = num + "NULL";
			}

			String n = " next: ";
			if (next != null) {
				n = n + next.i + " " + next.j;
			} else {
				n = n + "NULL";
			}

			String p = " prev: ";
			if (prev != null) {
				p = p + prev.i + " " + prev.j;
			} else {
				p = p + "NULL";
			}

			String b = " belognsTo: ";

			if (belongsToNumber != null) {
				b = b + belongsToNumber.value;
			} else {
				b = b + "NULL";
			}
			return val + num + n + p + b;
		}
	}

	// Klasa reprezentujaca liczbe, ktora określa długość odcinka jaki trzeba
	// wyznaczyć, odcinek musi być połączony z inną liczą o takiej samej
	// wartości
	class LaNumber {
		public final byte value; // Wartosc liczby

		public final int i, j; // pozycja liczny

		public LaNumber secondNumber; // druga liczba z która jest połączona

		// Ścieżka do drugiej liczby
		// public LinkedList<Field> path = new LinkedList<Field>();

		public LaNumber(byte value, int i, int j) {
			this.value = value;
			this.i = i;
			this.j = j;
		}

		public LaNumber(LaNumber number) {
			this.value = number.value;
			this.i = number.i;
			this.j = number.j;
			this.secondNumber = number.secondNumber;
			// this.path = number.path;
		}

		public void unselet() {
			Field field = area[this.i][this.j];

			if (field.next != null) {
				clearNextFields(field.next);
			} else if (field.prev != null) {
				clearPrevFields(field.prev);
			}

			field.val = ABSENCE;
			field.belongsToNumber = null;
			field.next = null;
			field.prev = null;
			this.secondNumber = null;
		}
	}

	public void clearNextFields(Field next) {
		next.val = ABSENCE;
		next.belongsToNumber = null;

		if (next.next != null) {
			clearNextFields(next.next);
		} else {
			if (next.number != null) {
				next.number.secondNumber = null;
			}
		}

		next.next = null;
		next.prev = null;
	}

	public void clearPrevFields(Field prev) {
		prev.val = ABSENCE;
		prev.belongsToNumber = null;

		if (prev.prev != null) {
			clearPrevFields(prev.prev);
		} else {
			if (prev.number != null) {
				prev.number.secondNumber = null;
			}
		}
		prev.next = null;
		prev.prev = null;
	}

}
