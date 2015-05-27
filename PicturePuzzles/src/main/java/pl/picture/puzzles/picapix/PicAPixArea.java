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
						this.area[j][i].belongsToVertical = paNumber;

						// jezeli dzialamy na pierwszej linii pionowej
						if (i == 0) {
							ListOfNumber horizontalList = this.horizontalListsOfNumbers
									.get(j);
							PaNumber firstNumber = horizontalList.numbers
									.get(0);
							for (int k = 1; k < firstNumber.val; k++) {
								this.area[j][k].val = 1;
							}
							this.area[j][firstNumber.val].val = 0;
							firstNumber.enable = false;
							horizontalList.otherNumbers--;

						}

						// jezeli dzialamy na ostatniej linii pionewej
						if (i == this.x - 1) {
							ListOfNumber horizontalList = this.horizontalListsOfNumbers
									.get(j);
							PaNumber lastNumber = horizontalList.numbers
									.get(horizontalList.numbers.size() - 1);

							for (int k = this.x - 2; k >= this.x
									- lastNumber.val; k--) {
								this.area[j][k].val = 1;
							}
							this.area[j][this.x - lastNumber.val - 1].val = 0;
							lastNumber.enable = false;
							horizontalList.otherNumbers--;
						}
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
						this.area[i][j].belongsToHorizontal = paNumber;

						// jezeli dzialamy na pierwszej linii poziomej
						if (i == 0) {
							ListOfNumber verticalList = this.verticalListsOfNumbers
									.get(j);
							PaNumber firstNumber = verticalList.numbers.get(0);
							for (int k = 1; k < firstNumber.val; k++) {
								this.area[k][j].val = 1;
							}
							this.area[firstNumber.val][j].val = 0;
							firstNumber.enable = false;
							verticalList.otherNumbers--;

						}

						// jezeli dzialamy na ostatniej linii poziomej
						if (i == this.y - 1) {
							ListOfNumber verticalList = this.verticalListsOfNumbers
									.get(j);
							PaNumber lastNumber = verticalList.numbers
									.get(verticalList.numbers.size() - 1);

							for (int k = this.y - 2; k >= this.y
									- lastNumber.val; k--) {
								this.area[k][j].val = 1;
							}
							this.area[this.y - lastNumber.val - 1][j].val = 0;
							lastNumber.enable = false;
							verticalList.otherNumbers--;
						}
					}
				}
				startPosition += paNumber.val + 1;
			}
			i++;
		}

		// II - laczenie odcinkow "czarnych" oraz wyznaczanie odcinkow "szarych"
		// Linie pionowe
		i = 0;
		for (ListOfNumber verticalList : this.verticalListsOfNumbers) {
			verticalList.lengths = new ArrayList<Length>();

			// Wyznaczenie odcinkow
			int s = 0, e = 0;
			int lastType = ABSENCE;
			PaNumber lastPaNumber = null;
			for (int j = 0; j < this.y; j++) {
				Field field = this.area[j][i];
				if (field.val == ABSENCE) {
					if (lastType != field.val) {
						Length l = new Length(s, e - 1, lastType);
						if (lastPaNumber != null) {
							l.listOfNumbersToBelong = new ArrayList<PaNumber>();
							l.listOfNumbersToBelong.add(lastPaNumber);
						}
						verticalList.lengths.add(l);
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
						verticalList.lengths.add(l);
						lastPaNumber = field.belongsToVertical;
						lastType = field.val;

						s = e = j + 1;
						continue;
					}
					if (lastType == SELECTED
							&& lastPaNumber != field.belongsToVertical) {
						Length l = new Length(s, e - 1, lastType);
						if (lastPaNumber != null) {
							l.listOfNumbersToBelong = new ArrayList<PaNumber>();
							l.listOfNumbersToBelong.add(lastPaNumber);
						}
						verticalList.lengths.add(l);
						lastPaNumber = field.belongsToVertical;
						lastType = field.val;

						s = j;
						e = j + 1;
						continue;
					}

					lastPaNumber = field.belongsToVertical;
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
						verticalList.lengths.add(l);
						lastPaNumber = field.belongsToVertical;
						lastType = field.val;

						s = e = j + 1;
						continue;
					}
					lastPaNumber = field.belongsToVertical;
					lastType = field.val;
					e = j + 1;
					continue;
				}
			}

			// DEBUG
			// for (Length l : verticalList.lengths) {
			// String open, close;
			// if (l.type == SELECTED) {
			// open = "<";
			// close = ">";
			// } else {
			// open = "(";
			// close = ")";
			// }
			// String belongsTo;
			// if (l.listOfNumbersToBelong == null) {
			// belongsTo = " nalezy do: NULL";
			// } else {
			// belongsTo = " nelezu do: "
			// + l.listOfNumbersToBelong.get(0).val;
			// }
			//
			// System.out.println(i + ": " + open + l.s + ", " + l.e + close
			// + belongsTo);
			// }
			// System.out.println();
			i++;
		}

		// Linie poziome
		i = 0;
		for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {
			horizontalList.lengths = new ArrayList<Length>();

			// Wyznaczenie odcinkow
			int s = 0, e = 0;
			int lastType = ABSENCE;
			PaNumber lastPaNumber = null;
			for (int j = 0; j < this.x; j++) {
				Field field = this.area[i][j];
				if (field.val == ABSENCE) {
					if (lastType != field.val) {
						Length l = new Length(s, e - 1, lastType);
						if (lastPaNumber != null) {
							l.listOfNumbersToBelong = new ArrayList<PaNumber>();
							l.listOfNumbersToBelong.add(lastPaNumber);
						}
						horizontalList.lengths.add(l);
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
						horizontalList.lengths.add(l);
						lastPaNumber = field.belongsToHorizontal;
						lastType = field.val;

						s = e = j + 1;
						continue;
					}
					if (lastType == SELECTED
							&& lastPaNumber != field.belongsToHorizontal) {
						Length l = new Length(s, e - 1, lastType);
						if (lastPaNumber != null) {
							l.listOfNumbersToBelong = new ArrayList<PaNumber>();
							l.listOfNumbersToBelong.add(lastPaNumber);
						}
						horizontalList.lengths.add(l);
						lastPaNumber = field.belongsToHorizontal;
						lastType = field.val;

						s = j;
						e = j + 1;
						continue;
					}

					lastPaNumber = field.belongsToHorizontal;
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
						horizontalList.lengths.add(l);
						lastPaNumber = field.belongsToHorizontal;
						lastType = field.val;

						s = e = j + 1;
						continue;
					}
					lastPaNumber = field.belongsToHorizontal;
					lastType = field.val;
					e = j + 1;
					continue;
				}
			}

			// DEBUG
			for (Length l : horizontalList.lengths) {
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
					belongsTo = " nelezy do: "
							+ l.listOfNumbersToBelong.get(0).val;
				}

				System.out.println(i + ": " + open + l.s + ", " + l.e + close
						+ belongsTo);
			}
			System.out.println();
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
