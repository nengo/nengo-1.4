package ca.nengo.util.impl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.util.ScriptGenException;


public class ScriptGenerator extends DFSIterator{

	HashMap<Node, String> prefixes;
	
	PrintWriter writer;
	StringBuilder script;
	char spaceDelimiter = '_';
	String topLevelPrefix = "net";

    int inTemplateNetwork;
	
	public ScriptGenerator(File file) throws FileNotFoundException
	{
		prefixes = new HashMap<Node, String>();
		
		script = new StringBuilder(); 
		
		this.writer = new PrintWriter(file);
		
		writer.write("import nef\n");
		writer.write("from ca.nengo.math.impl import ConstantFunction, FourierFunction, PostfixFunction\n");
		writer.write("import math\n");
		writer.write("\n");
		
        inTemplateNetwork = 0;
	}
	
	protected void pre(Node node)
	{
		if (inTemplateNetwork <= 0)
        {
			if (topLevel)
			{
				prefixes.put(node, topLevelPrefix);
			}
			
			for (Node child : node.getChildren())
			{
				String prefix;
				String nameNoSpaces = node.getName().replace(' ', spaceDelimiter);
				
				if(topLevel)
					prefix = topLevelPrefix + spaceDelimiter + nameNoSpaces;
				else
					prefix = prefixes.get(node) + spaceDelimiter + nameNoSpaces ;
				
				prefixes.put(child, prefix);
			}
			
			HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
			toScriptArgs.put("prefix", prefixes.get(node) + spaceDelimiter);
			toScriptArgs.put("isSubnet", !topLevel);
			toScriptArgs.put("netName", prefixes.get(node));
			toScriptArgs.put("spaceDelim", spaceDelimiter);
	
	        
            try {
                String code = node.toScript(toScriptArgs);
                script.append(code);
            } catch(ScriptGenException e) {
                System.out.println(e.getMessage());
            } 
        }
        
        if (node instanceof Network && ((Network)node).getMetaData("type") != null)
        {
            inTemplateNetwork++;
        }
	}
	
	protected void post(Node node)
	{
        if (node instanceof Network)
        {
            Network net = (Network)node;
            
            if (net.getMetaData("type") != null)
            {
                inTemplateNetwork--;
            }
            
            HashMap<String, Object> toScriptArgs = new HashMap<String, Object>();
            toScriptArgs.put("prefix", prefixes.get(node) + spaceDelimiter);
            toScriptArgs.put("isSubnet", !topLevel);
            toScriptArgs.put("netName", prefixes.get(node));
            toScriptArgs.put("spaceDelim", spaceDelimiter);
            
            if (inTemplateNetwork <= 0)
            {
	            
	
	            try {
	                String code = net.toPostScript(toScriptArgs);
	                script.append(code);
	            } catch(ScriptGenException e) {
	                System.out.println(e.getMessage());
	            }
	            
	           
            }
            
            if (net.getMetaData("type") != null)
            {
                inTemplateNetwork++;
            }
            
            if (inTemplateNetwork <= 0)
            {
            	for(Projection proj : ((Network) node).getProjections())
        		{
            		try {
    	                String code = proj.toScript(toScriptArgs);
    	                script.append(code);
    	            } catch(ScriptGenException e) {
    	                System.out.println(e.getMessage());
    	            }
        		}
            }
            
            if (net.getMetaData("type") != null)
            {
                inTemplateNetwork--;
            }
            
            if(topLevel)
            {
            	String nameNoSpaces = topLevelPrefix + spaceDelimiter + node.getName().replace(' ', spaceDelimiter);
            	script.append( nameNoSpaces + ".add_to_nengo()\n");
            }
        }
	}
	
	protected void finish()
	{	
		writer.write(script.toString());
		writer.close();
	}
}
