package com.btc.commons.java;

/**
 * This is an abstract base class for simplifying the implementation of typical
 * inserters. It implements the addAll method by calling add on each individual
 * element.
 * 
 * This class is intended to be subclassed. Subclasses may not call its addAll
 * method.
 * 
 * @author SIGIESEC
 * 
 * @param <T>
 */
public abstract class InserterBase<T> implements IInserter<T> {

	@Override
	public void addAll(final Iterable<T> objects) {
		for (T obj : objects) {
			add(obj);
		}
	}

}
