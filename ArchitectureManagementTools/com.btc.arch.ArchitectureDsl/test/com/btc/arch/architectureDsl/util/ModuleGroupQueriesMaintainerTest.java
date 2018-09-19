package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import com.btc.arch.architectureDsl.ModuleGroup;

public class ModuleGroupQueriesMaintainerTest extends ArchDslFileUsageTestBase {

	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupMaintainerTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupMaintainerTest";
	}

	@Test
	public void testGetEffectiveMaintainer1() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "A.A");
		assertEquals("A.Maintainer",
				ModuleGroupQueries.getEffectiveMaintainer(moduleGroup));
	}

	@Test
	public void testGetEffectiveMaintainer2() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "B.A");
		assertEquals("B.Maintainer",
				ModuleGroupQueries.getEffectiveMaintainer(moduleGroup));
	}

	@Test
	public void testGetEffectiveMaintainer3() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "C.A");
		assertEquals("C.A.Maintainer",
				ModuleGroupQueries.getEffectiveMaintainer(moduleGroup));
	}
}
