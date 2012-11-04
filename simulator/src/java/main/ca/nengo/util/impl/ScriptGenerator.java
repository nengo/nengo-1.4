package ca.nengo.util.impl;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import ca.nengo.model.Node;
import ca.nengo.util.ScriptGenException;


public class ScriptGenerator extends DFSIterator{

	HashMap<Node, String> prefixes;
	
	PrintWriter writer;
	
	public ScriptGenerator(PrintWriter writer) throws FileNotFoundException
	{
		this.writer = writer;
		writer.write("import nef");
		writer.write("from ca.nengo.math.impl import ConstantFunction, FourierFunction, PostfixFunction");
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
		toScriptArgs.put("netName", prefixes.get(node));
		toScriptArgs.put("delimiter", '%');
		
		try{
		
			String code = node.toScript(toScriptArgs);
			this.writer.write(code);
		}catch(ScriptGenException e){
			System.out.println(e.getMessage());
		} 
	}
	
	public void post(Node node)
	{
	}
}
