package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExplorerTreeRoot
{
	public File root = null;
	
	public List<ExplorerTreeSpecialFileNode> specialFileNodes = new ArrayList<ExplorerTreeSpecialFileNode>();	
	
	public List<ExplorerTreeSpecialFileGroup> specialFileGroups = new ArrayList<ExplorerTreeSpecialFileGroup>();
	
	public ExplorerTreeRoot()
	{
	}	
	
	public ExplorerTreeRoot(File root)
	{
		this.root = root;
	}
	
	public ExplorerTreeSpecialFileGroup getGroup(String name)
	{
		for( ExplorerTreeSpecialFileGroup group : specialFileGroups )
		{
			if( group.name.equals(name) )
			{
				return group;
			}
		}
		
		return null;
	}
	
	private boolean valid = false;
	
	public void setValidState(boolean valid)
	{
		this.valid = valid;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	
}
