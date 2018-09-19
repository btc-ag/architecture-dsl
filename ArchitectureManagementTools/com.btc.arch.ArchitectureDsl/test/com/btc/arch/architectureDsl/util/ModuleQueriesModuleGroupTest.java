package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleQueriesModuleGroupTest extends ArchDslFileUsageTestBase {
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
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"B.C.Y");
		String domainName = getEffectiveDomainName(module);
		assertEquals("Zero", domainName);
	}

	@Test
	public void testGetEffectiveDomains2() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"C.D.A");
		String domainName = getEffectiveDomainName(module);
		assertEquals("Crypto", domainName);
	}

	@Test
	public void testGetEffectiveDomains3() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"D.E.Good");
		String domainName = getEffectiveDomainName(module);
		assertEquals("TimeSeries", domainName);
	}

	@Test
	public void testGetEffectiveDomains4() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"NoDomain");
		assertEquals(null, ModuleQueries.getEffectiveDomain(module));
	}

	@Test
	public void testGetEffectiveDomains5() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"C.D.E.X");
		String domainName = getEffectiveDomainName(module);
		assertEquals("Crypto", domainName);
	}

	@Test
	public void testGetEffectiveModuleGroup1() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"B.C.Y");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals("B.C", moduleGroupName);
	}

	@Test
	public void testGetEffectiveModuleGroup2() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"C.D.A");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals("C.D", moduleGroupName);
	}

	@Test
	public void testGetEffectiveModuleGroup3() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"D.E.Good");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals("D.E", moduleGroupName);
	}

	@Test
	public void testGetEffectiveModuleGroup4() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"NoDomain");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals(null, moduleGroupName);
	}

	@Test
	public void testGetEffectiveModuleGroup5() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "D.E.X.Y");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals("D.E", moduleGroupName);
	}

	@Test
	public void testGetEffectiveModuleGroup6() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "ExplicitModuleGroup");
		String moduleGroupName = getEffectiveModuleGroupName(module);
		assertEquals("B", moduleGroupName);
	}

	@Test
	public void testGetVersionedEffectiveModuleGroup1() {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"E.A.A");
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "E");
		assertEquals(moduleGroup,
				ModuleQueries.getVersionedEffectiveModuleGroup(module));
	}

	private String getEffectiveModuleGroupName(Module module) {
		final ModuleGroup effectiveModuleGroup = ModuleQueries
				.getEffectiveModuleGroup(module);
		return effectiveModuleGroup != null ? effectiveModuleGroup.getName()
				: null;
	}

	private String getEffectiveDomainName(Module module) {
		final Domain domain = ModuleQueries.getEffectiveDomain(module);
		// TODO: Should this return null or an empty string?
		if (domain == null)
			return null;
		final String domainName = domain.getName();
		return domainName;
	}

	@Test
	public void testGetAllDependenciesAsPairs() {
		final Module module = ModelQueries.getModuleByName(
				resource.getContents(), "D.E.X.Y");
		final Collection<Pair<Module, Module>> allDependenciesAsPairs = IterationUtils
				.materialize(ModuleQueries.getAllDependenciesAsPairs(module));
		final Pair<String, String>[] result = new Pair[allDependenciesAsPairs
				.size()];
		int i = 0;
		for (final Pair<Module, Module> dependencyPair : allDependenciesAsPairs) {
			result[i] = new Pair<String, String>(dependencyPair.getFirst()
					.getName(), dependencyPair.getSecond().getName());
			i++;
		}
		assertArrayEquals(new Pair[] {
				new Pair<String, String>("D.E.X.Y", "B.C.Y"),
				new Pair<String, String>("D.E.X.Y", "C.D.A") }, result);
	}
}
