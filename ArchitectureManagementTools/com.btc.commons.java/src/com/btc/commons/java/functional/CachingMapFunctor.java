package com.btc.commons.java.functional;

import java.util.HashMap;
import java.util.Map;


public class CachingMapFunctor<FROM, TO> implements IMapFunctor<FROM, TO> {
	private final Map<FROM, TO> cache;
	private final IMapFunctor<FROM, TO> decoratee;

	public CachingMapFunctor(IMapFunctor<FROM, TO> decoratee) {
		this(decoratee, new HashMap<FROM, TO>());
	}

	public CachingMapFunctor(IMapFunctor<FROM, TO> decoratee,
			Map<FROM, TO> cache) {
		this.decoratee = decoratee;
		this.cache = cache;
	}

	@Override
	public TO mapItem(FROM proxy) {
		if (!this.cache.containsKey(proxy)) {
			final TO resolvedName = decoratee.mapItem(proxy);
			this.cache.put(proxy, resolvedName);
		}
		return this.cache.get(proxy);

	}

}