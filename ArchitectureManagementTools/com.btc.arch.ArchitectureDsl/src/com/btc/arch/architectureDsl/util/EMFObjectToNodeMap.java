package com.btc.arch.architectureDsl.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Component;
import com.btc.arch.architectureDsl.Interface;
import com.btc.arch.architectureDsl.Module;

public class EMFObjectToNodeMap<T> {

	private final Map<String, T> id_to_node_map = new HashMap<String, T>();

	/**
	 * Return an String id for a model object, which is unique across all
	 * objects of that model.
	 * 
	 * @param object
	 *            an ArchitectureDsl model object
	 * @return a unique String id, or "null"
	 */
	static public String getNodeId(final EObject object) {
		// TODO kann man diese nicht irgendwie allgemein für beliebige EObject's
		// erzeugen?
		// mit object.getEClass().getName() und ArchitectureDslNameMapper

		if (object instanceof Component) {
			return "Component_" + ((Component) object).getName();
		} else if (object instanceof Interface) {
			return "Interface_" + ((Interface) object).getName();
		} else if (object instanceof Module) {
			return "Module_" + ((Module) object).getName();
		}
		return "null";
	}

	public boolean containsNode(final EObject object) {
		return id_to_node_map.containsKey(getNodeId(object));
	}

	public T getNode(final EObject object) {
		return id_to_node_map.get(getNodeId(object));
	}

	public void putNode(final EObject object, T node) {
		id_to_node_map.put(getNodeId(object), node);
	}

}