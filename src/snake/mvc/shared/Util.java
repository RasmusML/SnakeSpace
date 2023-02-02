package snake.mvc.shared;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {

	public static final String highscoreFile = "highscore";

	public static void stopProgram () {
		System.exit(0);
	}

	public static boolean isNumeric (String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String readFile (String path) {
		String content = null;

		try {
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static void createFileIfNotExists (String filename) {
		File file = new File(filename);
		try {
			file.createNewFile(); // "Creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist."
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile (String content, String path) {
		try {
			Files.write(Paths.get(path), content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int clamp (int val, int min, int max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}

	public static int sign (double val) {
		if (val < 0) return -1;
		if (val > 0) return 1;
		return 0;

	}

	public static double clamp (double val, double min, double max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}

	/*
	 * returns random value in the range, [min;max]
	 */
	public static int getRandomInt (int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}

	/*
	 * rounds up to integer
	 */
	public static int ceilInt (double value) {
		return (int) (value + 0.5);
	}

	/*
	 * rounds down to integer
	 */
	public static int floorInt (double value) {
		return (int) (value - 0.5);
	}
}
