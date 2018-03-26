.. java:import:: java.awt.geom Point2D

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.util ElasticLayout

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util ElasticLayout.LengthFunction

.. java:import:: edu.uci.ics.jung.graph ArchetypeVertex

.. java:import:: edu.uci.ics.jung.graph Edge

.. java:import:: edu.uci.ics.jung.graph Vertex

.. java:import:: edu.uci.ics.jung.graph.impl SparseGraph

ElasticLayoutRunner
===================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class ElasticLayoutRunner

Fields
------
RELAX_DELTA
^^^^^^^^^^^

.. java:field:: public static final double RELAX_DELTA
   :outertype: ElasticLayoutRunner

   Used to determine when to pause the algorithm

SPRING_LAYOUT_DEFAULT_LENGTH
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int SPRING_LAYOUT_DEFAULT_LENGTH
   :outertype: ElasticLayoutRunner

SPRING_LAYOUT_DEFAULT_REPULSION_DISTANCE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int SPRING_LAYOUT_DEFAULT_REPULSION_DISTANCE
   :outertype: ElasticLayoutRunner

SPRING_LAYOUT_FORCE_MULTIPLIER
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final float SPRING_LAYOUT_FORCE_MULTIPLIER
   :outertype: ElasticLayoutRunner

Constructors
------------
ElasticLayoutRunner
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ElasticLayoutRunner(ElasticGround world)
   :outertype: ElasticLayoutRunner

Methods
-------
forceMove
^^^^^^^^^

.. java:method:: public void forceMove(Vertex picked, double x, double y)
   :outertype: ElasticLayoutRunner

getLocation
^^^^^^^^^^^

.. java:method:: public Point2D getLocation(ArchetypeVertex v)
   :outertype: ElasticLayoutRunner

isLocked
^^^^^^^^

.. java:method:: public boolean isLocked(Vertex v)
   :outertype: ElasticLayoutRunner

isLockedVertex
^^^^^^^^^^^^^^

.. java:method:: public boolean isLockedVertex(Vertex v)
   :outertype: ElasticLayoutRunner

lockVertex
^^^^^^^^^^

.. java:method:: public void lockVertex(Vertex v)
   :outertype: ElasticLayoutRunner

start
^^^^^

.. java:method:: public void start()
   :outertype: ElasticLayoutRunner

stopLayout
^^^^^^^^^^

.. java:method:: public void stopLayout()
   :outertype: ElasticLayoutRunner

unlockVertex
^^^^^^^^^^^^

.. java:method:: public void unlockVertex(Vertex v)
   :outertype: ElasticLayoutRunner

updateLayout
^^^^^^^^^^^^

.. java:method:: public void updateLayout()
   :outertype: ElasticLayoutRunner

