.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util ListIterator

.. java:import:: edu.uci.ics.jung.graph Graph

.. java:import:: edu.uci.ics.jung.graph Vertex

.. java:import:: edu.uci.ics.jung.graph.impl SimpleSparseVertex

StretchedFeedForwardLayout
==========================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class StretchedFeedForwardLayout extends FeedForwardLayout

   Stretch vertices that are linked to other vertices in the same layer.

   :author: Wu Yan

Constructors
------------
StretchedFeedForwardLayout
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public StretchedFeedForwardLayout(Graph g)
   :outertype: StretchedFeedForwardLayout

   :param g:

Methods
-------
sortVertices
^^^^^^^^^^^^

.. java:method:: @Override protected LinkedList<LinkedList<Vertex>> sortVertices()
   :outertype: StretchedFeedForwardLayout

