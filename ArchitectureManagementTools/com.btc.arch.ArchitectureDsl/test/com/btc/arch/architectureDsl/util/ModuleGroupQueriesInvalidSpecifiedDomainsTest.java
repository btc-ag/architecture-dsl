package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.Collection;

import org.junit.Test;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.ModuleGroup;

public class ModuleGroupQueriesInvalidSpecifiedDomainsTest extends
		ArchDslFileUsageTestBase {

	@Override
	protected String getTestDataFileName() {
		return "R1.1Test.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "R1.1Test";
	}

	// Tests for Rule R1.1
	@Test
	public void testGetInvalidSpecifiedDomains1() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "ModuleGroup.A.A");
		Collection<Domain> invalidSpecifiedDomains = ModuleGroupQueries
				.getInvalidSpecifiedDomains(moduleGroup);
		assertEquals(0, invalidSpecifiedDomains.size());
	}

	@Test
	public void testGetInvalidSpecifiedDomains2() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "ModuleGroup.A.B.A");
		Collection<Domain> invalidSpecifiedDomains = ModuleGroupQueries
				.getInvalidSpecifiedDomains(moduleGroup);
		assertEquals(0, invalidSpecifiedDomains.size());
	}

	@Test
	public void testGetInvalidSpecifiedDomains3() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "ModuleGroup.A.C");
		Collection<Domain> invalidSpecifiedDomains = ModuleGroupQueries
				.getInvalidSpecifiedDomains(moduleGroup);
		Domain domain = ModelQueries.getDomainByName(resource.getContents(),
				"Domain.B");
		assertArrayEquals(new Domain[] { domain },
				invalidSpecifiedDomains.toArray());
	}

	@Test
	public void testGetInvalidSpecifiedDomains4() {
		ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
				resource.getContents(), "ModuleGroup.A.B.B");
		Collection<Domain> invalidSpecifiedDomains = ModuleGroupQueries
				.getInvalidSpecifiedDomains(moduleGroup);
		Domain domain = ModelQueries.getDomainByName(resource.getContents(),
				"Domain.B");
		assertArrayEquals(new Domain[] { domain },
				invalidSpecifiedDomains.toArray());
	}
}
