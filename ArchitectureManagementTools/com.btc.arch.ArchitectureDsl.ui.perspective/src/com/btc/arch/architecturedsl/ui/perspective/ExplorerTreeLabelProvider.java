package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ExplorerTreeLabelProvider extends LabelProvider
{
	//private static Logger log = LoggerFactory.getLogger(ExplorerTreeLabelProvider.class);

	private static final Image folderImage = PlatformUI.getWorkbench()
			.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

	private static final Image driveImage = PlatformUI.getWorkbench()
			.getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);

	private static final Image fileImage = PlatformUI.getWorkbench()
			.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof File)
		{

			File file = (File) element;

			if (file.isDirectory())
			{
				return file.getParent() != null ? folderImage : driveImage;
			}

			return fileImage;

		}
		
		if (element instanceof ExplorerTreeSpecialFileGroup)
		{
			return folderImage;
		}		
		
		if (element instanceof ExplorerTreeSpecialFileNode)
		{
			return fileImage;
		}		
		
		if( element instanceof ExplorerTreeRoot )
		{
			return folderImage;
		}

		return null;
	}

	@Override
	public String getText(Object element)
	{
		if( element instanceof File )
		{
			String fileName = ((File) element).getName();
			if (fileName.length() > 0)
			{
				return fileName;
			}
			
			return ((File) element).getPath();			
		}
		
		if( element instanceof ExplorerTreeSpecialFileGroup )
		{
			ExplorerTreeSpecialFileGroup obj = (ExplorerTreeSpecialFileGroup)element;
			return obj.name;
		}		
		
		if( element instanceof ExplorerTreeSpecialFileNode )
		{
			ExplorerTreeSpecialFileNode obj = (ExplorerTreeSpecialFileNode)element;
			return obj.file.getName();
		}		
		
		if( element instanceof ExplorerTreeRoot )
		{
			ExplorerTreeRoot obj = (ExplorerTreeRoot)element;
			
			try
			{
				String label = obj.root.getName();
				
				label += " [";
				label += obj.root.getCanonicalPath();
				label += "]";
				
				return label;
			}
			catch(Exception ex)
			{
				//log.error("ERROR!", ex);
				
				return obj.root.getName();				
			}
		}
		
		return "";
	}
}
