package ca.neo.ui.views;

import ca.neo.ui.views.symbols.EnsembleSym;
import ca.neo.ui.views.symbols.NetworkSym;
import ca.neo.ui.views.symbols.SymbolDragHandler;
import ca.sw.graphics.nodes.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class NeoCanvas extends WorldObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2580524986395972714L;

	public NeoCanvas() {
		super();

		// setBounds(0,0,200,20000);

		this.addItem(new NetworkSym());
		this.addItem(new EnsembleSym());
		this.addItem(PPath.createRectangle(0, 0, 50, 50));

		this.setFrameVisible(true);
		// addItem();

	}

	public void addItem(PNode item) {

		this.addToLayout(item);

	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();

		addInputEventListener(new SymbolDragHandler(this));
	}

}
