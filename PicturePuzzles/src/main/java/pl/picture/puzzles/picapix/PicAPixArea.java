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

			// uzupelnijLamiglowke();
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
					if (l.listOfNumbersToBelong.size() == 1
							&& (l.e - l.s + 1) == l.listOfNumbersToBelong
									.get(0).val) {
						l.isComplete = true;
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
						for (int k = 0; k < firstNumber.val; k++) {

							if (isVertical) {
								this.area[j][k].val = 1;
								this.area[j][k].belongsToHorizontal = firstNumber;
							} else {
								this.area[k][j].val = 1;
								this.area[k][j].belongsToVertical = firstNumber;
							}

						}
						// Kolorownie natępnej kratki na "czaro"
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

						for (int k = nPrim - 1; k >= nPrim - lastNumber.val; k--) {

							if (isVertical) {
								this.area[j][k].val = 1;
								this.area[j][k].belongsToHorizontal = lastNumber;
							} else {
								this.area[k][j].val = 1;
								this.area[k][j].belongsToVertical = lastNumber;
							}

						}
						// Kolorownie natępnej kratki na "czaro"
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

	private void actionOnLengths(ListOfNumber numberList, boolean isVertical,
			int i) {

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

	// Sprawdzanie czy łamigłówka została poprawnie rozwiązana
	public boolean checkPuzzle() {
		// wypiszKod();

		int length = 0; // długość odcinka
		int index = 0; // wskaźnik na liczbę określającą długość odcinka
		// Sprawdzanie czy zgadzaja sie numery pionowe
		for (int j = 0; j < this.x; j++) {
			ListOfNumber verticalList = this.verticalListsOfNumbers.get(j);
			// Jeżeli lista numerów zawiera numer o wartości zero sprawdź czy
			// żadne pole nie jest pokolorowane na czarno
			if (verticalList.numbers.get(0).val == 0) {
				for (int i = 0; i < this.y; i++) {
					if (this.area[i][j].val == SELECTED)
						return false;
				}
			} else {
				for (int i = 0; i < this.y; i++) {
					// Jezeli pole nie jest pokolorowane na czarno sprawdź czy
					// wyliczona długość odcinka jest równa zeru, jeżeli jest to
					// skocz do następnej iteracji w p.p. sprawdź czy długość
					// wyznaczonego odcinka zgadza się z aktualnie sprawdzanym
					// numerem
					if (this.area[i][j].val != SELECTED) {
						if (length == 0)
							continue;

						if (length != verticalList.numbers.get(index++).val) {
							System.out.println("length = " + length
									+ ", length != verticalList.numbers.get("
									+ (index - 1) + ").val - pion");
							return false;
						}
						length = 0;

					} else {
						length++;
					}

				}
				if (length > 0) {
					index++;
				}
				// Jeżeli wartość indeksu jest inna niż ilość liczb pionowych
				// lub ostatni odcinek jest większy od 0 i jego długość nie
				// odpowiada wartości ostatniej liczby zwróć fałsz
				if (index != verticalList.numbers.size()
						|| (length > 0 && length != verticalList.numbers
								.get(index - 1).val)) {
					return false;
				}

			}
			length = 0;
			index = 0;
		}
		// Sprawdzanie czy zgadzaja sie numery poziome
		for (int i = 0; i < this.y; i++) {
			ListOfNumber horizontalList = this.horizontalListsOfNumbers.get(i);
			// Jeżeli lista numerów zawiera numer o wartości zero sprawdź czy
			// żadne pole nie jest pokolorowane na czarno
			if (horizontalList.numbers.get(0).val == 0) {
				for (int j = 0; j < this.x; j++) {
					if (this.area[i][j].val == SELECTED)
						return false;
				}
			} else {
				for (int j = 0; j < this.x; j++) {
					// Jezeli pole nie jest pokolorowane na czarno sprawdź czy
					// wyliczona długość odcinka jest równa zeru, jeżeli jest to
					// skocz do następnej iteracji w p.p. sprawdź czy długość
					// wyznaczonego odcinka zgadza się z aktualnie sprawdzanym
					// numerem
					if (this.area[i][j].val != SELECTED) {
						if (length == 0)
							continue;

						if (length != horizontalList.numbers.get(index++).val) {
							System.out.println("length = " + length
									+ ", length != verticalList.numbers.get("
									+ (index - 1) + ").val - poziom");

							return false;
						}
						length = 0;

					} else {
						length++;
					}

				}

				if (length > 0) {
					index++;
				}
				// Jeżeli wartość indeksu jest inna niż ilość liczb poziomych
				// lub ostatni odcinek jest większy od 0 i jego długość nie
				// odpowiada wartości ostatniej liczby zwróć fałsz
				if (index != horizontalList.numbers.size()
						|| (length > 0 && length != horizontalList.numbers
								.get(index - 1).val)) {
					return false;
				}
			}
			length = 0;
			index = 0;
		}
		return true;
	}

	// debug
	private void wypiszKod() {
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				System.out.print("this.area[" + i + "][" + j + "].val = "
						+ this.area[i][j].val + "; ");
			}
			System.out.println();
		}

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
		public boolean isComplete = false; // czy odcinek kompletny

		public Length() {

		}

		public Length(int s, int e, int type) {
			this.s = s;
			this.e = e;
			this.type = type;
		}
	}

	// debug
	private void uzupelnijLamiglowke() {
		this.area[0][0].val = -1;
		this.area[0][1].val = -1;
		this.area[0][2].val = -1;
		this.area[0][3].val = -1;
		this.area[0][4].val = -1;
		this.area[0][5].val = -1;
		this.area[0][6].val = -1;
		this.area[0][7].val = -1;
		this.area[0][8].val = -1;
		this.area[0][9].val = -1;
		this.area[0][10].val = -1;
		this.area[0][11].val = -1;
		this.area[0][12].val = -1;
		this.area[0][13].val = -1;
		this.area[0][14].val = 1;
		this.area[0][15].val = 1;
		this.area[0][16].val = 1;
		this.area[0][17].val = 1;
		this.area[0][18].val = 1;
		this.area[0][19].val = 1;
		this.area[0][20].val = 1;
		this.area[0][21].val = 1;
		this.area[0][22].val = 1;
		this.area[0][23].val = -1;
		this.area[0][24].val = -1;
		this.area[1][0].val = 1;
		this.area[1][1].val = 1;
		this.area[1][2].val = 1;
		this.area[1][3].val = -1;
		this.area[1][4].val = -1;
		this.area[1][5].val = -1;
		this.area[1][6].val = -1;
		this.area[1][7].val = -1;
		this.area[1][8].val = -1;
		this.area[1][9].val = -1;
		this.area[1][10].val = -1;
		this.area[1][11].val = -1;
		this.area[1][12].val = -1;
		this.area[1][13].val = 1;
		this.area[1][14].val = 1;
		this.area[1][15].val = -1;
		this.area[1][16].val = 1;
		this.area[1][17].val = -1;
		this.area[1][18].val = 1;
		this.area[1][19].val = -1;
		this.area[1][20].val = 1;
		this.area[1][21].val = 1;
		this.area[1][22].val = 1;
		this.area[1][23].val = 1;
		this.area[1][24].val = -1;
		this.area[2][0].val = -1;
		this.area[2][1].val = -1;
		this.area[2][2].val = 1;
		this.area[2][3].val = 1;
		this.area[2][4].val = -1;
		this.area[2][5].val = -1;
		this.area[2][6].val = -1;
		this.area[2][7].val = -1;
		this.area[2][8].val = 1;
		this.area[2][9].val = 1;
		this.area[2][10].val = 1;
		this.area[2][11].val = 1;
		this.area[2][12].val = 1;
		this.area[2][13].val = 1;
		this.area[2][14].val = -1;
		this.area[2][15].val = 1;
		this.area[2][16].val = -1;
		this.area[2][17].val = 1;
		this.area[2][18].val = -1;
		this.area[2][19].val = 1;
		this.area[2][20].val = -1;
		this.area[2][21].val = 1;
		this.area[2][22].val = 1;
		this.area[2][23].val = 1;
		this.area[2][24].val = 1;
		this.area[3][0].val = 1;
		this.area[3][1].val = -1;
		this.area[3][2].val = -1;
		this.area[3][3].val = 1;
		this.area[3][4].val = 1;
		this.area[3][5].val = -1;
		this.area[3][6].val = 1;
		this.area[3][7].val = 1;
		this.area[3][8].val = 1;
		this.area[3][9].val = 1;
		this.area[3][10].val = 1;
		this.area[3][11].val = 1;
		this.area[3][12].val = 1;
		this.area[3][13].val = -1;
		this.area[3][14].val = 1;
		this.area[3][15].val = -1;
		this.area[3][16].val = 1;
		this.area[3][17].val = -1;
		this.area[3][18].val = 1;
		this.area[3][19].val = -1;
		this.area[3][20].val = 1;
		this.area[3][21].val = -1;
		this.area[3][22].val = 1;
		this.area[3][23].val = 1;
		this.area[3][24].val = 1;
		this.area[4][0].val = -1;
		this.area[4][1].val = 1;
		this.area[4][2].val = -1;
		this.area[4][3].val = 1;
		this.area[4][4].val = 1;
		this.area[4][5].val = 1;
		this.area[4][6].val = 1;
		this.area[4][7].val = -1;
		this.area[4][8].val = 1;
		this.area[4][9].val = -1;
		this.area[4][10].val = -1;
		this.area[4][11].val = 1;
		this.area[4][12].val = 1;
		this.area[4][13].val = 1;
		this.area[4][14].val = -1;
		this.area[4][15].val = 1;
		this.area[4][16].val = -1;
		this.area[4][17].val = 1;
		this.area[4][18].val = -1;
		this.area[4][19].val = 1;
		this.area[4][20].val = -1;
		this.area[4][21].val = 1;
		this.area[4][22].val = 1;
		this.area[4][23].val = 1;
		this.area[4][24].val = 1;
		this.area[5][0].val = 1;
		this.area[5][1].val = -1;
		this.area[5][2].val = -1;
		this.area[5][3].val = 1;
		this.area[5][4].val = 1;
		this.area[5][5].val = -1;
		this.area[5][6].val = 1;
		this.area[5][7].val = 1;
		this.area[5][8].val = 1;
		this.area[5][9].val = -1;
		this.area[5][10].val = -1;
		this.area[5][11].val = 1;
		this.area[5][12].val = 1;
		this.area[5][13].val = 1;
		this.area[5][14].val = 1;
		this.area[5][15].val = -1;
		this.area[5][16].val = 1;
		this.area[5][17].val = -1;
		this.area[5][18].val = 1;
		this.area[5][19].val = -1;
		this.area[5][20].val = 1;
		this.area[5][21].val = 1;
		this.area[5][22].val = 1;
		this.area[5][23].val = 1;
		this.area[5][24].val = 1;
		this.area[6][0].val = -1;
		this.area[6][1].val = -1;
		this.area[6][2].val = 1;
		this.area[6][3].val = 1;
		this.area[6][4].val = -1;
		this.area[6][5].val = -1;
		this.area[6][6].val = -1;
		this.area[6][7].val = -1;
		this.area[6][8].val = 1;
		this.area[6][9].val = -1;
		this.area[6][10].val = -1;
		this.area[6][11].val = 1;
		this.area[6][12].val = 1;
		this.area[6][13].val = -1;
		this.area[6][14].val = 1;
		this.area[6][15].val = 1;
		this.area[6][16].val = 1;
		this.area[6][17].val = 1;
		this.area[6][18].val = 1;
		this.area[6][19].val = 1;
		this.area[6][20].val = 1;
		this.area[6][21].val = 1;
		this.area[6][22].val = 1;
		this.area[6][23].val = 1;
		this.area[6][24].val = -1;
		this.area[7][0].val = 1;
		this.area[7][1].val = 1;
		this.area[7][2].val = 1;
		this.area[7][3].val = -1;
		this.area[7][4].val = -1;
		this.area[7][5].val = -1;
		this.area[7][6].val = -1;
		this.area[7][7].val = -1;
		this.area[7][8].val = 1;
		this.area[7][9].val = 1;
		this.area[7][10].val = 1;
		this.area[7][11].val = 1;
		this.area[7][12].val = 1;
		this.area[7][13].val = -1;
		this.area[7][14].val = -1;
		this.area[7][15].val = 1;
		this.area[7][16].val = 1;
		this.area[7][17].val = 1;
		this.area[7][18].val = 1;
		this.area[7][19].val = 1;
		this.area[7][20].val = 1;
		this.area[7][21].val = 1;
		this.area[7][22].val = -1;
		this.area[7][23].val = -1;
		this.area[7][24].val = -1;
		this.area[8][0].val = -1;
		this.area[8][1].val = -1;
		this.area[8][2].val = -1;
		this.area[8][3].val = -1;
		this.area[8][4].val = -1;
		this.area[8][5].val = -1;
		this.area[8][6].val = -1;
		this.area[8][7].val = 1;
		this.area[8][8].val = 1;
		this.area[8][9].val = 1;
		this.area[8][10].val = 1;
		this.area[8][11].val = 1;
		this.area[8][12].val = 1;
		this.area[8][13].val = 1;
		this.area[8][14].val = -1;
		this.area[8][15].val = -1;
		this.area[8][16].val = -1;
		this.area[8][17].val = -1;
		this.area[8][18].val = -1;
		this.area[8][19].val = -1;
		this.area[8][20].val = -1;
		this.area[8][21].val = -1;
		this.area[8][22].val = -1;
		this.area[8][23].val = -1;
		this.area[8][24].val = -1;
		this.area[9][0].val = -1;
		this.area[9][1].val = -1;
		this.area[9][2].val = -1;
		this.area[9][3].val = -1;
		this.area[9][4].val = -1;
		this.area[9][5].val = -1;
		this.area[9][6].val = -1;
		this.area[9][7].val = 1;
		this.area[9][8].val = -1;
		this.area[9][9].val = -1;
		this.area[9][10].val = -1;
		this.area[9][11].val = 1;
		this.area[9][12].val = 1;
		this.area[9][13].val = 1;
		this.area[9][14].val = -1;
		this.area[9][15].val = -1;
		this.area[9][16].val = -1;
		this.area[9][17].val = -1;
		this.area[9][18].val = -1;
		this.area[9][19].val = -1;
		this.area[9][20].val = -1;
		this.area[9][21].val = -1;
		this.area[9][22].val = -1;
		this.area[9][23].val = -1;
		this.area[9][24].val = -1;
		this.area[10][0].val = -1;
		this.area[10][1].val = -1;
		this.area[10][2].val = -1;
		this.area[10][3].val = -1;
		this.area[10][4].val = -1;
		this.area[10][5].val = -1;
		this.area[10][6].val = -1;
		this.area[10][7].val = 1;
		this.area[10][8].val = -1;
		this.area[10][9].val = -1;
		this.area[10][10].val = -1;
		this.area[10][11].val = 1;
		this.area[10][12].val = 1;
		this.area[10][13].val = 1;
		this.area[10][14].val = -1;
		this.area[10][15].val = -1;
		this.area[10][16].val = -1;
		this.area[10][17].val = -1;
		this.area[10][18].val = -1;
		this.area[10][19].val = -1;
		this.area[10][20].val = -1;
		this.area[10][21].val = -1;
		this.area[10][22].val = -1;
		this.area[10][23].val = -1;
		this.area[10][24].val = -1;
		this.area[11][0].val = -1;
		this.area[11][1].val = -1;
		this.area[11][2].val = -1;
		this.area[11][3].val = -1;
		this.area[11][4].val = -1;
		this.area[11][5].val = -1;
		this.area[11][6].val = -1;
		this.area[11][7].val = 1;
		this.area[11][8].val = -1;
		this.area[11][9].val = -1;
		this.area[11][10].val = -1;
		this.area[11][11].val = 1;
		this.area[11][12].val = 1;
		this.area[11][13].val = 1;
		this.area[11][14].val = -1;
		this.area[11][15].val = -1;
		this.area[11][16].val = -1;
		this.area[11][17].val = -1;
		this.area[11][18].val = -1;
		this.area[11][19].val = -1;
		this.area[11][20].val = -1;
		this.area[11][21].val = -1;
		this.area[11][22].val = -1;
		this.area[11][23].val = -1;
		this.area[11][24].val = -1;
		this.area[12][0].val = -1;
		this.area[12][1].val = -1;
		this.area[12][2].val = -1;
		this.area[12][3].val = -1;
		this.area[12][4].val = -1;
		this.area[12][5].val = -1;
		this.area[12][6].val = -1;
		this.area[12][7].val = 1;
		this.area[12][8].val = 1;
		this.area[12][9].val = 1;
		this.area[12][10].val = 1;
		this.area[12][11].val = 1;
		this.area[12][12].val = 1;
		this.area[12][13].val = 1;
		this.area[12][14].val = -1;
		this.area[12][15].val = -1;
		this.area[12][16].val = -1;
		this.area[12][17].val = -1;
		this.area[12][18].val = -1;
		this.area[12][19].val = -1;
		this.area[12][20].val = -1;
		this.area[12][21].val = -1;
		this.area[12][22].val = -1;
		this.area[12][23].val = -1;
		this.area[12][24].val = -1;
		this.area[13][0].val = -1;
		this.area[13][1].val = -1;
		this.area[13][2].val = -1;
		this.area[13][3].val = -1;
		this.area[13][4].val = -1;
		this.area[13][5].val = -1;
		this.area[13][6].val = 1;
		this.area[13][7].val = 1;
		this.area[13][8].val = 1;
		this.area[13][9].val = 1;
		this.area[13][10].val = 1;
		this.area[13][11].val = 1;
		this.area[13][12].val = 1;
		this.area[13][13].val = 1;
		this.area[13][14].val = 1;
		this.area[13][15].val = -1;
		this.area[13][16].val = -1;
		this.area[13][17].val = -1;
		this.area[13][18].val = -1;
		this.area[13][19].val = -1;
		this.area[13][20].val = -1;
		this.area[13][21].val = -1;
		this.area[13][22].val = -1;
		this.area[13][23].val = -1;
		this.area[13][24].val = -1;
		this.area[14][0].val = -1;
		this.area[14][1].val = -1;
		this.area[14][2].val = -1;
		this.area[14][3].val = -1;
		this.area[14][4].val = -1;
		this.area[14][5].val = 1;
		this.area[14][6].val = 1;
		this.area[14][7].val = 1;
		this.area[14][8].val = -1;
		this.area[14][9].val = 1;
		this.area[14][10].val = -1;
		this.area[14][11].val = 1;
		this.area[14][12].val = 1;
		this.area[14][13].val = 1;
		this.area[14][14].val = 1;
		this.area[14][15].val = 1;
		this.area[14][16].val = -1;
		this.area[14][17].val = -1;
		this.area[14][18].val = -1;
		this.area[14][19].val = -1;
		this.area[14][20].val = -1;
		this.area[14][21].val = -1;
		this.area[14][22].val = -1;
		this.area[14][23].val = -1;
		this.area[14][24].val = -1;
		this.area[15][0].val = -1;
		this.area[15][1].val = -1;
		this.area[15][2].val = -1;
		this.area[15][3].val = -1;
		this.area[15][4].val = 1;
		this.area[15][5].val = 1;
		this.area[15][6].val = 1;
		this.area[15][7].val = -1;
		this.area[15][8].val = 1;
		this.area[15][9].val = -1;
		this.area[15][10].val = 1;
		this.area[15][11].val = -1;
		this.area[15][12].val = 1;
		this.area[15][13].val = 1;
		this.area[15][14].val = 1;
		this.area[15][15].val = 1;
		this.area[15][16].val = 1;
		this.area[15][17].val = -1;
		this.area[15][18].val = -1;
		this.area[15][19].val = -1;
		this.area[15][20].val = -1;
		this.area[15][21].val = -1;
		this.area[15][22].val = -1;
		this.area[15][23].val = -1;
		this.area[15][24].val = -1;
		this.area[16][0].val = -1;
		this.area[16][1].val = -1;
		this.area[16][2].val = -1;
		this.area[16][3].val = 1;
		this.area[16][4].val = 1;
		this.area[16][5].val = 1;
		this.area[16][6].val = -1;
		this.area[16][7].val = 1;
		this.area[16][8].val = -1;
		this.area[16][9].val = 1;
		this.area[16][10].val = -1;
		this.area[16][11].val = 1;
		this.area[16][12].val = -1;
		this.area[16][13].val = 1;
		this.area[16][14].val = 1;
		this.area[16][15].val = 1;
		this.area[16][16].val = 1;
		this.area[16][17].val = 1;
		this.area[16][18].val = -1;
		this.area[16][19].val = -1;
		this.area[16][20].val = -1;
		this.area[16][21].val = -1;
		this.area[16][22].val = -1;
		this.area[16][23].val = -1;
		this.area[16][24].val = -1;
		this.area[17][0].val = -1;
		this.area[17][1].val = -1;
		this.area[17][2].val = -1;
		this.area[17][3].val = 1;
		this.area[17][4].val = 1;
		this.area[17][5].val = -1;
		this.area[17][6].val = 1;
		this.area[17][7].val = -1;
		this.area[17][8].val = 1;
		this.area[17][9].val = -1;
		this.area[17][10].val = 1;
		this.area[17][11].val = -1;
		this.area[17][12].val = 1;
		this.area[17][13].val = -1;
		this.area[17][14].val = 1;
		this.area[17][15].val = 1;
		this.area[17][16].val = 1;
		this.area[17][17].val = 1;
		this.area[17][18].val = -1;
		this.area[17][19].val = -1;
		this.area[17][20].val = -1;
		this.area[17][21].val = -1;
		this.area[17][22].val = -1;
		this.area[17][23].val = -1;
		this.area[17][24].val = -1;
		this.area[18][0].val = -1;
		this.area[18][1].val = -1;
		this.area[18][2].val = 1;
		this.area[18][3].val = 1;
		this.area[18][4].val = 1;
		this.area[18][5].val = 1;
		this.area[18][6].val = 1;
		this.area[18][7].val = 1;
		this.area[18][8].val = 1;
		this.area[18][9].val = 1;
		this.area[18][10].val = 1;
		this.area[18][11].val = 1;
		this.area[18][12].val = 1;
		this.area[18][13].val = 1;
		this.area[18][14].val = 1;
		this.area[18][15].val = 1;
		this.area[18][16].val = 1;
		this.area[18][17].val = 1;
		this.area[18][18].val = 1;
		this.area[18][19].val = -1;
		this.area[18][20].val = -1;
		this.area[18][21].val = -1;
		this.area[18][22].val = -1;
		this.area[18][23].val = -1;
		this.area[18][24].val = -1;
		this.area[19][0].val = -1;
		this.area[19][1].val = -1;
		this.area[19][2].val = 1;
		this.area[19][3].val = -1;
		this.area[19][4].val = -1;
		this.area[19][5].val = -1;
		this.area[19][6].val = -1;
		this.area[19][7].val = -1;
		this.area[19][8].val = -1;
		this.area[19][9].val = -1;
		this.area[19][10].val = -1;
		this.area[19][11].val = -1;
		this.area[19][12].val = -1;
		this.area[19][13].val = -1;
		this.area[19][14].val = -1;
		this.area[19][15].val = 1;
		this.area[19][16].val = 1;
		this.area[19][17].val = 1;
		this.area[19][18].val = 1;
		this.area[19][19].val = -1;
		this.area[19][20].val = -1;
		this.area[19][21].val = -1;
		this.area[19][22].val = -1;
		this.area[19][23].val = -1;
		this.area[19][24].val = -1;
		this.area[20][0].val = -1;
		this.area[20][1].val = -1;
		this.area[20][2].val = 1;
		this.area[20][3].val = -1;
		this.area[20][4].val = -1;
		this.area[20][5].val = -1;
		this.area[20][6].val = -1;
		this.area[20][7].val = -1;
		this.area[20][8].val = -1;
		this.area[20][9].val = -1;
		this.area[20][10].val = -1;
		this.area[20][11].val = -1;
		this.area[20][12].val = -1;
		this.area[20][13].val = -1;
		this.area[20][14].val = -1;
		this.area[20][15].val = 1;
		this.area[20][16].val = 1;
		this.area[20][17].val = 1;
		this.area[20][18].val = 1;
		this.area[20][19].val = -1;
		this.area[20][20].val = -1;
		this.area[20][21].val = -1;
		this.area[20][22].val = -1;
		this.area[20][23].val = -1;
		this.area[20][24].val = -1;
		this.area[21][0].val = -1;
		this.area[21][1].val = -1;
		this.area[21][2].val = 1;
		this.area[21][3].val = -1;
		this.area[21][4].val = -1;
		this.area[21][5].val = 1;
		this.area[21][6].val = 1;
		this.area[21][7].val = -1;
		this.area[21][8].val = 1;
		this.area[21][9].val = 1;
		this.area[21][10].val = -1;
		this.area[21][11].val = -1;
		this.area[21][12].val = -1;
		this.area[21][13].val = -1;
		this.area[21][14].val = -1;
		this.area[21][15].val = 1;
		this.area[21][16].val = 1;
		this.area[21][17].val = 1;
		this.area[21][18].val = 1;
		this.area[21][19].val = -1;
		this.area[21][20].val = -1;
		this.area[21][21].val = -1;
		this.area[21][22].val = -1;
		this.area[21][23].val = -1;
		this.area[21][24].val = -1;
		this.area[22][0].val = 1;
		this.area[22][1].val = 1;
		this.area[22][2].val = 1;
		this.area[22][3].val = -1;
		this.area[22][4].val = -1;
		this.area[22][5].val = -1;
		this.area[22][6].val = 1;
		this.area[22][7].val = -1;
		this.area[22][8].val = 1;
		this.area[22][9].val = -1;
		this.area[22][10].val = -1;
		this.area[22][11].val = -1;
		this.area[22][12].val = -1;
		this.area[22][13].val = -1;
		this.area[22][14].val = -1;
		this.area[22][15].val = 1;
		this.area[22][16].val = 1;
		this.area[22][17].val = 1;
		this.area[22][18].val = 1;
		this.area[22][19].val = 1;
		this.area[22][20].val = 1;
		this.area[22][21].val = 1;
		this.area[22][22].val = 1;
		this.area[22][23].val = 1;
		this.area[22][24].val = 1;
		this.area[23][0].val = -1;
		this.area[23][1].val = 1;
		this.area[23][2].val = 1;
		this.area[23][3].val = 1;
		this.area[23][4].val = -1;
		this.area[23][5].val = 1;
		this.area[23][6].val = 1;
		this.area[23][7].val = -1;
		this.area[23][8].val = 1;
		this.area[23][9].val = 1;
		this.area[23][10].val = -1;
		this.area[23][11].val = -1;
		this.area[23][12].val = -1;
		this.area[23][13].val = -1;
		this.area[23][14].val = -1;
		this.area[23][15].val = 1;
		this.area[23][16].val = 1;
		this.area[23][17].val = 1;
		this.area[23][18].val = 1;
		this.area[23][19].val = 1;
		this.area[23][20].val = -1;
		this.area[23][21].val = -1;
		this.area[23][22].val = 1;
		this.area[23][23].val = 1;
		this.area[23][24].val = -1;
		this.area[24][0].val = -1;
		this.area[24][1].val = -1;
		this.area[24][2].val = 1;
		this.area[24][3].val = 1;
		this.area[24][4].val = -1;
		this.area[24][5].val = -1;
		this.area[24][6].val = -1;
		this.area[24][7].val = -1;
		this.area[24][8].val = -1;
		this.area[24][9].val = -1;
		this.area[24][10].val = -1;
		this.area[24][11].val = -1;
		this.area[24][12].val = -1;
		this.area[24][13].val = -1;
		this.area[24][14].val = 1;
		this.area[24][15].val = 1;
		this.area[24][16].val = 1;
		this.area[24][17].val = 1;
		this.area[24][18].val = -1;
		this.area[24][19].val = 1;
		this.area[24][20].val = 1;
		this.area[24][21].val = -1;
		this.area[24][22].val = -1;
		this.area[24][23].val = 1;
		this.area[24][24].val = 1;
		this.area[25][0].val = 1;
		this.area[25][1].val = -1;
		this.area[25][2].val = -1;
		this.area[25][3].val = 1;
		this.area[25][4].val = 1;
		this.area[25][5].val = -1;
		this.area[25][6].val = -1;
		this.area[25][7].val = -1;
		this.area[25][8].val = -1;
		this.area[25][9].val = -1;
		this.area[25][10].val = -1;
		this.area[25][11].val = -1;
		this.area[25][12].val = -1;
		this.area[25][13].val = -1;
		this.area[25][14].val = 1;
		this.area[25][15].val = 1;
		this.area[25][16].val = 1;
		this.area[25][17].val = 1;
		this.area[25][18].val = -1;
		this.area[25][19].val = -1;
		this.area[25][20].val = 1;
		this.area[25][21].val = 1;
		this.area[25][22].val = -1;
		this.area[25][23].val = -1;
		this.area[25][24].val = 1;
		this.area[26][0].val = 1;
		this.area[26][1].val = 1;
		this.area[26][2].val = -1;
		this.area[26][3].val = -1;
		this.area[26][4].val = 1;
		this.area[26][5].val = 1;
		this.area[26][6].val = -1;
		this.area[26][7].val = 1;
		this.area[26][8].val = -1;
		this.area[26][9].val = 1;
		this.area[26][10].val = -1;
		this.area[26][11].val = 1;
		this.area[26][12].val = -1;
		this.area[26][13].val = 1;
		this.area[26][14].val = 1;
		this.area[26][15].val = 1;
		this.area[26][16].val = 1;
		this.area[26][17].val = 1;
		this.area[26][18].val = 1;
		this.area[26][19].val = -1;
		this.area[26][20].val = -1;
		this.area[26][21].val = 1;
		this.area[26][22].val = 1;
		this.area[26][23].val = -1;
		this.area[26][24].val = -1;
		this.area[27][0].val = -1;
		this.area[27][1].val = 1;
		this.area[27][2].val = 1;
		this.area[27][3].val = -1;
		this.area[27][4].val = -1;
		this.area[27][5].val = 1;
		this.area[27][6].val = 1;
		this.area[27][7].val = -1;
		this.area[27][8].val = 1;
		this.area[27][9].val = -1;
		this.area[27][10].val = 1;
		this.area[27][11].val = -1;
		this.area[27][12].val = 1;
		this.area[27][13].val = 1;
		this.area[27][14].val = 1;
		this.area[27][15].val = 1;
		this.area[27][16].val = 1;
		this.area[27][17].val = -1;
		this.area[27][18].val = 1;
		this.area[27][19].val = 1;
		this.area[27][20].val = -1;
		this.area[27][21].val = -1;
		this.area[27][22].val = 1;
		this.area[27][23].val = 1;
		this.area[27][24].val = -1;
		this.area[28][0].val = -1;
		this.area[28][1].val = -1;
		this.area[28][2].val = 1;
		this.area[28][3].val = 1;
		this.area[28][4].val = -1;
		this.area[28][5].val = -1;
		this.area[28][6].val = 1;
		this.area[28][7].val = 1;
		this.area[28][8].val = 1;
		this.area[28][9].val = 1;
		this.area[28][10].val = 1;
		this.area[28][11].val = 1;
		this.area[28][12].val = 1;
		this.area[28][13].val = 1;
		this.area[28][14].val = 1;
		this.area[28][15].val = 1;
		this.area[28][16].val = 1;
		this.area[28][17].val = -1;
		this.area[28][18].val = -1;
		this.area[28][19].val = 1;
		this.area[28][20].val = 1;
		this.area[28][21].val = -1;
		this.area[28][22].val = -1;
		this.area[28][23].val = 1;
		this.area[28][24].val = 1;
		this.area[29][0].val = -1;
		this.area[29][1].val = -1;
		this.area[29][2].val = -1;
		this.area[29][3].val = 1;
		this.area[29][4].val = 1;
		this.area[29][5].val = -1;
		this.area[29][6].val = -1;
		this.area[29][7].val = 1;
		this.area[29][8].val = 1;
		this.area[29][9].val = 1;
		this.area[29][10].val = 1;
		this.area[29][11].val = 1;
		this.area[29][12].val = 1;
		this.area[29][13].val = 1;
		this.area[29][14].val = 1;
		this.area[29][15].val = -1;
		this.area[29][16].val = 1;
		this.area[29][17].val = 1;
		this.area[29][18].val = -1;
		this.area[29][19].val = -1;
		this.area[29][20].val = 1;
		this.area[29][21].val = 1;
		this.area[29][22].val = -1;
		this.area[29][23].val = -1;
		this.area[29][24].val = 1;

	}
}
