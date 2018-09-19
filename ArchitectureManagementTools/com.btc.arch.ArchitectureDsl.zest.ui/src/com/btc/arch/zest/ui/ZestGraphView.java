/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package com.btc.arch.zest.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.zest.core.widgets.Graph;

import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.arch.zest.ModelBasedGraphProviderFactory;
import com.btc.arch.zest.ZestGraphInfo;
import com.btc.arch.zest.ZestGraphProvider;
import com.btc.arch.zest.ZestGraphProviderFactory;
import com.btc.commons.eclipse.core.EclipseWorkspaceUtils;

/**
 * View showing a zest graph.
 * 
 * @author Niels Streekmann
 * @author Simon Giesecke
 * @author Fabian Steeg (fsteeg) (original version)
 */
public final class ZestGraphView extends ViewPart {

	private static final RGB BACKGROUND = JFaceResources.getColorRegistry()
			.getRGB("org.eclipse.jdt.ui.JavadocView.backgroundColor");
	public static final String ID = "com.btc.arch.zest.ZestView";

	private static ZestGraphView zestGraphView;
	private static boolean forceUpdate = false;
	private static XtextResource currentResource;

	private Composite graphParent;
	private Graph graph;
	private ZestGraphInfo currentGraphInfo;
	private ZestGraphProviderFactory graphProviderFactory;

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
	}

	public static void setForceUpdate(boolean force) {
		forceUpdate = force;
	}

	public void setGraph(final ZestGraphProvider graphProvider,
			final ZestGraphInfo graphInfo, boolean async) {
		// TODO avoid overlapping graph updates
		if (this.currentGraphInfo == null
				|| !this.currentGraphInfo.equivalent(graphInfo) || forceUpdate) {
			this.currentGraphInfo = graphInfo;
			Runnable runnable = new Runnable() {
				public void run() {
					updateZestGraph(graphProvider);
				}

				private void updateZestGraph(ZestGraphProvider graphProvider) {
					if (graph != null) {
						graph.dispose();
					}
					graph = graphProvider.getGraph();
					setupLayout();
					graphParent.layout();
					graph.applyLayout();
				}
			};
			Display display = getViewSite().getShell().getDisplay();
			if (async) {
				display.asyncExec(runnable);
			} else {
				display.syncExec(runnable);
			}

		}
	}

	private void setupLayout() {
		if (graph != null) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			graph.setLayout(new GridLayout());
			graph.setLayoutData(gd);
			Color color = new Color(graph.getDisplay(), BACKGROUND);
			graph.setBackground(color);
			graph.getParent().setBackground(color);
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
		if (graph != null) {
			graph.dispose();
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
		if (graph != null && !graph.isDisposed()) {
			graph.setFocus();
		}
	}

	protected void applyGraphLayout() {
		if (graph != null) {
			graph.applyLayout();
		}
	}

	public Composite getGraphParent() {
		return this.graphParent;
	}

	public ZestGraphInfo getCurrentGraphInfo() {
		return currentGraphInfo;
	}

	public static ZestGraphView getZestGraphView() {
		if (zestGraphView != null)
			return zestGraphView;
		// TODO does this guarantee to find the Zest graph view if it exists?
		for (IViewReference viewRef : PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
			if (viewRef.getId().equals(ID)) {
				zestGraphView = (ZestGraphView) viewRef.getView(true);
				return (ZestGraphView) viewRef.getView(true);
			}

		}
		return null;
	}

	public void setGraphProviderFactory(
			ZestGraphProviderFactory graphProviderFactory) {
		this.graphProviderFactory = graphProviderFactory;
	}

	public ZestGraphProviderFactory getGraphProviderFactory() {
		if (this.graphProviderFactory == null) {
			this.graphProviderFactory = new ModelBasedGraphProviderFactory();
		}
		return graphProviderFactory;
	}

	static void asyncSetResource(final Resource architectureDslResource)
			throws CoreException {
		IWorkspaceRunnable workspaceRunnable = new UpdateGraphRunnable(
				architectureDslResource);
		try {
			EclipseWorkspaceUtils.runUnlessLocked(workspaceRunnable);
		} catch (IllegalArgumentException e) {
			// IGNORE THE EXCEPTION
			System.err.println("THROW " + e.getMessage());

			// TODO: This exception is thrown when more than one XtextEditor is
			// open and
			// the files are modified. The reason for this is unknown and it
			// seems to have
			// no consequences
		}
	}

	public static void setCurrentEditorResource() {
		IEditorPart editorPage = ZestGraphViewCommandHandler
				.getActiveWorkbenchEditorOrNull();
		if (editorPage != null
				&& editorPage.getEditorInput() instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) editorPage.getEditorInput())
					.getFile();
			if (file.getLocation().toString()
					.endsWith(ArchitectureDslFileUtils.EXTENSION)) {

				XtextEditor editor = (XtextEditor) editorPage;

				// IXtextDocument document = editor.getDocument();
				// if (!documentsWithModelListener.contains(document)) {
				// document.addModelListener(new ZestGraphXtextModelListener(
				// document));
				// documentsWithModelListener.add(document);
				// }

				editor.getDocument().readOnly(
						new IUnitOfWork.Void<XtextResource>() {
							public void process(XtextResource resource)
									throws CoreException {
								currentResource = resource;
								ZestGraphView.asyncSetResource(resource);
							}
						});
			}

		} else
			try {
				ZestGraphView.asyncSetResource(currentResource);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
}
