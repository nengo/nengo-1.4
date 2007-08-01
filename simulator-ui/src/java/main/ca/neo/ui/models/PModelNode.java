package ca.neo.ui.models;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.widgets.Probe;
import ca.neo.ui.models.wrappers.PDecodedTermination;
import ca.neo.ui.models.wrappers.POrigin;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public abstract class PModelNode extends PModelConfigurable {

	/*
	 * Widgets are attached objects which are not children.
	 */
	Vector<WorldObjectImpl> widgets;

	public PModelNode() {
		super();
		init();
	}

	public PModelNode(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		init();
	}

	public PModelNode(IConfigurationManager configManager) {
		super(configManager);
		init();
	}

	@Override
	public void addedToWorld() {
		// TODO Auto-generated method stub
		super.addedToWorld();
		updateWidgets();
	}

	public void addWidget(WorldObjectImpl widget) {
		if (widgets == null) {
			widgets = new Vector<WorldObjectImpl>(3);
		}
		widgets.add(widget);

		updateWidgets();
	}

	/**
	 * @return The Widget found matching parameters, null if not found
	 * @param name
	 *            of the widget
	 * @param type
	 *            of the widget
	 */
	@SuppressWarnings("unchecked")
	public WorldObjectImpl getWidget(String name, Class type) {

		if (widgets == null)
			return null;
		/*
		 * Linear search used because there tends to be only a small number of
		 * widets
		 */
		Iterator<WorldObjectImpl> it = widgets.iterator();

		while (it.hasNext()) {
			WorldObjectImpl wo = it.next();

			if (type != null && type.isInstance(wo)) {
				return wo;
			}

		}
		return null;
	}

	class AddScopeAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1;

		public AddScopeAction() {
			super("Add probe");
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			addProbe();
		}

	}

	public void addProbe() {
		addChild(new Probe(this));

	}

	@Override
	public PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		// menu.addSubMenu(label)
		menu.addSection("Node");

		menu.addAction(new AddScopeAction());

		Origin[] origins = getNode().getOrigins();
		if (origins.length > 0) {

			MenuBuilder originsMenu = menu.createSubMenu("Show origins");

			originsMenu.addAction(new ShowAllOriginAction());
			for (int i = 0; i < origins.length; i++) {
				originsMenu
						.addAction(new ShowOriginAction(origins[i].getName()));
			}

		}

		return menu;
	}

	/**
	 * @return Network model the node is attached to
	 */
	public Network getNetworkModel() {
		NetworkViewer netV = getNetworkViewer();
		if (netV != null) {
			return netV.getModel();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return NetworkViewer the node is attached to
	 */
	public NetworkViewer getNetworkViewer() {

		World network = getWorld();
		if (network != null && network instanceof NetworkViewer) {
			return (NetworkViewer) network;
		} else {
			Util.Error("Node is not attached to a network");
			return null;
		}
	}

	public Node getNode() {
		return (Node) getModel();
	}

	@Override
	public PropertyStructure[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	// Vector<POrigin> origins;

	@Override
	public void moveToFront() {
		// TODO Auto-generated method stub
		super.moveToFront();
		updateWidgets();
	}

	public void removeWidget(WorldObjectImpl widget) {
		widgets.remove(widget);
		widget.removeFromParent();

		updateWidgets();
	}

	/**
	 * Shows all the origins on the Node model
	 */
	public void showAllOrigins() {

		Origin[] origins = getNode().getOrigins();

		for (int i = 0; i < origins.length; i++) {
			getOrigin(origins[i].getName());
		}

	}

	/**
	 * @param name
	 *            Name of an Origin on the Node model
	 * @return the POrigin shown
	 */
	public POrigin getOrigin(String originName) {
		/*
		 * Try to find if the origin has already been created
		 */
		POrigin uiOrigin = (POrigin) getWidget(originName, POrigin.class);
		if (uiOrigin != null) {
			return uiOrigin;
		}

		/*
		 * Otherwise try to create it
		 */

		try {
			uiOrigin = new POrigin(this, getNode().getOrigin(originName));
			uiOrigin.setScale(0.5);

			addWidget(uiOrigin);

			return uiOrigin;
		} catch (StructuralException e) {
			Util.Error(e.toString());
		}
		return null;
	}

	private void init() {

	}

	@Override
	protected Object createModel() {
		// TODO Auto-generated method stub
		return null;
	}

	// Vector<POrigin> origins;
	// public void showOrigin(Origin origin) {
	// POrigin originObject = new POrigin(origin);
	// addChild(originObject);
	//		
	// if (origins == null) {
	// origins = new Vector<POrigin>();
	// }
	// origins.add(e)
	//		
	// }

	@SuppressWarnings("unchecked")
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();

		layoutWidgets();

	}

	/**
	 * layout widgets such as Origins and Terminations
	 */
	protected void layoutWidgets() {
		if (widgets != null) {
			Point2D bounds = localToGlobal(new Point2D.Double(0, 0));

			double termX = -20 + bounds.getX();
			double termY = getIcon().getHeight() + bounds.getY();

			double originX = getIcon().getWidth() + 5 + +bounds.getX();
			double originY = termY;

			Iterator<WorldObjectImpl> it = widgets.iterator();

			/*
			 * Lays out origin objects
			 */
			while (it.hasNext()) {
				WorldObjectImpl widget = it.next();

				if (widget.getParent() == null) {
					/*
					 * Check to see that the origin has not been removed from
					 * the world
					 */

				} else {
					if (widget instanceof POrigin) {
						originY -= widget.getScale() * widget.getHeight() + 5;
						widget.setOffset(originX, originY);

					} else if (widget instanceof PDecodedTermination) {
						termY -= widget.getScale() * widget.getHeight() + 5;
						widget.setOffset(termX, termY);
					}
				}
			}

		}
	}

	protected void updateWidgets() {
		if (widgets == null)
			return;

		WorldLayer layer = getWorldLayer();

		if (layer == null)
			return;

		Iterator<WorldObjectImpl> it = widgets.iterator();

		while (it.hasNext()) {
			WorldObjectImpl wo = it.next();
			wo.signalBoundsChanged();
			layer.addChild(wo);
			wo.moveToFront();
		}

		layoutWidgets();
	}

	class ShowAllOriginAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ShowAllOriginAction() {
			super("Show all");
		}

		public void actionPerformed(ActionEvent e) {
			showAllOrigins();
		}
	}

	class ShowOriginAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		String originName;

		public ShowOriginAction(String originName) {
			super(originName);
			this.originName = originName;
		}

		public void actionPerformed(ActionEvent e) {
			getOrigin(originName);
		}
	}

}
