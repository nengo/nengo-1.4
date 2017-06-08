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

ElasticLayout
=============

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public class ElasticLayout extends AbstractLayout implements LayoutMutable

   The SpringLayout package represents a visualization of a set of nodes. The SpringLayout, which is initialized with a Graph, assigns X/Y locations to each node. When called \ ``relax()``\ , the SpringLayout moves the visualization forward one step.

   Modified by ShuWu to dimensionless layout

   :author: Danyel Fisher, Joshua O'Madadhain

Fields
------
UNITLENGTHFUNCTION
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final LengthFunction UNITLENGTHFUNCTION
   :outertype: ElasticLayout

force_multiplier
^^^^^^^^^^^^^^^^

.. java:field:: protected double force_multiplier
   :outertype: ElasticLayout

key
^^^

.. java:field::  Object key
   :outertype: ElasticLayout

lengthFunction
^^^^^^^^^^^^^^

.. java:field:: protected LengthFunction lengthFunction
   :outertype: ElasticLayout

stretch
^^^^^^^

.. java:field:: protected double stretch
   :outertype: ElasticLayout

Constructors
------------
ElasticLayout
^^^^^^^^^^^^^

.. java:constructor:: public ElasticLayout(Graph g)
   :outertype: ElasticLayout

   Constructor for a SpringLayout for a raw graph with associated dimension--the input knows how big the graph is. Defaults to the unit length function.

ElasticLayout
^^^^^^^^^^^^^

.. java:constructor:: public ElasticLayout(Graph g, LengthFunction f)
   :outertype: ElasticLayout

   Constructor for a SpringLayout for a raw graph with associated component.

   :param g: the input Graph
   :param f: the length function

Methods
-------
advancePositions
^^^^^^^^^^^^^^^^

.. java:method:: public void advancePositions()
   :outertype: ElasticLayout

   Relaxation step. Moves all nodes a smidge.

calcEdgeLength
^^^^^^^^^^^^^^

.. java:method:: protected void calcEdgeLength(SpringEdgeData sed, LengthFunction f)
   :outertype: ElasticLayout

calculateRepulsion
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void calculateRepulsion()
   :outertype: ElasticLayout

getAVertex
^^^^^^^^^^

.. java:method:: protected Vertex getAVertex(Edge e)
   :outertype: ElasticLayout

getForceMultiplier
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getForceMultiplier()
   :outertype: ElasticLayout

   :return: the current value for the edge length force multiplier

   **See also:** :java:ref:`.setForceMultiplier(double)`

getLength
^^^^^^^^^

.. java:method:: public double getLength(Edge e)
   :outertype: ElasticLayout

getSpringData
^^^^^^^^^^^^^

.. java:method:: public SpringEdgeData getSpringData(Edge e)
   :outertype: ElasticLayout

getSpringData
^^^^^^^^^^^^^

.. java:method:: public SpringVertexData getSpringData(Vertex v)
   :outertype: ElasticLayout

getSpringKey
^^^^^^^^^^^^

.. java:method:: public Object getSpringKey()
   :outertype: ElasticLayout

getStatus2
^^^^^^^^^^

.. java:method:: public String getStatus2()
   :outertype: ElasticLayout

   Returns the status.

getStretch
^^^^^^^^^^

.. java:method:: public double getStretch()
   :outertype: ElasticLayout

   :return: the current value for the stretch parameter

   **See also:** :java:ref:`.setStretch(double)`

incrementsAreDone
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean incrementsAreDone()
   :outertype: ElasticLayout

   For now, we pretend it never finishes.

initialize
^^^^^^^^^^

.. java:method:: public void initialize()
   :outertype: ElasticLayout

   New initializer for layout of unbounded size

initializeLocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void initializeLocation(Vertex v, Coordinates coord, Dimension d)
   :outertype: ElasticLayout

initialize_local
^^^^^^^^^^^^^^^^

.. java:method:: protected void initialize_local()
   :outertype: ElasticLayout

initialize_local_vertex
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void initialize_local_vertex(Vertex v)
   :outertype: ElasticLayout

   (non-Javadoc)

   **See also:** :java:ref:`edu.uci.ics.jung.visualization.AbstractLayout.initialize_local_vertex(edu.uci.ics.jung.graph.Vertex)`

isIncremental
^^^^^^^^^^^^^

.. java:method:: public boolean isIncremental()
   :outertype: ElasticLayout

   This one is an incremental visualization

moveNodes
^^^^^^^^^

.. java:method:: protected void moveNodes()
   :outertype: ElasticLayout

relaxEdges
^^^^^^^^^^

.. java:method:: protected void relaxEdges()
   :outertype: ElasticLayout

setForceMultiplier
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setForceMultiplier(double force)
   :outertype: ElasticLayout

   Sets the force multiplier for this instance. This value is used to specify how strongly an edge "wants" to be its default length (higher values indicate a greater attraction for the default length), which affects how much its endpoints move at each timestep. The default value is 1/3. A value of 0 turns off any attempt by the layout to cause edges to conform to the default length. Negative values cause long edges to get longer and short edges to get shorter; use at your own risk.

setStretch
^^^^^^^^^^

.. java:method:: public void setStretch(double stretch)
   :outertype: ElasticLayout

   Sets the stretch parameter for this instance. This value specifies how much the degrees of an edge's incident vertices should influence how easily the endpoints of that edge can move (that is, that edge's tendency to change its length).

   The default value is 0.70. Positive values less than 1 cause high-degree vertices to move less than low-degree vertices, and values > 1 cause high-degree vertices to move more than low-degree vertices. Negative values will have unpredictable and inconsistent results.

   :param stretch:

update
^^^^^^

.. java:method:: public void update()
   :outertype: ElasticLayout

   **See also:** :java:ref:`edu.uci.ics.jung.visualization.LayoutMutable.update()`

