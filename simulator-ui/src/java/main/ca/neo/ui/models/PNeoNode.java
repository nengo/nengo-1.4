package ca.neo.ui.models;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.models.nodes.connectors.PWidget;
import ca.neo.ui.models.nodes.widgets.GProbe;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.uci.ics.jung.graph.Vertex;

public abstract class PNeoNode extends PModelConfigurable {

	boolean isHoverOver = false;

	/*
	 * Probes can be attached to the node, or the node's widgets
	 */
	Vector<GProbe> probes;

	Vertex vertex;

	public PNeoNode() {
		super();
		init();

	}

	public PNeoNode(Node model) {
		super(model);
		init();
	}

	/**
	 * Creates a new probe and adds the UI object to the node
	 * 
	 * @param stateName
	 *            The name of the state variable to probe
	 */
	public GProbe addProbe(String stateName) {

		GProbe probeUI = new GProbe(this, stateName);
		newProbeAdded(probeUI);

		return probeUI;
	}

	public void addWidget(PWidget widget) {
		widget.setScale(0.5);
		addChild(widget);
	}

	@Override
	public Node getModel() {
		// TODO Auto-generated method stub
		return (Node) super.getModel();
	}

	@Override
	public String getName() {
		if (getModel() != null) {
			return getModel().getName();
		} else {
			return "Model not constructed";
		}
	}

	/**
	 * 
	 * @return The viewer the node is contained in, this may be a regular world
	 *         or a specialized viewer such as a NetworkViewer or EnsembleViewer
	 */
	public NodeViewer getParentViewer() {

		World viewer = getWorld();
		if (viewer != null && viewer instanceof NodeViewer) {
			return (NodeViewer) viewer;
		} else {
			return null;
		}
	}

	/**
	 * @return Network model the node is attached to
	 */
	public Network getParentNetwork() {
		World viewer = getWorld();

		/*
		 * Can only access parent network if the Node is inside a Network Viewer
		 */
		if (viewer instanceof NetworkViewer) {
			return ((NetworkViewer) viewer).getNetwork();
		}

		return null;
	}

	/**
	 * 
	 * @return Vertex to be used by Jung Graph visualization library
	 */
	public Vertex getVertex() {
		return vertex;
	}

	/**
	 * Hides all origins and terminations
	 */
	@SuppressWarnings("unchecked")
	public void hideAllOandT() {

		Iterator<Object> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof PWidget
					&& (obj instanceof PTermination || obj instanceof POrigin)) {
				((PWidget) obj).setPermenantlyAttached(false);
			}

		}
		layoutChildren();
	}

	/**
	 * Hide all widgets
	 */
	@SuppressWarnings("unchecked")
	public void hideAllWidgets() {

		Iterator<Object> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof PWidget) {
				((PWidget) obj).setPermenantlyAttached(false);
			}
		}
		layoutChildren();
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

	public void setHoveredOver(boolean bool) {
		isHoverOver = bool;
		layoutChildren();
	}

	@Override
	public final void setName(String name) {
		/*
		 * Set name is disabled, the Name is automatically retrieved from model
		 */
		throw new NotImplementedException();
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
			POrigin originUI = showOrigin(origins[i].getName());
			originUI.setPermenantlyAttached(true);
		}
		layoutChildren();
	}

	/**
	 * Shows all the terminations on the Node model
	 */
	public void showAllTerminations() {

		Termination[] terminations = getModel().getTerminations();

		for (int i = 0; i < terminations.length; i++) {
			PTermination termUI = showTermination(terminations[i].getName());
			termUI.setPermenantlyAttached(true);
		}
		layoutChildren();
	}

	/**
	 * Show all widgets
	 */
	@SuppressWarnings("unchecked")
	public void showAllWidgets() {
		Iterator<Object> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof PWidget) {
				((PWidget) obj).setPermenantlyAttached(true);
			}
		}
		layoutChildren();
	}

	/**
	 * @param layoutName
	 *            Name of an Origin on the Node model
	 * @return the POrigin shown
	 */
	public POrigin showOrigin(String originName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object origin = getChild(originName, POrigin.class);
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
	 * Call this function if the probe already exists in the simulator and only
	 * needs to be shown
	 * 
	 * @param probe
	 *            To be shown
	 * @return Probe UI Object
	 */
	public GProbe showProbe(Probe probe) {
		GProbe probeUI = new GProbe(this, probe);
		newProbeAdded(probeUI);
		return probeUI;
	}

	// public void removeWidget(WorldObjectImpl widget) {
	// widgets.remove(widget);
	//
	// removeChild(widget);
	// assignProbes();
	// }

	/**
	 * @param layoutName
	 *            Name of an Termination on the Node model
	 * @return the POrigin shown
	 */
	public PTermination showTermination(String terminationName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object term = getChild(terminationName, PTermination.class);
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

	/**
	 * Todo: update origins and terminations based on model
	 */
	public void update() {

		// Origin[] origins = getModel().getOrigins();
		// Termination[] terminations = getModel().getTerminations();
		//
		// for (int i = 0; i < origins.length; i++) {
		// Origin origin = origins[i];
		//
		// }
	}

	/**
	 * 
	 * @return The Child found matching parameters, null if not found
	 * @param name
	 *            of the Child
	 * @param type
	 *            of the Child
	 */
	@SuppressWarnings("unchecked")
	private WorldObject getChild(String name, Class type) {

		/*
		 * Linear search used because there tends to be only a small number of
		 * widets
		 */
		Iterator it = getChildrenIterator();

		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof WorldObject) {

				WorldObject wo = (WorldObject) obj;

				if (type != null) {
					if (type.isInstance(wo)
							&& (wo.getName().compareTo(name) == 0)) {
						return wo;
					}
				} else if ((wo.getName().compareTo(name) == 0)) {
					return wo;
				}
			}
		}
		return null;
	}

	private void init() {

	}

	private void newProbeAdded(GProbe probeUI) {
		if (probes == null)
			probes = new Vector<GProbe>();

		addChild(probeUI);
		probes.add(probeUI);

		/*
		 * Assign the probe to a Origin / Termination
		 */

		WorldObject probeHolder = null;

		Origin origin = null;
		try {
			origin = getModel().getOrigin(probeUI.getName());
			probeHolder = showOrigin(origin.getName());
		} catch (StructuralException e1) {
		}
		if (origin == null) {
			Termination term;
			try {
				term = getModel().getTermination(probeUI.getName());
				probeHolder = showTermination(term.getName());
			} catch (StructuralException e) {
			}
		}
		if (probeHolder != null) {
			probeUI.setOffset(0, probeHolder.getHeight() / 2);

		} else {
			addChild(probeUI);
			probeUI.setOffset(0, getHeight() / 2);
		}

		// assignProbes();

	}


	@SuppressWarnings("unchecked")
	@Override
	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		// menu.addSubMenu(label)
		menu.addSection("Node");

		MenuBuilder docMenu = menu.createSubMenu("Documentation");
		docMenu.addAction(new SetDocumentationAction("Set"));
		docMenu.addAction(new ViewDocumentationAction("View"));

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

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = new TooltipBuilder(getName() + "("
				+ getTypeName() + ")");

		tooltips.addPart(new PropertyPart("Documentation", getModel()
				.getDocumentation()));
		tooltips.addPart(new PropertyPart("Simulation mode", getModel()
				.getMode().toString()));

		return tooltips;

	}

	// /**
	// * Assign Probe UI objects to the appropriate widget
	// */
	// protected void assignProbes() {
	// if (probes == null)
	// return;
	// Iterator<GProbe> it = probes.iterator();
	//
	// while (it.hasNext()) {
	// GProbe probe = it.next();
	//
	// WorldObjectImpl widget = getWidget(probe.getName());
	// if (widget != null) {
	// probe.setOffset(0, widget.getHeight() / 2);
	// widget.addChild(probe);
	// } else {
	//			
	// }
	//
	// }
	// }

	@SuppressWarnings("unchecked")
	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		/*
		 * layout widgets such as Origins and Terminations
		 */

		Rectangle2D bounds = getIcon().localToParent(getIcon().getBounds());

		double offsetX = bounds.getX();
		double offsetY = bounds.getY();

		double centerX = offsetX + bounds.getWidth() / 2f;
		double centerY = offsetY + bounds.getHeight() / 2f;

		double termX = -20 + bounds.getX();
		double termY = getIcon().getHeight() + offsetY;

		double originX = getIcon().getWidth() + 5 + offsetX;
		double originY = termY;

		Iterator<Object> it = getChildrenIterator();

		/*
		 * Lays out origin objects
		 */
		while (it.hasNext()) {
			Object obj = it.next();

			if (obj instanceof PWidget) {
				PWidget widget = (PWidget) obj;
				if (widget.getParent() == null) {
					/*
					 * Check to see that the origin has not been removed from
					 * the world
					 */

				} else {

					double scale = widget.getScale();

					if (!isHoverOver && !(widget).isWidgetVisible()) {
						double x = centerX - widget.getWidth() * scale / 2f;
						double y = centerY - widget.getHeight() * scale / 2f;

						widget.setOffset(x, y);

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
	protected void prepareForDestroy() {

		NodeViewer viewer = getParentViewer();
		if (viewer != null)
			getParentViewer().removeNeoNode(this);

		super.prepareForDestroy();
	}

	// /**
	// * Moves widgets to front, so that they are always visible in front of the
	// * node
	// */
	// protected void moveWidgetsToFront() {
	// if (widgets.size() == 0)
	// return;
	//
	// WorldLayer layer = getWorldLayer();
	//
	// if (layer == null)
	// return;
	//
	// Iterator<PModelWidget> it = widgets.iterator();
	//
	// while (it.hasNext()) {
	// WorldObjectImpl wo = it.next();
	// wo.signalBoundsChanged();
	//
	// layer.addChild(wo);
	// wo.moveToFront();
	// }
	//
	// layoutWidgets();
	// }

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

			probeCreated = addProbe(state.getKey());

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
			hideAllWidgets();
		}
	}

	class SetDocumentationAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		String prevDoc;

		public SetDocumentationAction(String actionName) {
			super("Set documentation on " + getName(), actionName);

		}

		@Override
		protected void action() throws ActionException {
			prevDoc = getModel().getDocumentation();
			String text = JOptionPane.showInputDialog("Enter documentation");
			if (text != null) {

				getModel().setDocumentation(text);
				getWorld().showTransientMsg("Documentation changed",
						PNeoNode.this);
			} else {
				throw new UserCancelledException();
			}

		}

		@Override
		protected void undo() throws ActionException {
			getModel().setDocumentation(prevDoc);
			getWorld().showTransientMsg("Documentation changed", PNeoNode.this);
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
			showOrigin(originName);
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
			showTermination(termName);
		}
	}

	class ViewDocumentationAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ViewDocumentationAction(String actionName) {
			super("View documentation on " + getName(), actionName);

		}

		@Override
		protected void action() throws ActionException {
			Util.Message(getModel().getDocumentation(), "Title");

		}

	}
}
