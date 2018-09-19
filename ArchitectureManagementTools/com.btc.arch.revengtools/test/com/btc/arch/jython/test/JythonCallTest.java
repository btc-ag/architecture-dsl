package com.btc.arch.jython.test;

import org.junit.Test;

import com.btc.arch.jython.RevEngToolsInterpreter;

public class JythonCallTest {

	@Test
	public void callJythonScript() {
		// Properties props = new Properties();
		// props.setProperty("python.path", REVENGTOOLS_BASEDIR
		// + ";D:/Programme/Java/jython2.5.2/Lib;scripts");
		// PythonInterpreter.initialize(System.getProperties(), props,
		// new String[] { "" });

		RevEngToolsInterpreter interpreter = getRevEngToolsInterpreter();
		interpreter.execfile_RevEngTools("list_module_prefixes_run.py");
	}

	@Test
	public void callJythonDirectly() {
		// Properties props = new Properties();
		// props.setProperty("python.path", REVENGTOOLS_BASEDIR
		// + ";D:/Programme/Java/jython2.5.2/Lib;scripts");
		// PythonInterpreter.initialize(System.getProperties(), props,
		// new String[] { "" });

		RevEngToolsInterpreter interpreter = getRevEngToolsInterpreter();
		interpreter.exec("from commons.configurator import Configurator");
		interpreter
				.exec("from base.dependency.dependency_if import ModuleGrouper");
		interpreter.exec("from base.modules_if import ModuleListSupply");
		interpreter.exec("Configurator().default()");
		interpreter
				.exec("module_list_supply = Configurator().get_concrete_adapter(ModuleListSupply)()");
		interpreter
				.exec("module_grouper = Configurator().get_concrete_adapter(ModuleGrouper)(module_list_supply.get_module_list())");
		interpreter
				.exec("prefixes = sorted(module_grouper.node_group_prefixes())");

		Iterable<String> prefixes = interpreter.get("prefixes", Iterable.class);
		for (String prefix : prefixes) {
			System.out.println(prefix);
		}
	}

	private RevEngToolsInterpreter getRevEngToolsInterpreter() {
		return new RevEngToolsInterpreter();
	}

}
