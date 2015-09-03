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
					// sprawdź czy aby napewno
					// można dojsć do wszystkich znalezionych liczb
					for (LaNumber secNumber : laNumber.secondNumber) {
						List<Field[]> paths = findPathToNumber(laNumber,
								secNumber);
					}

					// DEBUG
					drugieLiczby(laNumber);
				}
			}
		}
		return false;
	}

	// Metoda szukająca ściężki do danej liczby, zwraca listę ścieżek
	private List<Field[]> findPathToNumber(LaNumber sourceNumber,
			LaNumber targetNumber) {

		List<Field[]> paths = new ArrayList<Field[]>();

		Field[] path = new Field[sourceNumber.value];

		findingPath(sourceNumber, targetNumber, paths, path,
				sourceNumber.value - 1, 0, sourceNumber.i, sourceNumber.j);

		return paths;
	}

	// Metoda rekurencyjna. szukanie ścieżki
	private void findingPath(LaNumber sourceNumber, LaNumber targetNumber,
			List<Field[]> paths, Field[] path, int leftLength, int index,
			int posI, int posJ) {

		// Jeżeli znaleziono więcej niż jedną ścieżkę zakończ poszukiwania
		if (paths.size() > 1)
			return;

		Field field = area[posI][posJ];
		path[index] = field;

		if (leftLength == 0 && field.number == targetNumber) {
			paths.add(path.clone());
			return;
		}

		if (checkIfCanGoThere(targetNumber, leftLength, index, path, posI - 1,
				posJ)) {

			findingPath(sourceNumber, targetNumber, paths, path,
					leftLength - 1, index + 1, posI - 1, posJ);
		}

	}

	// Fukcja sprawdzająca czy można przejsć do danego pola
	private boolean checkIfCanGoThere(LaNumber targetNumber, int leftLength,
			int index, Field[] path, int posI, int posJ) {

		// Jeżeli pozycja wybiega poza obszar planszy zwróc fałsz
		if (posI < 0 || posI >= this.y || posJ < 0 || posJ >= this.x) {
			return false;
		}

		Field field = area[posI][posJ];

		if (field.val == SELECTED
				|| field.number != targetNumber
				|| checkIfBelognsToPath(path, index, field)
				|| checkIfBlockSomeNumbers(path, index, targetNumber, posI,
						posJ)) {
			return false;
		}

		return false;
	}

	// Metoda sprawdzająca czy liczby występujące obok pola są blokowane,
	// pierwszy etap szuka liczb, jeżeli znajdzie liczbe to spwradza czy jest
	// blokowana
	private boolean checkIfBlockSomeNumbers(Field[] path, int index,
			LaNumber targetNumber, int posI, int posJ) {

		if (posI - 1 >= 0 && area[posI - 1][posJ].number != null
				&& area[posI - 1][posJ].val != SELECTED
				&& area[posI - 1][posJ] != path[0]
				&& area[posI - 1][posJ].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI - 1][posJ], path, index)) {
				return true;
			}
		}

		if (posI + 1 < this.y && area[posI + 1][posJ].number != null
				&& area[posI + 1][posJ].val != SELECTED
				&& area[posI + 1][posJ] != path[0]
				&& area[posI + 1][posJ].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI + 1][posJ], path, index)) {
				return true;
			}
		}

		if (posJ - 1 >= 0 && area[posI][posJ - 1].number != null
				&& area[posI][posJ - 1].val != SELECTED
				&& area[posI][posJ - 1] != path[0]
				&& area[posI][posJ - 1].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI][posJ - 1], path, index)) {
				return true;
			}
		}

		if (posJ + 1 < this.x && area[posI][posJ + 1].number != null
				&& area[posI][posJ + 1].val != SELECTED
				&& area[posI][posJ + 1] != path[0]
				&& area[posI][posJ + 1].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI][posJ + 1], path, index)) {
				return true;
			}
		}

		return false;
	}

	// Sprawdza czy liczba jest blokowana
	private boolean checkIfNumberIsBlock(Field field, Field[] path, int index) {

		int k = 0;

		if (field.number != null && field.number.value == 2) {
			return false;
		}

		if (field.i - 1 < 0
				|| area[field.i - 1][field.j].val == SELECTED
				|| area[field.i - 1][field.j].number != null
				|| checkIfBelognsToPath(path, index, area[field.i - 1][field.j])) {

			k++;
		}

		if (field.i + 1 >= this.y
				|| area[field.i + 1][field.j].val == SELECTED
				|| area[field.i + 1][field.j].number != null
				|| checkIfBelognsToPath(path, index, area[field.i + 1][field.j])) {

			k++;
		}

		if (field.j - 1 < 0
				|| area[field.i][field.j - 1].val == SELECTED
				|| area[field.i][field.j - 1].number != null
				|| checkIfBelognsToPath(path, index, area[field.i][field.j - 1])) {

			k++;
		}

		if (field.j + 1 >= this.x
				|| area[field.i][field.j + 1].val == SELECTED
				|| area[field.i][field.j + 1].number != null
				|| checkIfBelognsToPath(path, index, area[field.i][field.j + 1])) {

			k++;
		}

		if (k == 4)
			return true;

		return false;
	}

	// Metoda sprawdzająca czy pole należy do aktualnie wyznaczonej ścieżki
	private boolean checkIfBelognsToPath(Field[] path, int index, Field field) {

		for (int i = 0; i < index; i++) {
			if (path[i] == field)
				return true;
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
