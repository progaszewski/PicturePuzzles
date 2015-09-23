package pl.picture.puzzles.linkapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JPanel;

import pl.picture.puzzles.common.PuzzleUtilities;

public class LinkAPixArea {

	public static final byte ABSENCE = -1; // Nieoznaczone pole
	public static final byte SELECTED = 1; // Zaznaczone

	public Field[][] area;
	public Map<Byte, ArrayList<LaNumber>> numbers;
	public int x = 0, y = 0; // Wymiary lamiglowki

	private boolean isAdvance = false;
	private boolean onlyOnePath = false;

	private Map<Field, Integer> commonFields;

	public LinkAPixArea(File f) {
		init(f);
	}

	private JPanel debugPanel;
	private boolean isDetermitingCommonFields;
	private boolean spr = false;

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

			}
			s.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean solvePuzzle(JPanel panel) {

		this.debugPanel = panel;
		clearArea();

		long startCountTimeElapsed = System.currentTimeMillis();
		// numbers.keySet().toArray();
		boolean change = true;
		this.isAdvance = false;

		// pobranie listy liczb o wartosci 1
		List<LaNumber> oneNumbers = numbers.get((byte) 1);

		if (oneNumbers != null && oneNumbers.size() > 0) {
			for (LaNumber laNumber : oneNumbers) {
				area[laNumber.i][laNumber.j].val = SELECTED;
			}
		}

		// Usinięcie listy liczb o wartości 1
		numbers.remove((byte) 1);

		try {
			while (change) {
				change = false;

				List<Byte> numberKeysToRemove = new ArrayList<Byte>();

				for (Byte number : numbers.keySet()) {
					List<LaNumber> numbersByKey = numbers.get(number);

					boolean change2 = true;

					while (change2 && numbersByKey.size() > 0) {

						change2 = false;

						List<LaNumber> numbersByKeyToRemove = new ArrayList<LaNumber>();
						for (LaNumber laNumber : numbersByKey) {

							// Jeżeli pole do którego należy liczba jest
							// pokolorowane to
							// znaczy że liczba ta została już wyznaczona czyli
							// można ją
							// ominąć
							if (area[laNumber.i][laNumber.j].val == SELECTED) {
								continue;
							}

							if (laNumber.secondNumbers == null) {
								searchSecondNumbers(laNumber);

								// jeżeli znaleziono tylko jedną "drugą liczbę"
								// to
								// znaczy, że można tylko te dwie liczby
								// połączyć,
								// czyli
								// wskaż tej drugiej liczbe, liczbę aktualnie
								// iterowaną
								if (laNumber.secondNumbers.size() == 1) {
									LaNumber secondNumber = laNumber.secondNumbers
											.get(0);

									secondNumber.secondNumbers = new ArrayList<LaNumber>();
									secondNumber.secondNumbers.add(laNumber);
								}

								change = change2 = true;
							}
							// sprawdź czy aby napewno
							// można dojsć do wszystkich znalezionych liczb
							if (laNumber.secondNumbers.size() > 1) {
								List<LaNumber> numbersToRemove = new ArrayList<LaNumber>();
								for (LaNumber secNumber : laNumber.secondNumbers) {

									// Sprawdzanie czy druga liczba jest już
									// oznaczona
									if (area[secNumber.i][secNumber.j].val == SELECTED) {
										numbersToRemove.add(secNumber);
										continue;
									}
									this.onlyOnePath = true;
									List<Field[]> paths = findPathsToNumber(
											laNumber, secNumber);
									this.onlyOnePath = false;
									if (paths.size() == 0) {
										numbersToRemove.add(secNumber);
									}

								}

								if (numbersToRemove.size() > 0) {
									laNumber.secondNumbers
											.removeAll(numbersToRemove);

									change = change2 = true;
								}

								// Jeżeli po usunięciu "drugich liczb" została
								// tylko
								// jedna druga liczba to połącz te liczby
								// relacją
								if (laNumber.secondNumbers.size() == 1) {
									LaNumber secondNumber = laNumber.secondNumbers
											.get(0);

									secondNumber.secondNumbers = new ArrayList<LaNumber>();
									secondNumber.secondNumbers.add(laNumber);

									change = change2 = true;
								}
							}

							// Jeżeli liczba ma tylko jednego brata sprawdź czy
							// można
							// wyznaczyć do niego sieżkę
							if (laNumber.secondNumbers.size() == 1) {
								List<Field[]> paths = findPathsToNumber(
										laNumber, laNumber.secondNumbers.get(0));

								if (paths.size() == 1) {
									drawPath(paths.get(0), laNumber);

									numbersByKeyToRemove.add(laNumber);
									numbersByKeyToRemove
											.add(laNumber.secondNumbers.get(0));

									change = change2 = true;
								}
							}
						}

						if (numbersByKeyToRemove.size() > 0) {
							numbersByKey.removeAll(numbersByKeyToRemove);
						}

					}

					if (numbersByKey.size() == 0) {
						numberKeysToRemove.add(number);
					}
				}

				if (numberKeysToRemove.size() > 0) {
					for (Byte num : numberKeysToRemove) {
						numbers.remove(num);
					}
				}

				// Jeżeli nic nie udalo sie zmienic i tryb zaawansowany jest
				// wyłączony włączy tryb zaawansowany
				if (!change && !this.isAdvance) {
					change = this.isAdvance = true;
				}

				if (!change && this.isAdvance) {
					this.isDetermitingCommonFields = true;
					change = determinateCommonFields();

					this.isDetermitingCommonFields = false;
					this.spr = true;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			panel.repaint();
		}

		PuzzleUtilities.showTimeElapsed(System.currentTimeMillis()
				- startCountTimeElapsed);
		return checkSolve();
	}

	private boolean determinateCommonFields() {

		boolean change = false;
		for (Byte number : numbers.keySet()) {

			List<LaNumber> numberByKey = numbers.get(number);

			for (LaNumber laNumber : numberByKey) {

				if (laNumber.secondNumbers.size() > 1 || laNumber.haveCommon) {
					laNumber.haveCommon = false;
					continue;
				}

				this.commonFields = new HashMap<Field, Integer>();

				findPathsToNumber(laNumber, laNumber.secondNumbers.get(0));

				if (commonFields != null && commonFields.size() > 0) {

					for (Field commonField : commonFields.keySet()) {

						if (commonField.val == SELECTED)
							continue;

						commonField.belongToNumber = laNumber;
						commonField.val = SELECTED;
						commonField.belongToNumberSecond = laNumber.secondNumbers
								.get(0);
						change = true;
					}

				}

				laNumber.secondNumbers.get(0).haveCommon = true;
			}
		}

		return change;
	}

	// Rysowanie wyznaczonej ścieżki
	private void drawPath(Field[] fields, LaNumber laNumber) {

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			field.belongToNumber = laNumber;
			field.val = SELECTED;
			field.belongToNumberSecond = laNumber.secondNumbers.get(0);

			if (i > 0) {
				field.prev = fields[i - 1];
			}

			if (i < fields.length - 1) {
				field.next = fields[i + 1];
			}
		}
	}

	// Metoda szukająca ściężki do danej liczby, zwraca listę ścieżek
	private List<Field[]> findPathsToNumber(LaNumber sourceNumber,
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
		if (paths.size() > 1
				|| (this.onlyOnePath && paths.size() == 1)
				|| (this.isDetermitingCommonFields && this.commonFields == null))
			return;

		Field field = area[posI][posJ];
		path[index] = field;

		if (leftLength == 0 && field.number == targetNumber) {

			if (this.isDetermitingCommonFields) {
				selectCommonFields(path);
			} else {
				paths.add(path.clone());
			}
			return;
		}

		if (checkIfCanGoThere(targetNumber, leftLength, index, path, posI - 1,
				posJ)) {

			findingPath(sourceNumber, targetNumber, paths, path,
					leftLength - 1, index + 1, posI - 1, posJ);
		}

		if (checkIfCanGoThere(targetNumber, leftLength, index, path, posI + 1,
				posJ)) {

			findingPath(sourceNumber, targetNumber, paths, path,
					leftLength - 1, index + 1, posI + 1, posJ);
		}

		if (checkIfCanGoThere(targetNumber, leftLength, index, path, posI,
				posJ - 1)) {

			findingPath(sourceNumber, targetNumber, paths, path,
					leftLength - 1, index + 1, posI, posJ - 1);
		}

		if (checkIfCanGoThere(targetNumber, leftLength, index, path, posI,
				posJ + 1)) {

			findingPath(sourceNumber, targetNumber, paths, path,
					leftLength - 1, index + 1, posI, posJ + 1);
		}
	}

	private void selectCommonFields(Field[] fields) {

		if (this.commonFields.size() == 0) {
			for (int i = 1; i < fields.length - 1; i++) {
				Field field = fields[i];
				commonFields.put(field, 1);
			}
			return;
		}

		Map<Field, Integer> temp = new HashMap<Field, Integer>();

		for (int i = 1; i < fields.length - 1; i++) {
			Field field = fields[i];
			Integer j = this.commonFields.get(field);

			if (j != null) {
				temp.put(field, 1);
			}
		}

		if (temp.size() == 0) {
			this.commonFields = null;
			return;
		}
		// this.commonFields.clear();
		this.commonFields = temp;

	}

	// Fukcja sprawdzająca czy można przejsć do danego pola
	private boolean checkIfCanGoThere(LaNumber targetNumber, int leftLength,
			int index, Field[] path, int posI, int posJ) {

		// Jeżeli pozycja wybiega poza obszar planszy zwróc fałsz
		if (posI < 0 || posI >= this.y || posJ < 0 || posJ >= this.x) {

			// System.out.println("1");
			return false;
		}

		Field field = area[posI][posJ];

		if ((field.val == SELECTED && (field.belongToNumber != path[0].number && field.belongToNumberSecond != path[0].number))
				|| checkIfBelognsToPath(path, index, field)) {

			// System.out.println("2");
			return false;
		}

		if (field.number != null && field.number != targetNumber) {
			// System.out.println("3");
			return false;
		}

		// Jeżeli pozstała tylko jedek krok do narysowania ścieżki a następne
		// pole nie jest docelowym numerem zwróć fałsz
		if (leftLength == 1 && field.number != targetNumber) {
			// System.out.println("4");
			return false;
		}
		// Jeżeli pozostało więcej niż jedek krok do wyznaczenia sieżki a
		// następne pole jest docelową liczbą zwróc fałsz
		if (leftLength > 1 && field.number == targetNumber) {
			// System.out.println("5");
			return false;
		}

		// Wyznaczanie jaka jest najkrótsza długość do docelowej liczby
		int minLeftLength = 0;

		if (posI > targetNumber.i) {
			minLeftLength = minLeftLength + (posI - targetNumber.i);
		}

		if (posI < targetNumber.i) {
			minLeftLength = minLeftLength + (targetNumber.i - posI);
		}
		if (posI == targetNumber.i) {
			if (posJ > targetNumber.j && posJ > path[index].j) {
				minLeftLength += 2;
			}

			if (posJ < targetNumber.j && posJ < path[index].j) {
				minLeftLength += 2;
			}
		}

		if (posJ > targetNumber.j) {
			minLeftLength = minLeftLength + (posJ - targetNumber.j);
		}

		if (posJ < targetNumber.j) {
			minLeftLength = minLeftLength + (targetNumber.j - posJ);
		}

		if (posJ == targetNumber.j) {
			if (posI > targetNumber.i && posI > path[index].i) {
				minLeftLength += 2;
			}

			if (posI < targetNumber.i && posI < path[index].i) {
				minLeftLength += 2;
			}
		}

		// Jeżeli minimalna długość jaką można wyznaczyć jest większa od
		// pozostałych kroków do wyznaczenia zwróć fałsz
		if (leftLength - 1 < minLeftLength) {
			// System.out.println("6");
			return false;
		}

		if (checkIfBlockSomeNumbers(path, index, targetNumber, posI, posJ)) {
			return false;
		}

		return true;
	}

	// Metoda sprawdzająca czy liczby występujące obok pola są blokowane,
	// pierwszy etap szuka liczb, jeżeli znajdzie liczbe to spwradza czy jest
	// blokowana
	private boolean checkIfBlockSomeNumbers(Field[] path, int index,
			LaNumber targetNumber, int posI, int posJ) {

		Field field = area[posI][posJ];

		path[index + 1] = field;

		if (posI - 1 >= 0 && area[posI - 1][posJ].number != null
				&& area[posI - 1][posJ].val != SELECTED
				&& area[posI - 1][posJ] != path[0]
				&& area[posI - 1][posJ].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI - 1][posJ], path, index + 1)) {

				return true;
			}
		}

		if (posI + 1 < this.y && area[posI + 1][posJ].number != null
				&& area[posI + 1][posJ].val != SELECTED
				&& area[posI + 1][posJ] != path[0]
				&& area[posI + 1][posJ].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI + 1][posJ], path, index + 1)) {

				return true;
			}
		}

		if (posJ - 1 >= 0 && area[posI][posJ - 1].number != null
				&& area[posI][posJ - 1].val != SELECTED
				&& area[posI][posJ - 1] != path[0]
				&& area[posI][posJ - 1].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI][posJ - 1], path, index + 1)) {

				return true;
			}
		}

		if (posJ + 1 < this.x && area[posI][posJ + 1].number != null
				&& area[posI][posJ + 1].val != SELECTED
				&& area[posI][posJ + 1] != path[0]
				&& area[posI][posJ + 1].number != targetNumber) {

			if (checkIfNumberIsBlock(area[posI][posJ + 1], path, index + 1)) {

				return true;
			}
		}

		if (isAdvance) {
			selectPath(path, index + 1);
			// zaznaczenie drugiel liczby poto aby nie brać jej pod uwagę
			// podczas sprawdzania czy inne liczby są blokowane
			area[targetNumber.i][targetNumber.j].val = SELECTED;
			boolean turnOn = false;
			if (this.isDetermitingCommonFields) {
				this.isDetermitingCommonFields = false;
				turnOn = true;
			}

			boolean isBlock = checkIfNumbersAreBlockAdvance(path[0].number,
					targetNumber);
			if (turnOn) {
				this.isDetermitingCommonFields = true;
			}

			area[targetNumber.i][targetNumber.j].val = ABSENCE;
			unselectPath(path, index + 1);

			return isBlock;
		}

		return false;
	}

	// Metoda sprawdzająca czy liczby są blokowane wersja zaawansowana
	private boolean checkIfNumbersAreBlockAdvance(LaNumber sourceNumber,
			LaNumber targetNumber) {

		// System.out.println(" --- START ---");
		if (sourceNumber.numbersInScope == null) {
			sourceNumber.numbersInScope = new ArrayList<LaNumber>();
			searchNumbersInScope(sourceNumber);
		}

		// na czas sprawdzania czy jakaś liczba jest blokowana wyłączamy tryb
		// zaawansowany po to aby nie zapętlić algorytmu
		this.isAdvance = false;

		for (LaNumber number : sourceNumber.numbersInScope) {

			if (number == targetNumber
					|| area[number.i][number.j].val == SELECTED)
				continue;

			boolean isOk = false;
			for (LaNumber secNumber : number.secondNumbers) {
				this.onlyOnePath = true;
				List<Field[]> paths = findPathsToNumber(number, secNumber);
				this.onlyOnePath = false;
				if (paths.size() > 0) {
					isOk = true;
					break;
				}
			}

			// Jeżeli liczba jest blokowana to zwróc true;
			if (!isOk) {

				this.isAdvance = true;
				return true;
			}
		}
		this.isAdvance = true;
		return false;
	}

	private void searchNumbersInScope(LaNumber laNumber) {

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
				startJ = 0;
			}

			for (int j = startJ; j <= laNumber.j + k && j < this.x; j++) {

				Field field = this.area[i][j];

				if (field.number != null && field.val != SELECTED
						&& field.number != laNumber) {

					if (laNumber.numbersInScope == null) {
						laNumber.numbersInScope = new ArrayList<LaNumber>();
					}

					laNumber.numbersInScope.add(field.number);
				}
			}
		}
	}

	// Sprawdza czy liczba jest blokowana
	private boolean checkIfNumberIsBlock(Field field, Field[] path, int index) {

		int k = 0;

		// if (field.number != null && field.number.value == 2) {
		// return false;
		// }

		if (field.i - 1 < 0
				|| (area[field.i - 1][field.j].val == SELECTED && (area[field.i - 1][field.j].belongToNumber != field.number && area[field.i - 1][field.j].belongToNumberSecond != field.number))
				|| (area[field.i - 1][field.j].number != null && area[field.i - 1][field.j].number.value != field.number.value)
				|| checkIfBelognsToPath(path, index, area[field.i - 1][field.j])) {

			k++;
		}

		if (field.i + 1 >= this.y
				|| (area[field.i + 1][field.j].val == SELECTED && (area[field.i + 1][field.j].belongToNumber != field.number && area[field.i + 1][field.j].belongToNumberSecond != field.number))
				|| (area[field.i + 1][field.j].number != null && area[field.i + 1][field.j].number.value != field.number.value)
				|| checkIfBelognsToPath(path, index, area[field.i + 1][field.j])) {

			k++;
		}

		if (field.j - 1 < 0
				|| (area[field.i][field.j - 1].val == SELECTED && (area[field.i][field.j - 1].belongToNumber != field.number && area[field.i][field.j - 1].belongToNumberSecond != field.number))
				|| (area[field.i][field.j - 1].number != null && area[field.i][field.j - 1].number.value != field.number.value)
				|| checkIfBelognsToPath(path, index, area[field.i][field.j - 1])) {

			k++;
		}

		if (field.j + 1 >= this.x
				|| (area[field.i][field.j + 1].val == SELECTED && (area[field.i][field.j + 1].belongToNumber != field.number && area[field.i][field.j + 1].belongToNumberSecond != field.number))
				|| (area[field.i][field.j + 1].number != null && area[field.i][field.j + 1].number.value != field.number.value)
				|| checkIfBelognsToPath(path, index, area[field.i][field.j + 1])) {

			k++;
		}

		if (k == 4)
			return true;

		return false;
	}

	private void selectPath(Field[] path, int index) {

		for (int i = 0; i <= index; i++) {
			path[i].val = SELECTED;
		}
	}

	private void unselectPath(Field[] path, int index) {
		for (int i = 0; i <= index; i++) {
			if (path[i].belongToNumber == null)
				path[i].val = ABSENCE;
		}
	}

	// Metoda sprawdzająca czy pole należy do aktualnie wyznaczonej ścieżki
	private boolean checkIfBelognsToPath(Field[] path, int index, Field field) {

		for (int i = 0; i <= index; i++) {
			if (path[i] == field)
				return true;
		}
		return false;
	}

	// Szukanie liczby z którymi możę się polączyć dana liczba
	private void searchSecondNumbers(LaNumber laNumber) {

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
						&& (field.number.secondNumbers == null || field.number.secondNumbers
								.size() > 1)) {

					if (laNumber.secondNumbers == null) {
						laNumber.secondNumbers = new ArrayList<LaNumber>();
					}

					laNumber.secondNumbers.add(field.number);
				}
			}
		}

	}

	public boolean checkSolve() {
		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				Field field = this.area[i][j];
				if (field.number == null)
					continue;

				if (field.val != SELECTED)
					return false;

				if (field.number.value == 1) {
					continue;
				}

				if (field.prev == null && field.next == null)
					return false;

				int length = 0;
				if (field.prev != null) {
					length = checkPrevFields(field.prev, 1);

				} else {
					length = checkNextFields(field.next, 1);
				}

				if (length != field.number.value)
					return false;
			}
		}

		return true;
	}

	// Klasa reprezentujaca pole (kratke do zanzaczenia)
	class Field {
		public Byte val = -1; // -1: BRAK, 1: ZAZNACZONE
		public LaNumber number; // Liczba

		public int i, j; // pozycja pola

		public Field next, prev;
		public LaNumber belongToNumber;
		public LaNumber belongToNumberSecond;

		// public boolean isCommon = false;

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
			this.belongToNumber = selectedNumber;

		}

		@Override
		public String toString() {
			String num = " number: ";

			if (number != null) {
				num = num + number.value + " " + number.secondNumbers;
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

			if (belongToNumber != null) {
				b = b + belongToNumber.value;
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

		public List<LaNumber> secondNumbers; // druga liczba z która jest
												// połączona

		public List<LaNumber> numbersInScope; // lista liczb należących do
												// zasięgu liczby

		// Ścieżka do drugiej liczby
		// public LinkedList<Field> path = new LinkedList<Field>();

		public boolean haveCommon = false;

		public LaNumber(byte value, int i, int j) {
			this.value = value;
			this.i = i;
			this.j = j;
		}

		public LaNumber(LaNumber number) {
			this.value = number.value;
			this.i = number.i;
			this.j = number.j;
			this.secondNumbers = number.secondNumbers;
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
			field.belongToNumber = null;
			field.next = null;
			field.prev = null;
			this.secondNumbers = null;
		}
	}

	public void clearNextFields(Field next) {
		next.val = ABSENCE;
		next.belongToNumber = null;

		if (next.next != null) {
			clearNextFields(next.next);
		} else {
			if (next.number != null) {
				next.number.secondNumbers = null;
			}
		}

		next.next = null;
		next.prev = null;
	}

	public void clearPrevFields(Field prev) {
		prev.val = ABSENCE;
		prev.belongToNumber = null;

		if (prev.prev != null) {
			clearPrevFields(prev.prev);
		} else {
			if (prev.number != null) {
				prev.number.secondNumbers = null;
			}
		}
		prev.next = null;
		prev.prev = null;
	}

	private int checkNextFields(Field next, int length) {

		if (next.val != SELECTED) {
			return -1;
		}

		if (next.next != null) {
			return checkNextFields(next.next, length + 1);
		} else {
			return length + 1;
		}
	}

	private int checkPrevFields(Field prev, int length) {
		if (prev.val != SELECTED)
			return -1;

		if (prev.prev != null) {
			return checkPrevFields(prev.prev, length + 1);
		} else {
			return length + 1;
		}
	}

	private void clearArea() {
		this.numbers = new HashMap<Byte, ArrayList<LaNumber>>();

		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				Field field = this.area[i][j];

				field.val = ABSENCE;
				field.belongToNumber = null;
				field.belongToNumberSecond = null;
				field.prev = null;
				field.next = null;

				if (field.number != null) {
					LaNumber laNumber = field.number;

					laNumber.haveCommon = false;
					laNumber.numbersInScope = null;
					laNumber.secondNumbers = null;

					ArrayList<LaNumber> listNumber = numbers
							.get(laNumber.value);
					if (listNumber == null) {
						listNumber = new ArrayList<LaNumber>();
						numbers.put(laNumber.value, listNumber);
					}

					listNumber.add(laNumber);
				}
			}
		}
	}
}
