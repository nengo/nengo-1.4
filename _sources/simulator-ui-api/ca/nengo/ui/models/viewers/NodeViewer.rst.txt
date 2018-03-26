.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util Hashtable

.. java:import:: java.util List

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.actions LayoutAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedStatusMsg

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.handlers AbstractStatusHandler

.. java:import:: ca.nengo.ui.models ModelsContextMenu

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

NodeViewer
==========

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type:: public abstract class NodeViewer extends ElasticWorld implements Interactable

   Viewer for looking at NEO Node models

   :author: Shu

Fields
------
neoNodesChildren
^^^^^^^^^^^^^^^^

.. java:field:: protected final Hashtable<Node, UINeoNode> neoNodesChildren
   :outertype: NodeViewer

   Children of NEO nodes

Constructors
------------
NodeViewer
^^^^^^^^^^

.. java:constructor:: public NodeViewer(UINodeViewable nodeContainer)
   :outertype: NodeViewer

   :param nodeContainer: UI Object containing the Node model

Methods
-------
addUINode
^^^^^^^^^

.. java:method:: protected void addUINode(UINeoNode node, boolean dropInCenterOfCamera, boolean moveCameraToNode)
   :outertype: NodeViewer

   :param node: node to be added
   :param updateModel: if true, the network model is updated. this may be false, if it is known that the network model already contains this node
   :param dropInCenterOfCamera: whether to drop the node in the center of the camera
   :param moveCameraToNode: whether to move the camera to where the node is

applyDefaultLayout
^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract void applyDefaultLayout()
   :outertype: NodeViewer

   Applies the default layout

applySortLayout
^^^^^^^^^^^^^^^

.. java:method:: public void applySortLayout(SortMode sortMode)
   :outertype: NodeViewer

   Applies a square layout which is sorted

   :param sortMode: Type of sort layout to use

canRemoveChildModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean canRemoveChildModel(Node node)
   :outertype: NodeViewer

constructSelectionMenu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructSelectionMenu(Collection<WorldObject> selection, PopupMenuBuilder menu)
   :outertype: NodeViewer

doSortByNameLayout
^^^^^^^^^^^^^^^^^^

.. java:method:: public void doSortByNameLayout()
   :outertype: NodeViewer

getJustOpened
^^^^^^^^^^^^^

.. java:method:: public Boolean getJustOpened()
   :outertype: NodeViewer

getModel
^^^^^^^^

.. java:method:: public Node getModel()
   :outertype: NodeViewer

   :return: NEO Model represented by the viewer

getUINode
^^^^^^^^^

.. java:method:: public UINeoNode getUINode(Node node)
   :outertype: NodeViewer

getUINodes
^^^^^^^^^^

.. java:method:: public List<UINeoNode> getUINodes()
   :outertype: NodeViewer

   :return: A collection of NEO Nodes contained in this viewer

getViewerParent
^^^^^^^^^^^^^^^

.. java:method:: public UINodeViewable getViewerParent()
   :outertype: NodeViewer

   :return: Parent of this viewer

initialize
^^^^^^^^^^

.. java:method:: protected void initialize()
   :outertype: NodeViewer

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: NodeViewer

removeChildModel
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void removeChildModel(Node node)
   :outertype: NodeViewer

setJustOpened
^^^^^^^^^^^^^

.. java:method:: public void setJustOpened(Boolean justOpened)
   :outertype: NodeViewer

setOriginsTerminationsVisible
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setOriginsTerminationsVisible(boolean visible)
   :outertype: NodeViewer

updateViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void updateViewFromModel(boolean isFirstUpdate)
   :outertype: NodeViewer

   Called when the model changes. Updates the viewer based on the NEO model.

updateViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateViewFromModel()
   :outertype: NodeViewer

