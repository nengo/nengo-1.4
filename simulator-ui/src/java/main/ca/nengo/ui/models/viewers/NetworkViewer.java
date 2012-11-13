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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Probeable;
import ca.nengo.model.Projection;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.util.menus.PopupMenuBuilder;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
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
    private File layoutFile;

    /**
     * @param pNetwork
     *            Parent Network UI wrapper
     */
    public NetworkViewer(UINetwork pNetwork) {
        super(pNetwork);
        this.layoutFile = new File("layouts/" + pNetwork.getName() + ".layout");
    }

    @Override
    protected boolean canRemoveChildModel(Node node) {
        return true;
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
            if (restoreNodeLayout()) {
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
    public void constructMenu(PopupMenuBuilder menu, Double posX, Double posY) {
        super.constructMenu(menu, posX, posY);

        /*
         * Origins & Terminations
         */
        menu.addSection("Origins and Terminations");
        menu.addAction(new SetOTVisiblityAction("Unhide all", true));
        menu.addAction(new SetOTVisiblityAction("Hide all", false));

        /*
         * Construct simulator menu
         */
        UINetwork.constructSimulatorMenu(menu, getViewerParent());

        /*
         * Create new models
         */
        /*menu.addSection("Add model");
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

		menu.addAction(new OpenNeoFileAction(this));*/

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
     */
    public boolean restoreNodeLayout() {
        if (!layoutFile.exists()) {
            return false;
        }

        getGround().setElasticEnabled(false);
        boolean enableElasticMode = false;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(layoutFile));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        HashMap<String, Float[]> nodeXY = new HashMap<String, Float[]>();
        String line = null;
        PBounds fullBounds = null;

        try {
            while((line = reader.readLine()) != null) {
                if (line.length() >= 2 && line.substring(0, 2).equals("# ")) {
                    if (line.indexOf("elasticmode=") != -1) {
                        enableElasticMode = Boolean.parseBoolean(
                                line.substring(line.indexOf('=') + 1));
                    } else if (line.indexOf("viewbounds=") != -1) {
                        float x = Float.parseFloat(line.substring(
                                line.indexOf("x=") + 2, line.indexOf(',')));
                        float y = Float.parseFloat(line.substring(
                                line.indexOf("y=") + 2, line.indexOf(',', line.indexOf("y="))));
                        float width = Float.parseFloat(line.substring(
                                line.indexOf("width=") + 6, line.indexOf(',', line.indexOf("width="))));
                        float height = Float.parseFloat(line.substring(
                                line.indexOf("height=") + 7, line.indexOf(']', line.indexOf("height="))));
                        // Hax
                        x += 161.5;
                        y += 100;
                        width -= 323;
                        height -= 200;
                        fullBounds = new PBounds(x, y, width, height);
                    } else {
                        float x = Float.parseFloat(line.substring(
                                line.indexOf(")=Point2D.Double[") + 17, line.indexOf(',',
                                        line.indexOf(")=Point2D.Double["))));
                        float y = Float.parseFloat(line.substring(
                                line.indexOf(",", line.indexOf(")=Point2D.Double[")) + 1,
                                line.indexOf(']', line.indexOf(")=Point2D.Double["))));
                        String fullName = line.substring(2,
                                line.indexOf("=Point2D.Double["));
                        nodeXY.put(fullName, new Float[]{x, y});
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (UINeoNode node : getUINodes()) {
            Float[] xy = nodeXY.get(node.getFullName());

            if (xy != null) {
                if (!enableElasticMode) {
                    node.animateToPositionScaleRotation(xy[0], xy[1], 1, 0, 700);
                } else {
                    node.setOffset(xy[0], xy[1]);
                }
            }
        }

        if (fullBounds != null) {
            zoomToBounds(fullBounds, 700);
        }

        if (enableElasticMode) {
            getGround().setElasticEnabled(true);
        }

        return fullBounds != null;
    }

    /**
     * 
     */
    public void saveNodeLayout() {
        StringBuilder newfile = new StringBuilder();
        if (layoutFile.exists()) {
            File backup = new File(layoutFile.getAbsolutePath() + ".bak");
            try {
                Util.copyFile(layoutFile, backup);
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(backup));
                String line = null;

                while((line = reader.readLine()) != null) {
                    if (line.length() > 0 && line.charAt(0) != '#') {
                        newfile.append(line + "\n");
                    }
                }
                reader.close();
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }

        newfile.append("##############################\n");
        newfile.append("### Nengo Workspace layout ###\n");
        newfile.append("##############################\n");
        newfile.append("# elasticmode=" +
                Boolean.toString(getGround().isElasticMode()) + "\n");
        newfile.append("# viewbounds=" +
                getSky().getViewBounds().toString() + "\n");
        for (UINeoNode object : getUINodes()) {
            newfile.append("# " + object.getFullName() + "=" +
                    object.getOffset().toString() + "\n");
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(layoutFile));

            bw.write(newfile.toString());
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
            if (!restoreNodeLayout()) {
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

        public SaveLayout() {
            super("Save layout");
        }

        @Override
        protected void action() throws ActionException {
            String name = JOptionPane.showInputDialog(UIEnvironment.getInstance(), "Name");

            if (name != null) {
                saveNodeLayout();
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
        	// first, add node to UI
            UINeoNode nodeUI = UINeoNode.createNodeUI(node);

            if (posX != null && posY != null) {
                nodeUI.setOffset(posX, posY);
                addUINode(nodeUI, false, false);
            } else {
                addUINode(nodeUI, true, false);
            }
            
            // second, add node to model. This must be done second, otherwise
            // it updates the view and there is a race to add the UI node
            getModel().addNode(node);
            
            return nodeUI;
        } catch (StructuralException e) {
            throw new ContainerException(e.toString());
        }
    }
}
