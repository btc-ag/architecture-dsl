package com.btc.commons.java.functional;

import java.util.Collection;

final public class CollectionAdder<T> implements IUnaryClosure<T> {
	private final Collection<T> collection;

	public CollectionAdder(Collection<T> collection) {
		this.collection = collection;
	}

	@Override
	public void process(T obj) {
		collection.add(obj);
	}
}