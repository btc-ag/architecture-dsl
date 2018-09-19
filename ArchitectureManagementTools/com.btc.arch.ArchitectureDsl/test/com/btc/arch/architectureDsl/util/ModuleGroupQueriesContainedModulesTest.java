package com.btc.arch.architectureDsl.util;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;

public class ModuleGroupQueriesContainedModulesTest extends
		ArchDslFileUsageTestBase {

	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupContainedModulesTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupContainedModulesTest";
	}

	@Test
	public void testGetEffectivelyContainedModules1() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "A");
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.A");
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(module);
		assertArrayEquals(modules.toArray(), ModuleGroupQueries
				.getEffectivelyContainedModules(moduleGroup).toArray());
	}

	@Test
	public void testGetEffectivelyContainedModules2() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "B");
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"Indirect.B");
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(module);
		assertArrayEquals(modules.toArray(), ModuleGroupQueries
				.getEffectivelyContainedModules(moduleGroup).toArray());
	}

	@Test
	public void testGetEffectivelyContainedModules3() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "C");
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"C.A.A");
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(module);
		assertArrayEquals(modules.toArray(), ModuleGroupQueries
				.getEffectivelyContainedModules(moduleGroup).toArray());
	}
}
