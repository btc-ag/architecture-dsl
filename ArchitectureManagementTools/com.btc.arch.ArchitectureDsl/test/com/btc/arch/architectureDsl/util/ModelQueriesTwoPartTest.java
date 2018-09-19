package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;

public class ModelQueriesTwoPartTest extends ArchDslFileUsageTestBase {

	@Test
	public void testGetModuleByName() {
		final Module module = ModelQueries.getModuleByName(getAllContents(),
				"A.B");
		assertNotNull(module);
		assertEquals("A.B", module.getName());
	}

	@Test
	public void testGetModuleGroupByName() {
		final Collection<? extends EObject> allContents = getAllContents();
		final ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				allContents, "A");
		assertNotNull(moduleGroup);
		assertEquals("A", moduleGroup.getName());
	}

	@Override
	protected String getTestDataFileName() {
		// TODO Auto-generated method stub
		return "Module.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "TwoPartTest";
	}

}
