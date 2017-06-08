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

GroundNode
==========

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type::  class GroundNode extends PXNode

Constructors
------------
GroundNode
^^^^^^^^^^

.. java:constructor:: public GroundNode()
   :outertype: GroundNode

Methods
-------
addEdge
^^^^^^^

.. java:method:: public void addEdge(PXEdge edge)
   :outertype: GroundNode

containsEdge
^^^^^^^^^^^^

.. java:method:: public boolean containsEdge(PXEdge edge)
   :outertype: GroundNode

getEdges
^^^^^^^^

.. java:method:: public Collection<PXEdge> getEdges()
   :outertype: GroundNode

setParent
^^^^^^^^^

.. java:method:: @Override public void setParent(PNode newParent)
   :outertype: GroundNode

