package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

public class ProjectExplorerListener implements IOpenListener
{
	public ProjectExplorerListener()
	{
		
	}
	
	@Override
	public void open(OpenEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		
		Object obj = selection.getFirstElement();
		
		if( obj instanceof ExplorerTreeSpecialFileNode )
		{
			ExplorerTreeSpecialFileNode node = (ExplorerTreeSpecialFileNode)obj;
			obj = node.file;
		}
		
		if (obj instanceof File)
		{
			File file = (File) obj;
			
			if( file.isDirectory() )
				return;
			
			try
			{
			    IWorkbenchPage page = 
			    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    
			    IEditorDescriptor desc = PlatformUI.getWorkbench().
	        		getEditorRegistry().getDefaultEditor(file.getName());
				
				if( null == desc )
				{
					desc = PlatformUI.getWorkbench().
	        		getEditorRegistry().getDefaultEditor("foo.properties");
					
					IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
					page.openEditor(new FileStoreEditorInput(fileStore), desc.getId());
				}
				else
				{
					IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
				    IDE.openEditorOnFileStore( page, fileStore );
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}	
		}
	}
}
