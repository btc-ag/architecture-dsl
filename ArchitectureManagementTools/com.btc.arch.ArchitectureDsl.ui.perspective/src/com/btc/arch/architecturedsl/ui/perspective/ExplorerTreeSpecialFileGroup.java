package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExplorerTreeSpecialFileGroup
{
	public String name = "";
	
	public List<ExplorerTreeSpecialFileNode> specialFileNodes = new ArrayList<ExplorerTreeSpecialFileNode>();
	
	public ExplorerTreeSpecialFileGroup(String name)
	{		
		this.name = name;
	}
	
	public ExplorerTreeSpecialFileNode getNode(File f)
	{
		if( null == f )
		{
			return null;
		}
		
		for( ExplorerTreeSpecialFileNode node : specialFileNodes )
		{
			if( node.file.getName().equals(f.getName()) )
			{
				return node;
			}
		}
		
		return null;
	}	
}
