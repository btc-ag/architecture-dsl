package com.btc.arch.jython.zest.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;

import com.btc.arch.jython.LinkDepsGraphProvider;
import com.btc.arch.jython.RevEngToolsInterpreter;
import com.btc.arch.jython.zest.PythonZestGraphProvider;
import com.btc.arch.jython.zest.RevEngToolsZestGraphInfo;
import com.btc.arch.zest.ui.ZestGraphView;

public class ShowLinkDependenciesRunnable implements IWorkspaceRunnable {
	private ZestGraphView zestGraphView;

	public ShowLinkDependenciesRunnable(ExecutionEvent event) {
		this.zestGraphView = (ZestGraphView) HandlerUtil.getActivePart(event);
	}

	public void run(final IProgressMonitor monitor) throws CoreException {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
		// TODO: There should be no dependency on the RevEngTools in
		// this base
		// implementation of the ZestGraphView.
		// Rather, an extension point should be defined, to which
		// another plugin
		// contributes this implementation! BTCEPMARCH-72
		this.zestGraphView.setGraph(new PythonZestGraphProvider(
				this.zestGraphView.getGraphParent(), SWT.NONE,
				new LinkDepsGraphProvider(interpreter), interpreter),
				new RevEngToolsZestGraphInfo(
						RevEngToolsZestGraphInfo.FULL_LINK_DEPS_GRAPH), true);
	}
}
