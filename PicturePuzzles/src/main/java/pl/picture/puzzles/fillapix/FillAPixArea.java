package pl.picture.puzzles.fillapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.picture.puzzles.common.PuzzleUtilities;

public class FillAPixArea {
	public static final byte EMPTY = 0; // Puste pole
	public static final byte ABSENCE = -1; // Nieoznaczone pole
	public static final byte SELECTED = 1; // Zaznaczone

	public Field[][] area;
	public ArrayList<FaPNumber> numbers;
	public int x = 0, y = 0; // Wymiary lamiglowki

	private Field[][] tmp;

	public FillAPixArea(File f) {
		init(f);
	}

	private void init(File f) {

		String areaFile = "";
		numbers = new ArrayList<FaPNumber>();
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
						area[i][j].number = Byte.parseByte(String
								.valueOf(areaFile.charAt(i * x + j)));
						addToNumberList(new FaPNumber(area[i][j].number, i, j));
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Tworzenie listy numerow z informacja czy dany numer lezy na rogu lub
	// krawedzi
	private void addToNumberList(FaPNumber number) {
		// Lewy gorny rog / prawy gorny rog / lewy dony rog / prawy dolny rog
		if ((number.i == 0 && number.j == 0)
				|| (number.i == 0 && number.j == x - 1)
				|| (number.i == y - 1 && number.j == 0)
				|| (number.i == y - 1 && number.j == x - 1)) {

			number.numberEmpty = 5;
			this.numbers.add(number);
			return;
		}

		// Lewa kolumna / prawa kolumna / gorny wiersz / dolny wiersz
		if ((number.j == 0) || (number.j == x - 1) || (number.i == 0)
				|| (number.i == y - 1)) {

			number.numberEmpty = 3;
			this.numbers.add(number);
			return;
		}

		this.numbers.add(number);

	}

	// liczenie pokolorowanych pol
	private int countColored(FaPNumber number) {
		int i = number.i, j = number.j;
		number.numberSelected = 0;
		for (int y = i - 1; y <= i + 1; y++) {
			if (y != -1 && y != this.y) {
				for (int x = j - 1; x <= j + 1; x++) {
					if (x != -1 && x != this.x) {
						// System.out.println(y + ", " + x);
						if (area[y][x].val == SELECTED) {
							number.numberSelected++;
						}
					}
				}
			}

		}

		return number.numberSelected;
	}

	// Zliczanie pol oznacznoych jako PUSTE i ZANZNACZONE, zwraca true jezeli
	// się cos zmienilo, false w p.p.
	private boolean countMarkedFields(final FaPNumber number) {
		byte tmpCountEmpty = number.numberEmpty, tmpCountSelected = number.numberSelected;

		number.numberEmpty = 0;
		number.numberSelected = 0;
		for (int i = number.i - 1; i <= number.i + 1; i++) {
			for (int j = number.j - 1; j <= number.j + 1; j++) {
				if (i < 0 || j < 0 || i == y || j == x) {
					number.numberEmpty++;
				} else if (this.area[i][j].val == EMPTY) {
					number.numberEmpty++;
				} else if (this.area[i][j].val == SELECTED) {
					number.numberSelected++;
				}
			}
		}

		return tmpCountEmpty != number.numberEmpty
				|| tmpCountSelected != number.numberSelected ? true : false;
	}

	// Sprawdzanie czy gra zostala poprawnie rozwiazana
	public boolean checkSolve() {
		for (FaPNumber number : numbers) {
			if (number.value != countColored(number)) {
				return false;
			}
		}
		return true;
	}

	// Klasa reprezentujaca pole (kratke do zanzaczenia)
	class Field {
		public Byte val = -1; // -1: BRAK, 0: PUSTE, 1: ZAZNACZONE
		public Byte number; // Liczba
		public FaPNumber belongsToNumber;

		public Field() {

		}

		public Field(Field field) {
			this.number = field.number;
			this.val = field.val;
		}
	}

	// Klasa reprezentujaca liczbe, ktora okresla ile pol nalezy zaznaczyc
	class FaPNumber {
		public final byte value; // Wartosc liczby
		public byte numberSelected; // Ilosc pol zaznaczonych wokol liczby
		public byte numberEmpty; // Ilosc pol niezaznaczonych / pustych wokol
									// liczby
		public int npm; // Ilosc mozliwych ruchow (Number Possible Moves)
		public final int i, j; // pozycja liczny

		public FaPNumber bestNumber; // Liczba, ktora wchodzi w "relacje" z ta
										// liczba o najwyzszym
										// prawdopodobienstwu
		public byte numberBestNumber; // Ilosc Liczb o najwyzszym
										// prawdopodobienstwu

		public FaPNumber(byte value, int i, int j) {
			this.value = value;
			this.i = i;
			this.j = j;
		}

		public FaPNumber(FaPNumber number) {
			this.value = number.value;
			this.numberSelected = number.numberSelected;
			this.numberEmpty = number.numberEmpty;
			this.i = number.i;
			this.j = number.j;
		}
	}

	// Zamalowanie wszystkich pol pusty na szaro oraz przygotowanie kopii
	// zapasowej areny
	public void beforeCheck() {
		this.tmp = new Field[this.y][this.x];

		for (int i = 0; i < this.y; i++) {
			for (int j = 0; j < this.x; j++) {
				this.tmp[i][j] = this.new Field(this.area[i][j]);

				if (this.area[i][j].val == ABSENCE) {
					this.area[i][j].val = EMPTY;
				}
			}
		}
	}

	// Przyrocenie areny
	public void restoreArea() {
		this.area = this.tmp.clone();
		this.tmp = null;
	}

	public boolean solvePuzzle() {
		ArrayList<FaPNumber> tmpNumbers = new ArrayList<FaPNumber>();
		ArrayList<FaPNumber> numbersToRemove = new ArrayList<FaPNumber>();
		boolean changeFlag = false; // Flaga informujaca czy podczas iteracji
									// petli while nastapila jakas zmiana

		// Kopiowanie numbers do tmpNumbers
		for (FaPNumber number : this.numbers) {
			tmpNumbers.add(new FaPNumber(number));
		}

		// Rozpoczęcie odmierzania czasu rozwiazywania lamiglowki
		long startCountTimeElapsed = System.currentTimeMillis();

		do {
			changeFlag = false;
			for (FaPNumber number : tmpNumbers) {
				// Jezeli liczba = 9 oznacz wszystko na ZANZNACZONE
				if (number.value == 9) {
					selectFields(number, SELECTED);
					changeFlag = true;
					numbersToRemove.add(number);
					continue;
				}
				// Jezeli liczba = 0 oznacz wszystko na PUSTE
				if (number.value == 0) {
					selectFields(number, EMPTY);
					changeFlag = true;
					numbersToRemove.add(number);
					continue;
				}
				// Lewy gorny rog / prawy gorny rog / lewy dony rog /
				// prawy dolny rog
				if ((number.i == 0 && number.j == 0)
						|| (number.i == 0 && number.j == x - 1)
						|| (number.i == y - 1 && number.j == 0)
						|| (number.i == y - 1 && number.j == x - 1)) {

					// Jezeli rob ma liczbe 4 oznacz wszystko na ZANZACZONE
					if (number.value == 4) {
						selectFields(number, SELECTED);
						changeFlag = true;
						numbersToRemove.add(number);
						continue;
					}
				}

				// Lewa kolumna / prawa kolumna / gorny wiersz / dolny wiersz
				if ((number.j == 0) || (number.j == x - 1) || (number.i == 0)
						|| (number.i == y - 1)) {

					// Jezeli krawedz ma liczbe 6 oznacz wszystko na ZANZACZONE
					if (number.value == 6) {
						selectFields(number, SELECTED);
						changeFlag = true;
						numbersToRemove.add(number);
						continue;
					}
				}

				// Liczenie pol oznaczonych na PUSTE i ZAZNACZONE, jezeli sa
				// roznice zwraca flaga sygnalizujaca miana jest ustawiana na
				// true
				if (countMarkedFields(number)) {
					changeFlag = true;
				}

				// Jezeli liczba pol ZANZACZONE jest rowna liczbie to reszte
				// oznacz na PUSTE
				if (number.numberSelected == number.value) {
					if (number.numberEmpty != 9 - number.numberSelected) {
						selectFields(number, EMPTY);
					}
					changeFlag = true;
					numbersToRemove.add(number);
					continue;
				}

				// Jezeli liczba pol ktore nie sa ozaczone (maja status PUSTE)
				// jest rowna liczbe to oznacz pozostale pola na ZAZNACZONE
				if (9 - number.numberEmpty == number.value) {
					if (number.numberSelected != number.value) {
						selectFields(number, SELECTED);
					}
					changeFlag = true;
					numbersToRemove.add(number);
					continue;
				}

			}
			tmpNumbers.removeAll(numbersToRemove);
			// Jezeli nie ma zadnych cyfr, które posiadaja taka sama ilosc
			// pol niepokolorowanych co wartosc cyfry oraz nie ma cyfr, ktore
			// posiadaja juz taka sama ilosc pokolorowanych pol co ich wartosc,
			// to wykonaj zaawanasowane kolorowanie
			if (!changeFlag) {
				changeFlag = advanceColoring(tmpNumbers);
			}

		} while (tmpNumbers.size() > 0 && changeFlag);
		// Zakonczenie odmierzania czasu

		if (!changeFlag)
			System.out
					.println("ALGORYTM: Nie potrafie rozwiazac lamiglowki ;(");

		PuzzleUtilities.showTimeElapsed(System.currentTimeMillis()
				- startCountTimeElapsed);

		return checkSolve();
	}

	// Zaawansowane kolorowanie lamiglowki
	private boolean advanceColoring(ArrayList<FaPNumber> numbers) {

		// Liczenie mozliwych pokolorowan
		countingPossibleMoves(numbers);

		return false;
	}

	// Liczenie mozliwych pokoloorowan dla pol danej cyfry, jezeli dane pole juz
	// nalezy do jakiejs cyfry ale ilosc mozliwych pokolorowan jest mniejsza
	// pole zostanie przypisane do cyfry o mniejszej ilosc pokolorowan
	private void countingPossibleMoves(ArrayList<FaPNumber> numbers) {
		for (FaPNumber number : numbers) {
			int availableFields = 9 - number.numberEmpty
					- number.numberSelected;
			int numberFieldToColor = number.value - number.numberSelected;

			number.npm = PuzzleUtilities.c(availableFields, numberFieldToColor);

			int i = number.i - 1;

			if (i < 0)
				i = 0;

			for (; i < this.y && i <= number.i + 1; i++) {
				int j = number.j - 1;

				if (j < 0)
					j = 0;
				for (; j < this.x && j <= number.j + 1; j++) {

					// Jezeli pole nie jest pokolorowane
					if (this.area[i][j].val == ABSENCE) {
						// Jezeli pole nie nalezy do zadnej cyfry to przypisz do
						// aktualnie iterowanej cyfry
						if (this.area[i][j].belongsToNumber == null) {
							this.area[i][j].belongsToNumber = number;

							// Jezel aktualna cyfra nie posiada cyfry o
							// najwyzszym prawdopodobienstwie to przywpisz do
							// niej ja sama i ustaw ilosc cyfr o najwyzszym
							// prawdopodobienstwie na 1, w przeciwnym przypadku
							// zwiesz licznik cyfr o nejwyzszym
							// prawdopodobienstwie
							if (number.bestNumber == null) {
								number.bestNumber = number;
								number.numberBestNumber = 1;
							} else if (number.bestNumber == number) {
								number.numberBestNumber++;
							}
							// Jezeli pole przynalezy do liczby o mniejszym
							// prawdopodobienstwie od aktualnie iterowanej to
							// wykonaj:
						} else if (this.area[i][j].belongsToNumber.npm > number.npm
								|| (this.area[i][j].belongsToNumber.npm == number.npm && this.area[i][j].belongsToNumber.value
										- this.area[i][j].belongsToNumber.numberSelected > number.value
										- number.numberSelected)) {
							// Jezeli do pola przynalezy cyfra inna niz
							// aktualnie iterowana to przypisz ustawa cyfrze tej
							// te aktualnie iterowana, w p.p. zwieksz jej ilosc
							// cyfr o najwyzszym prawdopodobienstwie
							if (this.area[i][j].belongsToNumber.bestNumber != number) {
								this.area[i][j].belongsToNumber.bestNumber = number;
								this.area[i][j].belongsToNumber.numberBestNumber = 1;
							} else if (this.area[i][j].belongsToNumber.bestNumber == number) {
								this.area[i][j].belongsToNumber.numberBestNumber++;
							}

							// Jezeli aktualnie iterowana cyfra nie posiada
							// najlepszej cyfry to ustaw ja sama, w p.p. zwieksz
							// ilosc cyfr o najwyzszym prawdopodobienstwie
							if (number.bestNumber == null) {
								number.bestNumber = number;
								number.numberBestNumber = 1;
							} else if (number.bestNumber == number) {
								number.numberBestNumber++;
							}
							// Jezeli pole przynalezy do liczby o wiekszym
							// pradpopodobiestwie to wykonaj:
						} else {
							// Jezeli aktualnie iterowana cyfra nie ma
							// ustawionej cyfry o najwyzszym prawdopodobienstwu
							// ustaw ja przypisjac cyfre do ktorej nalezy pole,
							if (number.bestNumber == null) {
								number.bestNumber = this.area[i][j].belongsToNumber;
								number.numberBestNumber = 1;

								// Jezeli aktualnie iterowana cyfra ma ustawiona
								// taka sama cyfre o najwyzszym
								// prawdopodobienstwie co cyfra ktora nalezy do
								// pola to zwieszk licznik cyfr o najwyzszym
								// prawdopodobienstwie
							} else if (number.bestNumber == this.area[i][j].belongsToNumber) {
								number.numberBestNumber++;

								// Jezeli aktualnie iterowana cyfra posiada
								// najlepsza cyfre gorsza od tej ktora nalezy do
								// pola to przypisz najlepsza cyfre ktora nalezy
								// do pola
							} else if (number.bestNumber.npm > this.area[i][j].belongsToNumber.npm
									|| (number.bestNumber.npm == this.area[i][j].belongsToNumber.npm && number.bestNumber.value
											- number.bestNumber.numberSelected > this.area[i][j].belongsToNumber.value
											- this.area[i][j].belongsToNumber.numberSelected)) {
								number.bestNumber = this.area[i][j].belongsToNumber;
								number.numberBestNumber = 1;
							}

						}
					}

					// Jezeli pole nie jest pokolorowane oraz (pole nie należy
					// do żadnej cyfry lub pole nalezy do cyfry o gorszym
					// prawdopodobienstwem lub (cyfra do której nalezy pole ma
					// takie samo prawdopodobienstwo ale cyfra ta ma wiecej pol
					// pozostalych do zakolorowania)) wtedy
					// przypisz pole do nowej cyfry
					// if (this.area[i][j].val == ABSENCE
					// && (this.area[i][j].belongsToNumber == null
					// || this.area[i][j].belongsToNumber.npm > number.npm ||
					// (this.area[i][j].belongsToNumber.npm == number.npm &&
					// this.area[i][j].belongsToNumber.value
					// - this.area[i][j].belongsToNumber.numberSelected >
					// number.value
					// - number.numberSelected))) {
					// this.area[i][j].belongsToNumber = number;
					//
					// }
				}
			}
		}
	}

	// Kolorowanie pol niepokolorowanyh na "PUSTE" lub "ZAZNACZONE"
	private void selectFields(final FaPNumber number, byte color) {
		int i = number.i - 1;

		if (i < 0)
			i = 0;

		for (; i < this.y && i <= number.i + 1; i++) {
			int j = number.j - 1;

			if (j < 0)
				j = 0;
			for (; j < this.x && j <= number.j + 1; j++) {
				if (this.area[i][j].val == ABSENCE) {
					this.area[i][j].val = color;
					this.area[i][j].belongsToNumber = null;
				}
			}
		}

	}
}
