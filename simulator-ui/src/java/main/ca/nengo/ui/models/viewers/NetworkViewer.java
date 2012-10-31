/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "NetworkViewer.java". Description: 
"Viewer for peeking into a Network
  
  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import ca.nengo.config.ClassRegistry;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Probeable;
import ca.nengo.model.Projection;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.actions.CreateModelAction;
import ca.nengo.ui.actions.CreateModelAdvancedAction;
import ca.nengo.ui.actions.OpenNeoFileAction;
import ca.nengo.ui.actions.PasteAction;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.util.menus.MenuBuilder;
import ca.nengo.ui.lib.util.menus.PopupMenuBuilder;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.constructors.ConstructableNode;
import ca.nengo.ui.models.constructors.ModelFactory;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.models.nodes.widgets.UIOrigin;
import ca.nengo.ui.models.nodes.widgets.UIProbe;
import ca.nengo.ui.models.nodes.widgets.UIProjection;
import ca.nengo.ui.models.nodes.widgets.UIStateProbe;
import ca.nengo.ui.models.nodes.widgets.UITermination;
import ca.nengo.util.Probe;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Viewer for peeking into a Network
 * 
 * @author Shu Wu
 */
public class NetworkViewer extends NodeViewer implements NodeContainer {
	private static final boolean ELASTIC_LAYOUT_ENABLED_DEFAULT = false;

	/**
	 * Name given to the current layout by default, saved when the viewer is
	 * closed
	 */
	public static final String DEFAULT_NODE_LAYOUT_NAME = "AutoSaved";

	/**
	 * @param pNetwork
	 *            Parent Network UI wrapper
	 */
	public NetworkViewer(UINetwork pNetwork) {
		super(pNetwork);

	}

	@Override
	protected boolean canRemoveChildModel(Node node) {
		return true;
	}

	@Override
	protected void constructLayoutMenu(MenuBuilder menu) {
		super.constructLayoutMenu(menu);
		menu.addSection("File");
		menu.addAction(new SaveLayout("Save"));

		MenuBuilder restoreLayout = menu.addSubMenu("Restore");

		String[] layoutNames = getConfig().getLayoutNames();

		if (layoutNames.length > 0) {
			for (String element : layoutNames) {
				restoreLayout.addAction(new RestoreLayout(element));
			}
		} else {
			restoreLayout.addLabel("none");
		}

		MenuBuilder deleteLayout = restoreLayout.addSubMenu("Delete");

		if (layoutNames.length > 0) {
			for (String element : layoutNames) {
				deleteLayout.addAction(new DeleteLayout(element));
			}
		} else {
			deleteLayout.addLabel("none");
		}

	}

	private HashSet<Origin> exposedOrigins;
	private HashSet<Termination> exposedTerminations;

	@Override
	protected void initialize() {
		exposedOrigins = new HashSet<Origin>(getModel().getOrigins().length);
		exposedTerminations = new HashSet<Termination>(getModel().getTerminations().length);

		super.initialize();
		updateSimulatorProbes();
	}

	@Override
	protected void prepareForDestroy() {

		saveLayoutAsDefault();
		super.prepareForDestroy();
	}

	@Override
	protected void removeChildModel(Node node) {
		try {
			getModel().removeNode(node.getName());
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}
	
	
	protected Double newItemPositionX;
	protected Double newItemPositionY;
	public void setNewItemPosition(Double x, Double y) {
		newItemPositionX=x;
		newItemPositionY=y;
	}
	

	/**
	 * Construct UI Nodes from the NEO Network model
	 */
	protected void updateViewFromModel(boolean isFirstUpdate) {

		/*
		 * Get the current children and map them
		 */
		HashMap<Node, UINeoNode> currentNodes = new HashMap<Node, UINeoNode>(
				getGround().getChildrenCount());

		Enumeration<UINeoNode> en = neoNodesChildren.elements();
		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();
			if (!node.isDestroyed()) {
				Util.Assert(node.getModel() != null);
				currentNodes.put(node.getModel(), node);
			}
		}
		neoNodesChildren.clear();

		/*
		 * Construct Nodes from the Network model
		 */
		Node[] nodes = getModel().getNodes();

		for (Node node : nodes) {
			if (getUINode(node) == null) {
				UINeoNode nodeUI = currentNodes.get(node);

				if (nodeUI == null) {
					/*
					 * Create UI Wrappers here
					 */
					nodeUI = UINeoNode.createNodeUI(node);

					
					if (newItemPositionX != null && newItemPositionY != null) {
						nodeUI.setOffset(newItemPositionX, newItemPositionY);
						neoNodesChildren.put(nodeUI.getModel(), nodeUI);
						getGround().addChildFancy(nodeUI, false);

					} else {
						boolean centerAndNotify = !isFirstUpdate;
						addUINode(nodeUI, centerAndNotify, false);
						if (centerAndNotify) {
							nodeUI.showPopupMessage("Node " + node.getName() + " added to Network");
						}
					}
				} else {
					neoNodesChildren.put(nodeUI.getModel(), nodeUI);
				}

			} else {
				Util.Assert(false, "Trying to add node which already exists");
			}
		}
		
		newItemPositionX=null;
		newItemPositionY=null;


		/*
		 * Prune existing nodes by deleting them
		 */
		for (Node node : currentNodes.keySet()) {
			// Remove nodes which are no longer referenced by the network model
			if (getUINode(node) == null) {
				UINeoNode nodeUI = currentNodes.get(node);
				nodeUI.showPopupMessage("Node " + nodeUI.getName() + " removed from Network");
				nodeUI.destroy();
			}
		}

		/*
		 * Create projection map
		 */
		HashSet<Projection> projectionsToAdd = new HashSet<Projection>(
				getModel().getProjections().length);
		for (Projection projection : getModel().getProjections()) {
			projectionsToAdd.add(projection);
		}

		HashMap<Termination, Projection> projectionMap = new HashMap<Termination, Projection>(
				projectionsToAdd.size());

		for (Projection projection : projectionsToAdd) {
			Util.Assert(!projectionMap.containsKey(projection.getTermination()),
					"More than one projection found per termination");

			projectionMap.put(projection.getTermination(), projection);
		}

		/*
		 * Get UI projections
		 */
		LinkedList<UIProjection> projectionsToRemove = new LinkedList<UIProjection>();

		for (UINeoNode nodeUI : getUINodes()) {
			for (UITermination terminationUI : nodeUI.getVisibleTerminations()) {
				if (terminationUI.getConnector() != null) {
					UIOrigin originUI = terminationUI.getConnector().getOriginUI();

					Termination termination = terminationUI.getModel();
					Origin origin = originUI.getModel();

					Projection projection = projectionMap.get(termination);
					if (projection != null && projection.getOrigin() == origin) {
						/*
						 * Projection already exists
						 */
						projectionsToAdd.remove(projectionMap.get(termination));

					} else {
						projectionsToRemove.add(terminationUI.getConnector());
					}
				}
			}
		}

		/*
		 * Destroy unreferenced projections
		 */
		for (UIProjection projectionUI : projectionsToRemove) {
			UITermination terminationUI = projectionUI.getTermination();

			projectionUI.destroy();
			if (!isFirstUpdate) {
				terminationUI.showPopupMessage("REMOVED Projection to "
						+ terminationUI.getNodeParent().getName() + "." + terminationUI.getName());
			}
		}

		/*
		 * Construct projections
		 */
		for (Projection projection : projectionsToAdd) {
			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			UINeoNode nodeOrigin = getUINode(origin.getNode());

			UINeoNode nodeTerm = getUINode(term.getNode());

			if (nodeOrigin != null && nodeTerm != null) {
				UIOrigin originUI = nodeOrigin.showOrigin(origin.getName());
				UITermination termUI = nodeTerm.showTermination(term.getName());

				originUI.connectTo(termUI, false);
				if (!isFirstUpdate) {
					termUI.showPopupMessage("NEW Projection to " + termUI.getName() + "."
							+ getName());
				}
			} else {
				if (nodeOrigin == null) {
					Util.Assert(false, "Could not find a Origin attached to a projection: "
							+ origin.getNode().getName());
				}
				if (nodeTerm == null) {
					Util.Assert(false, "Could not find a Termination attached to a projection: "
							+ term.getNode().getName());
				}
			}

		}

		updateViewExposed();
	}

	private void updateViewExposed() {
		/*
		 * Get exposed Origins and Terminations
		 */
		HashSet<Origin> exposedOriginsTemp = new HashSet<Origin>(getModel().getOrigins().length);
		HashSet<Termination> exposedTerminationsTemp = new HashSet<Termination>(
				getModel().getTerminations().length);

		for (Origin origin : getModel().getOrigins()) {
			if (origin instanceof NetworkImpl.OriginWrapper) {
				NetworkImpl.OriginWrapper originWr = (NetworkImpl.OriginWrapper) origin;
				exposedOriginsTemp.add(originWr.getWrappedOrigin());
			}
		}

		for (Termination termination : getModel().getTerminations()) {
			if (termination instanceof NetworkImpl.TerminationWrapper) {
				NetworkImpl.TerminationWrapper terminationWr = (NetworkImpl.TerminationWrapper) termination;
				exposedTerminationsTemp.add(terminationWr.getWrappedTermination());
			}
		}

		/*
		 * Check to see if terminations have been added or removed
		 */
		boolean exposedOriginsChanged = false;
		if (exposedOriginsTemp.size() != exposedOrigins.size()) {
			exposedOriginsChanged = true;
		} else {
			/*
			 * Iterate through origins to see if any have changed
			 */
			for (Origin origin : exposedOriginsTemp) {
				if (!exposedOrigins.contains(origin)) {
					break;
				}
				exposedOriginsChanged = true;
			}
		}
		// Copy changed exposed origins if needed
		if (exposedOriginsChanged) {
			exposedOrigins = exposedOriginsTemp;
		}

		boolean exposedTerminationsChanged = false;
		if (exposedTerminationsTemp.size() != exposedTerminations.size()) {
			exposedTerminationsChanged = true;
		} else {
			/*
			 * Iterate through Termination to see if any have changed
			 */
			for (Termination termination : exposedTerminationsTemp) {
				if (!exposedTerminations.contains(termination)) {
					break;
				}
				exposedTerminationsChanged = true;
			}
		}
		// Copy changed exposed terminations if needed
		if (exposedTerminationsChanged) {
			exposedTerminations = exposedTerminationsTemp;
		}

		if (exposedTerminationsChanged || exposedOriginsChanged) {
			/*
			 * Update exposed terminations and origins
			 */
			for (WorldObject wo : getGround().getChildren()) {
				if (wo instanceof UINeoNode) {
					UINeoNode nodeUI = (UINeoNode) wo;

					if (exposedOriginsChanged) {
						for (UIOrigin originUI : nodeUI.getVisibleOrigins()) {
							boolean isExposed = exposedOrigins.contains(originUI.getModel());
							originUI.setExposed(isExposed);
						}
					}
					if (exposedTerminationsChanged) {
						for (UITermination terminationUI : nodeUI.getVisibleTerminations()) {
							boolean isExposed = exposedTerminations.contains(terminationUI.getModel());
							terminationUI.setExposed(isExposed);
						}
					}
				}
			}
		}
	}

	@Override
	public void applyDefaultLayout() {
		if (getUINodes().size() != 0) {
			if (restoreNodeLayout(DEFAULT_NODE_LAYOUT_NAME)) {
				return;
			} else {
				applySortLayout(SortMode.BY_NAME);
				// applyJungLayout(KKLayout.class);
			}
		}
		if (ELASTIC_LAYOUT_ENABLED_DEFAULT) {
			// enable elastic layout for Jung && when no nodes are loaded.
			getGround().setElasticEnabled(true);
		}
	}

	@Override
	public void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		/*
		 * Construct simulator menu
		 */
		UINetwork.constructSimulatorMenu(menu, getViewerParent());

		/*
		 * Create new models
		 */
		menu.addSection("Add model");
		MenuBuilder createNewMenu = menu.addSubMenu("Create new");

		// Nodes
		for (ConstructableNode constructable : ModelFactory.getNodeConstructables(this)) {
			createNewMenu.addAction(new CreateModelAction(this, constructable));
		}

		MenuBuilder createAdvancedMenu = createNewMenu.addSubMenu("Other");
		for (Class<?> element : ClassRegistry.getInstance().getRegisterableTypes()) {
			if (Node.class.isAssignableFrom(element)) {
				createAdvancedMenu.addAction(new CreateModelAdvancedAction(this, element));
			}
		}

		menu.addAction(new OpenNeoFileAction(this));

		/*
		 * Origins & Terminations
		 */
		menu.addSection("Origins and Terminations");
		menu.addAction(new SetOTVisiblityAction("Unhide all", true));
		menu.addAction(new SetOTVisiblityAction("Hide all", false));

	}

	/**
	 * @param name
	 *            Name of layout to delete
	 */
	public void deleteNodeLayout(String name) {
		NetworkViewerConfig layouts = getConfig();
		layouts.removeLayout(name);
	}

	/**
	 * @return Static settings including saved layouts
	 */
	public NetworkViewerConfig getConfig() {
		return getViewerParent().getSavedConfig();
	}

	@Override
	public Network getModel() {
		return (Network) super.getModel();
	}

	@Override
	public UINetwork getViewerParent() {
		return (UINetwork) super.getViewerParent();
	}

	/**
	 * @return Whether the operation was successful
	 * @param name
	 *            Name of layout to restore
	 */
	public boolean restoreNodeLayout(String name) {

		NetworkViewerConfig config = getConfig();
		NodeLayout layout = config.getLayout(name);

		if (layout == null) {
			return false;
		}
		getGround().setElasticEnabled(false);
		boolean enableElasticMode = layout.elasticModeEnabled();

		double startX = Double.MAX_VALUE;
		double startY = Double.MAX_VALUE;
		double endX = Double.MIN_VALUE;
		double endY = Double.MIN_VALUE;
		boolean foundSavedPosition = false;

		for (UINeoNode node : getUINodes()) {

			Point2D savedPosition = layout.getPosition(node);
			if (savedPosition != null) {
				double x = savedPosition.getX();
				double y = savedPosition.getY();

				if (!enableElasticMode) {
					node.animateToPositionScaleRotation(x, y, 1, 0, 700);
				} else {
					node.setOffset(x, y);
				}

				if (x < startX) {
					startX = x;
				}
				if (x + node.getWidth() > endX) {
					endX = x + node.getWidth();
				}

				if (y < startY) {
					startY = y;
				}
				if (y + node.getHeight() > endY) {
					endY = y + node.getHeight();
				}

				foundSavedPosition = true;
			}

		}

		if (foundSavedPosition) {
			PBounds fullBounds = new PBounds(startX, startY, endX - startX, endY - startY);
			zoomToBounds(fullBounds, 700);
		}

		if (enableElasticMode) {
			getGround().setElasticEnabled(true);
		}

		return true;
	}

	/**
	 * Saves the current layout as the default
	 */
	public void saveLayoutAsDefault() {
		saveNodeLayout(NetworkViewer.DEFAULT_NODE_LAYOUT_NAME);
	}

	/**
	 * @param name
	 *            Name given to the saved layout
	 */
	public void saveNodeLayout(String name) {

		NetworkViewerConfig layouts = getConfig();
		if (layouts != null) {
			NodeLayout nodeLayout = new NodeLayout(name, this, getGround().isElasticMode());

			layouts.addLayout(nodeLayout);
		} else {
			UserMessages.showError("Could not save node layout");
		}
	}

	public void updateSimulatorProbes() {
		/*
		 * Construct probes
		 */
		Probe[] probesArray = getModel().getSimulator().getProbes();

		/*
		 * Hashset of probes
		 */
		HashSet<Probe> probeToAdd = new HashSet<Probe>(probesArray.length);
		for (Probe probe : probesArray) {
			probeToAdd.add(probe);
		}

		/*
		 * Get current probes in UI
		 */
		LinkedList<UIStateProbe> probesToDestroy = new LinkedList<UIStateProbe>();
		for (UINeoNode nodeUI : getUINodes()) {
			for (UIProbe probeUI : nodeUI.getProbes()) {
				if (probeUI instanceof UIStateProbe) {
					UIStateProbe stateProbe = (UIStateProbe) probeUI;
					if (probeToAdd.contains(stateProbe.getModel())) {
						probeToAdd.remove(stateProbe.getModel());
					} else {
						probesToDestroy.add(stateProbe);

					}
				}
			}
		}

		/*
		 * Remove probes
		 */
		for (UIStateProbe probeUI : probesToDestroy) {
			probeUI.destroy();
		}

		/*
		 * Add probes
		 */
		for (Probe probe : probeToAdd) {
			Probeable target = probe.getTarget();

			if (!(target instanceof Node)) {
				UserMessages.showError("Unsupported target type for probe");
			} else {

				if (!probe.isInEnsemble()) {

					Node node = (Node) target;

					UINeoNode nodeUI = getUINode(node);
					if (nodeUI != null) {
						nodeUI.showProbe(probe);
					} else {
						Util.debugMsg("There is a dangling probe in the Simulator");
					}
				}
			}
		}

	}

	/**
	 * Action to delete a layout
	 * 
	 * @author Shu Wu
	 */
	class DeleteLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		String layoutName;

		public DeleteLayout(String name) {
			super("Delete layout: " + name, name);
			this.layoutName = name;
		}

		@Override
		protected void action() throws ActionException {
			deleteNodeLayout(layoutName);
		}
	}

	/**
	 * Action to restore a layout
	 * 
	 * @author Shu Wu
	 */
	class RestoreLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		String layoutName;

		public RestoreLayout(String name) {
			super("Restore layout: " + name, name);
			this.layoutName = name;
		}

		@Override
		protected void action() throws ActionException {
			if (!restoreNodeLayout(layoutName)) {
				throw new ActionException("Could not restore layout");
			}
		}
	}

	/**
	 * Action to save a layout
	 * 
	 * @author Shu Wu
	 */
	class SaveLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		public SaveLayout(String description) {
			super("Save layout", description);
		}

		@Override
		protected void action() throws ActionException {
			String name = JOptionPane.showInputDialog(UIEnvironment.getInstance(), "Name");

			if (name != null) {
				saveNodeLayout(name);
			} else {
				throw new ActionException("Could not get layout name", false);
			}

		}

	}

	/**
	 * Action to hide all widgets
	 * 
	 * @author Shu Wu
	 */
	class SetOTVisiblityAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		private boolean visible;

		public SetOTVisiblityAction(String actionName, boolean visible) {
			super(actionName);
			this.visible = visible;
		}

		@Override
		protected void action() throws ActionException {
			setOriginsTerminationsVisible(visible);
		}

	}

	public Node getNodeModel(String name) {
		try {
			return getModel().getNode(name);
		} catch (StructuralException e) {
			// Node does not exist
			return null;
		}
	}

	public UINeoNode addNodeModel(Node node) throws ContainerException {
		return addNodeModel(node, null, null);
	}

	public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException {
		try {
			getModel().addNode(node);

			UINeoNode nodeUI = UINeoNode.createNodeUI(node);

			if (posX != null && posY != null) {
				nodeUI.setOffset(posX, posY);
				addUINode(nodeUI, false, false);
			} else {
				addUINode(nodeUI, true, false);
			}
			return nodeUI;
		} catch (StructuralException e) {
			throw new ContainerException(e.toString());
		}
	}

}
