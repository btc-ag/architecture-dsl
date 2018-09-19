package com.btc.arch.base.test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.btc.arch.base.Context;
import com.btc.arch.base.IContext;

public class ContextTest {

	public IContext getTestSubject() {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.put("test1", "test1");
		map.put("test2.test", "test2");
		return new Context(map);
	}

	@Test
	public void testGetSubContext() {
		final IContext baseContext = getTestSubject();
		final IContext subContext = baseContext.createSubContext("test2");
		assertTrue(subContext.hasParameter("test"));
		assertFalse(subContext.hasParameter("test1"));
		assertEquals("test2", subContext.getParameter("test"));
		assertNull(subContext.getParameter("test1"));
		// the following is only true if baseContext is a root context...
		assertEquals(
				baseContext.getParameter(subContext.getAbsoluteName("test")),
				subContext.getParameter("test"));

		// Implementation-dependent assertions
		assertEquals("test2.test", subContext.getAbsoluteName("test"));
		assertEquals("test2.test1", subContext.getAbsoluteName("test1"));
	}

	@Test
	public void testGetParameterString() {
		assertNull(getTestSubject().getParameter("test2"));
		assertEquals("test1", getTestSubject().getParameter("test1"));

		// Implementation-dependent assertions
		assertEquals("test2", getTestSubject().getParameter("test2.test"));
	}

	@Test
	public void testGetParameterStringString() {
		assertEquals("foo", getTestSubject().getParameter("test2", "foo"));
		assertEquals("test1", getTestSubject().getParameter("test1", "foo"));

		// Implementation-dependent assertions
		assertEquals("test2", getTestSubject()
				.getParameter("test2.test", "foo"));
	}

	@Test
	public void testGetAbsoluteName() {
		assertEquals("test1", getTestSubject().getAbsoluteName("test1"));

		// Implementation-dependent assertions
		assertEquals("test2.test",
				getTestSubject().getAbsoluteName("test2.test"));
	}

	@Test
	public void testHasParameter() {
		assertFalse(getTestSubject().hasParameter("test2"));
		assertTrue(getTestSubject().hasParameter("test1"));

		// Implementation-dependent assertions
		assertTrue(getTestSubject().hasParameter("test2.test"));
	}

	@Test
	public void testHasAllParameters() {
		assertFalse(getTestSubject().hasAllParameters(new String[] { "test2" }));
		assertFalse(getTestSubject().hasAllParameters(
				new String[] { "test1", "test2" }));

		// Implementation-dependent assertions
		assertTrue(getTestSubject().hasAllParameters(
				new String[] { "test1", "test2.test" }));
	}

}
