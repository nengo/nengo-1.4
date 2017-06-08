.. java:import:: java.awt BasicStroke

.. java:import:: java.awt Paint

.. java:import:: java.awt Stroke

.. java:import:: java.awt.event InputEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util HashSet

.. java:import:: java.util Iterator

.. java:import:: ca.nengo.ui.lib.actions DragAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticGround

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldSkyImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects SelectionBorder

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.event PDragSequenceEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.event PInputEventFilter

.. java:import:: edu.umd.cs.piccolo.nodes PPath

.. java:import:: edu.umd.cs.piccolo.util PBounds

.. java:import:: edu.umd.cs.piccolo.util PDimension

.. java:import:: edu.umd.cs.piccolo.util PNodeFilter

.. java:import:: edu.umd.cs.piccolox.event PNotificationCenter

SelectionHandler.BoundsFilter
=============================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: protected class BoundsFilter implements PNodeFilter
   :outertype: SelectionHandler

Fields
------
bounds
^^^^^^

.. java:field::  PBounds bounds
   :outertype: SelectionHandler.BoundsFilter

localBounds
^^^^^^^^^^^

.. java:field::  PBounds localBounds
   :outertype: SelectionHandler.BoundsFilter

Constructors
------------
BoundsFilter
^^^^^^^^^^^^

.. java:constructor:: protected BoundsFilter(PBounds bounds)
   :outertype: SelectionHandler.BoundsFilter

Methods
-------
accept
^^^^^^

.. java:method:: public boolean accept(PNode node)
   :outertype: SelectionHandler.BoundsFilter

acceptChildrenOf
^^^^^^^^^^^^^^^^

.. java:method:: public boolean acceptChildrenOf(PNode node)
   :outertype: SelectionHandler.BoundsFilter

