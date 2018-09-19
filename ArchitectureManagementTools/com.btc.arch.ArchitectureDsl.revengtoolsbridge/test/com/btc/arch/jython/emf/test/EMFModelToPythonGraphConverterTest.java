package com.btc.arch.jython.emf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Before;
import org.junit.Test;
import org.python.core.PyObject;

import com.btc.arch.ArchitectureDslStandaloneSetup;
import com.btc.arch.jython.RevEngToolsInterpreter;
import com.btc.arch.jython.emf.EMFModelToPythonGraphConverter;
import com.google.inject.Injector;

public class EMFModelToPythonGraphConverterTest {
	private Resource resource;
	private RevEngToolsInterpreter interpreter;

	@Before
	public void createResource() throws UnsupportedEncodingException {
		Injector injector = new ArchitectureDslStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
		ResourceSet set = injector.getInstance(ResourceSet.class);

		URI uri = URI
				.createFileURI("test/com/btc/arch/jython/emf/test/Test.archdsl");
		this.resource = set.getResource(uri, true);
	}

	@Before
	public void createInterpreter() {
		this.interpreter = new RevEngToolsInterpreter();
	}

	@Test
	public void testConvertToPythonGraph() {
		PyObject graph = new EMFModelToPythonGraphConverter(this.interpreter)
				.convertToPythonGraph(resource);
		assertNotNull(graph);
		this.interpreter.set("par_graph", graph);
		this.interpreter.exec("res_string = str(par_graph)");
		String description = this.interpreter.get("res_string", String.class);
		assertEquals("<MutableAttributeGraph(4 nodes, 4 edges)>", description);
	}

}
