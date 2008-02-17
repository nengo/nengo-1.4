package ca.neo.ui.models;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.io.FileManager;
import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.actions.AddProbeAction;
import ca.neo.ui.actions.SaveNodeAction;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.nodes.UIFunctionInput;
import ca.neo.ui.models.nodes.UIGenericNode;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.UINeuron;
import ca.neo.ui.models.nodes.widgets.UIOrigin;
import ca.neo.ui.models.nodes.widgets.UIProbe;
import ca.neo.ui.models.nodes.widgets.UIStateProbe;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.ui.models.nodes.widgets.Widget;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.neo.util.Probe;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutable.Event;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.activities.TransientStatusMessage;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.WorldImpl;

/**
 * UI Wrapper for a NEO Node Model
 * 
 * @author Shu
 */
public abstract class UINeoNode extends UINeoModel {

	/**
	 * Factory method which creates a Node UI object around a Node
	 * 
	 * @param node
	 *            Node to be wrapped
	 * @return Node UI Wrapper
	 */
	public static UINeoNode createNodeUI(Node node) {

		UINeoNode nodeUI = null;
		if (node instanceof Network) {
			nodeUI = new UINetwork((Network) node);
		} else if (node instanceof Ensemble) {
			if (node instanceof NEFEnsemble) {
				nodeUI = new UINEFEnsemble((NEFEnsemble) node);
			} else {
				nodeUI = new UIEnsemble((Ensemble) node);
			}
		} else if (node instanceof Neuron) {
			nodeUI = new UINeuron((Neuron) node);
		} else if (node instanceof FunctionInput) {
			nodeUI = new UIFunctionInput((FunctionInput) node);
		} else {
			nodeUI = new UIGenericNode(node);
		}
		return nodeUI;
	}

	private ModelUpdateListener myUpdateListener;

	/**
	 * Attached probes
	 */
	private Vector<UIProbe> probes;

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

		for (WorldObject wo : getChildren()) {
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
	 * @param widget
	 *            Widget to be added
	 */
	protected void addWidget(Widget widget) {
		widget.setScale(0.5);
		addChild(widget);
	}

	@SuppressWarnings("unchecked")
	protected void constructDataCollectionMenu(AbstractMenuBuilder menu) {
		/*
		 * Build the "add probe" menu
		 */
		AbstractMenuBuilder probesMenu = menu.addSubMenu("Add probe");
		boolean somethingFound = false;
		if (getModel() instanceof Probeable) {

			Probeable probeable = (Probeable) getModel();
			Properties states = probeable.listStates();

			// Enumeration e = states.elements();
			Iterator<?> it = states.entrySet().iterator();

			while (it.hasNext()) {
				somethingFound = true;
				Entry<String, String> el = (Entry<String, String>) it.next();
				probesMenu.addAction(new AddProbeAction(this, el));

			}
		}

		if (!somethingFound) {
			probesMenu.addLabel("Nothing probeable");
		}

	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("File");
		menu.addAction(new SaveNodeAction(this));

		menu.addSection("View");
		AbstractMenuBuilder docMenu = menu.addSubMenu("Documentation");
		docMenu.addAction(new SetDocumentationAction("Set"));
		docMenu.addAction(new ViewDocumentationAction("View"));
		constructViewMenu(menu);

		menu.addSection("Data Collection");
		constructDataCollectionMenu(menu);

	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		if (getModel().getDocumentation() != null) {
			tooltips.addProperty("Documentation", Util.truncateString(
					getModel().getDocumentation(), 100));
		}
		tooltips.addProperty("Simulation mode", getModel().getMode().toString());

	}

	protected void constructViewMenu(AbstractMenuBuilder menu) {

		AbstractMenuBuilder originsAndTerminations = menu.addSubMenu("Origins and terminations");

		/*
		 * Build the "show origins" menu
		 */
		Origin[] origins = getModel().getOrigins();
		if (origins.length > 0) {

			AbstractMenuBuilder originsMenu = originsAndTerminations.addSubMenu("Show origin");

			for (Origin element : origins) {
				originsMenu.addAction(new ShowOriginAction(element.getName()));
			}

		}

		/*
		 * Build the "show origins" menu
		 */
		Termination[] terminations = getModel().getTerminations();
		if (terminations.length > 0) {

			AbstractMenuBuilder terminationsMenu = originsAndTerminations
					.addSubMenu("Show termination");

			for (Termination element : terminations) {
				terminationsMenu.addAction(new ShowTerminationAction(element.getName()));
			}

		}
		originsAndTerminations.addAction(new ShowAllOandTAction("Show all"));
		originsAndTerminations.addAction(new HideAllOandTAction("Hide all"));

	}

	@Override
	protected void initialize() {
		super.initialize();
		probes = new Vector<UIProbe>();
		myUpdateListener = new ModelUpdateListener();
	}

	/**
	 * Called when a new probe is added
	 * 
	 * @param probeUI
	 *            New probe that was just added
	 */
	protected void newProbeAdded(UIProbe probeUI) {

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
	}

	@Override
	protected void prepareToDestroyModel() {
		super.prepareToDestroyModel();

		for (WorldObject wo : getChildren()) {
			if (wo instanceof UITermination) {
				((UITermination) wo).disconnect();
			}
		}

	}

	/**
	 * Creates a new probe and adds the UI object to the node
	 * 
	 * @param stateName
	 *            The name of the state variable to probe
	 */
	public UIStateProbe addProbe(String stateName) throws SimulationException {
		UIStateProbe probeUI = new UIStateProbe(this, stateName);
		newProbeAdded(probeUI);
		return probeUI;
	}

	@Override
	public void attachViewToModel() {
		super.attachViewToModel();
		if (getModel() instanceof VisiblyMutable) {
			VisiblyMutable visiblyMutable = (VisiblyMutable) getModel();
			visiblyMutable.addChangeListener(myUpdateListener);
		}
	}

	@Override
	public void detachViewFromModel() {
		super.detachViewFromModel();
		if (getModel() instanceof VisiblyMutable) {
			VisiblyMutable visiblyMutable = (VisiblyMutable) getModel();

			Util.Assert(myUpdateListener != null);
			visiblyMutable.removeChangeListener(myUpdateListener);
		}
	}

	/**
	 * @return The default file name for this node
	 */
	public String getFileName() {
		return this.getName() + "." + NeoGraphics.NEONODE_FILE_EXTENSION;
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
		WorldImpl viewer = getWorld();

		/*
		 * Can only access parent network if the Node is inside a Network Viewer
		 */
		if (viewer instanceof NetworkViewer) {
			return ((NetworkViewer) viewer).getModel();
		}

		return null;
	}

	/**
	 * @return The viewer the node is contained in, this may be a regular world
	 *         or a specialized viewer such as a NetworkViewer or EnsembleViewer
	 */
	public NodeViewer getParentViewer() {

		WorldImpl viewer = getWorld();
		if (viewer != null && viewer instanceof NodeViewer) {
			return (NodeViewer) viewer;
		} else {
			return null;
		}
	}

	/**
	 * Hides all origins and terminations
	 */
	@SuppressWarnings("unchecked")
	public void hideAllOandT() {
		for (WorldObject wo : getChildren()) {
			if (wo instanceof Widget && (wo instanceof UITermination || wo instanceof UIOrigin)) {
				((Widget) wo).setWidgetVisible(false);
			}
		}
		layoutChildren();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void layoutChildren() {
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

		/*
		 * Lays out origin objects
		 */
		for (WorldObject wo : getChildren()) {

			if (wo instanceof UIProbe) {
				UIProbe probe = (UIProbe) wo;

				probe.setOffset(getWidth() * (1f / 4f), probeY + getHeight() * (1f / 4f));
				probeY += probe.getHeight() + 5;

			} else if (wo instanceof Widget) {
				Widget widget = (Widget) wo;
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

	/**
	 * Removes a Probe UI object from node
	 * 
	 * @param probe
	 *            to be removed
	 */
	public void removeProbe(UIProbe probe) {
		probes.remove(probe);
		probe.destroy();

	}

	/**
	 * @param file
	 *            File to be saved in
	 * @throws IOException
	 *             if model cannot be saved to file
	 */
	public void saveModel(File file) throws IOException {
		FileManager fm = new FileManager();

		fm.save(this.getModel(), file);
		new TransientStatusMessage(this.getFullName() + " was saved to " + file.toString(), 2500);
	}

	@Override
	public final void setName(String name) {
		/*
		 * Set name is disabled, the Name is automatically retrieved from model
		 */
		throw new NotImplementedException();
	}

	/**
	 * Sets the visibility of widgets
	 */
	public void setWidgetsVisible(boolean visible) {
		for (WorldObject wo : getChildren()) {
			if (wo instanceof Widget) {
				((Widget) wo).setWidgetVisible(visible);
			}
		}
		layoutChildren();
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
			UserMessages.showError(e.toString());
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
	public UIProbe showProbe(Probe probe) {
		/*
		 * Check if the probe is already shown
		 */
		for (UIProbe probeUI : probes) {
			if (probeUI.getModel() == probe) {
				return probeUI;
			}
		}

		UIStateProbe probeUI = new UIStateProbe(this, probe);
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
			termUI = new UITermination(this, getModel().getTermination(terminationName));
			addWidget(termUI);
			return termUI;
		} catch (StructuralException e) {
			UserMessages.showError(e.toString());
		}
		return null;

	}

	private class ModelUpdateListener implements VisiblyMutable.Listener {
		private boolean modelUpdatePending = false;

		public void changed(Event e) {
			if (!modelUpdatePending) {
				modelUpdatePending = true;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						modelUpdatePending = false;
						modelUpdated();
					}
				});
			}
		}
	}

	/**
	 * Action for hiding all origins and terminations
	 * 
	 * @author Shu Wu
	 */
	class HideAllOandTAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HideAllOandTAction(String actionName) {
			super("Hide all origins and terminations", actionName);
		}

		@Override
		protected void action() throws ActionException {
			setWidgetsVisible(false);
		}
	}

	/**
	 * Action for setting the documentation of the node
	 * 
	 * @author Shu Wu
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

			int rtnValue = JOptionPane.showOptionDialog(UIEnvironment.getInstance(), editor,
					getName() + " - Documenation Editor", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null, null);

			if (rtnValue == JOptionPane.OK_OPTION) {
				String text = editor.getText();

				if (text != null) {

					getModel().setDocumentation(text);
					showPopupMessage("Documentation changed");
				} else {
					throw new UserCancelledException();
				}
			}

		}

		@Override
		protected void undo() throws ActionException {
			getModel().setDocumentation(prevDoc);
			showPopupMessage("Documentation changed");
		}
	}

	/**
	 * Action for showing all origins and terminations
	 * 
	 * @author Shu Wu
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

			JOptionPane.showMessageDialog(UIEnvironment.getInstance(), editor, getName()
					+ " - Documentation Viewer", JOptionPane.PLAIN_MESSAGE);

		}

	}

	public Vector<UIProbe> getProbes() {
		return probes;
	}
}
