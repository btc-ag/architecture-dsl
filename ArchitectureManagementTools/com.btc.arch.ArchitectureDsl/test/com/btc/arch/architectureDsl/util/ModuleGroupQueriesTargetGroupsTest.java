package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.btc.arch.architectureDsl.ModuleGroup;

public class ModuleGroupQueriesTargetGroupsTest extends
		ArchDslFileUsageTestBase {
	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupTargetGroupsTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupTargetGroupsTest";
	}

	@Test
	public void testGetAllVersionedTargetModuleGroups1() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "A");
		ModuleGroup moduleGroup2 = ModelQueries.getModuleGroupByName(
				resource.getContents(), "B");
		ModuleGroup[] targetGroups = { moduleGroup2 };
		assertArrayEquals(targetGroups, ModuleGroupQueries
				.getAllVersionedTargetModuleGroups(moduleGroup).toArray());
	}

}
