package org.memorydb.structure;

/**
 * This class is not thread-safe but is much more efficient in regard to memory allocation if
 * reused for multiple tests.
 */
public class StringDistance {
	private int[] d = new int[1000];

	public int distance(String a, String b) {
		int al = a.length() + 1;
		int s = al * (b.length() + 1);
		if (d.length < s)
			d = new int[s];
		for (int i = 0; i <= a.length(); i++)
			d[i] = i;
		for (int j = 0; j <= b.length(); j++)
			d[al * j] = j;
		for (int i = 0; i < a.length(); i++) {
			for (int j = 0; j < b.length(); j++) {
				int cost = a.charAt(i) == b.charAt(j) ? 0 : 1;
				d[i + 1 + al * (j + 1)] = Math.min(Math.min( //
						d[i + al * (j + 1)] + 1, // deletion
						d[i + 1 + al * j] + 1), // insertion
						d[i + al * j] + cost); // substitution
				if (i > 0 && j > 0 && a.charAt(i) == b.charAt(j - 1) && a.charAt(i - 1) == b.charAt(j))
					d[i + 1 + al * (j + 1)] = Math.min(d[i + 1 + al * (j + 1)], d[i - 1 + al * (j - 1)] + cost); // transposition
			}
		}
		return d[a.length() + al * b.length()];
	}
}
