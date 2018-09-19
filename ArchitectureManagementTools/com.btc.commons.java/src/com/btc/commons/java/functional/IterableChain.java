package com.btc.commons.java.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.IteratorChain;

public class IterableChain<T> implements Iterable<T> {
	private final List<Iterable<? extends T>> iterables;
	private final IMapFunctor<Iterable<? extends T>, Iterator<? extends T>> iterableAsIteratorMapFunctor = new IterableAsIteratorMapFunctor<T>();

	/**
	 * Creates a new IterableChain with no initial chain elements.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IterableChain() {
		this((Collection) Collections.emptyList());
	}

	/**
	 * Creates a new IterableChain with some initial chain elements.
	 * 
	 * @param initial
	 *            The initial chain elements. The collection is copied by the
	 *            constructor, so later changes to the collection are not
	 *            reflected by the created object.
	 */
	public IterableChain(final Collection<Iterable<T>> initial) {
		this.iterables = new ArrayList<Iterable<? extends T>>(initial);
	}

	/**
	 * Creates a new IterableChain with some initial chain elements.
	 * 
	 * @param initial
	 *            The initial chain elements. The array is copied by the
	 *            constructor, so later changes to the array are not reflected
	 *            by the created object.
	 */
	public IterableChain(final Iterable<T>[] initial) {
		this(Arrays.asList(initial));
	}

	/**
	 * Adds an iterable to the end of the chain.
	 * 
	 * @param iterable
	 *            the iterable to be added
	 * @return this object
	 */
	public IterableChain<T> addIterable(final Iterable<? extends T> iterable) {
		this.iterables.add(iterable);
		return this;
	}

	/**
	 * Creates an iterator using the current chained iterables. Later changes to
	 * the IterableChain are not reflected by the created iterator, however
	 * changes to the underlying iterables will be reflected under the following
	 * conditions: Changes to an iterable whose processing has not started yet
	 * will always be reflected by the returned iterator. Changes to an iterable
	 * whose iteration has started will be reflected by the iterator iff this is
	 * the case for the iterable, possibly resulting in an
	 * ConcurrentModificationException.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		// TODO this should be made more efficient
		return new IteratorChain(IterationUtils.materialize(
				IterationUtils.map(iterables, iterableAsIteratorMapFunctor))
				.toArray(new Iterator[0]));

	}
}
