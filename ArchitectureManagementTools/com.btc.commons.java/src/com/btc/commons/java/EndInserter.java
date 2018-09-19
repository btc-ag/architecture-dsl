package com.btc.commons.java;

import java.util.Collection;

public class EndInserter<T> extends InserterBase<T> {
	private final Collection<T> coll;

	public EndInserter(Collection<T> coll) {
		this.coll = coll;

	}

	@Override
	public void add(T obj) {
		this.coll.add(obj);
	}
}
