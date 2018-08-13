package toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * 
 * @author Jonathan Brose
 */

public class BubbleSort {

	/**
	 * 
	 * the main method runs a input for a various number and prints out all
	 * inputs sorted.
	 */
	public static void main(String args[]) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		float[] zahlen;
		int anzahl = 0;
		String input;
		int i;
		String[] multipleInputs;
		int numbersPerLine = 10;

		System.out
				.println("Bitte geben sie ein wie viele Zahlen sie sortieren möchten.");
		do {
			try {
				input = reader.readLine();
				anzahl = Integer.parseInt(input);
				break;
			} catch (Exception e) {
				System.out.println("Bitte geben sie eine Ganzzahl ein!");
			}
		} while (true);
		System.out.println();

		zahlen = new float[anzahl];
		System.out
				.println("Bitte geben sie nun die zu sortierenden Zahlen ein.\nSie können auch mehrere Zahlen auf einmal eingeben,\nwenn sie diese mit \";\" oder mit der Leertaste trennen.\n");
		for (i = 0; i < anzahl; i++) {
			System.out.println("Noch " + (anzahl - i) + " Zahlen verbleibend");
			do {

				try {
					input = reader.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (input.contains(";")) {
					multipleInputs = input.split(";");
					for (int j = 0; j < multipleInputs.length; j++) {
						try {
							if (i >= zahlen.length) {
								System.err
										.println("Es wurden zu viele Zahlen eingegeben, der Überschuss wurde ignoriert.");
								break;
							}
							zahlen[i] = Float.parseFloat(multipleInputs[j]);
							i++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					i--;
					break;
				} else if (input.contains(" ")) {
					multipleInputs = input.split(" ");
					for (int j = 0; j < multipleInputs.length; j++) {
						try {
							if (i >= zahlen.length) {
								System.err
										.println("Es wurden zu viele Zahlen eingegeben, der Überschuss wurde ignoriert.");
								break;
							}
							zahlen[i] = Float.parseFloat(multipleInputs[j]);
							i++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					i--;
					break;
				} else {
					try {
						input = input.replaceAll(",", ".");
						zahlen[i] = Float.parseFloat(input);
						break;
					} catch (Exception e) {
						System.out
								.println("Bitte geben sie eine keine Buchstaben ein!");
					}
				}
			} while (true);
		}
		System.out.println("Die Zahlen sortiert:\n"
				+ floatArrayToString(sort(zahlen), numbersPerLine));

	}

	/**
	 * 
	 * Sortiert ein Array vom Typ float mit dem BubbleSort Verfahren.
	 *
	 * @param input
	 *            - ein floatArray das sortiert werden soll.
	 * 
	 * 
	 * @return ein floatArray mit den Zahlen in sortierter Reihenfolge.
	 */
	public static float[] sort(float[] input) {
		for (int i = 1; i < input.length; i++) {
			for (int j = 0; j < input.length - i; j++) {
				if (input[j] > input[j + 1])
					swap(input, j, j + 1);
			}
		}
		return input;
	}

	/**
	 * 
	 * @param in
	 *            - a Array of float
	 * @param numbersPerLine
	 *            - count of numbers shown on one commandline.
	 * @return a String with all the input values.
	 */

	public static String floatArrayToString(float[] in, int numbersPerLine) {
		String out = "[";
		for (int i = 0; i < in.length; i++) {
			if (i == in.length - 1) {
				out += in[i] + "]";
			} else {
				out += in[i] + "; ";
			}
			if ((i + 1) % numbersPerLine == 0 && (i + 1) != 0)
				out += "\n ";
		}
		return out;
	}

	/**
	 * swaps the float values in the array at the two given indices
	 * 
	 * @param array
	 *            - the Array to manipulate.
	 * @param pos1
	 *            - index 1
	 * @param pos2
	 *            - index 2
	 */

	public static void swap(float[] array, int pos1, int pos2) {
		float c = array[pos1];
		array[pos1] = array[pos2];
		array[pos2] = c;
	}
}
