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

ElasticLayout.UnitLengthFunction
================================

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public static final class UnitLengthFunction implements LengthFunction
   :outertype: ElasticLayout

   Returns all edges as the same length: the input value

   :author: danyelf

Fields
------
length
^^^^^^

.. java:field::  int length
   :outertype: ElasticLayout.UnitLengthFunction

Constructors
------------
UnitLengthFunction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UnitLengthFunction(int length)
   :outertype: ElasticLayout.UnitLengthFunction

Methods
-------
getLength
^^^^^^^^^

.. java:method:: public double getLength(Edge e)
   :outertype: ElasticLayout.UnitLengthFunction

getMass
^^^^^^^

.. java:method:: public double getMass(Vertex v)
   :outertype: ElasticLayout.UnitLengthFunction

