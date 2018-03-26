.. java:import:: java.util Collection

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticEdge

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

Edge
====

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type::  class Edge extends ElasticEdge

   This edge is only visible when the LineEndWell is visible or the LineEnd is connected

   :author: Shu Wu

Constructors
------------
Edge
^^^^

.. java:constructor:: public Edge(LineWell startNode, LineConnector endNode, double length)
   :outertype: Edge

Methods
-------
paint
^^^^^

.. java:method:: @Override protected void paint(PPaintContext paintContext)
   :outertype: Edge

