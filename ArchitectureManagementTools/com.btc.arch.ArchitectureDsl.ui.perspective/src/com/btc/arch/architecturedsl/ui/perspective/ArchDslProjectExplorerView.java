package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ArchDslProjectExplorerView extends ViewPart 
{
	public static final String ID = "com.btc.arch.architecturedsl.ui.perspective.ArchDslExplorerView";
	
	//private static final Logger log = LoggerFactory.getLogger(ProjectExplorerView.class);
	
	private TreeViewer viewer;
	
	private String homeFolder;
	
	public ArchDslProjectExplorerView()
	{
		super();
		DirectoryDialog dialog = new DirectoryDialog(new Shell());
		dialog.setMessage("Choose source folder");
		this.homeFolder = dialog.open();
	}
	
	private void setFolderSelection()
	{
		String folderString = homeFolder;
		setFolderSelection(folderString);
	}
	
	private void setFolderSelection(String path)
	{
		File folder = new File(path);
		viewer.setSelection(new StructuredSelection(folder), true);		
	}

	public void createPartControl(Composite parent) 
	{	
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		getSite().setSelectionProvider(viewer);		
		
		viewer.setContentProvider(new ArchDslExplorerTreeContentProvider());
		viewer.setLabelProvider(new ExplorerTreeLabelProvider());
		viewer.setInput( new File(homeFolder) );
		
		//viewer.addSelectionChangedListener(this);
		
		viewer.addOpenListener( new ProjectExplorerListener() );
		
		//viewer.setComparator(new ExplorerTreeComparator());
		
		this.setFolderSelection();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}


