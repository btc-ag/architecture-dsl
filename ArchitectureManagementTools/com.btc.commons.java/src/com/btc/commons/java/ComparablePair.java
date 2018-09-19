package com.btc.commons.java;

public class ComparablePair<T extends Comparable<? super T>, U extends Comparable<? super U>>
		extends Pair<T, U> implements Comparable<Pair<T, U>> {
	public ComparablePair(T firstElement, U secondElement) {
		super(firstElement, secondElement);
	}

	@Override
	public int compareTo(Pair<T, U> other) {
		int result;
		result = getFirst().compareTo(other.getFirst());
		if (result == 0) {
			result = getSecond().compareTo(other.getSecond());
		}
		return result;
	}

}
