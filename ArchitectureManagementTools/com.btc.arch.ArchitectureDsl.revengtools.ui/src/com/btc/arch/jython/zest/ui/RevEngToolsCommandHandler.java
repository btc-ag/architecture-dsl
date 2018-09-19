package com.btc.arch.jython.zest.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import com.btc.arch.jython.zest.PythonZestGraphProviderFactory;
import com.btc.arch.zest.ui.ArchitectureDslZestUiActivator;
import com.btc.arch.zest.ui.ZestGraphView;
import com.btc.commons.eclipse.core.EclipseWorkspaceUtils;

public class RevEngToolsCommandHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO this class should be split up if possible.
		// the handler should not normally rely on the id of the command that
		// triggered it

		if (event
				.getCommand()
				.getId()
				.equals("com.btc.arch.ArchitectureDsl.jython.zest.ui.showLinkDependencies")) {
			executeShowLinkDependenciesCommand(event);
		} else if (event
				.getCommand()
				.getId()
				.equals("com.btc.arch.ArchitectureDsl.jython.zest.ui.activaterevengtools")) {
			executeActivateRevEngToolsCommand(event);
		}
		return null;
	}

	private void executeShowLinkDependenciesCommand(ExecutionEvent event) {
		IWorkspaceRunnable workspaceRunnable = new ShowLinkDependenciesRunnable(
				event);
		try {
			EclipseWorkspaceUtils.runUnlessLocked(workspaceRunnable);
		} catch (CoreException e) {
			ArchitectureDslZestUiActivator.getLogger().log(
					new Status(Status.WARNING,
							ArchitectureDslZestUiActivator.PLUGIN_ID,
							"Exception during graph update", e));
		}
	}

	private void executeActivateRevEngToolsCommand(ExecutionEvent event) {
		ZestGraphView zestGraphView = ZestGraphView.getZestGraphView();
		// This does only work if the event is triggered by the toolbar
		// item. If other triggers are added, this has to be generalised.
		Event trigger = (Event) event.getTrigger();
		ToolItem item = (ToolItem) trigger.widget;
		if (item.getSelection()) {
			zestGraphView
					.setGraphProviderFactory(new PythonZestGraphProviderFactory());
		} else {
			zestGraphView.setGraphProviderFactory(null);
		}
		zestGraphView.setCurrentEditorResource();
	}

}
