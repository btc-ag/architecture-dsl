package com.btc.arch.architecturedsl.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

import com.btc.arch.zest.ui.ZestGraphView;

public class ArchitectureManagementPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		layout.setEditorAreaVisible(true);
		layout.setFixed(false);	
		
		String folder = "BOTTOM_FOLDER"; 
		IFolderLayout folderLayout = layout.createFolder(folder, IPageLayout.BOTTOM, 0.6f, editorArea);
		folderLayout.addView(ZestGraphView.ID);
		folderLayout.addView(IPageLayout.ID_PROBLEM_VIEW);
		
		//layout.addStandaloneView(ArchDslProjectExplorerView.ID,  true, IPageLayout.LEFT, 0.25f, editorArea);
		layout.addStandaloneView(ProjectExplorer.VIEW_ID,  true, IPageLayout.LEFT, 0.25f, editorArea);
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, editorArea);
		//layout.addView(ZestGraphView.ID, IPageLayout.BOTTOM, 0.6f, editorArea);
		
		layout.addShowViewShortcut(ZestGraphView.ID);
		layout.addShowViewShortcut(ProjectExplorer.VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
	}
}
