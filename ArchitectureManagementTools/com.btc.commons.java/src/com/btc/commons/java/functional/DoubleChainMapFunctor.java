package com.btc.commons.java.functional;

public class DoubleChainMapFunctor<FROM, TO> implements IMapFunctor<FROM, TO> {
	private final DoubleChainMapFunctor.FailureHandler<FROM, TO> failureHandler;
	private final Iterable<? extends IMapFunctor<FROM, TO>> primaryMapFunctors,
			secondaryMapFunctors;

	public static interface FailureHandler<FROM, TO> {

		public abstract void handlePrimaryFailure(FROM from, TO to);

	}

	public DoubleChainMapFunctor(
			DoubleChainMapFunctor.FailureHandler<FROM, TO> failureHandler,
			Iterable<? extends IMapFunctor<FROM, TO>> primaryMapFunctors,
			Iterable<? extends IMapFunctor<FROM, TO>> secondaryMapFunctors) {
		this.failureHandler = failureHandler;
		this.primaryMapFunctors = primaryMapFunctors;
		this.secondaryMapFunctors = secondaryMapFunctors;
	}

	private static <FROM, TO> TO chainMapItem(final FROM proxy,
			final Iterable<? extends IMapFunctor<FROM, TO>> chainedMapFunctors) {
		for (final IMapFunctor<FROM, TO> mapFunctor : chainedMapFunctors) {
			final TO resolvedName = mapFunctor.mapItem(proxy);
			if (resolvedName != null)
				return resolvedName;
		}
		return null;

	}

	// Recursive version:
	// private static <FROM, TO> TO chainMapItem(FROM proxy,
	// Iterable<? extends IMapFunctor<FROM, TO>> chainedMapFunctors) {
	// return chainMapItemInternal(proxy, chainedMapFunctors.iterator());
	// }
	//
	// private static <FROM, TO> TO chainMapItemInternal(FROM proxy,
	// Iterator<? extends IMapFunctor<FROM, TO>> chainedMapFunctors) {
	// final IMapFunctor<FROM, TO> next = chainedMapFunctors.next();
	// if (next != null) {
	// final TO resolvedName = next.mapItem(proxy);
	// return resolvedName != null ? resolvedName : chainMapItemInternal(
	// proxy, chainedMapFunctors);
	// } else
	// return null;
	// }

	@Override
	public TO mapItem(FROM proxy) {
		TO resolvedName = null;
		resolvedName = chainMapItem(proxy, primaryMapFunctors);
		if (resolvedName == null) {
			resolvedName = chainMapItem(proxy, secondaryMapFunctors);
			failureHandler.handlePrimaryFailure(proxy, resolvedName);

		}
		return resolvedName;
	}
}
