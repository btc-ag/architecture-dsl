package com.btc.arch.architectureDsl.util;

import org.junit.Test;

public class ModuleGroupQueriesModuleGroupSubDomainTest extends
		ModuleGroupQueriesTestBase {
	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupSubDomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupSubDomainTest";
	}

	@Test
	public void testGetEffectiveAllowedTargetDomains() {
		final String[] moduleGroupNames = new String[] { "A", "A.B" };
		final String[][] expectedEffectiveAllowedTargetDomainNames = new String[][] {
				new String[] { "DomainWithSubdomain1", "DomainWithSubdomain2" },
				new String[] { "DomainWithSubdomain2", "Subdomain1",
						"Subdomain4Subdomain" }, };
		performTestGetEffectiveAllowedTargetDomains(moduleGroupNames,
				expectedEffectiveAllowedTargetDomainNames);
	}

}
