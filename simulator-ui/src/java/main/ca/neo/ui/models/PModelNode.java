package ca.neo.ui.models;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractAction;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.widgets.GProbe;
import ca.neo.ui.models.wrappers.PDecodedTermination;
import ca.neo.ui.models.wrappers.POrigin;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.neo.util.Probe;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public abstract class PModelNode extends PModelConfigurable {

	/*
	 * Probes can be attached to the node, or the node's widgets
	 */
	Vector<GProbe> probes;

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
		assignProbes();
	}

	@Override
	public PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		// menu.addSubMenu(label)
		menu.addSection("Node");

		/*
		 * Build the "add probe" menu
		 */
		if (getModel() instanceof Probeable) {

			Probeable probeable = (Probeable) getModel();
			Properties states = probeable.listStates();

			// Enumeration e = states.elements();
			Iterator it = states.entrySet().iterator();
			MenuBuilder probesMenu = null;

			while (it.hasNext()) {
				if (probesMenu == null) {
					probesMenu = menu.createSubMenu("Add probe");
				}
				Entry<String, String> el = (Entry<String, String>) it.next();
				probesMenu.addAction(new AddProbeAction(el));
			}
		}

		/*
		 * Build the "show origins" menu
		 */
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
	 * Creates a new probe and adds the UI object to the node
	 * 
	 * @param state
	 *            The name of the state variable to probe
	 */
	public void createProbe(String state) {
		if (probes == null)
			probes = new Vector<GProbe>();

		GProbe probe = new GProbe(this, state);
		addChild(probe);
		probes.add(probe);
		assignProbes();
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

	public WorldObjectImpl getWidget(String name) {
		return getWidget(name, null);
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

			if (type != null && type.isInstance(wo)
					&& (wo.getName().compareTo(name) == 0)) {
				return wo;
			} else if ((wo.getName().compareTo(name) == 0)) {
				return wo;
			}

		}
		return null;
	}

	@Override
	public void moveToFront() {
		// TODO Auto-generated method stub
		super.moveToFront();
		updateWidgets();
	}

	/**
	 * Removes a Probe UI object from node
	 * 
	 * @param probe
	 *            to be removed
	 */
	public void removeProbe(GProbe probe) {
		probes.remove(probe);

	}

	// Vector<POrigin> origins;

	public void removeWidget(WorldObjectImpl widget) {
		widgets.remove(widget);
		widget.removeFromParent();

		updateWidgets();
		assignProbes();
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

	private void init() {

	}

	/**
	 * Assign Probe UI objects to the appropriate widget
	 */
	protected void assignProbes() {
		if (probes == null)
			return;
		Iterator<GProbe> it = probes.iterator();

		while (it.hasNext()) {
			GProbe probe = it.next();

			WorldObjectImpl widget = getWidget(probe.getName());
			if (widget != null) {
				probe.setOffset(0, widget.getHeight() / 2);
				widget.addChild(probe);
			} else {
				addChild(probe);
				probe.setOffset(0, getHeight() / 2);
			}

		}
	}

	@Override
	protected Object createModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();

		layoutWidgets();

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

	class AddProbeAction extends AbstractAction {

		private static final long serialVersionUID = 1;

		Entry<String, String> state;

		public AddProbeAction(Entry<String, String> state) {
			super(state.getKey() + " - " + state.getValue());
			this.state = state;

		}

		public void actionPerformed(ActionEvent arg0) {

			createProbe(state.getKey());
		}

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
