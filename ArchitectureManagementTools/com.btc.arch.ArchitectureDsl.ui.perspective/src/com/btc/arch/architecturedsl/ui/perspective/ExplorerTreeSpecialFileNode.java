package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;

public class ExplorerTreeSpecialFileNode
{
	public ExplorerTreeSpecialFileGroup group;
	
	public File file;
	
	public ExplorerTreeSpecialFileNode(ExplorerTreeSpecialFileGroup group, String pathToFile)
	{		
		this.group = group;
		this.file = new File(pathToFile);
	}
	
	public ExplorerTreeSpecialFileNode(ExplorerTreeSpecialFileGroup group, File file)
	{	
		this.group = group;
		this.file = file;
	}	
	
}
