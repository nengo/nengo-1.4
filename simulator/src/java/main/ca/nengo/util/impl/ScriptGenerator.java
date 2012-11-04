package ca.nengo.util.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import ca.nengo.model.Network;
import ca.nengo.model.Node;



public class ScriptGenerator extends DFSIterator{

	HashMap<Node, String> prefixes;
	
	PrintWriter writer;
	
	public ScriptGenerator(PrintWriter writer) throws FileNotFoundException
	{
		this.writer = writer;
	}
	
	public void pre(Node node)
	{
		boolean isTopLevel = !prefixes.containsKey(node);
		
		if (isTopLevel)
		{
			prefixes.put(node, node.getName());
		}
		
		for (Node child : node.getChildren())
		{
			String prefix = prefixes.get(node) + "." + child.getName();
			prefixes.put(child, prefix);
		}
		
		HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
		toScriptArgs.put("prefix", prefixes.get(node));
		toScriptArgs.put("isSubnet", isTopLevel);
		toScriptArgs.put("netName", node.getName());
		toScriptArgs.put("delimiter", '%');
		
		String code = node.toScript(toScriptArgs);
		this.writer.write(code);
	}
	
	public void post(Node node)
	{
	}
}
