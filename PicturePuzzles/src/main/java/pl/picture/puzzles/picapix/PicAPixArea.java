package pl.picture.puzzles.picapix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;

import pl.picture.puzzles.common.PuzzleUtilities;

public class PicAPixArea {

	public static final byte EMPTY = 0; // Pole oznaczone jako puste
	public static final byte SELECTED = 1; // Pole pokolorowane
	public static final byte ABSENCE = -1; // Pole nie pokolorowane

	public Field[][] area; // tablica reprezentujaca plansze
	public int x = 0, y = 0; // wymiary planszy
	public List<ListOfNumber> verticalListsOfNumbers; // Lista liczb pionowych
	public List<ListOfNumber> horizontalListsOfNumbers; // Lista liczb poziomych

	public int maxVerticalNumbers, maxHorizontalNumbers;

	private boolean change;

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
					PaNumber number = new PaNumber(Byte.parseByte(numbers[j]),
							j);
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
					PaNumber number = new PaNumber(Byte.parseByte(numbers[j]),
							j);
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

	public boolean solvePuzzle(JPanel panel) {
		int k;
		// Rozpoczęcie odmierzania czasu rozwiazywania lamiglowki
		long startCountTimeElapsed = System.currentTimeMillis();
		try {
			// System.setOut(new PrintStream(new File("output-file.txt")));
			change = false;
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

			k = 0;

			// panel.repaint();
			// JOptionPane.showMessageDialog(panel, k, "Info",
			// JOptionPane.INFORMATION_MESSAGE);
			// while (k < 24) {
			while (change) {
				k++;
				change = false;
				i = 0;
				for (ListOfNumber verticalList : this.verticalListsOfNumbers) {

					if (verticalList.otherNumbers == 0) {
						i++;
						continue;
					}
					determiningOfLengths(verticalList, true, i++);
				}

				// Linie poziome
				i = 0;
				for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {
					if (horizontalList.otherNumbers == 0) {
						i++;
						continue;
					}
					determiningOfLengths(horizontalList, false, i++);
				}

				// DEBUG
				// i = 0;
				// System.out
				// .println(k
				// +
				// ": ------------------------------------------------------------");
				// for (ListOfNumber verticalList : this.verticalListsOfNumbers)
				// {
				// printSpecOfLengths(verticalList, i++);
				// }
				// i = 0;
				// for (ListOfNumber horizontalList :
				// this.horizontalListsOfNumbers) {
				// printSpecOfLengths(horizontalList, i++);
				// }
				// panel.repaint();
				// JOptionPane.showMessageDialog(panel, k, "Info",
				// JOptionPane.INFORMATION_MESSAGE);

			}

		} catch (Exception e) {
			// DEBUG
			System.out
					.println("##------------------------------------------------------------");
			int i = 0;
			for (ListOfNumber verticalList : this.verticalListsOfNumbers) {
				printSpecOfLengths(verticalList, i++);
			}
			i = 0;
			for (ListOfNumber horizontalList : this.horizontalListsOfNumbers) {
				printSpecOfLengths(horizontalList, i++);
			}
			// System.out.print(e);
			e.printStackTrace();
			return false;
		}
		PuzzleUtilities.showTimeElapsed(System.currentTimeMillis()
				- startCountTimeElapsed);
		System.out.println("Wykonano " + k + " prob rozwiazania lamiglowki!");
		return checkPuzzle();
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

			// wyznaczenie N
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
						if (firstNumber.scope == null) {
							firstNumber.scope = new int[2];
						}
						firstNumber.scope[0] = 0;
						firstNumber.scope[1] = firstNumber.val - 1;
						numberListPrim.otherNumbers--;

						if (numberListPrim.otherNumbers == 0) {
							selectAbsenceToEmpty(isVertical, i);
						}

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

						if (lastNumber.scope == null) {
							lastNumber.scope = new int[2];
						}

						lastNumber.scope[0] = nPrim - lastNumber.val;
						lastNumber.scope[1] = nPrim - 1;
						numberListPrim.otherNumbers--;
						if (numberListPrim.otherNumbers == 0) {
							selectAbsenceToEmpty(isVertical, i);
						}
					}
				}
				change = true;
			}

			// Wyznaczanie zasięgu liczby
			if (paNumber.scope == null) {
				paNumber.scope = new int[2];
				paNumber.scope[0] = startPosition;
				paNumber.scope[1] = n
						- (numberList.sumOfNumbers - startPosition
								- paNumber.val + numberList.numbers.size());
			}
			/*
			 * System.out.print(paNumber.val + ": [" + paNumber.scope[0] + "," +
			 * paNumber.scope[1] + "] ");
			 */
			startPosition += paNumber.val + 1;
		}
		// System.out.println("\n");
	}

	private void selectAbsenceToEmpty(boolean isVertical, int i) {
		int n = 0;
		if (isVertical) {
			n = this.y;
		} else {
			n = this.x;
		}

		for (int j = 0; j < n; j++) {
			Field field;
			if (isVertical) {
				field = this.area[j][i];
			} else {
				field = this.area[i][j];
			}

			if (field.type == ABSENCE) {
				field.changeType(EMPTY);
			}
		}
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
			// numbers.get(i + 1).first = true;

			for (int k = i + 1; k < numbers.size() - 1; k++) {
				PaNumber number = numbers.get(k);
				if (!number.enable) {
					number.first = false;
					continue;
				}

				if (number.enable) {
					number.first = true;
					break;
				}
			}

			setNumber.enable = false;
		}

		if (setNumber.last) {
			// numbers.get(i - 1).last = true;

			for (int k = i - 1; k >= 0; k--) {
				PaNumber number = numbers.get(k);
				if (!number.enable) {
					number.last = false;
					continue;
				}

				if (number.enable) {
					number.last = true;
					break;
				}
			}
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
					if (!l.isComplete)
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
			if (!l.isComplete)
				numberList.spaceLengths.add(l);
		}

		actionOnLengths(numberList, isVertical, i);
		tryDrawLength(numberList, isVertical, i);

	}

	// DEBUG
	private void printSpecOfLengths(ListOfNumber numberList, int i) {
		// DEBUG
		if (numberList.selectedLengths == null) {
			return;
		}

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
					belongsTo = belongsTo + number + ", ";
				}
			}

			System.out.println(i + ": " + open + l.s + ", " + l.e + close
					+ belongsTo);
		}
		if (numberList.spaceLengths == null) {
			return;
		}
		for (Length l : numberList.spaceLengths) {
			String open, close;

			open = "(";
			close = ")";
			String belongsTo;
			if (l.listOfNumbersToBelong == null) {
				belongsTo = " nalezy do: NULL";
			} else {
				belongsTo = " nelezy do: ";
				for (PaNumber number : l.listOfNumbersToBelong) {
					belongsTo = belongsTo + number + ", ";
				}
			}
			System.out.println(i + ": " + open + l.s + ", " + l.e + close
					+ belongsTo);
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

		// Sprawdzanie czy pierwszy odcinek odpowiada długości pierwszej liczby,
		// jeżeli tak to zmniejsz jej zasięg, to samo z ostanim odcinkiem i
		// ostatnią liczbą
		if (size > 1) {
			Length firstLength = lengths.get(0);
			Length lastLength = lengths.get(size - 1);

			PaNumber firstNumber = null, lastNumber = null;
			for (PaNumber number : numberList.numbers) {
				if (number.enable) {
					if (number.first && firstNumber == null) {
						firstNumber = number;
					}
					if (number.last && lastNumber == null) {
						lastNumber = number;
					}
				}
			}

			if (firstNumber != null
					// && firstLength.e - firstLength.s + 1 == firstNumber.val
					&& firstNumber.scope[0] <= firstLength.s
					&& firstNumber.scope[1] >= firstLength.e) {

				// firstNumber.changeScopeRight(numberList.numbers,
				// firstLength.e);
				firstNumber.changeScopeRight(numberList.numbers, firstLength.s
						+ firstNumber.val - 1);
				changeBelongsToNumberForLengths(lengths, firstNumber);
			}

			if (lastNumber != null
					// && lastLength.e - lastLength.s + 1 == lastNumber.val
					&& lastNumber.scope[0] <= lastLength.s
					&& lastNumber.scope[1] >= lastLength.e) {

				// lastNumber.changeScopeLeft(numberList.numbers, lastLength.s);
				lastNumber.changeScopeLeft(numberList.numbers, lastLength.e
						- lastNumber.val + 1);
				changeBelongsToNumberForLengths(lengths, lastNumber);
			}
		}

		while (k < size) {
			Length l = lengths.get(k);

			if (l.isComplete) {
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
								this.area[j][i].changeType(SELECTED);

							} else {
								this.area[i][j].changeType(SELECTED);

							}
						}
						// Scalenie odcinka l z lNext
						l.e = lNext.e;
						lengths.remove(lNext);
						size--;
					}
				}
				// Pobranie referancji do liczby zależnej od odcinka
				PaNumber numberOfL = l.listOfNumbersToBelong.get(0);

				// Sprawdzenie czy zmienia się zasięg liczby
				if (l.s + numberOfL.val - 1 < numberOfL.scope[1]
						|| l.e - numberOfL.val + 1 > numberOfL.scope[0]) {

					if (l.e - numberOfL.val + 1 >= 0) {
						// numberOfL.scope[0] = l.e - numberOfL.val + 1;
						numberOfL.changeScopeLeft(numberList.numbers, l.e
								- numberOfL.val + 1);
					}

					if (l.s + numberOfL.val - 1 <= n - 1) {
						// numberOfL.scope[1] = l.s + numberOfL.val - 1;
						numberOfL.changeScopeRight(numberList.numbers, l.s
								+ numberOfL.val - 1);
					}

					changeBelongsToNumberForLengths(lengths, numberOfL);

				}

				tryDrawMinLength(l, numberOfL.val, isVertical, n, i);
			}

			if (l.listOfNumbersToBelong.size() > 1) {

				int val = l.listOfNumbersToBelong.get(0).val;
				boolean theSame = true;
				int minLength = val;
				for (PaNumber number : l.listOfNumbersToBelong) {

					if (val != number.val) {
						theSame = false;
						// break;
					}
					if (number.val < minLength) {
						minLength = number.val;
					}
				}
				// Jeżeli wszystkie liczby należą do liczb o takiej samej
				// wartości i długość odcinka odpowiada wartości tych liczb to
				// pokoloruj graniczne pola na szaro
				if (theSame && val == (l.e - l.s + 1)) {
					if (l.s - 1 > 0) {
						if (isVertical) {
							this.area[l.s - 1][i].changeType(EMPTY);
						} else {
							this.area[i][l.s - 1].changeType(EMPTY);
						}
					}
					if (l.e + 1 < n - 1) {
						if (isVertical) {
							this.area[l.e + 1][i].changeType(EMPTY);
						} else {
							this.area[i][l.e + 1].changeType(EMPTY);
						}
					}
				}

				tryDrawMinLength(l, minLength, isVertical, n, i);

				// Sprawdzanie czy liczba powinna należeć do odcinka, jeżeli jej
				// zakres początkowy jest taki sam jak pozycja startowa odcinka
				// sprawdz czy można prawidłowo wyznaczyć odcinek, to samo w
				// drugą stronę.
				for (PaNumber number : l.listOfNumbersToBelong) {
					// Jeżeli zakres początkowy zgadza się z początkiem odcinka
					// i istnieje następny odcinek
					if (number.scope[0] == l.s && k + 1 < size) {
						Length nextLength = lengths.get(k + 1);

						if (number.scope[0] + number.val - 1 >= nextLength.s - 1
								&& number.scope[0] + number.val - 1 < nextLength.e) {

							number.changeScopeLeft(numberList.numbers, l.e + 1);

						}
					}

					if (number.scope[1] == l.e && k - 1 > 0) {
						Length prevLength = lengths.get(k - 1);

						if (number.scope[1] - number.val + 1 <= prevLength.e + 1
								&& number.scope[1] - number.val + 1 > prevLength.e) {

							number.changeScopeRight(numberList.numbers, l.s - 1);
						}
					}
				}
			}

			tryEliminateField(l, n, isVertical, i);

			k++;
		}
		// -----------------------------------------------------------------------
		// Działanie na odcinkach wyznaczonych pomiędzy odcinkami pokolorowanymi
		// na szaro
		lengths = numberList.spaceLengths;

		for (k = 0; k < lengths.size(); k++) {
			Length length = lengths.get(k);

			// Jeżeli do przestrzeni nienależą żadne liczby pokoloruj odcinek na
			// szaro
			if (length.listOfNumbersToBelong.size() == 0) {
				for (int j = length.s; j <= length.e; j++) {
					if (isVertical) {
						this.area[j][i].changeType(EMPTY);
					} else {
						this.area[i][j].changeType(EMPTY);
					}
				}
				// continue;
			}

			// Jeżeli jest to pierwsza przestrzeń
			if (k == 0 && length.s > 0) {
				// Spróbuj zmienić zasieg liczb należących do tej przesterzeni.

				// numberList.numbers.get(0).changeScopeLeft(numberList.numbers,
				// length.s);

				int scope = length.s;
				for (PaNumber number : numberList.numbers) {
					if (!number.enable) {
						// if (scope > length.s) {
						// break;
						// }
						continue;
					}
					if (number.scope[0] < scope) {
						number.scope[0] = scope;
						change = true;
						// number.changeLeftScope(numberList.numbers, scope);
					}
					scope += number.val + 1;
				}
			}

			// Jeżeli jest to ostatnia przestrzeń
			if (k == lengths.size() - 1 && k < n - 1) {
				// Spróbuj zmienieć zasięg liczb należących do tej przestrzeni
				// int lastNumberIndex = numberList.numbers.size() - 1;
				// numberList.numbers.get(lastNumberIndex).changeScopeRight(
				// numberList.numbers, length.e);

				int scope = length.e;
				for (int z = numberList.numbers.size() - 1; z >= 0; z--) {
					PaNumber number = numberList.numbers.get(z);
					if (!number.enable) {
						// if (scope < length.e) {
						// break;
						// }
						continue;
					}
					if (number.scope[1] > scope) {
						number.scope[1] = scope;
						change = true;
					}
					scope -= (number.val + 1);
				}
			}

			// Jeżeli przestrzeń należy tylko do jednej liczby spróbuj
			// zmniejszyć przestrzeń (pokolorowac granice zasięgu liczby na
			// szaro)
			if (length.listOfNumbersToBelong.size() == 1) {
				PaNumber number = length.listOfNumbersToBelong.get(0);
				if (number.scope[0] > length.s && number.scope[1] <= length.e) {

					for (int j = length.s; j < number.scope[0]; j++) {
						if (isVertical) {
							this.area[j][i].changeType(EMPTY);
						} else {
							this.area[i][j].changeType(EMPTY);
						}
					}
					length.s = number.scope[0];
				}
				if (number.scope[1] < length.e && number.scope[0] >= length.s) {

					for (int j = length.e; j > number.scope[1]; j--) {
						if (isVertical) {
							this.area[j][i].changeType(EMPTY);
						} else {
							this.area[i][j].changeType(EMPTY);
						}
					}
					length.e = number.scope[1];
				}
			}
			// Sprawdz czy liczby, które należą do przestrzeni powinny do niej
			// należeć, jeżeli nie to zmień danej liczbie zasięg, dodatkowo
			// sprawdź czy liczba należy tylko do tej przestrzeni, jeżeli tak to
			// spróbuj zmienić jej zasięg
			for (PaNumber number : length.listOfNumbersToBelong) {
				// Jeżeli liczba w ogóle nie należy do przestrzeni to kontynuuj
				if (number.scope[1] < length.s || number.scope[0] > length.e) {

					// length.listOfNumbersToBelong.remove(number);
					continue;
				}

				if (length.e - number.scope[0] + 1 < number.val) {

					for (int z = k + 1; z < lengths.size(); z++) {
						Length nextLength = lengths.get(z);
						// Szukanie czy liczba należy do przestrzeni
						boolean isBelong = false;
						for (PaNumber numb : nextLength.listOfNumbersToBelong) {
							if (numb == number) {
								isBelong = true;
								break;
							}
						}
						if (isBelong) {

							number.changeScopeLeft(numberList.numbers,
									nextLength.s);

							break;
						}
					}
				}

				if (number.scope[1] - length.s + 1 < number.val) {

					for (int z = k - 1; z >= 0; z--) {
						Length nextLength = lengths.get(z);
						// Szukanie czu liczba należy od przestrzeni
						boolean isBelong = false;
						for (PaNumber numb : nextLength.listOfNumbersToBelong) {
							if (numb == number) {
								isBelong = true;
								break;
							}
						}
						if (isBelong) {
							number.changeScopeRight(numberList.numbers,
									nextLength.e);
							break;
						}
					}

				}
				// Jeżeli lewy zasię większy od przestrzeni to sprawdź czy
				// należy do jakiejś inne przestrzeni na lewo, jeżeli nie to
				// ustaw lewy zasię taki jak początek przestrzeni
				if (number.scope[0] < length.s) {
					boolean isBelong = false;

					Start: for (int z = k - 1; z >= 0; z--) {
						Length leftLength = lengths.get(z);
						for (PaNumber numb : leftLength.listOfNumbersToBelong) {
							if (numb == number) {
								isBelong = true;
								break Start;
							}
						}

						if (number.scope[0] >= leftLength.s) {
							break;
						}
					}

					if (!isBelong) {
						number.changeScopeLeft(numberList.numbers, length.s);
					}
				}

				if (number.scope[1] > length.e) {
					boolean isBelong = false;

					Start: for (int z = k + 1; z < lengths.size(); z++) {
						Length rightLength = lengths.get(z);
						for (PaNumber numb : rightLength.listOfNumbersToBelong) {
							if (numb == number) {
								isBelong = true;
								break Start;
							}
						}

						if (number.scope[1] <= rightLength.e) {
							break;
						}
					}

					if (!isBelong) {
						number.changeScopeRight(numberList.numbers, length.e);
					}
				}
			}

		}
	}

	private void tryDrawMinLength(Length l, int minLength, boolean isVertical,
			int n, int i) {
		// Jeżeli np. minimalna liczba jest = 2 sprawdz czy można
		// pokolorowac odcinek na dlugosc tej minimalnej
		if (minLength > l.e - l.s + 1) {
			Field leftField = null, rightField = null;

			if (l.s - 1 > 0) {
				if (isVertical) {
					leftField = this.area[l.s - 1][i];
				} else {
					leftField = this.area[i][l.s - 1];
				}
			}

			if (l.e + 1 < n) {
				if (isVertical) {
					rightField = this.area[l.e + 1][i];
				} else {
					rightField = this.area[i][l.e + 1];
				}
			}

			if ((leftField != null && leftField.type == EMPTY) || l.s == 0) {

				for (int j = l.s + 1; j <= l.s + minLength - 1; j++) {
					if (isVertical) {
						this.area[j][i].changeType(SELECTED);

					} else {
						this.area[i][j].changeType(SELECTED);

					}
				}
			}

			if ((rightField != null && rightField.type == EMPTY)
					|| l.e == n - 1) {

				for (int j = l.e - 1; j >= l.e - minLength + 1; j--) {
					if (isVertical) {
						this.area[j][i].changeType(SELECTED);

					} else {
						this.area[i][j].changeType(SELECTED);

					}
				}
			}
		}
	}

	private void tryEliminateField(Length length, int n, boolean isVertical,
			int i) {

		if (length.listOfNumbersToBelong.size() < 2)
			return;

		Field leftField = null, rightField = null;

		if (length.s - 1 > 0) {
			if (isVertical) {
				leftField = this.area[length.s - 1][i];
			} else {
				leftField = this.area[i][length.s - 1];
			}
		}

		if (length.e + 1 < n) {
			if (isVertical) {
				rightField = this.area[length.e + 1][i];
			} else {
				rightField = this.area[i][length.e + 1];
			}
		}
		boolean isOk = false;
		if (leftField != null) {
			if (leftField.type == ABSENCE) {
				for (PaNumber number : length.listOfNumbersToBelong) {
					if (number.scope[0] <= length.s - 1
							&& number.scope[1] >= length.e
							&& length.e - (length.s - 1) + 1 <= number.val) {
						isOk = true;
						break;
					}
				}
				if (!isOk) {
					leftField.changeType(EMPTY);
				}
			}
		}

		isOk = false;
		if (rightField != null) {
			if (rightField.type == ABSENCE) {
				for (PaNumber number : length.listOfNumbersToBelong) {
					if (number.scope[0] <= length.s
							&& number.scope[1] >= length.e + 1
							&& (length.e + 1) - length.s + 1 <= number.val) {
						isOk = true;
						break;
					}
				}
				if (!isOk) {
					rightField.changeType(EMPTY);
				}
			}
		}
	}

	// Zmiana zależności od liczby dla odcinków
	private void changeBelongsToNumberForLengths(List<Length> lengths,
			PaNumber number) {
		for (Length length : lengths) {
			// if (length.type == ABSENCE)
			// continue;

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

	// sprawdz czy zasieg liczby pozwala na wyznaczenie części lub całego
	// odcinka
	private void tryDrawLength(ListOfNumber numberList, boolean isVertical,
			int i) {

		int n = 0;
		if (isVertical) {
			n = this.y;
		} else {
			n = this.x;
		}
		for (int k = 0; k < numberList.numbers.size(); k++) {
			PaNumber number = numberList.numbers.get(k);
			// Jeżeli liczba jest już wyznaczona przejdź do następnej
			if (!number.enable) {
				continue;
			}

			// Jeżeli liczba jest pierszą na liście to spróbuj pokolorować na
			// szaro kratki na lewo od niej
			if (number.first) {
				for (int j = number.scope[0] - 1; j >= 0; j--) {
					if (isVertical) {
						if (this.area[j][i].type == ABSENCE) {
							this.area[j][i].changeType(EMPTY);

						}

					} else {
						if (this.area[i][j].type == ABSENCE) {
							this.area[i][j].changeType(EMPTY);

						}
					}

				}
			}
			// Jeżeli liczba jest ostatnią na liście to spróbuj pokolorować na
			// szaro kratki na prawo od niej
			if (number.last) {
				for (int j = number.scope[1] + 1; j <= n - 1; j++) {
					if (isVertical) {
						if (this.area[j][i].type == ABSENCE) {
							this.area[j][i].changeType(EMPTY);

						}
					} else {
						if (this.area[i][j].type == ABSENCE) {
							this.area[i][j].changeType(EMPTY);

						}
					}

				}
			}

			int x = number.scope[1] - number.scope[0] + 1 - number.val;

			if (x < number.val) {
				for (int j = number.scope[0] + x; j < number.scope[0]
						+ number.val; j++) {

					if (isVertical) {
						this.area[j][i].changeType(SELECTED);
						// if (j == 0) {
						// System.out.println("ZAZNACZANIE V_2: area[" + j
						// + "][" + i + "]");
						// }
					} else {
						this.area[i][j].changeType(SELECTED);
						// if (i == 0) {
						// System.out.println("ZAZNACZANIE H_2: area[" + i
						// + "][" + j + "]");
						// }
					}
				}
			}
			// Jeżeli x == 0 to znaczy, że można wyznaczyć całą liczbę
			if (x == 0) {
				setNumberEnableToFalse(numberList.numbers, number, k);

				numberList.otherNumbers--;
				numberList.sumOfNumbers -= number.val;
				if (numberList.otherNumbers == 0) {
					// System.out.println("COMPLETE!!! " + i);
					selectAbsenceToEmpty(isVertical, i);
				}
				// Kolorowanie na szaro kratek, które graniczą z wyznaczonym
				// całym odcinkiem
				if (number.scope[0] - 1 >= 0) {
					if (isVertical) {
						this.area[number.scope[0] - 1][i].changeType(EMPTY);
					} else {
						this.area[i][number.scope[0] - 1].changeType(EMPTY);
					}
				}
				if (number.scope[1] + 1 <= n - 1) {
					if (isVertical) {
						this.area[number.scope[1] + 1][i].changeType(EMPTY);
					} else {
						this.area[i][number.scope[1] + 1].changeType(EMPTY);
					}
				}

				// Zmiana zasięgu pozostałych liczb
				// na lewo
				if (k - 1 >= 0 && numberList.numbers.get(k - 1).enable) {
					numberList.numbers.get(k - 1).changeScopeRight(
							numberList.numbers, number.scope[0] - 1);

				}
				// int scopeRight = number.scope[0] - 1;
				// for (int z = k - 1; z >= 0; z--) {
				// PaNumber leftNumber = numberList.numbers.get(z);
				//
				// // test
				// if (!leftNumber.enable) {
				// break;
				// }
				//
				// leftNumber.scope[1] = scopeRight;
				// scopeRight -= (leftNumber.val + 1);
				// }

				// // na prawo
				if (k + 1 < numberList.numbers.size()
						&& numberList.numbers.get(k + 1).enable) {
					numberList.numbers.get(k + 1).changeScopeLeft(
							numberList.numbers, number.scope[1] + 1);
				}

				// int scopeLeft = number.scope[1] + 1;
				// for (int z = k + 1; z < numberList.numbers.size(); z++) {
				// PaNumber rightNumber = numberList.numbers.get(z);
				//
				// // test
				// if (!rightNumber.enable) {
				// break;
				// }
				// rightNumber.scope[0] = scopeLeft;
				// scopeLeft += rightNumber.val + 1;
				// }
				// change = true;
			}

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

		public void changeType(byte type) {
			if (this.type != type) {
				this.type = type;
				change = true;
			}
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
		public int[] scope = null;// new int[2]; // Okreslenie zakresu mozliwego
									// wystapienia liczby
		public boolean first = false; // czy liczba jest pierwsza
		public boolean last = false; // czy liczba jest ostatnia
		public int index; // Numer indeksu liczby w liście

		public PaNumber() {

		}

		public PaNumber(byte val, int index) {
			this.val = val;
			this.index = index;
		}

		// Zmiana prawego zasięgu liczby, spróbuj zmienić zasięg pozostałych
		// liczb na lewo od liczby, której zmienielismy zasięg
		public void changeScopeRight(List<PaNumber> numbers, int rightScope) {
			if (!this.enable) {
				return;
			}
			if (this.scope[1] > rightScope) {
				this.scope[1] = rightScope;

				change = true;
			}
			rightScope = rightScope - this.val - 1;
			for (int i = this.index - 1; i >= 0; i--) {
				PaNumber number = numbers.get(i);
				if (!number.enable) {
					break;
				}
				if (number.scope[1] > rightScope) {
					number.scope[1] = rightScope;

					change = true;
				}

				rightScope = rightScope - number.val - 1;
			}
		}

		// Zmiana lewego zasięgu liczby, spróbuj zmienić zasięg pozostałych
		// liczb na prawo od liczby, której zmienił się zasięg
		public void changeScopeLeft(List<PaNumber> numbers, int leftScope) {
			if (!this.enable) {
				return;
			}
			if (this.scope[0] < leftScope) {
				this.scope[0] = leftScope;

				change = true;
			}
			leftScope = leftScope + this.val + 1;
			for (int i = this.index + 1; i < numbers.size(); i++) {
				PaNumber number = numbers.get(i);
				if (!number.enable) {
					break;
				}
				if (number.scope[0] < leftScope) {
					number.scope[0] = leftScope;

					change = true;
				}
				leftScope = leftScope + number.val + 1;
			}
		}

		@Override
		public String toString() {

			return "Number: " + val + " [" + scope[0] + ", " + scope[1]
					+ "] enable: " + enable + ", index = " + index;
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
			this.listOfNumbersToBelong = new ArrayList<PaNumber>();

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

						// number.scope[1] = s - 2;
						number.changeScopeRight(numbers, s - 2);
						continue;
					}

					if (number.scope[1] > (e + 1) && number.scope[0] <= e + 1
							&& number.scope[0] > s) {

						// number.scope[0] = e + 2;
						number.changeScopeLeft(numbers, e + 2);
						continue;
					}

					if (number.scope[0] <= s && number.scope[1] >= e) {

						this.listOfNumbersToBelong.add(number);
						continue;
					}

					System.out.println("Błąd! " + number.val + ":["
							+ number.scope[1] + ", " + number.scope[1] + "], <"
							+ s + ", " + e + ">, type:" + type);
				}
			}

			if (type == ABSENCE) {

				for (PaNumber number : numbers) {

					if (s > number.scope[1] || number.val > (e - s + 1)) {
						continue;
					}

					if (e < number.scope[0]) {
						break;
					}

					if (number.scope[0] >= s && number.scope[1] <= e) {

						// Jeżeli liczba jest już wyznaczone i jej zasięg
						// odpowiada dłuości wyznaczanego odcinka, to uznacz
						// odcinek jako kompletny i przerwij pętlę, w ten sposób
						// odcinek nie zostanie dodany
						if (!number.enable && number.scope[0] == s
								&& number.scope[1] == e) {

							isComplete = true;
							break;
						}
						this.listOfNumbersToBelong.add(number);
						continue;
					}

					if (number.scope[0] - s < 0
							&& number.scope[0] - s + 1 + number.val > 0) {
						// number.scope[0] = s;
						number.changeScopeLeft(numbers, s);
					}

					if (e - number.scope[1] < 0
							&& e - number.scope[1] + 1 + number.val > 0) {
						// number.scope[1] = e;
						number.changeScopeRight(numbers, e);
					}

					this.listOfNumbersToBelong.add(number);

					// System.out.println("Brak kontynuacji! " + number.val +
					// ":["
					// + number.scope[0] + ", " + number.scope[1] + "], ("
					// + s + ", " + e + "), type:" + type);
				}
			}
		}

		@Override
		public String toString() {
			return "[" + s + ", " + e + "]";
		}

		public String printNumbers() {
			String output = "";

			for (PaNumber number : listOfNumbersToBelong) {
				output = output + number + ", ";
			}

			return output;
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
