/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package com.btc.arch.zest.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.btc.arch.zest.ZestGraphProvider;

/**
 * View showing a zest graph.
 * 
 * @author Niels Streekmann
 * @author Simon Giesecke
 * @author Fabian Steeg (fsteeg) (original version)
 */
public final class ZestGraphViewZoom extends ViewPart implements
		IZoomableWorkbenchPart {

	private static final RGB BACKGROUND = JFaceResources.getColorRegistry()
			.getRGB("org.eclipse.jdt.ui.JavadocView.backgroundColor");

	private Composite graphParent;
	private GraphViewer graphViewer;
	ZoomContributionViewItem i;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(final Composite parent) {
		graphParent = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		graphParent.setLayout(layout);
		graphParent.setBackground(new Color(graphParent.getDisplay(),
				BACKGROUND));
		this.graphViewer = new GraphViewer(graphParent, SWT.BORDER);

		graphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		graphViewer.applyLayout();
		// NodeFilter filter = new NodeFilter();
		// ViewerFilter[] filters = new ViewerFilter[1];
		// filters[0] = filter;
		// graphViewer.setFilters(filters);

		getViewSite().getActionBars().getToolBarManager()
				.add(new ZoomContributionViewItem(this));
	}

	public void setGraph(final ZestGraphProvider graphProvider, boolean async) {
		Runnable runnable = new Runnable() {
			public void run() {
				updateZestGraph(graphProvider);
			}

			private void updateZestGraph(ZestGraphProvider graphProvider) {
				if (graphViewer.getGraphControl() != null) {
					graphViewer.getGraphControl().dispose();
				}
				graphViewer.setControl(graphProvider.getGraph());
				setupLayout();
				graphParent.layout();
				graphViewer.applyLayout();
			}
		};
		Display display = getViewSite().getShell().getDisplay();
		if (async) {
			display.asyncExec(runnable);
		} else {
			display.syncExec(runnable);
		}
	}

	private void setupLayout() {
		if (graphViewer.getGraphControl() != null) {
			GridData gd = new GridData(GridData.FILL_BOTH);
			graphViewer.getGraphControl().setLayout(new GridLayout());
			graphViewer.getGraphControl().setLayoutData(gd);
			Color color = new Color(graphViewer.getGraphControl().getDisplay(),
					BACKGROUND);
			graphViewer.getGraphControl().setBackground(color);
			graphViewer.getGraphControl().getParent().setBackground(color);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
		// TODO: How can the listeners in the ZestGraphViewCommandHandler be
		// removed?
		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(
		// resourceChangeListener);
		if (graphViewer.getGraphControl() != null) {
			graphViewer.getGraphControl().dispose();
		}
		if (graphParent != null) {
			graphParent.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (graphViewer.getGraphControl() != null
				&& !graphViewer.getGraphControl().isDisposed()) {
			graphViewer.getGraphControl().setFocus();
		}
	}

	protected void applyGraphLayout() {
		if (graphViewer.getGraphControl() != null) {
			graphViewer.applyLayout();
		}
	}

	protected Composite getGraphParent() {
		return this.graphParent;
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return this.graphViewer;
	}
}
