package com.btc.commons.eclipse.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class EclipseWorkspaceUtils {

	public static void runUnlessLocked(IWorkspaceRunnable workspaceRunnable)
			throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (!workspace.isTreeLocked()) {
			workspace.run(workspaceRunnable, null);
		} else {
			// FIXME this is bad
			System.err.println("Workspace tree is locked.");
		}
	}

}
