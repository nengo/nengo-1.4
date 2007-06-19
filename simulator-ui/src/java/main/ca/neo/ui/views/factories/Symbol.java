package ca.neo.ui.views.factories;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.proxies.ProxyGeneric;
import ca.sw.graphics.world.IDragAndDroppable;
import ca.sw.graphics.world.IWorldObject;
import ca.sw.graphics.world.WorldObjectImpl;
import ca.sw.handlers.IContextMenuCreator;

public class Symbol extends WorldObjectImpl implements IDragAndDroppable,
		IContextMenuCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4632212262932271038L;

	Icon icon;

	ProxyGeneric representation;

	NodeType neoObject;

	public Symbol(Icon icon, NodeType neoObject) {
		super();
		this.icon = icon;
		this.neoObject = neoObject;

		setChildrenPickable(false);
		addChild(icon);
		setBounds(getFullBounds());

		// addInputEventListener(new ContextMenuHandler(this, this));

		this.setFrameVisible(false);
	}

	public void justDropped() {

		/*
		 * Creates a representation to replace the symbol
		 */
		ProxyGeneric repNode = createRepresentation();

		getParent().addChild(repNode);

		repNode.setOffset(getOffset());
		removeFromParent();

		repNode.initProxy();

	}

	protected ProxyGeneric createRepresentation() {
		return neoObject.createNode();
	}

	public JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu(getName());
		JMenuItem item;

		item = new JMenuItem(new AbstractAction("Remove symbol") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				removeFromParent();
			}
		});
		menu.add(item);

		return menu;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return icon.getName();
	}

	@Override
	public void startDrag() {
		// TODO Auto-generated method stub
		super.startDrag();

		WorldObjectImpl clonedSymbol = (WorldObjectImpl) (clone());
		getParent().addChild(0,clonedSymbol);
		
	}

}
