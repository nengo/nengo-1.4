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

ElasticLayoutRunner.ElasticLengthFunction
=========================================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type::  class ElasticLengthFunction implements LengthFunction
   :outertype: ElasticLayoutRunner

Methods
-------
getLength
^^^^^^^^^

.. java:method:: public double getLength(Edge e)
   :outertype: ElasticLayoutRunner.ElasticLengthFunction

getMass
^^^^^^^

.. java:method:: public double getMass(Vertex v)
   :outertype: ElasticLayoutRunner.ElasticLengthFunction

