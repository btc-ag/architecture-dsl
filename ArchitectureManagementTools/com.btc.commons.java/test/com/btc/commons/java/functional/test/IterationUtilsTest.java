package com.btc.commons.java.functional.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryPredicate;
import com.btc.commons.java.functional.IterationUtils;

public class IterationUtilsTest {

	private static final class TestIterable implements Iterable<String> {
		private final Integer i;

		public TestIterable(final int i) {
			this.i = new Integer(i);
		}

		public TestIterable() {
			this.i = null;
		}

		@Override
		public Iterator<String> iterator() {
			if (this.i != null) {
				return new TestIterator(this.i);
			} else {
				return new TestIterator();
			}
		}
	}

	private static final class TestIterator implements Iterator<String> {
		private int count = 0;
		private final int max;

		public TestIterator() {
			this(1);
		}

		public TestIterator(final int i) {
			this.max = i;
		}

		@Override
		public boolean hasNext() {
			return !isDone();
		}

		@Override
		public String next() {
			if (!isDone()) {
				++this.count;
				return new Integer(this.count).toString();
			}
			fail("expected that next is only called once");
			return null; // unreachable
		}

		private boolean isDone() {
			assert this.count <= this.max;
			return this.count == this.max;
		}

		@Override
		public void remove() {
			fail("expected that remove is not called");
		}
	}

	@Test
	public void testCountIterableOfQEmpty() {
		final Collection<String> vec = new Vector<String>();
		assertEquals(0, IterationUtils.count(vec));
		assertEquals(0, vec.size());
	}

	@Test
	public void testCountIterableOfQNonEmpty() {
		final Collection<String> vec = new Vector<String>();
		vec.add("foo");
		assertEquals(1, IterationUtils.count(vec));
		assertEquals(1, vec.size());
	}

	@Test
	public void testCountIteratorOfQ() {
		final Collection<String> vec = new Vector<String>();
		assertEquals(0, IterationUtils.count(vec.iterator()));
		assertEquals(0, vec.size());
	}

	@Test
	public void testMaterializeIterableOfCollection() {
		final Collection<String> vec = new Vector<String>();
		assertSame(vec, IterationUtils.materialize(vec));
	}

	@Test
	public void testMaterializeIterableOfIterable() {
		final Iterable<String> iterable = new TestIterable();

		final Collection<String> materialized = IterationUtils
				.materialize(iterable);
		assertNotSame(iterable, materialized);
		assertEquals(1, materialized.size());
	}

	@Test
	public void testMaterializeIterableOfTColl() {
		final Vector<String> result = new Vector<String>();
		result.add("0");
		final Iterable<String> iterable = new TestIterable();

		final Collection<String> materialized = IterationUtils.materialize(
				iterable, result);
		assertNotSame(iterable, materialized);
		assertSame(result, materialized);
		assertEquals(2, materialized.size());
	}

	@Test
	public void testMaterializeSorted() {
		final Iterable<String> iterable = new TestIterable(10);

		final Collection<String> materialized = IterationUtils
				.materializeSorted(iterable);
		assertNotSame(iterable, materialized);
		assertArrayEquals(new String[] { "1", "10", "2", "3", "4", "5", "6",
				"7", "8", "9" }, materialized.toArray());
	}

	@Test
	public void testCreateTypeFilterIterable() {
		final Iterable<? extends Number> num = Arrays.asList(1, 2, 3, 4.0f, 5);
		final Iterator<Number> it = (Iterator<Number>) num.iterator();
		final Iterable<Float> typeFilterIterable = IterationUtils
				.filterByClass(it, Float.class);
		final Collection<Float> c = IterationUtils
				.materialize(typeFilterIterable);
		assertArrayEquals(new Float[] { 4.0f }, c.toArray());
	}

	@Test
	public void testMapToIterablesAndChainTypeVariance() {
		final Iterable<String> chain = IterationUtils.mapToIterablesAndChain(
				Arrays.asList(100, 200, 300),
				new IMapFunctor<Object, Collection<String>>() {

					@Override
					public Collection<String> mapItem(final Object obj) {
						final String[] split = obj.toString().split("");
						final List<String> asList = Arrays.asList(split);
						return asList.subList(1, asList.size());
					}

				});
		final Collection<String> materialized = IterationUtils
				.materialize(chain);
		assertArrayEquals("actual = " + materialized, new String[] { "1", "0",
				"0", "2", "0", "0", "3", "0", "0" }, materialized.toArray());
	}

	@Test
	public void testMapToIterablesAndChainDeferredTypeVariance() {
		final Iterable<String> chain = IterationUtils
				.mapToIterablesAndChainDeferred(Arrays.asList(100, 200, 300),
						new IMapFunctor<Object, Collection<String>>() {

							@Override
							public Collection<String> mapItem(final Object obj) {
								final String[] split = obj.toString().split("");
								final List<String> asList = Arrays
										.asList(split);
								return asList.subList(1, asList.size());
							}

						});
		final Collection<String> materialized = IterationUtils
				.materialize(chain);
		assertArrayEquals("actual = " + materialized, new String[] { "1", "0",
				"0", "2", "0", "0", "3", "0", "0" }, materialized.toArray());
	}

	// TODO add test case for mapToIterablesAndChainDeferred where some iterator
	// is empty

	@Test
	public void testMapOptional() {
		final Collection<String> mapped = IterationUtils
				.materialize(IterationUtils.mapOptional(
						Arrays.asList(true, false, true, false),
						new IMapFunctor<Boolean, String>() {

							@Override
							public String mapItem(final Boolean obj) {
								return obj.booleanValue() ? "yes" : null;
							}
						}));
		assertArrayEquals("actual = " + mapped, new String[] { "yes", "yes" },
				mapped.toArray());
	}

	@Test
	public void testFilter() {
		final Collection<String> filtered = IterationUtils
				.materialize(IterationUtils.filter(
						Arrays.asList("yes", "no", "yes", "no"),
						new IUnaryPredicate<String>() {

							@Override
							public boolean evaluate(final String obj) {
								return obj.length() > 2;
							}
						}));
		assertArrayEquals("actual = " + filtered,
				new String[] { "yes", "yes" }, filtered.toArray());
	}
}
