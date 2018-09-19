package com.btc.commons.java;

/**
 * An inserter is a restriction of a collection, which allows write-only access.
 * Typically, it will be implemented using an underlying collection, which may
 * or may not implement the {@see Collection} interface. Depending on the
 * implementation, the position of insertion will be different. Depending on the
 * implementation, the behaviour regarding multiple insertion of equal elements
 * may differ.
 * 
 * @author SIGIESEC
 * 
 * @param <T>
 */
public interface IInserter<T> {
	/**
	 * Adds one object to the underlying collection.
	 * 
	 * @param obj
	 * @return true if the addition of the object was successful
	 */
	void add(T obj);

	/**
	 * Adds all objects to the underlying collection.
	 * 
	 * @param obj
	 * @return true if add least one object was added successfully
	 */
	void addAll(Iterable<T> obj);

}
