package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class ModelQueriesTest extends ArchDslFileUsageTestBase {

	@Override
	protected String getTestDataFileName() {
		return "DomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "DomainTest";
	}

	@Test
	public void testGetModuleByName() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "A.B.D.A");
		assertNotNull(module);
		assertEquals("A.B.D.A", module.getName());
	}

	@Test
	public void testGetModuleGroupByName() {
		final ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "A.B.D");
		assertNotNull(moduleGroup);
		assertEquals("A.B.D", moduleGroup.getName());
	}

	@Test
	public void testGetDomainByName() {
		final Domain domain = ModelQueries.getDomainByName(
				resource.getContents(), "ABC");
		assertNotNull(domain);
		assertEquals("ABC", domain.getName());
	}

	@Test
	public void testGetModuleByNameFailure() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "__UNKNOWN__");
		assertNull(module);
	}

	@Test
	public void testGetAllModules() {
		final Iterable<Module> allModules = ModelQueries.getAllModules(resource
				.getContents());
		int count = IterationUtils.count(allModules);
		assertEquals(7, count);
	}

	@Test
	public void testGetAllDependenciesAsPairs() {
		final Collection<Pair<Module, Module>> allDependencies = IterationUtils
				.materialize(ModelQueries.getAllDependenciesAsPairs(resource
						.getContents()));
		assertEquals(4, allDependencies.size());
	}
}
