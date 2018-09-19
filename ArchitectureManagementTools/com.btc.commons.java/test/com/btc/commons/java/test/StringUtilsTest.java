package com.btc.commons.java.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.btc.commons.java.StringUtils;

public class StringUtilsTest {

	@Test
	public void testFindCommonPrefix() {
		assertEquals("", StringUtils.findCommonPrefix(new String[] {}));
		assertEquals("",
				StringUtils.findCommonPrefix(new String[] { "A", "B" }));
		assertEquals("A.",
				StringUtils.findCommonPrefix(new String[] { "A.B", "A.C" }));
	}
}
