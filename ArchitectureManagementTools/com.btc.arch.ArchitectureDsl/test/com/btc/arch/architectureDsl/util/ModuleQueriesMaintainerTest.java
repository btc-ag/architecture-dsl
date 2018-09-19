package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.btc.arch.architectureDsl.Module;

public class ModuleQueriesMaintainerTest extends ArchDslFileUsageTestBase {
	@Override
	protected String getTestDataFileName() {
		return "ModuleMaintainerTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleMaintainerTest";
	}

	@Test
	public void testGetEffectiveMaintainer1() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.A");
		assertEquals("A.Maintainer",
				ModuleQueries.getEffectiveMaintainer(module));
	}

	@Test
	public void testGetEffectiveMaintainer2() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.B");
		assertEquals("A.Maintainer",
				ModuleQueries.getEffectiveMaintainer(module));
	}

	@Test
	public void testGetEffectiveMaintainer3() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.C");
		assertEquals("A.C.Maintainer",
				ModuleQueries.getEffectiveMaintainer(module));
	}
}
