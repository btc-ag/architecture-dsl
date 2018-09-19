package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.commons.emf.EcoreUtils;

public abstract class ModuleGroupQueriesTestBase extends
		ArchDslFileUsageTestBase {

	public ModuleGroupQueriesTestBase() {
		super();
	}

	protected void performTestGetParentModuleGroups(
			final String[] moduleGroupNames,
			final String[] expectedParentModuleGroupNames) {
		final Collection<String> actualParentModuleGroupNames = new ArrayList<String>();
		for (String moduleGroupName : Arrays.asList(moduleGroupNames)) {
			final ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
					resource.getContents(), moduleGroupName);
			final ModuleGroup parentModuleGroup = ModuleGroupQueries
					.getParentModuleGroup(moduleGroup);
			actualParentModuleGroupNames
					.add(parentModuleGroup != null ? parentModuleGroup
							.getName() : null);
		}
		assertArrayEquals(expectedParentModuleGroupNames,
				actualParentModuleGroupNames.toArray());
	}

	protected void performTestGetEffectiveAllowedTargetDomains(
			final String[] moduleGroupNames,
			final String[][] expectedEffectiveAllowedTargetDomainNames) {
		for (int i = 0; i < expectedEffectiveAllowedTargetDomainNames.length; ++i) {
			final ModuleGroup moduleGroup = ModelQueries.getModuleGroupByName(
					resource.getContents(), moduleGroupNames[i]);
			final Collection<Domain> actualEffectiveAllowedTargetDomains = ModuleGroupQueries
					.getEffectiveAllowedTargetDomains(moduleGroup);
			String[] actualEffectiveAllowedTargetDomainNames = EcoreUtils
					.getStringFeatureArray(actualEffectiveAllowedTargetDomains,
							ArchitectureDslPackage.Literals.DOMAIN__NAME);
			Arrays.sort(actualEffectiveAllowedTargetDomainNames);
			assertEquals(
					"test case " + moduleGroup.getName(),
					Arrays.asList(expectedEffectiveAllowedTargetDomainNames[i]),
					Arrays.asList(actualEffectiveAllowedTargetDomainNames));
		}
	}
}