package ca.neo.ui.views.factories;

import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.proxies.ProxyGeneric;
import ca.sw.graphics.nodes.WorldObject;

public class Symbol extends WorldObject  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Icon icon;
	
	ProxyGeneric representation;
	NodeType neoObject;
	
	public Symbol(Icon icon, NodeType neoObject) {
		super();
		this.icon = icon;
		this.neoObject = neoObject;

		addChild(icon);
		setBounds(getFullBounds());
		
		this.setFrameVisible(false);
	}

	
	protected ProxyGeneric createRepresentation() {
		return neoObject.createNode();
	}

}
