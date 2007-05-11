package ca.neo.ui.views.symbols;

import edu.umd.cs.piccolo.PNode;

/*
 * Graphic symbols used to represent objects
 * 
 */
public interface ISymbol  {

	public PNode createNode();
	
	public Object clone();
	
}
