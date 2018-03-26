.. java:import:: java.io BufferedReader

.. java:import:: java.io BufferedWriter

.. java:import:: java.io File

.. java:import:: java.io FileReader

.. java:import:: java.io FileWriter

.. java:import:: java.io IOException

.. java:import:: java.util Enumeration

.. java:import:: java.util HashMap

.. java:import:: java.util HashSet

.. java:import:: java.util LinkedList

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Button

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons ArrowIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons LoadIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons SaveIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons ZoomIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProjection

.. java:import:: ca.nengo.ui.models.nodes.widgets UIStateProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.util Probe

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

NetworkViewer
=============

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type:: public class NetworkViewer extends NodeViewer implements NodeContainer

   Viewer for peeking into a Network

   :author: Shu Wu

Fields
------
newItemPositionX
^^^^^^^^^^^^^^^^

.. java:field:: protected Double newItemPositionX
   :outertype: NetworkViewer

newItemPositionY
^^^^^^^^^^^^^^^^

.. java:field:: protected Double newItemPositionY
   :outertype: NetworkViewer

Constructors
------------
NetworkViewer
^^^^^^^^^^^^^

.. java:constructor:: public NetworkViewer(UINetwork pNetwork)
   :outertype: NetworkViewer

   :param pNetwork: Parent Network UI wrapper

Methods
-------
addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node) throws ContainerException
   :outertype: NetworkViewer

addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException
   :outertype: NetworkViewer

applyDefaultLayout
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void applyDefaultLayout()
   :outertype: NetworkViewer

canRemoveChildModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean canRemoveChildModel(Node node)
   :outertype: NetworkViewer

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override public void constructMenu(PopupMenuBuilder menu, Double posX, Double posY)
   :outertype: NetworkViewer

getModel
^^^^^^^^

.. java:method:: @Override public Network getModel()
   :outertype: NetworkViewer

getNodeModel
^^^^^^^^^^^^

.. java:method:: public Node getNodeModel(String name)
   :outertype: NetworkViewer

getViewerParent
^^^^^^^^^^^^^^^

.. java:method:: @Override public UINetwork getViewerParent()
   :outertype: NetworkViewer

initialize
^^^^^^^^^^

.. java:method:: @Override protected void initialize()
   :outertype: NetworkViewer

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: NetworkViewer

removeChildModel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void removeChildModel(Node node)
   :outertype: NetworkViewer

restoreNodeLayout
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean restoreNodeLayout()
   :outertype: NetworkViewer

   :return: Whether the operation was successful

saveNodeLayout
^^^^^^^^^^^^^^

.. java:method:: public void saveNodeLayout()
   :outertype: NetworkViewer

setNewItemPosition
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setNewItemPosition(Double x, Double y)
   :outertype: NetworkViewer

updateSimulatorProbes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateSimulatorProbes()
   :outertype: NetworkViewer

updateViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateViewFromModel(boolean isFirstUpdate)
   :outertype: NetworkViewer

   Construct UI Nodes from the NEO Network model

