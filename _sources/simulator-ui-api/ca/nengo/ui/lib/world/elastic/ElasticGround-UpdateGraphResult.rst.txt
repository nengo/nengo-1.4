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

ElasticGround.UpdateGraphResult
===============================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public static class UpdateGraphResult
   :outertype: ElasticGround

Constructors
------------
UpdateGraphResult
^^^^^^^^^^^^^^^^^

.. java:constructor:: public UpdateGraphResult(boolean graphUpdated, Collection<ElasticVertex> addedVertices)
   :outertype: ElasticGround.UpdateGraphResult

Methods
-------
getAddedVertices
^^^^^^^^^^^^^^^^

.. java:method:: public Collection<ElasticVertex> getAddedVertices()
   :outertype: ElasticGround.UpdateGraphResult

isGraphUpdated
^^^^^^^^^^^^^^

.. java:method:: public boolean isGraphUpdated()
   :outertype: ElasticGround.UpdateGraphResult

