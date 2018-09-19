package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.btc.arch.architectureDsl.ModuleGroup;

public class ModuleGroupQueriesModuleGroupTest extends
		ModuleGroupQueriesTestBase {
	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupTest";
	}

	@Test
	public void testGetEffectiveDomains1() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "B.C");
		String domainName = ModuleGroupQueries.getEffectiveDomain(moduleGroup)
				.getName();
		assertEquals("Zero", domainName);
	}

	@Test
	public void testGetEffectiveDomains2() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "C.D");
		String domainName = ModuleGroupQueries.getEffectiveDomain(moduleGroup)
				.getName();
		assertEquals("Crypto", domainName);
	}

	@Test
	public void testGetEffectiveDomains3() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "D.E");
		String domainName = ModuleGroupQueries.getEffectiveDomain(moduleGroup)
				.getName();
		assertEquals("TimeSeries", domainName);
	}

	@Test
	public void testGetEffectiveDomains4() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "B");
		assertEquals(null, moduleGroup.getDomain());
	}

	@Test
	public void testGetEffectiveDomains5() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "C.D.E");
		String domainName = ModuleGroupQueries.getEffectiveDomain(moduleGroup)
				.getName();
		assertEquals("Crypto", domainName);
	}

	@Test
	public void testGetEffectiveDomains6() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "C");
		String domainName = ModuleGroupQueries.getEffectiveDomain(moduleGroup)
				.getName();
		assertEquals("Crypto", domainName);
	}

	@Test
	public void testGetParentModuleGroups() {
		final String[] moduleGroupNames = new String[] { "B", "B.C", "C",
				"C.D", "C.D.E", "D.E", "C.X.Y" };
		final String[] expectedParentModuleGroupNames = new String[] { null,
				"B", null, "C", "C.D", null, "C" };
		performTestGetParentModuleGroups(moduleGroupNames,
				expectedParentModuleGroupNames);
	}

	@Test
	public void testGetEffectiveAllowedTargetDomains() {
		final String[] moduleGroupNames = new String[] { "B", "B.C", "C",
				"C.D", "C.D.E" };
		final String[][] expectedEffectiveAllowedTargetDomainNames = new String[][] {
				new String[] {}, new String[] { "Zero" },
				new String[] { "Crypto", "Zero" },
				new String[] { "Crypto", "TimeSeries", "Zero" },
				new String[] { "Crypto", "TimeSeries", "Zero" }, };
		performTestGetEffectiveAllowedTargetDomains(moduleGroupNames,
				expectedEffectiveAllowedTargetDomainNames);

	}
}
