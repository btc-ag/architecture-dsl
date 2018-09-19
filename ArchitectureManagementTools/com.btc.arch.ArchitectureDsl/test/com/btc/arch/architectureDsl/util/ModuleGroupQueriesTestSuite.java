package com.btc.arch.architectureDsl.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ModuleGroupQueriesModuleGroupSubDomainTest.class,
		ModuleGroupQueriesModuleGroupTest.class,
		ModuleGroupQueriesInvalidSpecifiedDomainsTest.class,
		ModuleGroupQueriesContainedModulesTest.class,
		ModuleGroupQueriesMaintainerTest.class })
public class ModuleGroupQueriesTestSuite {

}
