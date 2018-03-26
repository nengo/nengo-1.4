.. java:import:: java.awt Dimension

.. java:import:: java.util LinkedList

.. java:import:: java.util Arrays

.. java:import:: java.util Iterator

.. java:import:: ca.nengo.ui.lib.world.elastic StretchedFeedForwardLayout.VoidVertex

.. java:import:: edu.uci.ics.jung.graph Graph

.. java:import:: edu.uci.ics.jung.graph Vertex

.. java:import:: edu.uci.ics.jung.visualization AbstractLayout

.. java:import:: edu.uci.ics.jung.visualization Coordinates

FeedForwardLayout
=================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class FeedForwardLayout extends AbstractLayout

   Arrange the layout of neural network according to signal flow.

   :author: Yan Wu

Constructors
------------
FeedForwardLayout
^^^^^^^^^^^^^^^^^

.. java:constructor:: public FeedForwardLayout(Graph g)
   :outertype: FeedForwardLayout

   :param g:

Methods
-------
advancePositions
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void advancePositions()
   :outertype: FeedForwardLayout

incrementsAreDone
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean incrementsAreDone()
   :outertype: FeedForwardLayout

initializeLocations
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void initializeLocations()
   :outertype: FeedForwardLayout

initialize_local_vertex
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void initialize_local_vertex(Vertex arg0)
   :outertype: FeedForwardLayout

isIncremental
^^^^^^^^^^^^^

.. java:method:: public boolean isIncremental()
   :outertype: FeedForwardLayout

sortVertices
^^^^^^^^^^^^

.. java:method:: protected LinkedList<LinkedList<Vertex>> sortVertices()
   :outertype: FeedForwardLayout

