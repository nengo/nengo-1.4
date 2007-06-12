package ca.neo.ui.views.symbol;

import ca.neo.ui.views.factories.GNodeCreator;
import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.ProxyObject;
import ca.sw.graphics.nodes.WorldObject;

public class Symbol extends WorldObject  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Icon icon;
	
	ProxyObject representation;
	GNodeCreator neoObject;
	
	public Symbol(Icon icon, GNodeCreator neoObject) {
		super();
		this.icon = icon;
		this.neoObject = neoObject;

		addChild(icon);
		setBounds(getFullBounds());
		
		this.setFrameVisible(false);
	}

	
	protected ProxyObject createRepresentation() {
		return neoObject.createNode();
	}

}
