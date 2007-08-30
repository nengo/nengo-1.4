package ca.neo.ui.models;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.models.nodes.widgets.UIOrigin;
import ca.neo.ui.models.nodes.widgets.UISimulatorProbe;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.ui.models.nodes.widgets.Widget;
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
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.uci.ics.jung.graph.Vertex;

/**
 * UI Wrapper for a NEO Node Model
 * 
 * @author Shu
 * 
 */
public abstract class UINeoNode extends UIModelConfigurable {

	/**
	 * Attached probes
	 */
	private Vector<UISimulatorProbe> probes;

	/**
	 * Representative vertex used by Jung layout algorithms
	 */
	private Vertex vertex;

	public UINeoNode() {
		super();
	}

	public UINeoNode(Node model) {
		super(model);
	}

	/**
	 * Does a linear search of the node's children and returns the result
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

	/**
	 * Called when a new probe is added
	 * 
	 * @param probeUI
	 *            New probe that was just added
	 */
	private void newProbeAdded(UISimulatorProbe probeUI) {
		if (probes == null)
			probes = new Vector<UISimulatorProbe>();

		addChild(probeUI);
		probes.add(probeUI);

		/*
		 * Assign the probe to a Origin / Termination
		 */

		WorldObject probeHolder = null;

		Origin origin = null;
		try {
			origin = getModel().getOrigin(probeUI.getName());

		} catch (StructuralException e1) {
		}

		if (origin != null) {
			probeHolder = showOrigin(origin.getName());
		} else if (origin == null) {
			Termination term = null;
			try {
				term = getModel().getTermination(probeUI.getName());

			} catch (StructuralException e) {
			}
			if (term != null)
				probeHolder = showTermination(term.getName());
		}

		if (probeHolder != null) {
			probeUI.setOffset(0, probeHolder.getHeight() / 2);
			probeHolder.addChild(probeUI);

		} else {
			addChild(probeUI);

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

		MenuBuilder originsAndTerminations = menu
				.createSubMenu("Origins and terminations");

		/*
		 * Build the "show origins" menu
		 */
		Origin[] origins = getModel().getOrigins();
		if (origins.length > 0) {

			MenuBuilder originsMenu = originsAndTerminations
					.createSubMenu("Show origin");

			for (Origin element : origins) {
				originsMenu.addAction(new ShowOriginAction(element.getName()));
			}

		}

		/*
		 * Build the "show origins" menu
		 */
		Termination[] terminations = getModel().getTerminations();
		if (terminations.length > 0) {

			MenuBuilder terminationsMenu = originsAndTerminations
					.createSubMenu("Show termination");

			for (Termination element : terminations) {
				terminationsMenu.addAction(new ShowTerminationAction(element
						.getName()));
			}

		}
		originsAndTerminations.addAction(new ShowAllOandTAction("Show all"));
		originsAndTerminations.addAction(new HideAllOandTAction("Hide all"));

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

		double probeY = 0;

		Iterator<Object> it = getChildrenIterator();

		/*
		 * Lays out origin objects
		 */
		while (it.hasNext()) {
			Object obj = it.next();

			if (obj instanceof UISimulatorProbe) {
				UISimulatorProbe probe = (UISimulatorProbe) obj;

				probe.setOffset(0, probeY + getHeight() / 2);
				probeY -= probe.getHeight() + 5;

			} else if (obj instanceof Widget) {
				Widget widget = (Widget) obj;
				if (widget.getParent() == null) {
					/*
					 * Check to see that the origin has not been removed from
					 * the world
					 */

				} else {

					double scale = widget.getScale();

					if (!(widget).isWidgetVisible()) {
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

						if (widget instanceof UIOrigin) {
							originY -= scale * widget.getHeight() + 8;
							widget.setOffset(originX, originY);

						} else if (widget instanceof UITermination) {
							termY -= scale * widget.getHeight() + 8;
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

	/**
	 * Creates a new probe and adds the UI object to the node
	 * 
	 * @param stateName
	 *            The name of the state variable to probe
	 */
	public UISimulatorProbe addProbe(String stateName) {

		UISimulatorProbe probeUI = new UISimulatorProbe(this, stateName);
		newProbeAdded(probeUI);

		return probeUI;
	}

	/**
	 * @param widget
	 *            Widget to be added
	 */
	public void addWidget(Widget widget) {
		widget.setScale(0.5);
		addChild(widget);
	}

	@Override
	public Node getModel() {
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
	 * @return The Network model the Node is attached to
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
			if (obj instanceof Widget
					&& (obj instanceof UITermination || obj instanceof UIOrigin)) {
				((Widget) obj).setWidgetVisible(false);
			}

		}
		layoutChildren();
	}

	/**
	 * Hides all widgets
	 */
	@SuppressWarnings("unchecked")
	public void hideAllWidgets() {

		Iterator<Object> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Widget) {
				((Widget) obj).setWidgetVisible(false);
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
	public void removeProbe(UISimulatorProbe probe) {
		probes.remove(probe);
		probe.destroy();

	}

	@Override
	public final void setName(String name) {
		/*
		 * Set name is disabled, the Name is automatically retrieved from model
		 */
		throw new NotImplementedException();
	}

	/**
	 * @param vertex
	 *            Vertex representation of this node
	 */
	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * Shows all the origins on the Node model
	 */
	public void showAllOrigins() {

		Origin[] origins = getModel().getOrigins();

		for (Origin element : origins) {
			UIOrigin originUI = showOrigin(element.getName());
			originUI.setWidgetVisible(true);
		}
		layoutChildren();
	}

	/**
	 * Shows all the terminations on the Node model
	 */
	public void showAllTerminations() {

		Termination[] terminations = getModel().getTerminations();

		for (Termination element : terminations) {
			UITermination termUI = showTermination(element.getName());
			termUI.setWidgetVisible(true);
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
			if (obj instanceof Widget) {
				((Widget) obj).setWidgetVisible(true);
			}
		}
		layoutChildren();
	}

	/**
	 * @param layoutName
	 *            Name of an Origin on the Node model
	 * @return the POrigin shown
	 */
	public UIOrigin showOrigin(String originName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object origin = getChild(originName, UIOrigin.class);
		if (origin != null) {
			return (UIOrigin) origin;
		}

		/*
		 * Otherwise try to create it
		 */

		UIOrigin originUI;
		try {
			originUI = new UIOrigin(this, getModel().getOrigin(originName));
			addWidget(originUI);

			return originUI;

		} catch (StructuralException e) {
			Util.UserError(e.toString());
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
	public UISimulatorProbe showProbe(Probe probe) {
		UISimulatorProbe probeUI = new UISimulatorProbe(this, probe);
		newProbeAdded(probeUI);
		return probeUI;
	}

	/**
	 * @param layoutName
	 *            Name of an Termination on the Node model
	 * @return the POrigin shown
	 */
	public UITermination showTermination(String terminationName) {
		/*
		 * Try to find if the origin has already been created
		 */
		Object term = getChild(terminationName, UITermination.class);
		if (term != null) {
			return (UITermination) term;
		}

		/*
		 * Otherwise try to create it
		 */

		UITermination termUI;
		try {
			termUI = new UITermination(this, getModel().getTermination(
					terminationName));
			addWidget(termUI);
			return termUI;
		} catch (StructuralException e) {
			Util.UserError(e.toString());
		}
		return null;

	}

	/**
	 * TODO: update origins and terminations based on model
	 */
	public void update() {

	}

	/**
	 * Action for adding probes
	 * 
	 * @author Shu Wu
	 * 
	 */
	class AddProbeAction extends ReversableAction {

		private static final long serialVersionUID = 1;

		UISimulatorProbe probeCreated;

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

	/**
	 * Action for hiding all origins and terminations
	 * 
	 * @author Shu Wu
	 * 
	 */
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

	/**
	 * Action for setting the documentation of the node
	 * 
	 * @author Shu Wu
	 * 
	 */
	class SetDocumentationAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		String prevDoc;

		public SetDocumentationAction(String actionName) {
			super("Set documentation on " + getName(), actionName);

		}

		@Override
		protected void action() throws ActionException {
			prevDoc = getModel().getDocumentation();

			JTextArea editor = new JTextArea(30, 50);
			editor.setText(prevDoc);

			int rtnValue = JOptionPane.showOptionDialog(UIEnvironment
					.getInstance(), editor, getName()
					+ " - Documenation Editor", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null, null);

			if (rtnValue == JOptionPane.OK_OPTION) {
				String text = editor.getText();

				if (text != null) {

					getModel().setDocumentation(text);
					popupTransientMsg("Documentation changed");
				} else {
					throw new UserCancelledException();
				}
			}

		}

		@Override
		protected void undo() throws ActionException {
			getModel().setDocumentation(prevDoc);
			popupTransientMsg("Documentation changed");
		}
	}

	/**
	 * Action for showing all origins and terminations
	 * 
	 * @author Shu Wu
	 * 
	 */
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

	/**
	 * Action for showing a specific origin
	 * 
	 * @author Shu Wu
	 * 
	 */
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

	/**
	 * Action for showing a specific termination
	 * 
	 * @author Shu Wu
	 * 
	 */
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

	/**
	 * Action for viewing the node's documentation
	 * 
	 * @author Shu Wu
	 * 
	 */
	class ViewDocumentationAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ViewDocumentationAction(String actionName) {
			super("View documentation on " + getName(), actionName);

		}

		@Override
		protected void action() throws ActionException {

			JTextArea editor = new JTextArea(30, 50);
			editor.setText(getModel().getDocumentation());
			editor.setEditable(false);

			JOptionPane.showMessageDialog(UIEnvironment.getInstance(), editor,
					getName() + " - Documentation Viewer",
					JOptionPane.PLAIN_MESSAGE);

		}

	}
}
