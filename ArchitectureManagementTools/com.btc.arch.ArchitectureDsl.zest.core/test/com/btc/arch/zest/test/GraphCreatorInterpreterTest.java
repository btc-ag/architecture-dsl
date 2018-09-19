package com.btc.arch.zest.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.junit.Test;

import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;
import com.btc.arch.zest.GraphCreatorInterpreter;

public class GraphCreatorInterpreterTest {

	@Test
	public void testGetGraph() {
		URI uri = URI.createFileURI("test/com/btc/arch/zest/test/Test.archdsl");
		Resource resource = ArchitectureDslFileUtils.getOneShotResource(uri);

		ArchitectureDslResourceManager architectureDslResourceManager = new ArchitectureDslResourceManager(
				resource);
		GraphCreatorInterpreter graphCreatorInterpreter = new GraphCreatorInterpreter(
				new Shell(), ZestStyles.NONE, architectureDslResourceManager);

		Graph graph = graphCreatorInterpreter.getGraph();

		assertEquals(4, graph.getNodes().size());
		assertEquals(4, graph.getConnections().size());
		System.out.println(graph);
	}
}
