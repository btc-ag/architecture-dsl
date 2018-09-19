package com.btc.commons.java.functional;

import com.btc.commons.java.Pair;

public final class PairSecondMapFunctor<T, FROM, TO> implements
		IMapFunctor<Pair<T, FROM>, Pair<T, TO>> {
	private final IMapFunctor<FROM, TO> nestedMapFunctor;

	public PairSecondMapFunctor(IMapFunctor<FROM, TO> nestedMapFunctor) {
		this.nestedMapFunctor = nestedMapFunctor;
	}

	@Override
	public Pair<T, TO> mapItem(final Pair<T, FROM> obj) {
		final TO moduleName = nestedMapFunctor.mapItem(obj.getSecond());
		if (moduleName != null) {
			return new Pair<T, TO>(obj.getFirst(), moduleName);
		} else {
			return null;
		}
	}

}