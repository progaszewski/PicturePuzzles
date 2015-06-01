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

	// metoda wyznaczająca narysowane odcinki
	private void determiningOfLengths(ListOfNumber numberList,
			boolean isVertical, int i) {

		numberList.lengths = new ArrayList<Length>();
		int n;

		if (isVertical) {
			n = this.y;
		} else {
			n = this.x;
		}

		int s = 0, e = 0;
		int lastType = ABSENCE;
		PaNumber lastPaNumber = null;
		for (int j = 0; j < n; j++) {

			Field field;
			PaNumber belongsToNumber;
			if (isVertical) {
				field = this.area[j][i];
				belongsToNumber = field.belongsToVertical;
			} else {
				field = this.area[i][j];
				belongsToNumber = field.belongsToHorizontal;
			}

			if (field.val == ABSENCE) {
				if (lastType != field.val) {
					Length l = new Length(s, e - 1, lastType);
					if (lastPaNumber != null) {
						l.listOfNumbersToBelong = new ArrayList<PaNumber>();
						l.listOfNumbersToBelong.add(lastPaNumber);
					}
					numberList.lengths.add(l);
					lastPaNumber = null;
					lastType = field.val;
				}
				lastType = field.val;
				s = e = j + 1;
				continue;
			}
			if (field.val == SELECTED) {
				if (lastType == EMPTY) {

					Length l = new Length(s, e - 1, lastType);
					if (lastPaNumber != null) {
						l.listOfNumbersToBelong = new ArrayList<PaNumber>();
						l.listOfNumbersToBelong.add(lastPaNumber);
					}
					numberList.lengths.add(l);
					lastPaNumber = belongsToNumber;
					lastType = field.val;

					s = j;
					e = j + 1;
					continue;
				}
				if ((lastType == SELECTED && lastPaNumber != belongsToNumber)) {
					Length l = new Length(s, e - 1, lastType);
					if (lastPaNumber != null) {
						l.listOfNumbersToBelong = new ArrayList<PaNumber>();
						l.listOfNumbersToBelong.add(lastPaNumber);
					}
					numberList.lengths.add(l);
					lastPaNumber = belongsToNumber;
					lastType = field.val;

					s = j;
					e = j + 1;
					continue;
				}

				lastPaNumber = belongsToNumber;
				lastType = field.val;
				e = j + 1;
				continue;
			}

			if (field.val == EMPTY) {
				if (lastType == SELECTED) {

					Length l = new Length(s, e - 1, lastType);
					if (lastPaNumber != null) {
						l.listOfNumbersToBelong = new ArrayList<PaNumber>();
						l.listOfNumbersToBelong.add(lastPaNumber);
					}
					numberList.lengths.add(l);
					lastPaNumber = belongsToNumber;
					lastType = field.val;

					s = j;
					e = j + 1;
					continue;
				}
				lastPaNumber = belongsToNumber;
				lastType = field.val;
				e = j + 1;
				continue;
			}
		}

		if (lastType == SELECTED || lastType == EMPTY) {
			Length l = new Length(s, e - 1, lastType);
			if (lastPaNumber != null) {
				l.listOfNumbersToBelong = new ArrayList<PaNumber>();
				l.listOfNumbersToBelong.add(lastPaNumber);
			}
			numberList.lengths.add(l);
		}

		// DEBUG
		for (Length l : numberList.lengths) {
			String open, close;
			if (l.type == SELECTED) {
				open = "<";
				close = ">";
			} else {
				open = "(";
				close = ")";
			}
			String belongsTo;
			if (l.listOfNumbersToBelong == null) {
				belongsTo = " nalezy do: NULL";
			} else {
				belongsTo = " nelezy do: " + l.listOfNumbersToBelong.get(0).val;
			}

			System.out.println(i + ": " + open + l.s + ", " + l.e + close
					+ belongsTo);
		}
		// DEBUG
		System.out.println();

	}

	// I etap - malowanie pewnych pol
	private void firstStep(ListOfNumber numberList, boolean isVertical, int i) {

		int n, nPrim;
		List<ListOfNumber> listsOfNumbers;
		if (isVertical) {
			n = this.y;
			nPrim = this.x;
			listsOfNumbers = this.horizontalListsOfNumbers;
		} else {
			n = this.x;
			nPrim = this.y;
			listsOfNumbers = this.verticalListsOfNumbers;
		}

		System.out.println(i + ":");
		int startPosition = 0;
		for (PaNumber paNumber : numberList.numbers) {

			// wyznazcenie N
			int N = n
					- (numberList.sumOfNumbers + numberList.numbers.size() - 1 - paNumber.val);
			// 2 * val > N
			if (2 * paNumber.val > N) {
				int diff = N - paNumber.val;
				for (int j = startPosition + diff; j < startPosition
						+ paNumber.val; j++) {
					if (isVertical) {
						this.area[j][i].val = 1;
						this.area[j][i].belongsToVertical = paNumber;
					} else {
						this.area[i][j].val = 1;
						this.area[i][j].belongsToHorizontal = paNumber;
					}

					// jezeli dzialamy na lewej (pionowej) lub górnej (poziomej)
					// krawędzi
					if (i == 0) {
						// pionowe -> poziome lub poziome -> pionowe
						ListOfNumber numberListPrim = listsOfNumbers.get(j);
						PaNumber firstNumber = numberListPrim.numbers.get(0);
						for (int k = 1; k < firstNumber.val; k++) {

							if (isVertical) {
								this.area[j][k].val = 1;
							} else {
								this.area[k][j].val = 1;
							}

						}
						if (isVertical) {
							this.area[j][firstNumber.val].val = 0;
						} else {
							this.area[firstNumber.val][j].val = 0;
						}

						firstNumber.enable = false;
						numberListPrim.otherNumbers--;

					}

					// jezeli dzialamy na prawej (pionowej) lub dolnej
					// (poziomej) krawedzi
					if (i == nPrim - 1) {
						// pionowe -> poziome lub poziome -> pionowe
						ListOfNumber numberListPrim = listsOfNumbers.get(j);
						PaNumber lastNumber = numberListPrim.numbers
								.get(numberListPrim.numbers.size() - 1);

						for (int k = nPrim - 2; k >= nPrim - lastNumber.val; k--) {
							if (isVertical) {
								this.area[j][k].val = 1;
							} else {
								this.area[k][j].val = 1;
							}

						}
						if (isVertical) {
							this.area[j][nPrim - lastNumber.val - 1].val = 0;
						} else {
							this.area[nPrim - lastNumber.val - 1][j].val = 0;
						}

						lastNumber.enable = false;
						numberListPrim.otherNumbers--;
					}
				}
			}

			// Wyznaczanie zasięgu liczby
			paNumber.scope[0] = startPosition;
			paNumber.scope[1] = n
					- (numberList.sumOfNumbers - startPosition - paNumber.val + numberList.numbers
							.size());

			System.out.print(paNumber.val + ": [" + paNumber.scope[0] + ","
					+ paNumber.scope[1] + "] ");
			startPosition += paNumber.val + 1;
		}
		System.out.println("\n");
	}

	public boolean solvePuzzle() {
		int i = 0;
		// I - szukanie pol, ktore musza zostac zamalowane.
		// Liczby pionowe:
		for (ListOfNumber verticalList : this.verticalListsOfNumbers) {
			firstStep(verticalList, true, i++);
		}

		i = 0;
		// Liczby poziome:
		for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {
			firstStep(horizontalList, false, i++);
		}

		// II - laczenie odcinkow "czarnych" oraz wyznaczanie odcinkow "szarych"
		// Linie pionowe
		i = 0;
		for (ListOfNumber verticalList : this.verticalListsOfNumbers) {

			determiningOfLengths(verticalList, true, i++);
		}

		// Linie poziome
		i = 0;
		for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {

			determiningOfLengths(horizontalList, false, i++);
		}

		return false;
	}

	// Klasa reprezetujaca pole / kratke na planszy
	public class Field {
		public byte val = ABSENCE; // mozliwe wartosci: -1 -- Brak koloru (pole
									// niepokolorowane), 0 -- Puste pole
									// (krzyzyk), 1 -- Pole zaznaczone
									// (pokolorowane)
		public PaNumber belongsToVertical;
		public PaNumber belongsToHorizontal;

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
		public List<Length> lengths;

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
		public int[] scope = new int[2]; // Okreslenie zakresu mozliwego
											// wystapienia liczby

		public PaNumber() {

		}

		public PaNumber(byte val) {
			this.val = val;
		}
	}

	// Odcinek
	public class Length {

		public int s; // Start
		public int e; // Koniec (end)
		public int type; // typ odcinka: 1 - "SELECTED", 0 - "EMPTY"
		public List<PaNumber> listOfNumbersToBelong; // lista numerów, które
														// należą do odcinka

		public Length() {

		}

		public Length(int s, int e, int type) {
			this.s = s;
			this.e = e;
			this.type = type;
		}
	}
}
