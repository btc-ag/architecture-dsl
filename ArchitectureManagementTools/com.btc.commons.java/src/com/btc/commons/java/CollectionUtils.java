package com.btc.commons.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains static utility functions extending the basic
 * functionality of {@link Collection} objects.
 * 
 * @author SIGIESEC
 * 
 */
public final class CollectionUtils {

	public static <T> boolean containsAny(
			final Collection<? super T> collection,
			final Iterable<? extends T> checkObjects) {
		// TODO if checkObjects is a collection, this can be optimised if
		// checkObjects contains more objects than collection
		for (final T obj : checkObjects) {
			if (collection.contains(obj)) {
				return true;
			}
		}
		return false;

	}

	public static <T> boolean containsAll(
			final Collection<? super T> collection,
			final Iterable<? extends T> checkObjects) {
		for (final T obj : checkObjects) {
			if (!collection.contains(obj)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds all elements of an {@link Iterable} to a {@link Collection}.
	 * 
	 * @param <T>
	 *            the type of the elements
	 * @param coll
	 *            the target collection
	 * @param newElements
	 *            the elements to be added
	 * @return the target collection
	 */
	public static <T> Collection<T> addAll(final Collection<T> coll,
			final Iterable<? extends T> newElements) {
		for (final T element : newElements) {
			coll.add(element);
		}
		return coll;
	}

	public static <T1, T2> void addIfNotExists(final Map<T1, T2> map,
			final T1 key, final T2 value) {
		if (!map.containsKey(key)) {
			map.put(key, value);
		}
	}

	public static <T1, T2> void addIfNotExists(final Map<T1, T2> map,
			final Pair<T1, T2> entry) {
		addIfNotExists(map, entry.getFirst(), entry.getSecond());
	}

	public static <T> Collection<T> removeAll(final Collection<T> coll,
			final Iterable<? extends T> removeElements) {
		for (final T element : removeElements) {
			coll.remove(element);
		}
		return coll;
	}

	public static <T1, T2> Map<T1, T2> createMap(final Pair<T1, T2>[] array) {
		return createMap(Arrays.asList(array));
	}

	protected static <T1, T2> Map<T1, T2> createMap(
			final Iterable<Pair<T1, T2>> iterable) {
		final Map<T1, T2> map = new HashMap<T1, T2>();
		for (final Pair<T1, T2> pair : iterable) {
			map.put(pair.getFirst(), pair.getSecond());
		}
		return map;
	}

	public static <T1, T2> Map<T1, Collection<T2>> createSetValuedMap(
			final Pair<T1, T2[]>[] array) {
		final Map<T1, Collection<T2>> map = new HashMap<T1, Collection<T2>>();
		for (final Pair<T1, T2[]> pair : array) {
			map.put(pair.getFirst(),
					new HashSet<T2>(Arrays.asList(pair.getSecond())));
		}
		return map;
	}

	public static <T1, T2> Map<T1, Set<T2>> createSetValuedMapFromIndividuals(
			final Iterable<Pair<T1, T2>> inputElements) {
		final Map<T1, Set<T2>> map = new HashMap<T1, Set<T2>>();
		for (final Pair<T1, T2> pair : inputElements) {
			final Set<T2> set;
			if (map.containsKey(pair.getFirst())) {
				set = map.get(pair.getFirst());
			} else {
				set = new HashSet<T2>();
				map.put(pair.getFirst(), set);
			}
			set.add(pair.getSecond());
		}
		return map;
	}

	public static <T, U> List<U> getOrCreateValueList(
			final Map<T, List<U>> map, final T key) {
		final List<U> valueList;
		if (map.containsKey(key)) {
			valueList = map.get(key);
		} else {
			valueList = new ArrayList<U>();
			map.put(key, valueList);
		}
		return valueList;
	}

	public static <T, U> Set<U> getOrCreateValueSet(final Map<T, Set<U>> map,
			final T key) {
		final Set<U> valueSet;
		if (map.containsKey(key)) {
			valueSet = map.get(key);
		} else {
			valueSet = new HashSet<U>();
			map.put(key, valueSet);
		}
		return valueSet;
	}

	public static <T> List<T> findCommonPrefix(final List<T>[] strings) {
		if (strings.length > 0) {
			return strings[0].subList(0,
					findCommonSubsequenceLength(strings, 0));
		} else {
			return Collections.emptyList();
		}
	}

	public static <T> int findCommonSubsequenceLength(final List<T>[] strings,
			final int startIndex) {
		final int minLength = findMinLength(strings);
		if (minLength <= startIndex) {
			return 0;
		} else if (allElementsEqualAtPosition(strings, startIndex)) {
			return 1 + findCommonSubsequenceLength(strings, startIndex + 1);
		} else {
			return 0;
		}
	}

	private static <T> int findMinLength(final List<T>[] strings) {
		int length = strings[0].size();
		for (final List<T> string : strings) {
			if (string.size() < length) {
				length = string.size();
			}
		}
		return length;
	}

	private static <T> boolean allElementsEqualAtPosition(
			final List<T>[] strings, final int index) {
		final T t = strings[0].get(index);
		for (final List<T> string : strings) {
			if (t != string.get(index)) {
				return false;
			}
		}

		return true;
	}
}
