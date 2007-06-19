package ca.neo.ui.views;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ca.neo.ui.views.factories.Symbol;
import ca.sw.graphics.basics.GButton;
import ca.sw.graphics.objects.controls.Tooltip;
import ca.sw.graphics.world.WorldObjectImpl;
import ca.sw.util.Util;

public class NeoToolbox extends WorldObjectImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2580524986395972714L;

	SymbolHolder symbolHolder;

	public NeoToolbox() {
		super("Toolbox");

		System.out.println("loading SymbolHolder");
		symbolHolder = (SymbolHolder) loadStatic("defaultSymbols");
		System.out.println("finished loading SymbolHolder");
		
		

		if (symbolHolder == null) {
			initSymbolHolder();
			this.setVisible(false);
		} else {
//			symbolHolder.translate(5, 30);
			addChild(symbolHolder);
			
			pack();
		}
//		symbolHolder.setPickable(false);

		this.setFrameVisible(true);
	}

	public void addItem(Symbol item) {
		this.setVisible(true);

		symbolHolder.addSymbol(item);
		
		pack();

		saveStatic(symbolHolder, "defaultSymbols");		
	}

	public void initSymbolHolder() {
		if (symbolHolder != null)
			symbolHolder.removeFromParent();

		symbolHolder = new SymbolHolder();

		
		addChild(symbolHolder);
		pack();
	}

	@Override
	public WorldObjectImpl getTooltipObject() {
		// TODO Auto-generated method stub
		return new ToolBoxControls(this);

	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();

//		addInputEventListener(new SymbolClonerHandler());
	}

	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();
		
		symbolHolder.setOffset(5,30);
		
	}
}


class SymbolHolder extends WorldObjectImpl {
	private static final long serialVersionUID = -776911995031604664L;

//	Vector<Symbol> symbols = new Vector<Symbol>();

	public SymbolHolder() {
		super();
		getLayoutManager().setLeftPadding(0);
		getLayoutManager().setVerticalPadding(0);
		
	}

	public void addSymbol(Symbol symbol) {
		Iterator<Symbol> it = getSymbols().iterator();
		while (it.hasNext()) {
			Symbol sym = it.next();

			// don't allow duplicate instances
			if (sym.getName().compareTo(symbol.getName()) == 0) {
				Util.Warning("There is already a " + symbol.getName()
						+ " symbol in the toolbox");
				return;
			}
		}
		
		addToLayout(symbol);

	}

	public List<Symbol> getSymbols() {
		Vector<Symbol> symbols = new Vector<Symbol>();
		
		Iterator it = getChildrenIterator();
		
		while (it.hasNext()) {
			Object obj = it.next();
			
			if (obj instanceof Symbol) {
				symbols.add((Symbol)(obj));
			}
			
		}
		
		return symbols;
	}
}

class ToolBoxControls extends Tooltip {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NeoToolbox canvas;

	ToolBoxControls(NeoToolbox canvas) {
		super();
		this.canvas = canvas;
	}

	@Override
	protected void initButtons() {
		addButton(new GButton("Clear Toolbox", new Runnable() {
			public void run() {
				canvas.initSymbolHolder();
			}
		}));

	}

}
