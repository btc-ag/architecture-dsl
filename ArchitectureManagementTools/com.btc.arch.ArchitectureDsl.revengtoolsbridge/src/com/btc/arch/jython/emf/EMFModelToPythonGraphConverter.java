package com.btc.arch.jython.emf;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.python.core.Py;
import org.python.core.PyObject;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;
import com.btc.arch.architectureDsl.util.ArchitectureDslSwitch;
import com.btc.arch.architectureDsl.util.EMFObjectToNodeMap;
import com.btc.arch.jython.RevEngToolsInterpreter;

class EMFModelToPythonGraphConverterSwitch extends
		ArchitectureDslSwitch<Object> {
	private EMFObjectToNodeMap<PyObject> emf_object_to_node_map = new EMFObjectToNodeMap<PyObject>();
	private final RevEngToolsInterpreter interpreter;
	private PyObject pythonGraph;

	public EMFModelToPythonGraphConverterSwitch(
			RevEngToolsInterpreter interpreter) {
		this.interpreter = interpreter;
		initPythonGraph();
	}

	private void initPythonGraph() {
		interpreter.exec("from commons.graph.graph_util import SimpleNode");
		interpreter
				.exec("from commons.graph.attrgraph_util import MutableAttributeGraph");
		interpreter.exec("graph = MutableAttributeGraph()");
		this.pythonGraph = interpreter.get("graph");
	}

	@Override
	public Object caseModule(Module module) {
		if (!emf_object_to_node_map.containsNode(module)) {
			PyObject node = makeNode(module.getName());
			// node.setData(nodeId);
			emf_object_to_node_map.putNode(module, node);

			for (Module usedModule : module.getUsedModules()) {
				caseModule(usedModule);
				PyObject from_node = node;
				PyObject to_node = emf_object_to_node_map.getNode(usedModule);
				if (to_node == null || to_node == Py.None) {
					to_node = makeNode(module.getName());
					emf_object_to_node_map.putNode(module, to_node);
					// TODO why does this happen?
				}

				addEdge(from_node, to_node);
			}

		}
		return null;
	}

	private void addEdge(PyObject from_node, PyObject to_node) {
		final String PYTHON_FROM_NODE = "par_from_node";
		final String PYTHON_TO_NODE = "par_to_node";

		assert from_node != null && from_node != Py.None;
		assert to_node != null && to_node != Py.None;

		interpreter.set(PYTHON_FROM_NODE, from_node);
		interpreter.set(PYTHON_TO_NODE, to_node);
		interpreter.exec("graph.add_edge(source=" + PYTHON_FROM_NODE
				+ ", target=" + PYTHON_TO_NODE + ")");
	}

	private PyObject makeNode(String name) {
		assert name != null && name != "";
		interpreter.set("name", name);
		interpreter.exec("node = name ; graph.add_node(name)");
		// interpreter.exec("node = SimpleNode(name) ; graph.add_node(node)");
		// // this currently does not work
		return interpreter.get("node");
	}

	public PyObject getPythonGraph() {
		// TODO return immutable copy?
		return this.pythonGraph;
	}

}

public class EMFModelToPythonGraphConverter {
	private RevEngToolsInterpreter interpreter;

	public EMFModelToPythonGraphConverter(RevEngToolsInterpreter interpreter) {
		if (interpreter != null) {
			this.interpreter = interpreter;
		} else {
			this.interpreter = new RevEngToolsInterpreter();
		}
	}

	public PyObject convertToPythonGraph(
			ArchitectureDslResourceManager resourceManager) {
		// remove this method?
		// TODO check for parse errors
		Resource resource = resourceManager.getResource();
		return convertToPythonGraph(resource);
	}

	public PyObject convertToPythonGraph(Resource resource) {
		TreeIterator<EObject> contents = EcoreUtil.getAllProperContents(
				resource, false);
		EMFModelToPythonGraphConverterSwitch modelSwitch = new EMFModelToPythonGraphConverterSwitch(
				interpreter);
		while (contents.hasNext()) {
			modelSwitch.doSwitch(contents.next());
		}
		return modelSwitch.getPythonGraph();
	}
}
