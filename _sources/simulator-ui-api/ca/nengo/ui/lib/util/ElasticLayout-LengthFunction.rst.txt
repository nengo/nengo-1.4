.. java:import:: java.awt Dimension

.. java:import:: java.awt.event ComponentAdapter

.. java:import:: java.awt.event ComponentEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ConcurrentModificationException

.. java:import:: java.util Iterator

.. java:import:: edu.uci.ics.jung.graph Edge

.. java:import:: edu.uci.ics.jung.graph Graph

.. java:import:: edu.uci.ics.jung.graph Vertex

.. java:import:: edu.uci.ics.jung.utils Pair

.. java:import:: edu.uci.ics.jung.utils UserData

.. java:import:: edu.uci.ics.jung.visualization AbstractLayout

.. java:import:: edu.uci.ics.jung.visualization Coordinates

.. java:import:: edu.uci.ics.jung.visualization LayoutMutable

ElasticLayout.LengthFunction
============================

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public static interface LengthFunction
   :outertype: ElasticLayout

   If the edge is weighted, then override this method to show what the visualized length is.

   :author: Danyel Fisher

Methods
-------
getLength
^^^^^^^^^

.. java:method:: public double getLength(Edge e)
   :outertype: ElasticLayout.LengthFunction

getMass
^^^^^^^

.. java:method:: public double getMass(Vertex v)
   :outertype: ElasticLayout.LengthFunction

