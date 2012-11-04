package ca.nengo.util.impl;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.util.ScriptGenException;


public class ScriptGenerator extends DFSIterator{

	HashMap<Node, String> prefixes;
	
	PrintWriter writer;

    int inTemplateNetwork;
    boolean isTopLevel;
	
	public ScriptGenerator(PrintWriter writer) throws FileNotFoundException
	{
		this.writer = writer;
		writer.write("import nef");
		writer.write("from ca.nengo.math.impl import ConstantFunction, FourierFunction, PostfixFunction");
		writer.write("import math");
		
        inTemplateNetwork = 0;
	}
	
	public void pre(Node node)
	{
		boolean isTopLevel = !prefixes.containsKey(node);

		if (isTopLevel)
		{
			prefixes.put(node, "");
		}
		
		for (Node child : node.getChildren())
		{
			String prefix = prefixes.get(node) + "." + node.getName();
			prefixes.put(child, prefix);
		}
		
		HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
		toScriptArgs.put("prefix", prefixes.get(node));
		toScriptArgs.put("isSubnet", isTopLevel);
		toScriptArgs.put("netName", prefixes.get(node));
		toScriptArgs.put("delimiter", '%');
		
        if (node instanceof Network && ((Network)node).getMetaData("type") != null)
        {
            inTemplateNetwork++;
        }

        if (inTemplateNetwork <= 0)
        {
            try {
                String code = node.toScript(toScriptArgs);
                this.writer.write(code);
            } catch(ScriptGenException e) {
                System.out.println(e.getMessage());
            } 
        }
	}
	
	public void post(Node node)
	{
        if (node instanceof Network)
        {
            Network net = (Network)node;

            if (net.getMetaData("type") != null)
            {
                inTemplateNetwork--;
            }
		
            HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
            toScriptArgs.put("prefix", prefixes.get(node));
            toScriptArgs.put("isSubnet", isTopLevel);
            toScriptArgs.put("netName", prefixes.get(node));
            toScriptArgs.put("delimiter", '%');

            try {
                String code = net.toPostScript(toScriptArgs);
                this.writer.write(code);
            } catch(ScriptGenException e) {
                System.out.println(e.getMessage());
            }
        }
        
        
	}
}
