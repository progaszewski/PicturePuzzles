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

			// pobranie listy liczb o wartosci 1
			List<LaNumber> oneNumbers = numbers.get((byte) 1);
			for (LaNumber laNumber : oneNumbers) {
				area[laNumber.i][laNumber.j].val = SELECTED;
			}

			// Usinięcie listy liczb o wartości 1
			numbers.remove((byte) 1);

			for (Byte number : numbers.keySet()) {
				List<LaNumber> numbersByKey = numbers.get(number);

				for (LaNumber laNumber : numbersByKey) {
					System.out.println(laNumber.value + " [" + laNumber.i
							+ ", " + laNumber.j + "]");
					if (laNumber.secondNumber == null) {
						searchSecondNumber(laNumber);

						// jeżeli znaleziono tylko jedną "drugą liczbę" to
						// znaczy, że można tylko te dwie liczby połączyć, czyli
						// wskaż tej drugiej liczbe, liczbę aktualnie iterowaną
						if (laNumber.secondNumber.size() == 1) {
							LaNumber secondNumber = laNumber.secondNumber
									.get(0);

							secondNumber.secondNumber = new ArrayList<LaNumber>();
							secondNumber.secondNumber.add(laNumber);
						}
					}

					// DEBUG
					drugieLiczby(laNumber);
				}
			}
		}
		return false;
	}

	// Szukanie liczby z którymi możę się polączyć dana liczba
	private void searchSecondNumber(LaNumber laNumber) {

		int diff = 0;
		int startI = laNumber.i - laNumber.value + 1;

		// Sprawdzamy czy zakres w górę wybiega poza planszę
		// jeżeli tak to ustaw wartość diff o długość tej różnicy
		if (startI < 0) {
			diff = laNumber.value - 1 - laNumber.i;
			startI = 0;
		}

		int k = 0;
		int l = 0;
		for (int i = startI; i <= laNumber.i + (laNumber.value - 1)
				&& i < this.y; i++) {

			// Jeżęli i jest mniejsze od pozycji "i" sprawdzanej liczby to
			// zwiększaj "k"
			if (i <= laNumber.i) {
				k = l + diff;
			} else {
				k = 2 * laNumber.value - 2 - (l + diff);
			}

			l++;
			int startJ = laNumber.j - k;

			if (startJ < 0) {
				startJ = (k - laNumber.j) % 2;
			}

			for (int j = startJ; j <= laNumber.j + k && j < this.x; j += 2) {

				Field field = this.area[i][j];

				// Jeżeli pole jest liczbą i ta liczba nie jest aktualnie
				// iterowaną liczbą oraz ta liczba jest takiej samej wartości co
				// aktualnie iterowana liczba oraz liczba nie posiada tylko
				// jednej drugiej liczby to dodaj są do listy "drugich liczby"
				if (field.number != null
						&& field.number != laNumber
						&& field.number.value == laNumber.value
						&& (field.number.secondNumber == null || field.number.secondNumber
								.size() > 1)) {

					if (laNumber.secondNumber == null) {
						laNumber.secondNumber = new ArrayList<LaNumber>();
					}

					laNumber.secondNumber.add(field.number);
				}
			}
		}

	}

	public boolean checkSolve() {
		return false;
	}

	// DEBUG
	private void drugieLiczby(LaNumber laNumber) {
		System.out.println(laNumber.value + " [" + laNumber.i + ", "
				+ laNumber.j + "] secondNumbers:");
		for (LaNumber secNumber : laNumber.secondNumber) {
			System.out.print(secNumber.value + " [" + secNumber.i + ", "
					+ secNumber.j + "], ");
		}
		System.out.println("\n");
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

		public List<LaNumber> secondNumber; // druga liczba z która jest
											// połączona

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

		public void unselect() {
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
