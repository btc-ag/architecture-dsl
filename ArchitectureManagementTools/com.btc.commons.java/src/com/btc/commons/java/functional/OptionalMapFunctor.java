package com.btc.commons.java.functional;

import java.util.Collections;

public final class OptionalMapFunctor<FROM, TO> implements
		IMapFunctor<FROM, Iterable<TO>> {
	private final IMapFunctor<FROM, TO> nestedMapFunctor;

	public OptionalMapFunctor(IMapFunctor<FROM, TO> nestedMapFunctor) {
		this.nestedMapFunctor = nestedMapFunctor;
	}

	@Override
	public Iterable<TO> mapItem(final FROM obj) {
		final TO result = nestedMapFunctor.mapItem(obj);
		if (result != null) {
			return Collections.singleton(result);
		} else {
			return Collections.emptyList();
		}

	}
}