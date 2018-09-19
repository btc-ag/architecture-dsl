package com.btc.arch.architectureDsl.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ModuleQueriesDomainTest.class,
		ModuleQueriesModuleGroupTest.class, ModuleQueriesMaintainerTest.class })
public class ModuleQueriesTestSuite {

}
