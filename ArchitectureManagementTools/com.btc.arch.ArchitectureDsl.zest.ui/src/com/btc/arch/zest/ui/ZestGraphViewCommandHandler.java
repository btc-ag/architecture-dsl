package com.btc.arch.zest.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.IEvaluationReference;
import org.eclipse.ui.services.IEvaluationService;

import com.btc.arch.architectureDsl.util.ArchDslException;
import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.commons.eclipse.core.EclipseFileUtils;

public class ZestGraphViewCommandHandler extends AbstractHandler {
	public static IEditorPart getActiveWorkbenchEditorOrNull() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			final IWorkbenchWindow activeWorkbenchWindow = workbench
					.getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null) {
				final IWorkbenchPage activePage = activeWorkbenchWindow
						.getActivePage();
				if (activePage != null)
					return (IEditorPart) activePage.getActiveEditor();
			}
		}
		return null;
	}

	private final class GraphUpdateListener implements IResourceChangeListener,
			IPropertyChangeListener {
		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			if (event.getType() != IResourceChangeEvent.POST_BUILD) {
				return;
			}
			IResourceDelta rootDelta = event.getDelta();
			try {
				rootDelta.accept(resourceVisitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			ZestGraphView.setCurrentEditorResource();
		}
	}

	private boolean listenToArchitectureDslContent = false;

	/** Listener that passes a visitor if a resource is changed. */
	private GraphUpdateListener resourceChangeListener = new GraphUpdateListener();

	private IEvaluationReference ref;

	/**
	 * If a *.archdsl file or a file with architecture dsl content is visited,
	 * we update the graph from it.
	 */
	private IResourceDeltaVisitor resourceVisitor = new IResourceDeltaVisitor() {
		public boolean visit(final IResourceDelta delta) {
			ZestGraphView.setCurrentEditorResource();
			// IResource resource = delta.getResource();
			// if (resource.getType() == IResource.FILE) {
			// try {

			// final IFile f = (IFile) resource;
			// if (!listenToArchitectureDslContent
			// && !f.getLocation()
			// .toString()
			// .endsWith(
			// ArchitectureDslFileUtils.EXTENSION)) {
			// return true;
			// }

			// Resource res = ArchitectureDslFileUtils
			// .loadResource(EclipseFileUtils
			// .convertIFileToJavaIOFile(f), false);
			// ZestGraphView.asyncSetResource(res);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			return true;
		}
	};

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO this class should be split up if possible.
		// the handler should not normally rely on the id of the command that
		// triggered it

		if (event.getCommand().getId()
				.equals("com.btc.arch.ArchitectureDsl.zest.ui.update-mode")) {
			executeUpdateModeCommand(event);
		} else if (event.getCommand().getId()
				.equals("com.btc.arch.ArchitectureDsl.zest.ui.layout")) {
			executeLayoutCommand(event);
		} else if (event.getCommand().getId()
				.equals("com.btc.arch.ArchitectureDsl.zest.ui.openFile")) {
			executeOpenFileCommand(event);
		}
		return null;
	}

	private void executeUpdateModeCommand(ExecutionEvent event) {
		Event trigger = (Event) event.getTrigger();
		// This does only work if the event is triggered by the toolbar
		// item. If other triggers are added, this has to be generalised.
		ToolItem item = (ToolItem) trigger.widget;
		listenToArchitectureDslContent = item.getSelection();

		toggleResourceListener();
	}

	private void toggleResourceListener() {
		// TODO this method does not actually "toggle" anything, but adds or
		// remove the listeners, depending on the current value of
		// listenToArchitectureDslContent
		IEvaluationService service = (IEvaluationService) PlatformUI
				.getWorkbench().getService(IEvaluationService.class);

		if (listenToArchitectureDslContent) {
			Expression exp = new Expression() {
				boolean rc = false;

				@Override
				public void collectExpressionInfo(ExpressionInfo info) {
					info.addVariableNameAccess(ISources.ACTIVE_EDITOR_NAME);
				}

				@Override
				public EvaluationResult evaluate(IEvaluationContext context)
						throws CoreException {
					rc = !rc;
					return EvaluationResult.valueOf(rc);
				}
			};
			ref = service.addEvaluationListener(exp, resourceChangeListener,
					"Editor");
			ResourcesPlugin.getWorkspace().addResourceChangeListener(
					resourceChangeListener, IResourceChangeEvent.POST_BUILD);
		} else {
			service.removeEvaluationListener(ref);
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					resourceChangeListener);
		}
	}

	private void executeLayoutCommand(ExecutionEvent event) {
		((ZestGraphView) HandlerUtil.getActivePart(event)).applyGraphLayout();
	}

	private void executeOpenFileCommand(ExecutionEvent event)
			throws ExecutionException {
		// ZestGraphView zestGraphView = (ZestGraphView) HandlerUtil
		// .getActivePart(event);
		Shell shell = HandlerUtil.getActiveWorkbenchWindowChecked(event)
				.getShell();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
				shell, root, IResource.FILE);
		if (dialog.open() == ResourceListSelectionDialog.OK) {
			Object[] selected = dialog.getResult();
			if (selected != null) {
				final IFile currentArchitectureDslModelFile = (IFile) selected[0];
				try {
					Resource res = null;
					try {
						res = ArchitectureDslFileUtils
								.loadResource(
										EclipseFileUtils
												.toJavaFile(currentArchitectureDslModelFile),
										false);
					} catch (ArchDslException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ZestGraphView.asyncSetResource(res);

				} catch (CoreException e) {
					throw new ExecutionException("Error accessing file "
							+ currentArchitectureDslModelFile, e);
				}
			}
		}
	}
}
