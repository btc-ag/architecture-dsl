package com.btc.arch.jython.emf;

import org.python.core.PyObject;

import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;
import com.btc.arch.jython.IPythonGraphProvider;
import com.btc.arch.jython.RevEngToolsInterpreter;

public class EMFPythonGraphProvider implements IPythonGraphProvider {
	private ArchitectureDslResourceManager resourceManager;
	private final RevEngToolsInterpreter interpreter;

	public EMFPythonGraphProvider(
			ArchitectureDslResourceManager resourceManager,
			RevEngToolsInterpreter interpreter) {
		this.resourceManager = resourceManager;
		this.interpreter = interpreter;
	}

	@Override
	public PyObject getGraph() {
		return new EMFModelToPythonGraphConverter(this.interpreter)
				.convertToPythonGraph(resourceManager);
	}

}
