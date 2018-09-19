package com.btc.commons.java.functional;

import java.util.Iterator;

import org.apache.commons.collections.iterators.UnmodifiableIterator;

public class UnmodifiableIterable<T> implements Iterable<T> {
	private final Iterable<T> wrappee;

	public UnmodifiableIterable(Iterable<T> wrappee) {
		this.wrappee = wrappee;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return UnmodifiableIterator.decorate(wrappee.iterator());
	}
}
