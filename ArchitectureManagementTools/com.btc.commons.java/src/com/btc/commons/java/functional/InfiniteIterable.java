package com.btc.commons.java.functional;

import java.util.Iterator;

public class InfiniteIterable<T> implements Iterable<T> {

	public class InfiniteIterator implements Iterator<T> {

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public T next() {
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final T value;

	public InfiniteIterable(T value) {
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
		return new InfiniteIterator();
	}

}
