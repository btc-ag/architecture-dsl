package com.btc.commons.java.functional;

import java.util.Map;


public final class MapLookupMapFunctor<T, U> implements
		IMapFunctor<T, U> {
	private final Map<T, U> map;

	public MapLookupMapFunctor(Map<T, U> map) {
		this.map = map;
	}

	@Override
	public U mapItem(T srcType) {
		return map.get(srcType);
	}
}