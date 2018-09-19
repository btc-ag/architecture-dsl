package com.btc.commons.java.functional;

import java.util.Iterator;

final class IterableAsIteratorMapFunctor<T> implements
		IMapFunctor<Iterable<? extends T>, Iterator<? extends T>> {
	@Override
	public Iterator<? extends T> mapItem(Iterable<? extends T> obj) {
		return obj.iterator();
	}
}