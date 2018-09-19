package com.btc.commons.java.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.IteratorChain;

import com.btc.commons.java.Pair;

/*
 * TODO consider 
 * a) switching implementation from org.apache.commons.collections to com.google.common.collect (which uses generics)
 * b) removing everything that duplicates com.google.common.collect (e.g. IUnaryPredicate -> Predicate, IMapFunctor -> Function, ...)
 */

class MapAndChainIterator<InputT, ResultT> implements Iterator<ResultT> {
	private final Iterator<InputT> inputObjects;
	private Iterator<? extends ResultT> currentIterator;
	private final IMapFunctor<? super InputT, ? extends Iterator<? extends ResultT>> mapFunctor;

	public MapAndChainIterator(
			final Iterator<InputT> inputObjects,
			final IMapFunctor<? super InputT, ? extends Iterator<? extends ResultT>> mapFunctor) {
		this.inputObjects = inputObjects;
		this.mapFunctor = mapFunctor;
		this.currentIterator = null;
	}

	@Override
	public boolean hasNext() {
		if (this.currentIterator != null && this.currentIterator.hasNext()) {
			return true;
		}
		if (this.inputObjects.hasNext()) {
			nextInput();
			if (this.currentIterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	private void nextInput() {
		while (!(this.currentIterator != null && this.currentIterator.hasNext())
				&& this.inputObjects.hasNext()) {
			this.currentIterator = this.mapFunctor.mapItem(this.inputObjects
					.next());
		}
	}

	@Override
	public ResultT next() {
		if (hasNext()) {
			return this.currentIterator.next();
		} else {
			throw new NoSuchElementException("Iterator is exhausted");
		}
	}

	@Override
	public void remove() {
		if (this.currentIterator != null) {
			this.currentIterator.remove();
		} else {
			throw new IllegalStateException(
					"No current element, call next() first");
		}
	}

}

/**
 * The class contains static utility functions related to {@link Iterable} and
 * {@link Iterator} objects.
 * 
 * @author SIGIESEC
 * 
 */
final public class IterationUtils {
	// private static final class FilterIterator<T> implements Iterator<T> {
	// private final Iterator<? extends T> baseIterator;
	// private final IUnaryPredicate<T> predicate;
	// private T current;
	//
	// public FilterIterator(final Iterator<? extends T> iterator,
	// final IUnaryPredicate<T> predicate) {
	// this.baseIterator = iterator;
	// this.predicate = predicate;
	// this.current = null;
	// }
	//
	// @Override
	// public boolean hasNext() {
	// if (current != null)
	// return true;
	// while (this.baseIterator.hasNext()) {
	// final T next = this.baseIterator.next();
	// if (this.predicate.evaluate(next)) {
	// current = next;
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// @Override
	// public T next() {
	// hasNext();
	// final T retval = current;
	// current = null;
	// return retval;
	// }
	//
	// @Override
	// public void remove() {
	// baseIterator.remove();
	// }
	// }

	public static int count(final Iterable<?> iterable) {
		final Iterator<?> iterator = iterable.iterator();
		return count(iterator);
	}

	/**
	 * Counts the elements of an {@link Iterator}. The iterator is consumed by
	 * count.
	 * 
	 * Postcondition: !iterator.next()
	 * 
	 * @param iterator
	 * @return the number of elements
	 */
	public static int count(final Iterator<?> iterator) {
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		return count;
	}

	/**
	 * Materializes an iterable as a collection. If the iterable is a
	 * collection, return the original iterable. Therefore, the client must
	 * usually ensure that he is the owner of the iterable.
	 * 
	 * @param <T>
	 *            element type of iterable
	 * @param iterable
	 *            an iterable
	 * @return a collection containing the elements of the iterable
	 */
	public static <T> Collection<T> materialize(final Iterable<T> iterable) {
		if (iterable instanceof Collection<?>) {
			return (Collection<T>) iterable;
		} else {
			return materialize(iterable, new Vector<T>());
		}
	}

	/**
	 * Materializes an iterable into a client-supplied collection. If the
	 * collection already contains elements, new elements are added.
	 * 
	 * @param <T>
	 *            element type of iterable
	 * @param <Coll>
	 *            the type of result
	 * @param iterable
	 *            an iterable
	 * @param result
	 *            a collection to which the elements of the iterable are added
	 * @return the result
	 */
	public static <T, Coll extends Collection<T>> Coll materialize(
			final Iterable<T> iterable, final Coll result) {
		for (final T obj : iterable) {
			result.add(obj);
		}
		return result;
	}

	public static <T extends Comparable<? super T>> List<T> materializeSorted(
			final Iterable<T> iterable) {
		return materializeSorted(iterable, new ArrayList<T>());
	}

	public static <T extends Comparable<? super T>, ListT extends List<T>> ListT materializeSorted(
			final Iterable<T> iterable, final ListT result) {
		materialize(iterable, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * Applies some mapping to a sequence of elements, which yields an
	 * {@link Iterable} of a possibly different element type for each element,
	 * and returns a chain of all result iterables.
	 * 
	 * @param <InputT>
	 * @param <ResultT>
	 * @param inputObjects
	 * @param factory
	 *            a mapping
	 * @return
	 */
	public static <InputT, ResultT> Iterable<ResultT> mapToIterablesAndChain(
			final Iterable<InputT> inputObjects,
			final IMapFunctor<? super InputT, ? extends Iterable<? extends ResultT>> factory) {
		final IterableChain<ResultT> resultChain = new IterableChain<ResultT>();
		for (final InputT obj : inputObjects) {
			resultChain.addIterable(factory.mapItem(obj));
		}
		return resultChain;
	}

	public static <InputT, ResultT> Iterable<ResultT> mapToIterablesAndChainDeferred(
			final Iterable<InputT> inputObjects,
			final IMapFunctor<? super InputT, ? extends Iterable<? extends ResultT>> factory) {
		return new Iterable<ResultT>() {

			@Override
			public Iterator<ResultT> iterator() {
				return new MapAndChainIterator<InputT, ResultT>(
						inputObjects.iterator(),
						new IMapFunctor<InputT, Iterator<ResultT>>() {

							@SuppressWarnings("unchecked")
							@Override
							public Iterator<ResultT> mapItem(final InputT obj) {
								return (Iterator<ResultT>) factory.mapItem(obj)
										.iterator();
							}
						});
			}
		};
	}

	public static <InputT, ResultT> Iterator<ResultT> mapToIteratorsAndChainDeferred(
			final Iterator<InputT> inputObjects,
			final IMapFunctor<? super InputT, ? extends Iterator<? extends ResultT>> factory) {
		return new MapAndChainIterator<InputT, ResultT>(inputObjects, factory);
	}

	@SuppressWarnings("unchecked")
	/**
	 * @param <InputT>
	 * @param <ResultT>
	 * @param inputObjects
	 * @param factory
	 * @return
	 */
	public static <InputT, ResultT> Iterator<ResultT> mapToIteratorsAndChain(
			final Iterable<InputT> inputObjects,
			final IMapFunctor<? super InputT, ? extends Iterator<? extends ResultT>> factory) {
		final IteratorChain resultChain = new IteratorChain();
		for (final InputT obj : inputObjects) {
			resultChain.addIterator(factory.mapItem(obj));
		}
		return resultChain;
	}

	/**
	 * Returns an iterable over only those elements of an iterator of a certain
	 * type.
	 * 
	 * @param <T>
	 * @param iterator
	 * @param classOfT
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> filterByClass(
			final Iterator<? super T> iterator, final Class<T> classOfT) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return new FilterIterator(iterator, new InstanceofPredicate(
						classOfT));
			}
		};

	}

	/**
	 * Returns an iterable over only those elements of an iterator of a certain
	 * type.
	 * 
	 * @param <T>
	 * @param iterator
	 * @param classOfT
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> filterByClass(
			final Iterable<? super T> iterable, final Class<T> classOfT) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return new FilterIterator(iterable.iterator(),
						new InstanceofPredicate(classOfT));
			}
		};

	}

	@SuppressWarnings("unchecked")
	public static <InputT, ResultT> Iterable<ResultT> map(
			final Iterable<InputT> inputIterable,
			final IMapFunctor<? super InputT, ? extends ResultT> mapFunctor) {

		return new Iterable<ResultT>() {

			@Override
			public Iterator<ResultT> iterator() {
				return IteratorUtils.transformedIterator(
						inputIterable.iterator(), new Transformer() {

							@Override
							public Object transform(final Object arg0) {
								return mapFunctor.mapItem((InputT) arg0);
							}
						});
			}
		};

	}

	public static <InputT, ResultT> List<ResultT> mapAndMaterialize(
			final Collection<InputT> inputCollection,
			final IMapFunctor<? super InputT, ? extends ResultT> mapFunctor) {
		return IterationUtils.mapAndMaterialize(inputCollection, mapFunctor,
				new ArrayList<ResultT>(inputCollection.size()));
	}

	public static <InputT, ResultT, CollT extends Collection<ResultT>> CollT mapAndMaterialize(
			final Iterable<InputT> inputIterable,
			final IMapFunctor<? super InputT, ? extends ResultT> mapFunctor,
			final CollT resultCollection) {
		for (final InputT inputObj : inputIterable) {
			resultCollection.add(mapFunctor.mapItem(inputObj));
		}
		return resultCollection;
	}

	public static <T> Iterable<T> filter(
			final Iterable<? extends T> inputIterable,
			final IUnaryPredicate<T> unaryPredicate) {
		return new Iterable<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<T> iterator() {
				return new FilterIterator(inputIterable.iterator(),
						createNonGenericAdapter(unaryPredicate));
			}

		};
	}

	private static <T> Predicate createNonGenericAdapter(
			final IUnaryPredicate<T> unaryPredicate) {
		return new Predicate() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean evaluate(final Object obj) {
				return unaryPredicate.evaluate((T) obj);
			}
		};
	}

	public static <FROM, TO> Iterable<TO> mapOptional(
			final Iterable<? extends FROM> inputObjects,
			final IMapFunctor<FROM, TO> mapFunctor) {
		return mapToIterablesAndChain(inputObjects,
				new OptionalMapFunctor<FROM, TO>(mapFunctor));
	}

	public static <T> boolean all(final Iterable<? extends T> objects,
			final IUnaryPredicate<? super T> predicate) {
		for (final T obj : objects) {
			if (!predicate.evaluate(obj)) {
				return false;
			}
		}
		return true;
	}

	public static <T, U> Iterable<Pair<T, U>> zip(final Iterable<T> first,
			final Iterable<U> second) {
		return new Iterable<Pair<T, U>>() {

			@Override
			public Iterator<Pair<T, U>> iterator() {
				return new Iterator<Pair<T, U>>() {
					private final Iterator<T> itFirst = first.iterator();
					private final Iterator<U> itSecond = second.iterator();

					@Override
					public boolean hasNext() {
						return this.itFirst.hasNext() && this.itSecond.hasNext();
					}

					@Override
					public Pair<T, U> next() {
						return new Pair<T, U>(this.itFirst.next(), this.itSecond.next());
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	public static <T> Iterable<T> chain(final Iterable<? super T>... iterable) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				// TODO this is terrible...
				return new IteratorChain(
						IterationUtils
								.mapAndMaterialize(
										Arrays.asList(iterable),
										new IMapFunctor<Iterable<? super T>, Iterator<? super T>>() {

											@Override
											public Iterator<? super T> mapItem(
													final Iterable<? super T> obj) {
												return obj.iterator();
											}
										}).toArray(new Iterator[] {}));
			}
		};
	}
}
