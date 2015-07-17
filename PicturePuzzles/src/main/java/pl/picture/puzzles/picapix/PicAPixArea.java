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
	public int x = 0, y = 0; // wymiary planszy
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
					PaNumber number = new PaNumber(Byte.parseByte(numbers[j]));
					if (j == 0) {
						number.first = true;
					}
					if (j == numbers.length - 1) {
						number.last = true;
					}

					paNumber.add(number);
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
					PaNumber number = new PaNumber(Byte.parseByte(numbers[j]));
					if (j == 0) {
						number.first = true;
					}
					if (j == numbers.length - 1) {
						number.last = true;
					}

					paNumber.add(number);
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

		// II - laczenie odcinkow "czarnych" oraz wyznaczanie przestrzeni
		// pomiędzy odcinkami "szarymi"
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

	// I etap - malowanie pewnych pol oraz wyznaczanie zasięgu liczby
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

		// System.out.println(i + ":");
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
						this.area[j][i].type = SELECTED;
						this.area[j][i].belongsToVertical = paNumber;
					} else {
						this.area[i][j].type = SELECTED;
						this.area[i][j].belongsToHorizontal = paNumber;
					}

					// jezeli dzialamy na lewej (pionowej) lub górnej
					// (poziomej)
					// krawędzi
					if (i == 0) {
						// pionowe -> poziome lub poziome -> pionowe
						ListOfNumber numberListPrim = listsOfNumbers.get(j);
						PaNumber firstNumber = numberListPrim.numbers.get(0);
						for (int k = 0; k < firstNumber.val; k++) {

							if (isVertical) {
								this.area[j][k].type = SELECTED;
								this.area[j][k].belongsToHorizontal = firstNumber;
							} else {
								this.area[k][j].type = SELECTED;
								this.area[k][j].belongsToVertical = firstNumber;
							}

						}
						// Kolorownie natępnej kratki na "szaro"
						if (isVertical) {
							this.area[j][firstNumber.val].type = EMPTY;
						} else {
							this.area[firstNumber.val][j].type = EMPTY;
						}

						setNumberEnableToFalse(numberListPrim.numbers,
								firstNumber, 0);
						// firstNumber.enable = false;

						firstNumber.scope[0] = 0;
						firstNumber.scope[1] = firstNumber.val - 1;
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
								this.area[j][k].type = SELECTED;
								this.area[j][k].belongsToHorizontal = lastNumber;
							} else {
								this.area[k][j].type = SELECTED;
								this.area[k][j].belongsToVertical = lastNumber;
							}

						}
						// Kolorownie natępnej kratki na "szaro"
						if (isVertical) {
							this.area[j][nPrim - lastNumber.val - 1].type = EMPTY;
						} else {
							this.area[nPrim - lastNumber.val - 1][j].type = EMPTY;
						}

						setNumberEnableToFalse(numberListPrim.numbers,
								lastNumber, numberListPrim.numbers.size() - 1);
						// lastNumber.enable = false;

						lastNumber.scope[0] = nPrim - lastNumber.val;
						lastNumber.scope[1] = nPrim - 1;
						numberListPrim.otherNumbers--;
					}
				}
			}

			// Wyznaczanie zasięgu liczby
			paNumber.scope[0] = startPosition;
			paNumber.scope[1] = n
					- (numberList.sumOfNumbers - startPosition - paNumber.val + numberList.numbers
							.size());

			/*
			 * System.out.print(paNumber.val + ": [" + paNumber.scope[0] + "," +
			 * paNumber.scope[1] + "] ");
			 */
			startPosition += paNumber.val + 1;
		}
		// System.out.println("\n");
	}

	private void setNumberEnableToFalse(List<PaNumber> numbers,
			PaNumber setNumber, int i) {
		// Jeżeli liczba nie jest pierwszą oraz ostatnią lub jest pierwszą i
		// ostatnią to tylko ją "wyłącz"
		if ((!setNumber.first && !setNumber.last)
				|| (setNumber.first && setNumber.last)) {
			setNumber.enable = false;
			return;
		}

		if (setNumber.first) {
			numbers.get(i + 1).first = true;
			setNumber.enable = false;
		}

		if (setNumber.last) {
			numbers.get(i - 1).last = true;
			setNumber.enable = false;
		}

	}

	// Metoda wyznaczająca pokolorowane odcinki. Wyznacza odcinki pokolorowane
	// na szaro oraz odcinki pokolorowane na czarno, odcinki pokolorowane na
	// czarno są rozdzielane według przynależności, czyli jeżeli odcinek jest
	// złożony z pól, które należą do liczby n lub nie należą do żadnej liczby
	// to jest on dzielony.
	private void determiningOfLengths(ListOfNumber numberList,
			boolean isVertical, int i) {

		numberList.selectedLengths = new ArrayList<Length>();
		numberList.spaceLengths = new ArrayList<Length>();
		int n;

		if (isVertical) {
			n = this.y;
		} else {
			n = this.x;
		}

		int s = 0, e = 0, ss = 0;
		// Zmienna zapamiętująca jakiego typu było ostatnie pole
		int lastType = EMPTY;
		// Zmienna zapamiętująca do jakiej liczby nalożelo ostatnie pole (jeżeli
		// null to znaczy że należało do żandej)
		// PaNumber lastPaNumber = null;
		for (int j = 0; j < n; j++) {

			// Pobranie pola oraz liczby do którego należy (jeżeli null to nie
			// nalezy do żadnej liczby)
			Field field;

			if (isVertical) {
				field = this.area[j][i];
			} else {
				field = this.area[i][j];
			}

			if (field.type == ABSENCE) {
				if (lastType == SELECTED) {
					Length l = new Length(s, e - 1, SELECTED,
							numberList.numbers);

					numberList.selectedLengths.add(l);
				}

				if (lastType == EMPTY) {
					ss = j;
				}
				lastType = field.type;
				s = e = j + 1;
				continue;
			}
			if (field.type == SELECTED) {
				if (lastType == EMPTY) {
					ss = s = j;
				}
				lastType = field.type;
				e = j + 1;
				continue;
			}

			if (field.type == EMPTY) {
				if (lastType != EMPTY) {
					Length l = new Length(ss, e - 1, ABSENCE,
							numberList.numbers);

					numberList.spaceLengths.add(l);
				}
				if (lastType == SELECTED) {

					Length l = new Length(s, e - 1, SELECTED,
							numberList.numbers);

					numberList.selectedLengths.add(l);

				}
				lastType = field.type;
				// e = j + 1;
				continue;
			}
		}

		if (lastType == SELECTED) {
			Length l = new Length(s, e - 1, SELECTED, numberList.numbers);

			numberList.selectedLengths.add(l);
		}
		if (lastType != EMPTY) {
			Length l = new Length(ss, e - 1, ABSENCE, numberList.numbers);
			numberList.spaceLengths.add(l);
		}

		actionOnLengths(numberList, isVertical, i);

		// DEBUG
		for (Length l : numberList.selectedLengths) {
			String open, close;

			open = "<";
			close = ">";

			String belongsTo;
			if (l.listOfNumbersToBelong == null) {
				belongsTo = " nalezy do: NULL";
			} else {
				belongsTo = " nelezy do: ";
				for (PaNumber number : l.listOfNumbersToBelong) {
					belongsTo = belongsTo + number.val + ": ["
							+ number.scope[0] + ", " + number.scope[1] + "], ";
				}
			}

			System.out.println(i + ": " + open + l.s + ", " + l.e + close
					+ belongsTo);
		}
		for (Length l : numberList.spaceLengths) {
			String open, close;

			open = "(";
			close = ")";

			System.out.println(i + ": " + open + l.s + ", " + l.e + close);
		}
		// DEBUG
		System.out.println();

	}

	private void actionOnLengths(ListOfNumber numberList, boolean isVertical,
			int i) {

		int n = 0;
		if (isVertical)
			n = this.y;
		else
			n = this.x;

		List<Length> lengths = numberList.selectedLengths;
		int k = 0; // wskaźnik na odcinek
		// wielkość listy odcinków (może się zmieniać podczas przebiegu pętli
		int size = lengths.size();

		while (k < size) {
			Length l = lengths.get(k);

			if (l.type == ABSENCE) {
				k++;
				continue;
			}

			if (l.listOfNumbersToBelong.size() == 1) {
				// Jezeli istnieje nastepny odcinek
				if (k + 1 < size) {
					Length lNext = lengths.get(k + 1);
					// Jezeli następny lNext należy do takiej samej liczby co l
					// i tylko do niej to scal odcinki
					if (lNext.listOfNumbersToBelong.size() == 1
							&& lNext.listOfNumbersToBelong.get(0) == l.listOfNumbersToBelong
									.get(0)) {

						// Pokolorowanie scalonego odcinke
						for (int j = l.e + 1; j < lNext.e; j++) {

							if (isVertical) {
								this.area[j][i].type = SELECTED;
							} else {
								this.area[i][j].type = SELECTED;
							}
						}
						// Scalenie odcinka l z lNext
						l.e = lNext.e;
						lengths.remove(lNext);
						size--;
					}
				}
				// Pobranie referancji liczby zależnej od odcinka
				PaNumber numberOfL = l.listOfNumbersToBelong.get(0);

				// Sprawdzenie czy zmienia się zasięg liczby
				if (l.s + numberOfL.val - 1 < numberOfL.scope[1]
						|| l.e - numberOfL.val + 1 > numberOfL.scope[0]) {

					if (l.e - numberOfL.val + 1 < 0) {
						numberOfL.scope[0] = 0;
					} else {
						numberOfL.scope[0] = l.e - numberOfL.val + 1;
					}

					if (l.s + numberOfL.val - 1 > n - 1) {
						numberOfL.scope[1] = n - 1;
					} else {
						numberOfL.scope[1] = l.s + numberOfL.val - 1;
					}
					changeBelongsToNumberForLengths(lengths, numberOfL);

				}
			}
			k++;
		}
	}

	// Zmiana zależności od liczby dla odcinków
	private void changeBelongsToNumberForLengths(List<Length> lengths,
			PaNumber number) {
		for (Length length : lengths) {
			if (length.type == ABSENCE)
				continue;

			if (length.s < number.scope[0] || length.e > number.scope[1]) {
				length.listOfNumbersToBelong.remove(number);
			}
		}
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
					if (this.area[i][j].type == SELECTED)
						return false;
				}
			} else {
				for (int i = 0; i < this.y; i++) {
					// Jezeli pole nie jest pokolorowane na czarno sprawdź czy
					// wyliczona długość odcinka jest równa zeru, jeżeli jest to
					// skocz do następnej iteracji w p.p. sprawdź czy długość
					// wyznaczonego odcinka zgadza się z aktualnie sprawdzanym
					// numerem
					if (this.area[i][j].type != SELECTED) {
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
					if (this.area[i][j].type == SELECTED)
						return false;
				}
			} else {
				for (int j = 0; j < this.x; j++) {
					// Jezeli pole nie jest pokolorowane na czarno sprawdź czy
					// wyliczona długość odcinka jest równa zeru, jeżeli jest to
					// skocz do następnej iteracji w p.p. sprawdź czy długość
					// wyznaczonego odcinka zgadza się z aktualnie sprawdzanym
					// numerem
					if (this.area[i][j].type != SELECTED) {
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
				System.out.print("this.area[" + i + "][" + j + "].type = "
						+ this.area[i][j].type + "; ");
			}
			System.out.println();
		}

	}

	// Klasa reprezetujaca pole / kratke na planszy
	public class Field {
		public byte type = ABSENCE; // typ pola, mozliwe wartosci: -1 -- Brak
									// koloru (pole niepokolorowane), 0 -- Puste
									// pole (krzyzyk), 1 -- Pole zaznaczone
									// (pokolorowane)
		public PaNumber belongsToVertical;
		public PaNumber belongsToHorizontal;

		public Field() {

		}

		public Field(Field field) {
			this.type = field.type;
		}
	}

	// Klasa reprezentujaca liste numerow dla kolumny lub rzedu
	public class ListOfNumber {
		public List<PaNumber> numbers;
		public byte otherNumbers = -1; // ile pozostalo numerow do wyznaczenia
										// (pokolorowania)
		public int sumOfNumbers = -1; // suma wszystkich numerow
		public List<Length> selectedLengths; // Wyznaczone pokolorowane odcinki
		public List<Length> spaceLengths; // Odcinki pomiędzy odcinkami
											// pokolorowanymi na szaro

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
		public boolean first = false; // czy liczba jest pierwsza
		public boolean last = false; // czy liczba jest ostatnia

		public PaNumber() {

		}

		public PaNumber(byte val) {
			this.val = val;
		}

		@Override
		public String toString() {

			return "Number: " + val + " [" + scope[0] + ", " + scope[1]
					+ "] enable: " + enable;
		}
	}

	// Odcinek
	public class Length {

		public int s; // Start odcinka
		public int e; // Koniec odcinka (end)
		public int type; // typ odcinka: 1 - "SELECTED", 0 - "EMPTY"
		public List<PaNumber> listOfNumbersToBelong; // lista numerów, które
														// należą do odcinka
		public boolean isComplete = false; // czy odcinek kompletny

		public Length() {

		}

		public Length(int s, int e, int type, List<PaNumber> numbers) {
			this.s = s;
			this.e = e;
			this.type = type;

			if (type == SELECTED) {

				// Wyznaczenie do jakich liczb należy odcinek
				for (PaNumber number : numbers) {
					// Jeżeli zasięg liczby nie pokrywa się z pozycją i
					// długoscią liczby przejdź do następnej
					if (s - 1 > number.scope[1] || number.val < (e - s + 1)) {
						continue;
					}

					if (e + 1 < number.scope[0]) {
						break;
					}

					if (number.scope[0] < (s - 1) && number.scope[1] >= (s - 1)
							&& number.scope[1] < e) {

						number.scope[1] = s - 2;
						continue;
					}

					if (number.scope[1] > (e + 1) && number.scope[0] <= e + 1
							&& number.scope[0] > s) {

						number.scope[0] = e + 2;
						continue;
					}

					if (number.scope[0] <= s && number.scope[1] >= e) {

						if (this.listOfNumbersToBelong == null) {
							this.listOfNumbersToBelong = new ArrayList<PaNumber>();
						}

						this.listOfNumbersToBelong.add(number);
						continue;
					}

					System.err.println("Błąd! [" + number.scope[1] + ", "
							+ number.scope[1] + "], <" + s + ", " + e
							+ ">, type:" + type);
				}
			}
		}
	}

	// debug
	private void uzupelnijLamiglowke() {
		this.area[0][0].type = -1;
		this.area[0][1].type = -1;
		this.area[0][2].type = -1;
		this.area[0][3].type = -1;
		this.area[0][4].type = -1;
		this.area[0][5].type = -1;
		this.area[0][6].type = -1;
		this.area[0][7].type = -1;
		this.area[0][8].type = -1;
		this.area[0][9].type = -1;
		this.area[0][10].type = -1;
		this.area[0][11].type = -1;
		this.area[0][12].type = -1;
		this.area[0][13].type = -1;
		this.area[0][14].type = 1;
		this.area[0][15].type = 1;
		this.area[0][16].type = 1;
		this.area[0][17].type = 1;
		this.area[0][18].type = 1;
		this.area[0][19].type = 1;
		this.area[0][20].type = 1;
		this.area[0][21].type = 1;
		this.area[0][22].type = 1;
		this.area[0][23].type = -1;
		this.area[0][24].type = -1;
		this.area[1][0].type = 1;
		this.area[1][1].type = 1;
		this.area[1][2].type = 1;
		this.area[1][3].type = -1;
		this.area[1][4].type = -1;
		this.area[1][5].type = -1;
		this.area[1][6].type = -1;
		this.area[1][7].type = -1;
		this.area[1][8].type = -1;
		this.area[1][9].type = -1;
		this.area[1][10].type = -1;
		this.area[1][11].type = -1;
		this.area[1][12].type = -1;
		this.area[1][13].type = 1;
		this.area[1][14].type = 1;
		this.area[1][15].type = -1;
		this.area[1][16].type = 1;
		this.area[1][17].type = -1;
		this.area[1][18].type = 1;
		this.area[1][19].type = -1;
		this.area[1][20].type = 1;
		this.area[1][21].type = 1;
		this.area[1][22].type = 1;
		this.area[1][23].type = 1;
		this.area[1][24].type = -1;
		this.area[2][0].type = -1;
		this.area[2][1].type = -1;
		this.area[2][2].type = 1;
		this.area[2][3].type = 1;
		this.area[2][4].type = -1;
		this.area[2][5].type = -1;
		this.area[2][6].type = -1;
		this.area[2][7].type = -1;
		this.area[2][8].type = 1;
		this.area[2][9].type = 1;
		this.area[2][10].type = 1;
		this.area[2][11].type = 1;
		this.area[2][12].type = 1;
		this.area[2][13].type = 1;
		this.area[2][14].type = -1;
		this.area[2][15].type = 1;
		this.area[2][16].type = -1;
		this.area[2][17].type = 1;
		this.area[2][18].type = -1;
		this.area[2][19].type = 1;
		this.area[2][20].type = -1;
		this.area[2][21].type = 1;
		this.area[2][22].type = 1;
		this.area[2][23].type = 1;
		this.area[2][24].type = 1;
		this.area[3][0].type = 1;
		this.area[3][1].type = -1;
		this.area[3][2].type = -1;
		this.area[3][3].type = 1;
		this.area[3][4].type = 1;
		this.area[3][5].type = -1;
		this.area[3][6].type = 1;
		this.area[3][7].type = 1;
		this.area[3][8].type = 1;
		this.area[3][9].type = 1;
		this.area[3][10].type = 1;
		this.area[3][11].type = 1;
		this.area[3][12].type = 1;
		this.area[3][13].type = -1;
		this.area[3][14].type = 1;
		this.area[3][15].type = -1;
		this.area[3][16].type = 1;
		this.area[3][17].type = -1;
		this.area[3][18].type = 1;
		this.area[3][19].type = -1;
		this.area[3][20].type = 1;
		this.area[3][21].type = -1;
		this.area[3][22].type = 1;
		this.area[3][23].type = 1;
		this.area[3][24].type = 1;
		this.area[4][0].type = -1;
		this.area[4][1].type = 1;
		this.area[4][2].type = -1;
		this.area[4][3].type = 1;
		this.area[4][4].type = 1;
		this.area[4][5].type = 1;
		this.area[4][6].type = 1;
		this.area[4][7].type = -1;
		this.area[4][8].type = 1;
		this.area[4][9].type = -1;
		this.area[4][10].type = -1;
		this.area[4][11].type = 1;
		this.area[4][12].type = 1;
		this.area[4][13].type = 1;
		this.area[4][14].type = -1;
		this.area[4][15].type = 1;
		this.area[4][16].type = -1;
		this.area[4][17].type = 1;
		this.area[4][18].type = -1;
		this.area[4][19].type = 1;
		this.area[4][20].type = -1;
		this.area[4][21].type = 1;
		this.area[4][22].type = 1;
		this.area[4][23].type = 1;
		this.area[4][24].type = 1;
		this.area[5][0].type = 1;
		this.area[5][1].type = -1;
		this.area[5][2].type = -1;
		this.area[5][3].type = 1;
		this.area[5][4].type = 1;
		this.area[5][5].type = -1;
		this.area[5][6].type = 1;
		this.area[5][7].type = 1;
		this.area[5][8].type = 1;
		this.area[5][9].type = -1;
		this.area[5][10].type = -1;
		this.area[5][11].type = 1;
		this.area[5][12].type = 1;
		this.area[5][13].type = 1;
		this.area[5][14].type = 1;
		this.area[5][15].type = -1;
		this.area[5][16].type = 1;
		this.area[5][17].type = -1;
		this.area[5][18].type = 1;
		this.area[5][19].type = -1;
		this.area[5][20].type = 1;
		this.area[5][21].type = 1;
		this.area[5][22].type = 1;
		this.area[5][23].type = 1;
		this.area[5][24].type = 1;
		this.area[6][0].type = -1;
		this.area[6][1].type = -1;
		this.area[6][2].type = 1;
		this.area[6][3].type = 1;
		this.area[6][4].type = -1;
		this.area[6][5].type = -1;
		this.area[6][6].type = -1;
		this.area[6][7].type = -1;
		this.area[6][8].type = 1;
		this.area[6][9].type = -1;
		this.area[6][10].type = -1;
		this.area[6][11].type = 1;
		this.area[6][12].type = 1;
		this.area[6][13].type = -1;
		this.area[6][14].type = 1;
		this.area[6][15].type = 1;
		this.area[6][16].type = 1;
		this.area[6][17].type = 1;
		this.area[6][18].type = 1;
		this.area[6][19].type = 1;
		this.area[6][20].type = 1;
		this.area[6][21].type = 1;
		this.area[6][22].type = 1;
		this.area[6][23].type = 1;
		this.area[6][24].type = -1;
		this.area[7][0].type = 1;
		this.area[7][1].type = 1;
		this.area[7][2].type = 1;
		this.area[7][3].type = -1;
		this.area[7][4].type = -1;
		this.area[7][5].type = -1;
		this.area[7][6].type = -1;
		this.area[7][7].type = -1;
		this.area[7][8].type = 1;
		this.area[7][9].type = 1;
		this.area[7][10].type = 1;
		this.area[7][11].type = 1;
		this.area[7][12].type = 1;
		this.area[7][13].type = -1;
		this.area[7][14].type = -1;
		this.area[7][15].type = 1;
		this.area[7][16].type = 1;
		this.area[7][17].type = 1;
		this.area[7][18].type = 1;
		this.area[7][19].type = 1;
		this.area[7][20].type = 1;
		this.area[7][21].type = 1;
		this.area[7][22].type = -1;
		this.area[7][23].type = -1;
		this.area[7][24].type = -1;
		this.area[8][0].type = -1;
		this.area[8][1].type = -1;
		this.area[8][2].type = -1;
		this.area[8][3].type = -1;
		this.area[8][4].type = -1;
		this.area[8][5].type = -1;
		this.area[8][6].type = -1;
		this.area[8][7].type = 1;
		this.area[8][8].type = 1;
		this.area[8][9].type = 1;
		this.area[8][10].type = 1;
		this.area[8][11].type = 1;
		this.area[8][12].type = 1;
		this.area[8][13].type = 1;
		this.area[8][14].type = -1;
		this.area[8][15].type = -1;
		this.area[8][16].type = -1;
		this.area[8][17].type = -1;
		this.area[8][18].type = -1;
		this.area[8][19].type = -1;
		this.area[8][20].type = -1;
		this.area[8][21].type = -1;
		this.area[8][22].type = -1;
		this.area[8][23].type = -1;
		this.area[8][24].type = -1;
		this.area[9][0].type = -1;
		this.area[9][1].type = -1;
		this.area[9][2].type = -1;
		this.area[9][3].type = -1;
		this.area[9][4].type = -1;
		this.area[9][5].type = -1;
		this.area[9][6].type = -1;
		this.area[9][7].type = 1;
		this.area[9][8].type = -1;
		this.area[9][9].type = -1;
		this.area[9][10].type = -1;
		this.area[9][11].type = 1;
		this.area[9][12].type = 1;
		this.area[9][13].type = 1;
		this.area[9][14].type = -1;
		this.area[9][15].type = -1;
		this.area[9][16].type = -1;
		this.area[9][17].type = -1;
		this.area[9][18].type = -1;
		this.area[9][19].type = -1;
		this.area[9][20].type = -1;
		this.area[9][21].type = -1;
		this.area[9][22].type = -1;
		this.area[9][23].type = -1;
		this.area[9][24].type = -1;
		this.area[10][0].type = -1;
		this.area[10][1].type = -1;
		this.area[10][2].type = -1;
		this.area[10][3].type = -1;
		this.area[10][4].type = -1;
		this.area[10][5].type = -1;
		this.area[10][6].type = -1;
		this.area[10][7].type = 1;
		this.area[10][8].type = -1;
		this.area[10][9].type = -1;
		this.area[10][10].type = -1;
		this.area[10][11].type = 1;
		this.area[10][12].type = 1;
		this.area[10][13].type = 1;
		this.area[10][14].type = -1;
		this.area[10][15].type = -1;
		this.area[10][16].type = -1;
		this.area[10][17].type = -1;
		this.area[10][18].type = -1;
		this.area[10][19].type = -1;
		this.area[10][20].type = -1;
		this.area[10][21].type = -1;
		this.area[10][22].type = -1;
		this.area[10][23].type = -1;
		this.area[10][24].type = -1;
		this.area[11][0].type = -1;
		this.area[11][1].type = -1;
		this.area[11][2].type = -1;
		this.area[11][3].type = -1;
		this.area[11][4].type = -1;
		this.area[11][5].type = -1;
		this.area[11][6].type = -1;
		this.area[11][7].type = 1;
		this.area[11][8].type = -1;
		this.area[11][9].type = -1;
		this.area[11][10].type = -1;
		this.area[11][11].type = 1;
		this.area[11][12].type = 1;
		this.area[11][13].type = 1;
		this.area[11][14].type = -1;
		this.area[11][15].type = -1;
		this.area[11][16].type = -1;
		this.area[11][17].type = -1;
		this.area[11][18].type = -1;
		this.area[11][19].type = -1;
		this.area[11][20].type = -1;
		this.area[11][21].type = -1;
		this.area[11][22].type = -1;
		this.area[11][23].type = -1;
		this.area[11][24].type = -1;
		this.area[12][0].type = -1;
		this.area[12][1].type = -1;
		this.area[12][2].type = -1;
		this.area[12][3].type = -1;
		this.area[12][4].type = -1;
		this.area[12][5].type = -1;
		this.area[12][6].type = -1;
		this.area[12][7].type = 1;
		this.area[12][8].type = 1;
		this.area[12][9].type = 1;
		this.area[12][10].type = 1;
		this.area[12][11].type = 1;
		this.area[12][12].type = 1;
		this.area[12][13].type = 1;
		this.area[12][14].type = -1;
		this.area[12][15].type = -1;
		this.area[12][16].type = -1;
		this.area[12][17].type = -1;
		this.area[12][18].type = -1;
		this.area[12][19].type = -1;
		this.area[12][20].type = -1;
		this.area[12][21].type = -1;
		this.area[12][22].type = -1;
		this.area[12][23].type = -1;
		this.area[12][24].type = -1;
		this.area[13][0].type = -1;
		this.area[13][1].type = -1;
		this.area[13][2].type = -1;
		this.area[13][3].type = -1;
		this.area[13][4].type = -1;
		this.area[13][5].type = -1;
		this.area[13][6].type = 1;
		this.area[13][7].type = 1;
		this.area[13][8].type = 1;
		this.area[13][9].type = 1;
		this.area[13][10].type = 1;
		this.area[13][11].type = 1;
		this.area[13][12].type = 1;
		this.area[13][13].type = 1;
		this.area[13][14].type = 1;
		this.area[13][15].type = -1;
		this.area[13][16].type = -1;
		this.area[13][17].type = -1;
		this.area[13][18].type = -1;
		this.area[13][19].type = -1;
		this.area[13][20].type = -1;
		this.area[13][21].type = -1;
		this.area[13][22].type = -1;
		this.area[13][23].type = -1;
		this.area[13][24].type = -1;
		this.area[14][0].type = -1;
		this.area[14][1].type = -1;
		this.area[14][2].type = -1;
		this.area[14][3].type = -1;
		this.area[14][4].type = -1;
		this.area[14][5].type = 1;
		this.area[14][6].type = 1;
		this.area[14][7].type = 1;
		this.area[14][8].type = -1;
		this.area[14][9].type = 1;
		this.area[14][10].type = -1;
		this.area[14][11].type = 1;
		this.area[14][12].type = 1;
		this.area[14][13].type = 1;
		this.area[14][14].type = 1;
		this.area[14][15].type = 1;
		this.area[14][16].type = -1;
		this.area[14][17].type = -1;
		this.area[14][18].type = -1;
		this.area[14][19].type = -1;
		this.area[14][20].type = -1;
		this.area[14][21].type = -1;
		this.area[14][22].type = -1;
		this.area[14][23].type = -1;
		this.area[14][24].type = -1;
		this.area[15][0].type = -1;
		this.area[15][1].type = -1;
		this.area[15][2].type = -1;
		this.area[15][3].type = -1;
		this.area[15][4].type = 1;
		this.area[15][5].type = 1;
		this.area[15][6].type = 1;
		this.area[15][7].type = -1;
		this.area[15][8].type = 1;
		this.area[15][9].type = -1;
		this.area[15][10].type = 1;
		this.area[15][11].type = -1;
		this.area[15][12].type = 1;
		this.area[15][13].type = 1;
		this.area[15][14].type = 1;
		this.area[15][15].type = 1;
		this.area[15][16].type = 1;
		this.area[15][17].type = -1;
		this.area[15][18].type = -1;
		this.area[15][19].type = -1;
		this.area[15][20].type = -1;
		this.area[15][21].type = -1;
		this.area[15][22].type = -1;
		this.area[15][23].type = -1;
		this.area[15][24].type = -1;
		this.area[16][0].type = -1;
		this.area[16][1].type = -1;
		this.area[16][2].type = -1;
		this.area[16][3].type = 1;
		this.area[16][4].type = 1;
		this.area[16][5].type = 1;
		this.area[16][6].type = -1;
		this.area[16][7].type = 1;
		this.area[16][8].type = -1;
		this.area[16][9].type = 1;
		this.area[16][10].type = -1;
		this.area[16][11].type = 1;
		this.area[16][12].type = -1;
		this.area[16][13].type = 1;
		this.area[16][14].type = 1;
		this.area[16][15].type = 1;
		this.area[16][16].type = 1;
		this.area[16][17].type = 1;
		this.area[16][18].type = -1;
		this.area[16][19].type = -1;
		this.area[16][20].type = -1;
		this.area[16][21].type = -1;
		this.area[16][22].type = -1;
		this.area[16][23].type = -1;
		this.area[16][24].type = -1;
		this.area[17][0].type = -1;
		this.area[17][1].type = -1;
		this.area[17][2].type = -1;
		this.area[17][3].type = 1;
		this.area[17][4].type = 1;
		this.area[17][5].type = -1;
		this.area[17][6].type = 1;
		this.area[17][7].type = -1;
		this.area[17][8].type = 1;
		this.area[17][9].type = -1;
		this.area[17][10].type = 1;
		this.area[17][11].type = -1;
		this.area[17][12].type = 1;
		this.area[17][13].type = -1;
		this.area[17][14].type = 1;
		this.area[17][15].type = 1;
		this.area[17][16].type = 1;
		this.area[17][17].type = 1;
		this.area[17][18].type = -1;
		this.area[17][19].type = -1;
		this.area[17][20].type = -1;
		this.area[17][21].type = -1;
		this.area[17][22].type = -1;
		this.area[17][23].type = -1;
		this.area[17][24].type = -1;
		this.area[18][0].type = -1;
		this.area[18][1].type = -1;
		this.area[18][2].type = 1;
		this.area[18][3].type = 1;
		this.area[18][4].type = 1;
		this.area[18][5].type = 1;
		this.area[18][6].type = 1;
		this.area[18][7].type = 1;
		this.area[18][8].type = 1;
		this.area[18][9].type = 1;
		this.area[18][10].type = 1;
		this.area[18][11].type = 1;
		this.area[18][12].type = 1;
		this.area[18][13].type = 1;
		this.area[18][14].type = 1;
		this.area[18][15].type = 1;
		this.area[18][16].type = 1;
		this.area[18][17].type = 1;
		this.area[18][18].type = 1;
		this.area[18][19].type = -1;
		this.area[18][20].type = -1;
		this.area[18][21].type = -1;
		this.area[18][22].type = -1;
		this.area[18][23].type = -1;
		this.area[18][24].type = -1;
		this.area[19][0].type = -1;
		this.area[19][1].type = -1;
		this.area[19][2].type = 1;
		this.area[19][3].type = -1;
		this.area[19][4].type = -1;
		this.area[19][5].type = -1;
		this.area[19][6].type = -1;
		this.area[19][7].type = -1;
		this.area[19][8].type = -1;
		this.area[19][9].type = -1;
		this.area[19][10].type = -1;
		this.area[19][11].type = -1;
		this.area[19][12].type = -1;
		this.area[19][13].type = -1;
		this.area[19][14].type = -1;
		this.area[19][15].type = 1;
		this.area[19][16].type = 1;
		this.area[19][17].type = 1;
		this.area[19][18].type = 1;
		this.area[19][19].type = -1;
		this.area[19][20].type = -1;
		this.area[19][21].type = -1;
		this.area[19][22].type = -1;
		this.area[19][23].type = -1;
		this.area[19][24].type = -1;
		this.area[20][0].type = -1;
		this.area[20][1].type = -1;
		this.area[20][2].type = 1;
		this.area[20][3].type = -1;
		this.area[20][4].type = -1;
		this.area[20][5].type = -1;
		this.area[20][6].type = -1;
		this.area[20][7].type = -1;
		this.area[20][8].type = -1;
		this.area[20][9].type = -1;
		this.area[20][10].type = -1;
		this.area[20][11].type = -1;
		this.area[20][12].type = -1;
		this.area[20][13].type = -1;
		this.area[20][14].type = -1;
		this.area[20][15].type = 1;
		this.area[20][16].type = 1;
		this.area[20][17].type = 1;
		this.area[20][18].type = 1;
		this.area[20][19].type = -1;
		this.area[20][20].type = -1;
		this.area[20][21].type = -1;
		this.area[20][22].type = -1;
		this.area[20][23].type = -1;
		this.area[20][24].type = -1;
		this.area[21][0].type = -1;
		this.area[21][1].type = -1;
		this.area[21][2].type = 1;
		this.area[21][3].type = -1;
		this.area[21][4].type = -1;
		this.area[21][5].type = 1;
		this.area[21][6].type = 1;
		this.area[21][7].type = -1;
		this.area[21][8].type = 1;
		this.area[21][9].type = 1;
		this.area[21][10].type = -1;
		this.area[21][11].type = -1;
		this.area[21][12].type = -1;
		this.area[21][13].type = -1;
		this.area[21][14].type = -1;
		this.area[21][15].type = 1;
		this.area[21][16].type = 1;
		this.area[21][17].type = 1;
		this.area[21][18].type = 1;
		this.area[21][19].type = -1;
		this.area[21][20].type = -1;
		this.area[21][21].type = -1;
		this.area[21][22].type = -1;
		this.area[21][23].type = -1;
		this.area[21][24].type = -1;
		this.area[22][0].type = 1;
		this.area[22][1].type = 1;
		this.area[22][2].type = 1;
		this.area[22][3].type = -1;
		this.area[22][4].type = -1;
		this.area[22][5].type = -1;
		this.area[22][6].type = 1;
		this.area[22][7].type = -1;
		this.area[22][8].type = 1;
		this.area[22][9].type = -1;
		this.area[22][10].type = -1;
		this.area[22][11].type = -1;
		this.area[22][12].type = -1;
		this.area[22][13].type = -1;
		this.area[22][14].type = -1;
		this.area[22][15].type = 1;
		this.area[22][16].type = 1;
		this.area[22][17].type = 1;
		this.area[22][18].type = 1;
		this.area[22][19].type = 1;
		this.area[22][20].type = 1;
		this.area[22][21].type = 1;
		this.area[22][22].type = 1;
		this.area[22][23].type = 1;
		this.area[22][24].type = 1;
		this.area[23][0].type = -1;
		this.area[23][1].type = 1;
		this.area[23][2].type = 1;
		this.area[23][3].type = 1;
		this.area[23][4].type = -1;
		this.area[23][5].type = 1;
		this.area[23][6].type = 1;
		this.area[23][7].type = -1;
		this.area[23][8].type = 1;
		this.area[23][9].type = 1;
		this.area[23][10].type = -1;
		this.area[23][11].type = -1;
		this.area[23][12].type = -1;
		this.area[23][13].type = -1;
		this.area[23][14].type = -1;
		this.area[23][15].type = 1;
		this.area[23][16].type = 1;
		this.area[23][17].type = 1;
		this.area[23][18].type = 1;
		this.area[23][19].type = 1;
		this.area[23][20].type = -1;
		this.area[23][21].type = -1;
		this.area[23][22].type = 1;
		this.area[23][23].type = 1;
		this.area[23][24].type = -1;
		this.area[24][0].type = -1;
		this.area[24][1].type = -1;
		this.area[24][2].type = 1;
		this.area[24][3].type = 1;
		this.area[24][4].type = -1;
		this.area[24][5].type = -1;
		this.area[24][6].type = -1;
		this.area[24][7].type = -1;
		this.area[24][8].type = -1;
		this.area[24][9].type = -1;
		this.area[24][10].type = -1;
		this.area[24][11].type = -1;
		this.area[24][12].type = -1;
		this.area[24][13].type = -1;
		this.area[24][14].type = 1;
		this.area[24][15].type = 1;
		this.area[24][16].type = 1;
		this.area[24][17].type = 1;
		this.area[24][18].type = -1;
		this.area[24][19].type = 1;
		this.area[24][20].type = 1;
		this.area[24][21].type = -1;
		this.area[24][22].type = -1;
		this.area[24][23].type = 1;
		this.area[24][24].type = 1;
		this.area[25][0].type = 1;
		this.area[25][1].type = -1;
		this.area[25][2].type = -1;
		this.area[25][3].type = 1;
		this.area[25][4].type = 1;
		this.area[25][5].type = -1;
		this.area[25][6].type = -1;
		this.area[25][7].type = -1;
		this.area[25][8].type = -1;
		this.area[25][9].type = -1;
		this.area[25][10].type = -1;
		this.area[25][11].type = -1;
		this.area[25][12].type = -1;
		this.area[25][13].type = -1;
		this.area[25][14].type = 1;
		this.area[25][15].type = 1;
		this.area[25][16].type = 1;
		this.area[25][17].type = 1;
		this.area[25][18].type = -1;
		this.area[25][19].type = -1;
		this.area[25][20].type = 1;
		this.area[25][21].type = 1;
		this.area[25][22].type = -1;
		this.area[25][23].type = -1;
		this.area[25][24].type = 1;
		this.area[26][0].type = 1;
		this.area[26][1].type = 1;
		this.area[26][2].type = -1;
		this.area[26][3].type = -1;
		this.area[26][4].type = 1;
		this.area[26][5].type = 1;
		this.area[26][6].type = -1;
		this.area[26][7].type = 1;
		this.area[26][8].type = -1;
		this.area[26][9].type = 1;
		this.area[26][10].type = -1;
		this.area[26][11].type = 1;
		this.area[26][12].type = -1;
		this.area[26][13].type = 1;
		this.area[26][14].type = 1;
		this.area[26][15].type = 1;
		this.area[26][16].type = 1;
		this.area[26][17].type = 1;
		this.area[26][18].type = 1;
		this.area[26][19].type = -1;
		this.area[26][20].type = -1;
		this.area[26][21].type = 1;
		this.area[26][22].type = 1;
		this.area[26][23].type = -1;
		this.area[26][24].type = -1;
		this.area[27][0].type = -1;
		this.area[27][1].type = 1;
		this.area[27][2].type = 1;
		this.area[27][3].type = -1;
		this.area[27][4].type = -1;
		this.area[27][5].type = 1;
		this.area[27][6].type = 1;
		this.area[27][7].type = -1;
		this.area[27][8].type = 1;
		this.area[27][9].type = -1;
		this.area[27][10].type = 1;
		this.area[27][11].type = -1;
		this.area[27][12].type = 1;
		this.area[27][13].type = 1;
		this.area[27][14].type = 1;
		this.area[27][15].type = 1;
		this.area[27][16].type = 1;
		this.area[27][17].type = -1;
		this.area[27][18].type = 1;
		this.area[27][19].type = 1;
		this.area[27][20].type = -1;
		this.area[27][21].type = -1;
		this.area[27][22].type = 1;
		this.area[27][23].type = 1;
		this.area[27][24].type = -1;
		this.area[28][0].type = -1;
		this.area[28][1].type = -1;
		this.area[28][2].type = 1;
		this.area[28][3].type = 1;
		this.area[28][4].type = -1;
		this.area[28][5].type = -1;
		this.area[28][6].type = 1;
		this.area[28][7].type = 1;
		this.area[28][8].type = 1;
		this.area[28][9].type = 1;
		this.area[28][10].type = 1;
		this.area[28][11].type = 1;
		this.area[28][12].type = 1;
		this.area[28][13].type = 1;
		this.area[28][14].type = 1;
		this.area[28][15].type = 1;
		this.area[28][16].type = 1;
		this.area[28][17].type = -1;
		this.area[28][18].type = -1;
		this.area[28][19].type = 1;
		this.area[28][20].type = 1;
		this.area[28][21].type = -1;
		this.area[28][22].type = -1;
		this.area[28][23].type = 1;
		this.area[28][24].type = 1;
		this.area[29][0].type = -1;
		this.area[29][1].type = -1;
		this.area[29][2].type = -1;
		this.area[29][3].type = 1;
		this.area[29][4].type = 1;
		this.area[29][5].type = -1;
		this.area[29][6].type = -1;
		this.area[29][7].type = 1;
		this.area[29][8].type = 1;
		this.area[29][9].type = 1;
		this.area[29][10].type = 1;
		this.area[29][11].type = 1;
		this.area[29][12].type = 1;
		this.area[29][13].type = 1;
		this.area[29][14].type = 1;
		this.area[29][15].type = -1;
		this.area[29][16].type = 1;
		this.area[29][17].type = 1;
		this.area[29][18].type = -1;
		this.area[29][19].type = -1;
		this.area[29][20].type = 1;
		this.area[29][21].type = 1;
		this.area[29][22].type = -1;
		this.area[29][23].type = -1;
		this.area[29][24].type = 1;

	}
}
