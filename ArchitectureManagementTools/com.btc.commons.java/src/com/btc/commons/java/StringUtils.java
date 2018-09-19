package com.btc.commons.java;

import java.util.Iterator;

public class StringUtils {
	/** Find the common prefix of the strings. */
	public static String findCommonPrefix(String[] strings) {
		if (strings.length > 0)
			return strings[0].substring(0,
					findCommonSubstringLength(strings, 0));
		else
			return "";
	}

	public static int findCommonSubstringLength(String[] strings, int startIndex) {
		int minLength = findMinLength(strings);
		if (minLength <= startIndex)
			return 0;
		else if (allCharactersEqualAtPosition(strings, startIndex)) {
			return 1 + findCommonSubstringLength(strings, startIndex + 1);
		} else {
			return 0;
		}
	}

	/** Get the minimum length of a string in strings[]. */
	private static int findMinLength(final String[] strings) {
		int length = strings[0].length();
		for (String string : strings) {
			if (string.length() < length) {
				length = string.length();
			}
		}
		return length;
	}

	/**
	 * Compare the nth character of all strings.
	 * 
	 * @param index
	 *            TODO
	 */
	private static boolean allCharactersEqualAtPosition(String[] strings,
			int index) {
		char c = strings[0].charAt(index);
		for (String string : strings) {
			if (c != string.charAt(index))
				return false;
		}

		return true;
	}

	public static String join(final Iterable<? extends CharSequence> s,
			final String delimiter) {
		final Iterator<? extends CharSequence> iter = s.iterator();
		if (iter.hasNext()) {
			final int capacity = calcJoinedLength(s, delimiter.length());
			final StringBuilder builder = new StringBuilder(capacity);

			if (iter.hasNext()) {
				builder.append(iter.next());
				while (iter.hasNext()) {
					builder.append(delimiter);
					builder.append(iter.next());
				}
			}
			return builder.toString();
		} else
			return "";
	}

	private static int calcJoinedLength(
			final Iterable<? extends CharSequence> s, final int delimLength) {
		int capacity = 0;
		final Iterator<? extends CharSequence> iter = s.iterator();
		while (iter.hasNext()) {
			capacity += iter.next().length() + delimLength;
		}
		capacity -= delimLength;
		return capacity;
	}

	public static String alternative(String first, String alternative) {
		return (first == null || first.isEmpty()) ? alternative : first;
	}
}
