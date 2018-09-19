package com.btc.arch.jython.zest;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.python.core.PyObject;

import com.btc.arch.jython.IPythonGraphProvider;
import com.btc.arch.jython.RevEngToolsInterpreter;
import com.btc.arch.zest.ZestGraphProvider;

public class PythonZestGraphProvider implements ZestGraphProvider {
	private final IPythonGraphProvider pythonGraphProvider;
	private RevEngToolsInterpreter interpreter;
	private final Composite parent;
	private final int style;
	private boolean collapse;

	public PythonZestGraphProvider(Composite parent, int style,
			IPythonGraphProvider pythonGraphProvider,
			RevEngToolsInterpreter interpreter) {
		this(parent, style, pythonGraphProvider, interpreter, true);
	}

	public PythonZestGraphProvider(Composite parent, int style,
			IPythonGraphProvider pythonGraphProvider,
			RevEngToolsInterpreter interpreter, boolean collapse) {
		this.parent = parent;
		this.style = style;
		this.pythonGraphProvider = pythonGraphProvider;
		this.interpreter = interpreter;
		this.collapse = collapse;
	}

	public Graph zestRenderGraph(PyObject input_graph, Composite parent,
			int style) {
		interpreter
				.exec("from infrastructure.graph_layout.zest.zest_outputter import ZestGraphOutputter");
		interpreter.exec("import commons.graph.attrgraph_util");
		interpreter
				.exec("from commons.graph.output_if import (NodeGroupingConfiguration, DecoratorSet)");
		interpreter
				.exec("from base.dependency.dependency_output_util import DependencyFilterOutputter");
		interpreter
				.exec("from base.dependency.dependency_if import ModuleGrouper");

		interpreter.doConfigure();
		interpreter.set("input_graph", input_graph);
		interpreter.set("parent", parent);
		interpreter.set("style", style);
		interpreter
				.exec("dependency_filter_outputter = Configurator().get_concrete_adapter(DependencyFilterOutputter)(decorator_config=DecoratorSet())");
		interpreter
				.exec("module_grouper = Configurator().get_concrete_adapter(ModuleGrouper)(input_graph.node_names())");
		interpreter
				.exec("module_group_conf = Configurator().get_concrete_adapter(NodeGroupingConfiguration)(module_grouper=module_grouper)");
		interpreter.set("collapse", this.collapse);
		interpreter
				.exec("graph_outputter = "
						+ "dependency_filter_outputter.output_graph(description='Test graph', "
						+ "outfile=None, graph=input_graph, "
						+ "node_group_conf=module_group_conf, "
						+ "graph_outputter_class=ZestGraphOutputter,"
						+ "add_graph_outputter_options=dict({'zest_parent': parent, 'zest_style': style}),"
						+ "collapse=collapse)");
		// interpreter.exec("outputter.set_parent(parent)");
		// interpreter.exec("outputter.set_style(style)");
		// interpreter.exec("outputter.output_all()");
		interpreter
				.exec("zest_graph = graph_outputter.get_output_zest_graph()");
		Graph graph = interpreter.get("zest_graph", Graph.class);
		return graph;
	}

	@Override
	public Graph getGraph() {
		PyObject input_graph = pythonGraphProvider.getGraph();
		Graph graph = zestRenderGraph(input_graph, parent, style);
		return graph;
	}

}
