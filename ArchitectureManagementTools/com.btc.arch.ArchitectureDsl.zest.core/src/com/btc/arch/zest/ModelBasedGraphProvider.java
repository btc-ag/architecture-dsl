package com.btc.arch.zest;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;

import com.btc.arch.architectureDsl.util.ArchDslException;

public class ModelBasedGraphProvider implements ZestGraphProvider {
	private final Resource architectureDslResource;
	private final Composite parent;

	public ModelBasedGraphProvider(Resource currentArchitectureDslResource,
			Composite parent) {
		this.architectureDslResource = currentArchitectureDslResource;
		this.parent = parent;
	}

	public Graph getGraph() {
		if (parent != null) {
			ArchitectureDslImport architectureDslImport = null;
			try {
				architectureDslImport = new ArchitectureDslImport(
						architectureDslResource, parent, SWT.NONE);
			} catch (ArchDslException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (architectureDslImport.getErrors().size() > 0) {
				String message = String
						.format("Could not import architecture DSL model: %s, ARCHDSL: %s", //$NON-NLS-1$
						architectureDslImport.getErrors(),
								architectureDslResource);
				Platform.getLog(
						Platform.getBundle(ArchitectureDslZestMessages.BUNDLE_NAME))
						.log(new Status(Status.ERROR,
								ArchitectureDslZestMessages.BUNDLE_NAME,
								message));
				return null;
			}
			return architectureDslImport.getGraph();
		}
		return null;
	}
}