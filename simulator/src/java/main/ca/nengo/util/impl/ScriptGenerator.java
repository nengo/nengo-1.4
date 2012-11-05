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
	StringBuilder script;

    int inTemplateNetwork;
    boolean isTopLevel;
	
	public ScriptGenerator(PrintWriter writer) throws FileNotFoundException
	{
		prefixes = new HashMap<Node, String>();
		
		script = new StringBuilder(); 
		
		this.writer = writer;
		writer.write("import nef\n");
		writer.write("from ca.nengo.math.impl import ConstantFunction, FourierFunction, PostfixFunction\n");
		writer.write("import math\n");
		writer.write("\n");
		
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
			String prefix;
			if(isTopLevel)
				prefix = node.getName();
			else
				prefix = prefixes.get(node) + "_" + node.getName() ;
			
			prefixes.put(child, prefix);
		}
		
		HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
		toScriptArgs.put("prefix", prefixes.get(node) + (isTopLevel ? "" : "_"));
		toScriptArgs.put("isSubnet", !isTopLevel);
		toScriptArgs.put("netName", prefixes.get(node));
		toScriptArgs.put("spaceDelim", '_');
		
        if (node instanceof Network && ((Network)node).getMetaData("type") != null)
        {
            inTemplateNetwork++;
        }

        if (inTemplateNetwork <= 0)
        {
            try {
                String code = node.toScript(toScriptArgs);
                //this.writer.write(code);
                script.append(code);
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
                script.append(code);
            } catch(ScriptGenException e) {
                System.out.println(e.getMessage());
            }
        }
	}
	
	public void finish()
	{
		writer.write(script.toString());
	}
}
