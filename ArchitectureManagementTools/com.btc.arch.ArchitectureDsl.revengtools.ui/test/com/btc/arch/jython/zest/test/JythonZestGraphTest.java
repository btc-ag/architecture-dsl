package com.btc.arch.jython.zest.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.junit.Test;

import com.btc.arch.jython.LinkDepsGraphProvider;
import com.btc.arch.jython.RevEngToolsInterpreter;
import com.btc.arch.jython.zest.PythonZestGraphProvider;
import com.btc.arch.zest.ZestGraphProvider;

public class JythonZestGraphTest {
	@Test
	public void testCreateGraphNode() {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		interpreter
				.exec("from infrastructure.graph_layout.zest.zest_outputter import ZestGraphOutputter");
		interpreter.exec("import commons.graph.attrgraph_util");
		interpreter.exec("from commons.graph.output_if import DecoratorSet");
		interpreter
				.exec("graph = commons.graph.attrgraph_util.MutableAttributeGraph()");
		interpreter.exec("graph.add_node('Foo')");
		interpreter
				.exec("outputter = ZestGraphOutputter(description='Test graph', output_groups=False,"
						+ "outfile=None, graph=graph, "
						+ "decorator_config=DecoratorSet(), node_grouper=None)");
		interpreter.set("parent", new Shell());
		interpreter.exec("outputter.set_parent(parent)");
		interpreter.exec("outputter.output_all()");
		interpreter.exec("zest_graph = outputter.get_output_zest_graph()");
		Graph graph = interpreter.get("zest_graph", Graph.class);
		assertEquals(1, graph.getNodes().size());
		assertEquals("Foo", ((GraphNode) graph.getNodes().get(0)).getText());
		assertEquals(0, graph.getConnections().size());
	}

	@Test
	public void testCreateGraphEdge() {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		interpreter
				.exec("from infrastructure.graph_layout.zest.zest_outputter import ZestGraphOutputter");
		interpreter.exec("import commons.graph.attrgraph_util");
		interpreter.exec("from commons.graph.output_if import DecoratorSet");
		interpreter
				.exec("graph = commons.graph.attrgraph_util.MutableAttributeGraph()");
		interpreter.exec("graph.add_node('Foo')");
		interpreter.exec("graph.add_node('Bar')");
		interpreter.exec("graph.add_edge('Foo', 'Bar')");
		interpreter
				.exec("outputter = ZestGraphOutputter(description='Test graph', output_groups=False,"
						+ "outfile=None, graph=graph, "
						+ "decorator_config=DecoratorSet(), node_grouper=None)");
		interpreter.set("parent", new Shell());
		interpreter.exec("outputter.set_parent(parent)");
		interpreter.exec("outputter.output_all()");
		// interpreter
		// .exec("outputter._render_edge(commons.graph.attrgraph_util.AttributedEdge(from_node='Foo', to_node='Bar'))");
		interpreter.exec("zest_graph = outputter.get_output_zest_graph()");
		Graph graph = interpreter.get("zest_graph", Graph.class);
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getConnections().size());
		GraphConnection graphConnection = (GraphConnection) graph
				.getConnections().get(0);
		assertEquals("Foo", graphConnection.getSource().getText());
		assertEquals("Bar", graphConnection.getDestination().getText());
	}

	@Test
	public void testOutputBasicGraph() {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		interpreter
				.exec("from infrastructure.graph_layout.zest.zest_outputter import ZestGraphOutputter");
		interpreter.exec("import commons.graph.attrgraph_util");
		interpreter.exec("from commons.graph.output_if import DecoratorSet");
		interpreter
				.exec("input_graph = commons.graph.attrgraph_util.MutableAttributeGraph()");
		interpreter.exec("input_graph.add_node('Foo')");
		interpreter
				.exec("outputter = ZestGraphOutputter(description='Test graph', output_groups=False,"
						+ "outfile=None, graph=input_graph, "
						+ "decorator_config=DecoratorSet(), node_grouper=None)");
		interpreter.set("parent", new Shell());
		interpreter.exec("outputter.set_parent(parent)");
		interpreter.exec("outputter.output_all()");
		interpreter.exec("zest_graph = outputter.get_output_zest_graph()");
		Graph graph = interpreter.get("zest_graph", Graph.class);
		assertEquals(1, graph.getNodes().size());
		assertEquals(0, graph.getConnections().size());
		assertEquals("Foo", ((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void testOutputLinkGraph() {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		ZestGraphProvider provider = new PythonZestGraphProvider(new Shell(),
				ZestStyles.NONE, new LinkDepsGraphProvider(interpreter),
				interpreter);
		System.out.println(provider.getGraph());
		// assertEquals(1, graph.getNodes().size());
		// assertEquals(0, graph.getConnections().size());
		// assertEquals("Foo", ((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void testConfigureLayout() {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		interpreter
				.exec("from infrastructure.graph_layout.zest.zest_outputter import ZestHelper");
		Graph graph = new Graph(new Shell(), SWT.NONE);
		interpreter.set("graph", graph);
		interpreter.exec("ZestHelper.configure_layouter(graph)");
	}

}
