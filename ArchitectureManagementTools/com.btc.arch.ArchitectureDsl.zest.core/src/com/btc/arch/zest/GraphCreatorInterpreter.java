/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package com.btc.arch.zest;

import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.btc.arch.architectureDsl.Component;
import com.btc.arch.architectureDsl.Interface;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;
import com.btc.arch.architectureDsl.util.ArchitectureDslSwitch;
import com.btc.arch.architectureDsl.util.EMFObjectToNodeMap;

/**
 * Create a Zest graph instance from a architecture dsl model string by
 * interpreting the AST of the parsed architecture dsl model.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class GraphCreatorInterpreter extends
		ArchitectureDslSwitch<Object> implements ZestGraphProvider {

	private EMFObjectToNodeMap<GraphNode> emf_object_to_node_map = new EMFObjectToNodeMap<GraphNode>();
	private Graph graph;
	private GraphContainer currentSubgraph;
	private final Composite parent;
	private final int style;
	private final ArchitectureDslResourceManager architectureDslResourceManager;

	public GraphCreatorInterpreter(Composite parent, int style,
			ArchitectureDslResourceManager architectureDslResourceManager) {
		this.parent = parent;
		this.style = style;
		this.architectureDslResourceManager = architectureDslResourceManager;
	}

	public Graph getGraph() {
		return create(architectureDslResourceManager, new Graph(parent, style));
	}

	public Graph create(
			ArchitectureDslResourceManager architectureDslResourceManager,
			Graph graph) {
		if (architectureDslResourceManager.getErrors().size() > 0) {
			throw new IllegalArgumentException(String.format(
					ArchitectureDslZestMessages.GraphCreatorInterpreter_0
							+ ": %s", architectureDslResourceManager //$NON-NLS-1$
							.getErrors().toString()));
		}
		this.graph = graph;
		TreeIterator<EObject> contents = architectureDslResourceManager
				.getContents();
		while (contents.hasNext()) {
			doSwitch(contents.next());
		}
		layoutSubgraph();
		return graph;
	}

	@Override
	public Object caseModel(Model model) {
		createGraph();
		return null;
	}

	@Override
	public Object caseModule(Module module) {
		createModuleNode(module);
		return null;
	}

	@Override
	public Object caseComponent(Component component) {
		createComponentNode(component);
		return super.caseComponent(component);
	}

	@Override
	public Object caseInterface(Interface iface) {
		createInterfaceNode(iface);
		return super.caseInterface(iface);
	}

	private void layoutSubgraph() {
		if (currentSubgraph != null) {
			currentSubgraph.applyLayout();
			currentSubgraph.open(false);
			/*
			 * TODO do this only after the end of each subgraph if possible, and
			 * set subgraph to null to have subsequent nodes added to the parent
			 * graph (currently subsequent nodes are in latest subgraph).
			 */}
	}

	private IContainer currentParentGraph() {
		return currentSubgraph != null ? currentSubgraph : graph;
	}

	private void createGraph() {
		graph.setLayoutAlgorithm(new CompositeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] {
						new TreeLayoutAlgorithm(
								LayoutStyles.NO_LAYOUT_NODE_RESIZING),
						new HorizontalShift(
								LayoutStyles.NO_LAYOUT_NODE_RESIZING) }), true);
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
	}

	private void createComponentNode(final Component component) {
		if (!emf_object_to_node_map.containsNode(component)) {
			GraphNode node = new GraphNode(currentParentGraph(),
					ZestStyles.NONE, component.getName());
			emf_object_to_node_map.putNode(component, node);

			String tooltip = "Modules: \n";
			for (Module module : component.getModules()) {
				tooltip += module.getName() + "\n";
			}
			node.setTooltip(new Label(tooltip));
			node.setBackgroundColor(parent.getDisplay().getSystemColor(
					SWT.COLOR_YELLOW));

			for (Interface providedInterface : component
					.getProvidedInterfaces()) {
				createInterfaceNode(providedInterface);
				GraphConnection graphConnection = new GraphConnection(graph,
						ZestStyles.NONE,
						emf_object_to_node_map.getNode(component),
						emf_object_to_node_map.getNode(providedInterface));
				graphConnection.setText("provides");
				graphConnection.setTooltip(new Label(component.getName()
						+ " provides " + providedInterface.getName()));
			}
			for (Interface requiredInterface : component
					.getRequiredInterfaces()) {
				createInterfaceNode(requiredInterface);
				GraphConnection graphConnection = new GraphConnection(graph,
						ZestStyles.NONE,
						emf_object_to_node_map.getNode(component),
						emf_object_to_node_map.getNode(requiredInterface));
				graphConnection.setText("requires");
				graphConnection.setTooltip(new Label(component.getName()
						+ " requires " + requiredInterface.getName()));
			}
		}
	}

	private void createInterfaceNode(final Interface iface) {
		if (!emf_object_to_node_map.containsNode(iface)) {
			GraphNode node = new GraphNode(currentParentGraph(),
					ZestStyles.NONE, iface.getName());
			node.setBackgroundColor(parent.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));
			emf_object_to_node_map.putNode(iface, node);
		}
	}

	private void createModuleNode(final Module module) {
		if (!emf_object_to_node_map.containsNode(module)) {
			GraphNode node = new GraphNode(currentParentGraph(),
					ZestStyles.NONE, module.getName());
			node.setBackgroundColor(getModuleNodeColor(module));
			// node.setData(nodeId);
			emf_object_to_node_map.putNode(module, node);
			String tooltip = "Depends on: \n";

			// Create implements connections
			for (Module usedModule : module.getImplementedModules()) {
				createModuleNode(usedModule);
				GraphConnection graphConnection = new GraphConnection(graph,
						ZestStyles.NONE,
						emf_object_to_node_map.getNode(module),
						emf_object_to_node_map.getNode(usedModule));
				graphConnection.setText("<<implements>>");
				tooltip += "Implements: " + usedModule.getName() + "\n";
			}

			// Create uses connections
			for (Module usedModule : module.getUsedModules()) {
				createModuleNode(usedModule);
				GraphConnection graphConnection = new GraphConnection(graph,
						ZestStyles.NONE,
						emf_object_to_node_map.getNode(module),
						emf_object_to_node_map.getNode(usedModule));
				graphConnection.setText("<<uses>>");
				tooltip += "Uses: " + usedModule.getName() + "\n";
			}

			// Create tests connections
			for (Module usedModule : module.getTestedModule()) {
				createModuleNode(usedModule);
				GraphConnection graphConnection = new GraphConnection(graph,
						ZestStyles.NONE,
						emf_object_to_node_map.getNode(module),
						emf_object_to_node_map.getNode(usedModule));
				graphConnection.setText("<<tests>>");
				tooltip += "Tests: " + usedModule.getName() + "\n";
			}

			node.setTooltip(new Label(tooltip));
		}
	}

	private Color getModuleNodeColor(Module module) {
		Display display = parent.getDisplay();
		Color color = display.getSystemColor(SWT.COLOR_WHITE);
		if (module.getType().contains(ModuleType.FRAMEWORK)) {
			color = display.getSystemColor(SWT.COLOR_GRAY);
		}
		if (module.getType().contains(ModuleType.FRAMEWORK_TEST)
				|| module.getType().contains(ModuleType.IMPLEMENTATION_TEST)
				|| module.getType().contains(ModuleType.INTERFACE_TEST)) {
			color = display.getSystemColor(SWT.COLOR_GREEN);
		}
		if (module.getType().contains(ModuleType.IMPLEMENTATION)) {
			color = display.getSystemColor(SWT.COLOR_YELLOW);
		}
		if (module.getType().contains(ModuleType.INTERFACE)) {
			color = display.getSystemColor(SWT.COLOR_CYAN);
		}
		return color;
	}
}
