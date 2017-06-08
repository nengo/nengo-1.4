.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util Hashtable

.. java:import:: java.util List

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.actions LayoutAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedStatusMsg

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.handlers AbstractStatusHandler

.. java:import:: ca.nengo.ui.models ModelsContextMenu

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

NodeViewer.ZoomToFitActivity
============================

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type::  class ZoomToFitActivity extends PActivity
   :outertype: NodeViewer

   Zooms the viewer to optimally fit all nodes

   :author: Shu Wu

Constructors
------------
ZoomToFitActivity
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ZoomToFitActivity()
   :outertype: NodeViewer.ZoomToFitActivity

Methods
-------
activityStarted
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void activityStarted()
   :outertype: NodeViewer.ZoomToFitActivity

