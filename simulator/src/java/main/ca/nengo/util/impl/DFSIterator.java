package ca.nengo.util.impl;

import ca.nengo.model.Node;


public class DFSIterator {
	public DFSIterator()
	{
	}
	
	public void pre(Node node)
	{
		
	}
	
	public DFSIterator DFS(Node node)
	{
		pre(node);
		Node[] children = node.getChildren();
		
		for(Node n : children)
		{
			DFS(n);
		}
		
		post(node);
		
		return this;
	}
	
	public void post(Node node)
	{
	}
}
