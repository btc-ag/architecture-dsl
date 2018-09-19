package com.btc.commons.java.functional;

public interface IMapFunctor<T1, T2> {
	/**
	 * This interface is intended to be used in a functional programming style
	 * and is equivalent to C# delegates.
	 * 
	 * TODO consider exception handling.
	 * 
	 * @param obj
	 * @return
	 */
	T2 mapItem(final T1 obj);
}
