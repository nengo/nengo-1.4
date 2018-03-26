.. java:import:: java.awt.geom Dimension2D

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Iterator

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world ObjectSet

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXNode

.. java:import:: edu.umd.cs.piccolo PLayer

.. java:import:: edu.umd.cs.piccolo PNode

WorldGroundImpl
===============

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type:: public class WorldGroundImpl extends WorldLayerImpl implements WorldLayer

   Layer within a world which is zoomable and pannable. It contains world objects.

   :author: Shu Wu

Constructors
------------
WorldGroundImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public WorldGroundImpl()
   :outertype: WorldGroundImpl

Methods
-------
addChild
^^^^^^^^

.. java:method:: @Override public void addChild(WorldObject wo, int index)
   :outertype: WorldGroundImpl

addChildFancy
^^^^^^^^^^^^^

.. java:method:: public void addChildFancy(WorldObject wo)
   :outertype: WorldGroundImpl

   Adds a child object. Like addChild, but with more pizzaz.

   :param wo: Object to add to the layer

addChildFancy
^^^^^^^^^^^^^

.. java:method:: public void addChildFancy(WorldObject wo, boolean centerCameraPosition)
   :outertype: WorldGroundImpl

addEdge
^^^^^^^

.. java:method:: public void addEdge(PXEdge edge)
   :outertype: WorldGroundImpl

childAdded
^^^^^^^^^^

.. java:method:: @Override public void childAdded(WorldObject wo)
   :outertype: WorldGroundImpl

childRemoved
^^^^^^^^^^^^

.. java:method:: @Override public void childRemoved(WorldObject wo)
   :outertype: WorldGroundImpl

containsEdge
^^^^^^^^^^^^

.. java:method:: public boolean containsEdge(PXEdge edge)
   :outertype: WorldGroundImpl

dropObject
^^^^^^^^^^

.. java:method:: protected static void dropObject(World world, WorldObject parent, WorldObject wo, boolean centerCameraPosition)
   :outertype: WorldGroundImpl

   Adds a little pizzaz when adding new objects

   :param wo: Object to be added
   :param centerCameraPosition: whether the object's position should be changed to appear at the center of the camera

getChildren
^^^^^^^^^^^

.. java:method:: @Override public Iterable<WorldObject> getChildren()
   :outertype: WorldGroundImpl

getEdges
^^^^^^^^

.. java:method:: public Collection<PXEdge> getEdges()
   :outertype: WorldGroundImpl

getGroundScale
^^^^^^^^^^^^^^

.. java:method:: public double getGroundScale()
   :outertype: WorldGroundImpl

   :return: The scale of the ground in relation to the sky

localToParent
^^^^^^^^^^^^^

.. java:method:: @Override public Dimension2D localToParent(Dimension2D localRectangle)
   :outertype: WorldGroundImpl

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: WorldGroundImpl

