package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleQueriesDomainTest extends ArchDslFileUsageTestBase {
	@Override
	protected String getTestDataFileName() {
		return "DomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "DomainTest";
	}

	@Test
	public void testGetAllDependenciesNone() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.B.C.Y");
		Iterable<Module> dependencies = ModuleQueries
				.getAllDependencyTargets(module);
		assertFalse(dependencies.iterator().hasNext());
	}

	@Test
	public void testGetAllDependenciesUsesOnly() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "A.B.D.A");
		final Iterable<Module> dependencies = ModuleQueries
				.getAllDependencyTargets(module);
		final Iterator<Module> depIterator = dependencies.iterator();
		assertTrue(depIterator.hasNext());
		final Module dependentModule = depIterator.next();
		assertEquals("A.B.C.Y", dependentModule.getName());
	}

	@Test
	public void testGetEffectiveAllowedTargetDomainsNoModuleGroup() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"NoDomain");
		Collection<Domain> domains = IterationUtils.materialize(ModuleQueries
				.getEffectiveAllowedTargetDomains(module));
		assertEquals(0, domains.size());
	}

	@Test
	public void testGetEffectiveAllowedTargetDomains() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.B.C.Y");
		Collection<Domain> domains = IterationUtils.materialize(ModuleQueries
				.getEffectiveAllowedTargetDomains(module));
		assertArrayEquals(
				new Domain[] { ModelQueries.getDomainByName(
						resource.getContents(), "Zero") }, domains.toArray());
	}

	@Test
	public void testIsDependencyAllowedByDomainModel1() {
		Module fromModule = ModelQueries.getModuleByName(
				resource.getContents(), "NoDomain"), toModule = ModelQueries
				.getModuleByName(resource.getContents(), "A.B.D.A");
		assertNotNull(fromModule);
		assertNotNull(toModule);
		assertFalse(ModuleQueries.isDependencyAllowedByDomainModel(fromModule,
				toModule));
	}

	@Test
	public void testIsDependencyAllowedByDomainModel2() {
		Module fromModule = ModelQueries.getModuleByName(
				resource.getContents(), "A.B.E.Bad"), toModule = ModelQueries
				.getModuleByName(resource.getContents(), "A.B.D.A");
		assertNotNull(fromModule);
		assertNotNull(toModule);
		assertFalse(ModuleQueries.isDependencyAllowedByDomainModel(fromModule,
				toModule));
	}

	@Test
	public void testIsDependencyAllowedByDomainModel3() {
		Module fromModule = ModelQueries.getModuleByName(
				resource.getContents(), "A.B.E.Good"), toModule = ModelQueries
				.getModuleByName(resource.getContents(), "A.B.C.Y");
		assertNotNull(fromModule);
		assertNotNull(toModule);
		assertTrue(ModuleQueries.isDependencyAllowedByDomainModel(fromModule,
				toModule));
	}

	@Test
	public void testIsDependencyAllowedByDomainModelSameDomain() {
		Module fromModule = ModelQueries.getModuleByName(
				resource.getContents(), "A.B.C.Y"), toModule = ModelQueries
				.getModuleByName(resource.getContents(), "A.B.C.Y");
		assertNotNull(fromModule);
		assertNotNull(toModule);
		assertTrue(ModuleQueries.isDependencyAllowedByDomainModel(fromModule,
				toModule));
	}

	// TODO:Test new method isEffectiveDomainLeafDomain
	// @Test
	// public void testGetNonLeafDomains1() {
	// Module module = ModelQueries.getModuleByName(resource.getContents(),
	// "LeafDomainModule");
	// Collection<Domain> domains = ModuleQueries.getNonLeafDomains(module);
	// assertEquals(0, domains.size());
	// }
	//
	// @Test
	// public void testGetNonLeafDomains2() {
	// Module module = ModelQueries.getModuleByName(resource.getContents(),
	// "NonLeafDomainModule");
	// Domain nonLeafDomain = ModelQueries.getDomainByName(
	// resource.getContents(), "NonLeafDomain");
	// Collection<Domain> domains = ModuleQueries.getNonLeafDomains(module);
	// assertEquals(nonLeafDomain, domains.toArray()[0]);
	// }
}
