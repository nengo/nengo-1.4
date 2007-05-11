package ca.neo.ui.views.symbols;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class NetworkSym extends Symbol {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NetworkSym() {
		super();
		addChild((PPath.createEllipse(0, 0, 50, 50)));
		setBounds(getFullBounds());
	}

	public PNode createNode() {
		return new NetworkSym();
	}

	
}
