package pl.picture.puzzles.picapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PicAPixArea {

	public static final byte EMPTY = 0; // Pole oznaczone jako puste
	public static final byte SELECTED = 1; // Pole pokolorowane
	public static final byte ABSENCE = -1; // Pole nie pokolorowane

	public Field[][] area; // tablica reprezentujaca plansze
	public int x = 0, y = 0; // wymiray planszy
	public List<ListOfNumber> verticalListsOfNumbers; // Lista liczb pionowych
	public List<ListOfNumber> horizontalListsOfNumbers; // Lista liczb poziomych

	public int maxVerticalNumbers, maxHorizontalNumbers;

	public PicAPixArea(File f) {
		init(f);
	}

	private void init(File f) {

		this.verticalListsOfNumbers = new ArrayList<ListOfNumber>();
		this.horizontalListsOfNumbers = new ArrayList<ListOfNumber>();

		try {
			Scanner readFile = new Scanner(f);

			String verticalNumbers = readFile.nextLine();
			String horizontalNumbers = readFile.nextLine();

			// System.out.println(verticalNumbers);
			// System.out.println(horizontalNumbers);

			readFile.close();

			String[] listOfVerticalNumbers = verticalNumbers.split("T");
			String[] listOFHorizontalNumbers = horizontalNumbers.split("T");

			this.y = listOFHorizontalNumbers.length;
			this.x = listOfVerticalNumbers.length;

			// Tworzenie planszy / siatki
			this.area = new Field[y][x];
			for (int i = 0; i < y; i++) {
				for (int j = 0; j < x; j++) {
					this.area[i][j] = new Field();
				}
			}

			// Tworzenie listy liczb pionowych
			for (int i = 0; i < x; i++) {
				String[] numbers = listOfVerticalNumbers[i].split(",");

				// System.out.println(listOfVerticalNumbers[i]);
				if (this.maxVerticalNumbers < numbers.length)
					this.maxVerticalNumbers = numbers.length;

				List<PaNumber> paNumber = new ArrayList<PaNumber>();

				int sumOfNumbers = 0;
				for (int j = 0; j < numbers.length; j++) {
					paNumber.add(new PaNumber(Byte.parseByte(numbers[j])));
					sumOfNumbers += Byte.parseByte(numbers[j]);
				}

				this.verticalListsOfNumbers.add(new ListOfNumber(paNumber,
						(byte) numbers.length, sumOfNumbers));

			}

			// Tworzenie listy liczb poziomych
			for (int i = 0; i < y; i++) {
				String[] numbers = listOFHorizontalNumbers[i].split(",");

				if (this.maxHorizontalNumbers < numbers.length)
					this.maxHorizontalNumbers = numbers.length;

				List<PaNumber> paNumber = new ArrayList<PaNumber>();
				int sumOfNumbers = 0;
				for (int j = 0; j < numbers.length; j++) {
					paNumber.add(new PaNumber(Byte.parseByte(numbers[j])));
					sumOfNumbers += Byte.parseByte(numbers[j]);
				}

				this.horizontalListsOfNumbers.add(new ListOfNumber(paNumber,
						(byte) numbers.length, sumOfNumbers));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean solvePuzzle() {
		int i = 0;
		// I - szukanie pol, ktore musza zostac zamalowane.
		// Liczby pionowe:
		for (ListOfNumber verticalList : this.verticalListsOfNumbers) {
			int startPosition = 0;
			for (PaNumber paNumber : verticalList.numbers) {

				// wyznazcenie N
				int N = this.y
						- (verticalList.sumOfNumbers
								+ verticalList.numbers.size() - 1 - paNumber.val);
				// 2 * val > N
				if (2 * paNumber.val > N) {
					int diff = N - paNumber.val;
					for (int j = startPosition + diff; j < startPosition
							+ paNumber.val; j++) {
						this.area[j][i].val = 1;
					}
				}
				startPosition += paNumber.val + 1;
			}
			i++;
		}

		i = 0;
		// Liczby poziome:
		for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {
			int startPosition = 0;
			for (PaNumber paNumber : horizontalList.numbers) {

				// wyznazcenie N
				int N = this.x
						- (horizontalList.sumOfNumbers
								+ horizontalList.numbers.size() - 1 - paNumber.val);
				// 2 * val > N
				if (2 * paNumber.val > N) {
					int diff = N - paNumber.val;
					for (int j = startPosition + diff; j < startPosition
							+ paNumber.val; j++) {
						this.area[i][j].val = 1;
					}
				}
				startPosition += paNumber.val + 1;
			}
			i++;
		}

		return false;
	}

	// Klasa reprezetujaca pole / kratke na planszy
	public class Field {
		public byte val = ABSENCE; // mozliwe wartosci: -1 -- Brak koloru (pole
									// niepokolorowane), 0 -- Puste pole
									// (krzyzyk), 1 -- Pole zaznaczone
									// (pokolorowane)

		public Field() {

		}

		public Field(Field field) {
			this.val = field.val;
		}
	}

	// Klasa reprezentujaca liste numerow dla kolumny lub rzedu
	public class ListOfNumber {
		public List<PaNumber> numbers;
		public byte otherNumbers = -1; // ile pozostalo numerow do wyznaczenia
										// (pokolorowania)
		public int sumOfNumbers = -1; // suma wszystkich numerow

		public ListOfNumber() {

		}

		public ListOfNumber(List<PaNumber> numbers, byte otherNumbers,
				int sumOfNumbers) {
			this.numbers = numbers;
			this.otherNumbers = otherNumbers;
			this.sumOfNumbers = sumOfNumbers;
		}

	}

	// Klasa reprezentujaca liczbe, ktora okrasla ile kratek zakolorowac
	public class PaNumber {
		public byte val = 0; // Wartosc liczby
		public boolean enable = true; // Czy liczba aktywna

		public PaNumber() {

		}

		public PaNumber(byte val) {
			this.val = val;
		}
	}
}
