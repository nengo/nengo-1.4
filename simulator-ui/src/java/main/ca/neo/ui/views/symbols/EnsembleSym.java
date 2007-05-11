package ca.neo.ui.views.symbols;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class EnsembleSym extends Symbol {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EnsembleSym() {
		super();
		addChild((PPath.createRectangle(0, 0, 50, 50)));
		setBounds(getFullBounds());
	}
	public PNode createNode() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
