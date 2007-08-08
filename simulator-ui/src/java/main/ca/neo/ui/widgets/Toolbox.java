package ca.neo.ui.widgets;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ca.neo.ui.models.PModel;
import ca.shu.ui.lib.objects.GButton;
import ca.shu.ui.lib.objects.Tooltip;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;

public class Toolbox extends WorldObjectImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2580524986395972714L;

	SymbolHolder symbolHolder;

	public Toolbox() {
		super("Toolbox");

		System.out.println("loading SymbolHolder");
//		symbolHolder = (SymbolHolder) Util.loadProperty(this, "defaultSymbols");
		System.out.println("finished loading SymbolHolder");

		if (symbolHolder == null) {
			initSymbolHolder();
			this.setVisible(false);
		} else {
			// symbolHolder.translate(5, 30);
			addChild(symbolHolder);

			pack();
		}
		// symbolHolder.setPickable(false);

		this.setFrameVisible(true);
	}

	public void addItem(PModel item) {
		this.setVisible(true);

		symbolHolder.addSymbol(item);

		pack();

//		Util.saveProperty(this, symbolHolder, "defaultSymbols");
	}

	public void initSymbolHolder() {
		if (symbolHolder != null)
			symbolHolder.removeFromParent();

		symbolHolder = new SymbolHolder();

		addChild(symbolHolder);
		pack();
	}
	public void pack() {
		boolean frameVisible = isFrameVisible();

		// hide the frame first, so it does not contribute to the bounds
		if (frameVisible) {
			setFrameVisible(false);
		}

		// if (border != null) {
		// border.removeFromParent();
		// }

		Rectangle2D newBounds = parentToLocal(getFullBounds());

		// if (border != null) {
		// border.setVisible(true);
		// }

		if (frameVisible) {
			setFrameVisible(true);
		}

		this.setBounds(newBounds);

	}
	@Override
	public WorldObjectImpl getTooltipObject() {
		// TODO Auto-generated method stub
		return new ToolBoxControls(this);

	}

	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();

		symbolHolder.setOffset(5, 30);

	}
}

class SymbolHolder extends WorldObjectImpl {
	private static final long serialVersionUID = -776911995031604664L;

	// Vector<Symbol> symbols = new Vector<Symbol>();

	public SymbolHolder() {
		super();
		getLayoutManager().setLeftPadding(0);
		getLayoutManager().setVerticalPadding(0);

	}

	public void addSymbol(PModel symbol) {
		Iterator<PModel> it = getSymbols().iterator();
		while (it.hasNext()) {
			PModel sym = it.next();

			// don't allow duplicate instances
			if (sym.getName().compareTo(symbol.getName()) == 0) {
				Util.Warning("There is already a " + symbol.getName()
						+ " symbol in the toolbox");
				return;
			}
		}

		addToLayout(symbol);

		symbol.addPropertyChangeListener(PNode.PROPERTY_BOUNDS,
				new SymbolCloner(symbol));

	}

	@SuppressWarnings("unchecked")
	public List<PModel> getSymbols() {
		Vector<PModel> symbols = new Vector<PModel>();

		Iterator it = getChildrenIterator();

		while (it.hasNext()) {
			Object obj = it.next();

			if (obj instanceof PModel) {
				symbols.add((PModel) (obj));
			}

		}

		return symbols;
	}
}

class SymbolCloner implements PropertyChangeListener {
	WorldObjectImpl symbol;

	public SymbolCloner(WorldObjectImpl symbol) {
		super();
		this.symbol = symbol;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		WorldObjectImpl clonedSymbol = (WorldObjectImpl) (symbol.clone());
		clonedSymbol.getParent().addChild(0, clonedSymbol);

	}
}

class ToolBoxControls extends Tooltip {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Toolbox canvas;

	ToolBoxControls(Toolbox canvas) {
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
