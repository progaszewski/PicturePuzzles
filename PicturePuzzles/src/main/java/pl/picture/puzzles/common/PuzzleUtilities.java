package pl.picture.puzzles.common;

public class PuzzleUtilities {

	public static void showTimeElapsed(long timeMilis) {
		long ms = 0;
		long s = 0;
		long min = 0;
		long h = 0;

		// Liczenie sekund i milisekund
		s = timeMilis / 1000;
		ms = timeMilis % 1000;

		// Licenie minut i sekund
		min = s / 60;
		s = s % 60;

		// Liczenie godzin i minut
		h = min / 60;
		min = min % 60;

		System.out.println("Rozwiązywanie trawało:");
		if (h != 0)
			System.out.print(h + "h, ");

		if (h != 0 || min != 0)
			System.out.print(min + "m, ");

		if (h != 0 || min != 0 || s != 0)
			System.out.print(s + "s i ");

		System.out.println(ms + "ms");
	}

	public static int factorial(int n, int k) {

		if (n < k || n == 0) {
			return 1;
		}
		return n * factorial(n - 1, k);
	}

	public static int c(int n, int k) {
		if (n - k > k) {
			return factorial(n, n - k) / factorial(k, 1);
		}
		return factorial(n, k) / factorial(n - k, 1);
	}

}
