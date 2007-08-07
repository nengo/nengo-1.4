package ca.neo.ui.models;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.models.nodes.connectors.PModelWidget;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.models.nodes.widgets.GProbe;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.uci.ics.jung.graph.Vertex;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class PNeoNode extends PModelConfigurable {

	/*
	 * Probes can be attached to the node, or the node's widgets
	 */
	Vector<GProbe> probes;

	Vertex vertex;

	/*
	 * Widgets are attached objects which are not children.
	 */
	Vector<PModelWidget> widgets;

	public PNeoNode() {
		super();
		init();

	}

	public PNeoNode(Node model) {
		super(model);
		init();
	}

	@Override
	public void addedToWorld() {
		// TODO Auto-generated method stub
		super.addedToWorld();
		moveWidgetsToFront();
	}

	public void addWidget(PModelWidget widget) {
		if (widgets == null) {
			widgets = new Vector<PModelWidget>(3);
		}
		widget.setScale(0.5);
		widgets.add(widget);

		moveWidgetsToFront();
		assignProbes();
	}

	@SuppressWarnings("unchecked")
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

		MenuBuilder originsAndTerminations = menu
				.createSubMenu("Origins and terminations");

		/*
		 * Build the "show origins" menu
		 */
		Origin[] origins = getModel().getOrigins();
		if (origins.length > 0) {

			MenuBuilder originsMenu = originsAndTerminations
					.createSubMenu("Show origin");

			for (int i = 0; i < origins.length; i++) {
				originsMenu
						.addAction(new ShowOriginAction(origins[i].getName()));
			}

		}

		/*
		 * Build the "show origins" menu
		 */
		Termination[] terminations = getModel().getTerminations();
		if (terminations.length > 0) {

			MenuBuilder terminationsMenu = originsAndTerminations
					.createSubMenu("Show termination");

			for (int i = 0; i < terminations.length; i++) {
				terminationsMenu.addAction(new ShowTerminationAction(
						terminations[i].getName()));
			}

		}
		originsAndTerminations.addAction(new ShowAllOandTAction("Show all"));
		originsAndTerminations.addAction(new HideAllOandTAction("Hide all"));
		return menu;
	}

	/**
	 * Creates a new probe and adds the UI object to the node
	 * 
	 * @param stateName
	 *            The name of the state variable to probe
	 */
	public GProbe createProbe(String stateName) {
		if (probes == null)
			probes = new Vector<GProbe>();

		GProbe probe = new GProbe(this, stateName);
		addChild(probe);
		probes.add(probe);
		assignProbes();

		return probe;
	}

	@Override
	public void destroy() {

		NetworkViewer viewer = getNetworkViewer();
		if (viewer != null)
			getNetworkViewer().removeNode(this);

		/*
		 * remove widgets... since they are not children, they have to be
		 * removed explicitly
		 */
		Object[] widgetsAr = widgets.toArray();

		for (int i = 0; i < widgetsAr.length; i++) {
			((WorldObjectImpl) (widgetsAr[i])).destroy();
		}

		super.destroy();
	}

	/**
	 * @param layoutName
	 *            Name of an Origin on the Node model
	 * @return the POrigin shown
	 */
	public POrigin getAndShowOrigin(String originName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object origin = getWidget(originName, POrigin.class);
		if (origin != null) {
			return (POrigin) origin;
		}

		/*
		 * Otherwise try to create it
		 */

		POrigin originUI;
		try {
			originUI = new POrigin(this, getModel().getOrigin(originName));
			addWidget(originUI);

			return originUI;

		} catch (StructuralException e) {
			Util.Error(e.toString());
		}
		return null;

	}

	/**
	 * @param layoutName
	 *            Name of an Termination on the Node model
	 * @return the POrigin shown
	 */
	public PTermination getAndShowTermination(String terminationName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object term = getWidget(terminationName, PTermination.class);
		if (term != null) {
			return (PTermination) term;
		}

		/*
		 * Otherwise try to create it
		 */

		PTermination termUI;
		try {
			termUI = new PTermination(this, getModel().getTermination(
					terminationName));
			addWidget(termUI);
			return termUI;
		} catch (StructuralException e) {
			Util.Error(e.toString());
		}
		return null;

	}

	@Override
	public Node getModel() {
		// TODO Auto-generated method stub
		return (Node) super.getModel();
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
			// Util.Error("Node is not attached to a network");
			return null;
		}
	}

	/**
	 * @return Network model the node is attached to
	 */
	public Network getParentNetwork() {
		NetworkViewer netV = getNetworkViewer();
		if (netV != null) {
			return netV.getNetwork();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return Vertex to be used by Jung Graph visualization library
	 */
	public Vertex getVertex() {
		return vertex;
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
		Iterator<PModelWidget> it = widgets.iterator();

		while (it.hasNext()) {
			PModelWidget wo = it.next();

			if (type != null) {
				if (type.isInstance(wo) && (wo.getName().compareTo(name) == 0)) {
					return wo;
				}
			} else if ((wo.getName().compareTo(name) == 0)) {
				return wo;
			}

		}
		return null;
	}

	/**
	 * Hides all origins and terminations
	 */
	public void hideAllOandT() {
		Iterator<PModelWidget> it = widgets.iterator();
		while (it.hasNext()) {
			PModelWidget widget = it.next();
			if (widget instanceof PTermination || widget instanceof POrigin) {
				widget.setWidgetVisible(false);
			}

		}
	}

	/**
	 * layout widgets such as Origins and Terminations
	 */
	public void layoutWidgets() {
		if (widgets != null) {
			Rectangle2D bounds = getIcon().localToGlobal(getIcon().getBounds());

			double offsetX = bounds.getX();
			double offsetY = bounds.getY();

			double centerX = offsetX + bounds.getWidth() / 2f;
			double centerY = offsetY + bounds.getHeight() / 2f;

			double termX = -20 + bounds.getX();
			double termY = getIcon().getHeight() + offsetY;

			double originX = getIcon().getWidth() + 5 + offsetX;
			double originY = termY;

			Iterator<PModelWidget> it = widgets.iterator();

			/*
			 * Lays out origin objects
			 */
			while (it.hasNext()) {
				PModelWidget widget = it.next();

				if (widget.getParent() == null) {
					/*
					 * Check to see that the origin has not been removed from
					 * the world
					 */

				} else {

					double scale = widget.getScale();

					if (!isHoverOver && !(widget).isWidgetVisible()) {
						widget
								.setOffset(centerX - widget.getWidth() * scale
										/ 2f, centerY - widget.getHeight()
										* scale / 2f);

						widget.setVisible(false);
						widget.setPickable(false);
						widget.setChildrenPickable(false);

					} else {
						widget.setVisible(true);
						widget.setPickable(true);
						widget.setChildrenPickable(true);

						if (widget instanceof POrigin) {
							originY -= scale * widget.getHeight() + 5;
							widget.setOffset(originX, originY);

						} else if (widget instanceof PTermination) {
							termY -= scale * widget.getHeight() + 5;
							widget.setOffset(termX, termY);
						}
					}
				}

			}
		}

	}

	@Override
	public void moveToFront() {
		// TODO Auto-generated method stub
		super.moveToFront();
		moveWidgetsToFront();
	}

	/**
	 * Removes a Probe UI object from node
	 * 
	 * @param probe
	 *            to be removed
	 */
	public void removeProbe(GProbe probe) {
		probes.remove(probe);
		probe.destroy();

	}

	public void removeWidget(WorldObjectImpl widget) {
		widgets.remove(widget);
		widget.removeFromParent();

		moveWidgetsToFront();
		assignProbes();
	}

	// Vector<POrigin> origins;

	@Override
	public void setModel(Object model) {
		// TODO Auto-generated method stub
		super.setModel(model);
		setName(((Node) model).getName());
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * Shows all the origins on the Node model
	 */
	public void showAllOrigins() {

		Origin[] origins = getModel().getOrigins();

		for (int i = 0; i < origins.length; i++) {
			POrigin originUI = getAndShowOrigin(origins[i].getName());
			originUI.setWidgetVisible(true);
		}

	}

	/**
	 * Shows all the terminations on the Node model
	 */
	public void showAllTerminations() {

		Termination[] terminations = getModel().getTerminations();

		for (int i = 0; i < terminations.length; i++) {
			PTermination termUI = getAndShowTermination(terminations[i]
					.getName());
			termUI.setWidgetVisible(true);
		}

	}

	@Override
	public void update() {

		super.update();

		Origin[] origins = getModel().getOrigins();
		Termination[] terminations = getModel().getTerminations();

		for (int i = 0; i < origins.length; i++) {
			Origin origin = origins[i];

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
	protected Object configureModel(PropertySet configuredProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Vector<PModelWidget> getWidgets() {
		return widgets;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();

		layoutWidgets();

	}

	/**
	 * Moves widgets to front, so that they are always visible in front of the
	 * node
	 */
	protected void moveWidgetsToFront() {
		if (widgets == null)
			return;

		WorldLayer layer = getWorldLayer();

		if (layer == null)
			return;

		Iterator<PModelWidget> it = widgets.iterator();

		while (it.hasNext()) {
			WorldObjectImpl wo = it.next();
			wo.signalBoundsChanged();
			layer.addChild(wo);
			wo.moveToFront();
		}

		layoutWidgets();
	}

	class AddProbeAction extends ReversableAction {

		private static final long serialVersionUID = 1;

		GProbe probeCreated;

		Entry<String, String> state;

		public AddProbeAction(Entry<String, String> state) {
			super(state.getKey() + " - " + state.getValue());
			this.state = state;

		}

		@Override
		protected void action() throws ActionException {

			probeCreated = createProbe(state.getKey());

		}

		@Override
		protected void undo() throws ActionException {
			removeProbe(probeCreated);

		}

	}

	class HideAllOandTAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HideAllOandTAction(String actionName) {
			super("Hide all origins and terminations", actionName);
		}

		@Override
		protected void action() throws ActionException {
			hideAllOandT();
		}
	}

	class ShowAllOandTAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ShowAllOandTAction(String actionName) {
			super("Show all origins and terminations", actionName);
		}

		@Override
		protected void action() throws ActionException {
			showAllOrigins();
			showAllTerminations();
		}
	}

	class ShowOriginAction extends StandardAction {

		private static final long serialVersionUID = 1L;
		String originName;

		public ShowOriginAction(String originName) {
			super(originName);
			this.originName = originName;
		}

		@Override
		protected void action() throws ActionException {
			getAndShowOrigin(originName);
		}
	}

	class ShowTerminationAction extends StandardAction {

		private static final long serialVersionUID = 1L;
		String termName;

		public ShowTerminationAction(String termName) {
			super(termName);
			this.termName = termName;
		}

		@Override
		protected void action() throws ActionException {
			getAndShowTermination(termName);
		}
	}

	boolean isHoverOver = false;

	public void setHoveredOver(boolean bool) {
		isHoverOver = bool;
		layoutWidgets();
	}

}
