package com.btc.arch.jython;

import org.python.core.PyObject;

public class LinkDepsGraphProvider implements IPythonGraphProvider {
	private final RevEngToolsInterpreter interpreter;

	public LinkDepsGraphProvider(RevEngToolsInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public PyObject getGraph() {
		interpreter
				.exec("from base.dependency.dependency_default import NullDependencyFilterConfiguration");
		interpreter
				.exec("from base.dependency.dependency_if import (DependencyFilter, DependencyFilterConfiguration, ModuleGrouper)");
		interpreter
				.exec("from base.dependency.dependency_if_deprecated import DependencyParser");
		interpreter.exec("from base.modules_if import ModuleListSupply");
		interpreter.doConfigure();
		interpreter
				.exec("parser = Configurator().get_concrete_adapter(DependencyParser)()");
		interpreter.exec("parser.process()");
		interpreter
				.exec("dep_filter = Configurator().get_concrete_adapter(DependencyFilter)(config=NullDependencyFilterConfiguration())");
		interpreter.exec("parser.output(dep_filter)");
		interpreter.exec("input_graph = dep_filter.graph()");
		PyObject input_graph = interpreter.get("input_graph");
		return input_graph;
	}

}
