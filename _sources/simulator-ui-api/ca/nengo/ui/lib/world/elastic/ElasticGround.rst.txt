.. java:import:: java.awt.geom Point2D

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Collections

.. java:import:: java.util Hashtable

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world ObjectSet

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: edu.uci.ics.jung.graph.impl AbstractSparseEdge

.. java:import:: edu.uci.ics.jung.graph.impl DirectedSparseEdge

.. java:import:: edu.uci.ics.jung.graph.impl SparseGraph

.. java:import:: edu.uci.ics.jung.graph.impl UndirectedSparseEdge

.. java:import:: edu.uci.ics.jung.utils UserData

.. java:import:: edu.uci.ics.jung.visualization Layout

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.util PBounds

ElasticGround
=============

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class ElasticGround extends WorldGroundImpl

Fields
------
ELASTIC_LENGTH_KEY
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String ELASTIC_LENGTH_KEY
   :outertype: ElasticGround

Constructors
------------
ElasticGround
^^^^^^^^^^^^^

.. java:constructor:: public ElasticGround()
   :outertype: ElasticGround

Methods
-------
childAdded
^^^^^^^^^^

.. java:method:: @Override public void childAdded(WorldObject wo)
   :outertype: ElasticGround

childRemoved
^^^^^^^^^^^^

.. java:method:: @Override public void childRemoved(WorldObject wo)
   :outertype: ElasticGround

getElasticChildren
^^^^^^^^^^^^^^^^^^

.. java:method:: public Iterable<ElasticObject> getElasticChildren()
   :outertype: ElasticGround

getElasticPosition
^^^^^^^^^^^^^^^^^^

.. java:method:: public Point2D getElasticPosition(ElasticObject node)
   :outertype: ElasticGround

getGraph
^^^^^^^^

.. java:method:: public SparseGraph getGraph()
   :outertype: ElasticGround

   :return: The current graph representation of the Ground.

getWorld
^^^^^^^^

.. java:method:: @Override public ElasticWorld getWorld()
   :outertype: ElasticGround

isElasticMode
^^^^^^^^^^^^^

.. java:method:: public boolean isElasticMode()
   :outertype: ElasticGround

isPositionLocked
^^^^^^^^^^^^^^^^

.. java:method:: public boolean isPositionLocked(ElasticObject node)
   :outertype: ElasticGround

modifyEdgeDistances
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void modifyEdgeDistances(ElasticObject obj, double delta)
   :outertype: ElasticGround

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: ElasticGround

setElasticEnabled
^^^^^^^^^^^^^^^^^

.. java:method:: public void setElasticEnabled(boolean enabled)
   :outertype: ElasticGround

setElasticPosition
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setElasticPosition(ElasticObject node, double x, double y)
   :outertype: ElasticGround

setPositionLocked
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPositionLocked(ElasticObject node, boolean lockEnabled)
   :outertype: ElasticGround

   Locks the position of an elastic node so it isn't affected by the layout runner

   :param node:
   :param lockEnabled:

updateChildrenFromLayout
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateChildrenFromLayout(Layout layout, boolean animateNodes, boolean zoomToLayout)
   :outertype: ElasticGround

updateGraph
^^^^^^^^^^^

.. java:method:: public UpdateGraphResult updateGraph()
   :outertype: ElasticGround

   This method must be executed from the swing dispatcher thread because it must be synchronized with the Graphical children elements.

