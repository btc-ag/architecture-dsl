package com.btc.arch.zest.ui;

import java.io.IOException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;

import com.btc.arch.zest.ResourceZestGraphInfo;
import com.btc.arch.zest.ZestGraphProvider;

final class UpdateGraphRunnable implements IWorkspaceRunnable {
	private final Resource architectureDslResource;

	UpdateGraphRunnable(Resource architectureDslResource) {
		this.architectureDslResource = architectureDslResource;
	}

	public void run(final IProgressMonitor monitor) throws CoreException {
		ZestGraphView zestGraphView = ZestGraphView.getZestGraphView();
		try {
			ZestGraphProvider provider = null;

			provider = zestGraphView.getGraphProviderFactory()
					.createGraphProvider(architectureDslResource,
							zestGraphView.getGraphParent());
			ResourceZestGraphInfo graphInfo = new ResourceZestGraphInfo(
					architectureDslResource, provider);
			zestGraphView.setGraph(provider, graphInfo, true);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR,
					ArchitectureDslZestUiActivator.PLUGIN_ID,
					"Invalid model file", e));
		}
	}
}